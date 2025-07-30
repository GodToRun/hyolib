package hyolib;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class FastGraphics {
    private BufferedImage buf;
    private BufferedImage fbuf;
    private int[] pixels;
    private int[] tex, normal;
    private int texWidth, texHeight, normalWidth, normalHeight;
    private float[] depth;
    private int width, height;

    /** paintComponent()에서 호출 */
    public void init(Graphics g, int compW, int compH) {
        Rectangle clip = g.getClipBounds();
        int w = clip!=null?clip.width:compW;
        int h = clip!=null?clip.height:compH;
        if (buf==null || w!=width || h!=height) {
            width=w; height=h;
            buf    = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            pixels = ((DataBufferInt)buf.getRaster().getDataBuffer()).getData();
            depth  = new float[w*h];
        }
        if (fbuf==null || width!=fbuf.getWidth() || height!=fbuf.getHeight()) {
        	fbuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        Arrays.fill(pixels, 0);
        Arrays.fill(depth, Float.POSITIVE_INFINITY);
    }
    public void setTex(int[] col, int w, int h) {
    	tex = col;
    	texWidth = w;
    	texHeight = h;
    	
    }
    public void setNormal(int[] normal, int w, int h) {
    	this.normal = normal;
    	this.normalWidth = w;
    	this.normalHeight = h;
    }
    public void fillTriangle(
            int x1, int y1, double z1,
            int x2, int y2, double z2,
            int x3, int y3, double z3,
            int argb, Vertex v1, Vertex v2, Vertex v3, int unused
    ) {
    	
    	// 시계 방향 삼각형만 허용 (backface culling)
    	int signedArea = (x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1);
    	if (signedArea <= 0) return; // 반시계 방향이면 그리지 않음
    	
        // 1) 바운딩 박스
        int minX = Math.max(0, Math.min(Math.min(x1, x2), x3));
        int maxX = Math.min(width - 1, Math.max(Math.max(x1, x2), x3));
        int minY = Math.max(0, Math.min(Math.min(y1, y2), y3));
        int maxY = Math.min(height - 1, Math.max(Math.max(y1, y2), y3));
        if (minX > maxX || minY > maxY) return;

        // 2) 엣지 함수 계수 A,B,C 계산
        int A12 = y1 - y2,  B12 = x2 - x1,  C12 = x1*y2 - x2*y1;
        int A23 = y2 - y3,  B23 = x3 - x2,  C23 = x2*y3 - x3*y2;
        int A31 = y3 - y1,  B31 = x1 - x3,  C31 = x3*y1 - x1*y3;

        // 3) planar 보간식 계수 미리 계산 (예: Z,L,S,U,V)
        double det  = (x2 - x1) * (double)(y3 - y1) - (x3 - x1) * (double)(y2 - y1);
        if (det == 0) return;
        double invD = 1.0 / det;
        // Z
        double aZ = ((z2 - z1)*(y3 - y1) - (z3 - z1)*(y2 - y1)) * invD;
        double bZ = ((x2 - x1)*(z3 - z1) - (x3 - x1)*(z2 - z1)) * invD;
        double cZ = z1 - aZ*x1 - bZ*y1;
        // diffuse
        double l1=v1.diffuse, l2=v2.diffuse, l3=v3.diffuse;
        double aL = ((l2 - l1)*(y3 - y1) - (l3 - l1)*(y2 - y1)) * invD;
        double bL = ((x2 - x1)*(l3 - l1) - (x3 - x1)*(l2 - l1)) * invD;
        double cL = l1 - aL*x1 - bL*y1;
        // specular
        double s1=v1.specular, s2=v2.specular, s3=v3.specular;
        double aS = ((s2 - s1)*(y3 - y1) - (s3 - s1)*(y2 - y1)) * invD;
        double bS = ((x2 - x1)*(s3 - s1) - (x3 - x1)*(s2 - s1)) * invD;
        double cS = s1 - aS*x1 - bS*y1;
        // UV
        double u1=v1.uv.getX(), u2=v2.uv.getX(), u3=v3.uv.getX();
        double aU = ((u2-u1)*(y3-y1) - (u3-u1)*(y2-y1)) * invD;
        double bU = ((x2-x1)*(u3-u1) - (x3-x1)*(u2-u1)) * invD;
        double cU = u1 - aU*x1 - bU*y1;
        double v1y=v1.uv.getY(), v2y=v2.uv.getY(), v3y=v3.uv.getY();
        double aV = ((v2y-v1y)*(y3-y1) - (v3y-v1y)*(y2-y1)) * invD;
        double bV = ((x2-x1)*(v3y-v1y) - (x3-x1)*(v2y-v1y)) * invD;
        double cV = v1y - aV*x1 - bV*y1;

        // 4) 바운딩 박스 스캔
        for (int y=minY; y<=maxY; y++) {
            // 엣지 함수 초기값 (x=minX)
            int f12 = A12*minX + B12*y + C12;
            int f23 = A23*minX + B23*y + C23;
            int f31 = A31*minX + B31*y + C31;

            // planar 보간 행별 상수
            double zRow = bZ*y + cZ;
            double lRow = bL*y + cL;
            double sRow = bS*y + cS;
            double uRow = bU*y + cU;
            double vRow = bV*y + cV;

            for (int x=minX; x<=maxX; x++) {
                // half-space test: 모두 ≥0 이면 삼각형 안쪽
                if (f12>=0 && f23>=0 && f31>=0) {
                    int idx = y*width + x;
                    // depth
                    double zv = aZ*x + zRow;
                    if (zv < depth[idx]) {
                        depth[idx] = (float)zv;
                        // lighting
                        double lv = aL*x + lRow;
                        double sv = aS*x + sRow;
                        // texture
                        double u = aU*x + uRow;
                        double v = aV*x + vRow;
                        u = Math.max(0, Math.min(0.999, u));
                        v = Math.max(0, Math.min(0.999, v));
                        int clr = 0xFFFFFFFF;
                        int nclr = 0xFFFFFFFF;
                        if (tex != null) clr = tex[(int)(u * texWidth) + (int)(v * texHeight) * texWidth];
                        if (normal != null) nclr = normal[(int)(u * normalWidth) + (int)(v * normalHeight) * normalWidth];
                        int si = (int)(sv);
                        double red =   (clr & 0x00ff0000) >> 16;
                		double green = (clr & 0x0000ff00) >> 8;
                		double blue =   clr & 0x000000ff;
                		
                		double redN =   (nclr & 0x00ff0000) >> 16;
                		double greenN = (nclr & 0x0000ff00) >> 8;
                		double blueN =   nclr & 0x000000ff;
                		
                		int alpha =     (clr & 0xff000000) >> 24;
                		int alphaP =     (clr & 0xff000000) >>> 24;
                		if (alphaP >= 250) {
                			depth[idx] = (float)zv;
                		}
                		
                		// normal mapping
                		
                		int ni = Math.min(255, (int)(redN + greenN + blueN) / 1);
                        si = (int)((double)si * (double)(ni-20) / 235D + (ni-255) / 16D);
                		
                		//alpha = 255;
                		if (alphaP >= 100)
                			pixels[idx] = packARGB(alpha, Math.min(255, Math.max(0, si+(int)(red*lv))), Math.min(255, Math.max(0, si+(int)(green*lv))), Math.min(255, Math.max(0, si+(int)(blue*lv))));
                    }
                }
                // x→x+1 시 엣지 함수 증분
                f12 += A12;  f23 += A23;  f31 += A31;
            }
        }
    }


    /*public void fillTriangle(
            int x1, int y1, double z1,
            int x2, int y2, double z2,
            int x3, int y3, double z3,
            int argb, Vertex v1, Vertex v2, Vertex v3, int unused
    ) {
        // 1) Y 범위 & 스팬 배열 초기화
        int minY = Math.max(0, Math.min(Math.min(y1,y2),y3));
        int maxY = Math.min(height-1, Math.max(Math.max(y1,y2),y3));
        int spanH = maxY - minY + 1;
        if (spanH <= 0) return;
        int[] xmin = new int[spanH], xmax = new int[spanH];
        Arrays.fill(xmin, width);
        Arrays.fill(xmax, -1);

        // 2) 세 변을 Bresenham edge-walk
        plotEdge(x1,y1, x2,y2, minY, xmin, xmax);
        plotEdge(x2,y2, x3,y3, minY, xmin, xmax);
        plotEdge(x3,y3, x1,y1, minY, xmin, xmax);

        // 3) planar 보간 계수 계산

        double det  = (x2-x1)*(double)(y3-y1) - (x3-x1)*(double)(y2-y1);
        if (det == 0) return;
        double invD = 1.0 / det;

        // 3.1) Z 보간
        double aZ = ((z2-z1)*(y3-y1)-(z3-z1)*(y2-y1))*invD;
        double bZ = ((x2-x1)*(z3-z1)-(x3-x1)*(z2-z1))*invD;
        double cZ = z1 - aZ*x1 - bZ*y1;

        // 3.2) l 보간
        double l1=v1.diffuse, l2=v2.diffuse, l3=v3.diffuse;
        double aL = ((l2-l1)*(y3-y1)-(l3-l1)*(y2-y1))*invD;
        double bL = ((x2-x1)*(l3-l1)-(x3-x1)*(l2-l1))*invD;
        double cL = l1 - aL*x1 - bL*y1;
        
        // 3.21) s 보간
        double s1=v1.specular, s2=v2.specular, s3=v3.specular;
        double aS = ((s2-s1)*(y3-y1)-(s3-s1)*(y2-y1))*invD;
        double bS = ((x2-x1)*(s3-s1)-(x3-x1)*(s2-s1))*invD;
        double cS = s1 - aS*x1 - bS*y1;

        // 3.3) **UV 보간**
        double u1 = v1.uv.getX(), u2 = v2.uv.getX(), u3 = v3.uv.getX();
        double aU = ((u2-u1)*(y3-y1)-(u3-u1)*(y2-y1))*invD;
        double bU = ((x2-x1)*(u3-u1)-(x3-x1)*(u2-u1))*invD;
        double cU = u1 - aU*x1 - bU*y1;

        double v_1 = v1.uv.getY(), v_2 = v2.uv.getY(), v_3 = v3.uv.getY();
        double aV = ((v_2-v_1)*(y3-y1)-(v_3-v_1)*(y2-y1))*invD;
        double bV = ((x2-x1)*(v_3-v_1)-(x3-x1)*(v_2-v_1))*invD;
        double cV = v_1 - aV*x1 - bV*y1;

        // 4) 스팬 채우기
        for (int yi=0; yi<spanH; yi++) {
            int y = yi + minY;
            int xL = xmin[yi], xR = xmax[yi];
            if (xL < 0) xL = 0;
            if (xR >= width) xR = width-1;
            if (xL > xR) continue;

            int base = y * width;
            double zRow = bZ * y + cZ;
            double sRow = bS * y + cS;
            double lRow = bL * y + cL;
            double uRow = bU * y + cU;
            double vRow = bV * y + cV;

            for (int x = xL; x <= xR; x++) {
                // depth 보간 & 테스트
                double zv = aZ*x + zRow;
                int idx = base + x;
                if (zv >= depth[idx]) continue;

                // l 보간
                double lv = aL*x + lRow;
                
                double sv = aS*x + sRow;
                //int Li = (int)Math.max(0, Math.min(255, Math.round(lv)));

                // **UV 보간**
                double u = aU*x + uRow;
                double v = aV*x + vRow;
                u = Math.max(Math.min(0.999, u), 0);
                v = Math.max(Math.min(0.999, v), 0);
                // 예: 텍스처 샘플링할 때 u,v 사용
                // int texColor = sampleTexture(u, v);
                int clr = 0xFFFFFFFF;
                int nclr = 0xFFFFFFFF;
                if (tex != null) clr = tex[(int)(u * texWidth) + (int)(v * texHeight) * texWidth];
                if (normal != null) nclr = normal[(int)(u * normalWidth) + (int)(v * normalHeight) * normalWidth];
                int si = (int)(sv);
                double red =   (clr & 0x00ff0000) >> 16;
        		double green = (clr & 0x0000ff00) >> 8;
        		double blue =   clr & 0x000000ff;
        		
        		double redN =   (nclr & 0x00ff0000) >> 16;
        		double greenN = (nclr & 0x0000ff00) >> 8;
        		double blueN =   nclr & 0x000000ff;
        		
        		int alpha =     (clr & 0xff000000) >> 24;
        		int alphaP =     (clr & 0xff000000) >>> 24;
        		if (alphaP >= 250) {
        			depth[idx] = (float)zv;
        		}
        		
        		// normal mapping
        		
        		int ni = Math.min(255, (int)(redN + greenN + blueN) / 1);
                si = (int)((double)si * (double)(ni-20) / 235D + (ni-255) / 16D);
        		
        		//alpha = 255;
        		if (alphaP >= 100)
        			pixels[idx] = packARGB(alpha, Math.min(255, Math.max(0, si+(int)(red*lv))), Math.min(255, Math.max(0, si+(int)(green*lv))), Math.min(255, Math.max(0, si+(int)(blue*lv))));
            }
        }
    }


    private void plotEdge(int x0, int y0, int x1, int y1,
                          int minY, int[] xmin, int[] xmax) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int x = x0, y = y0;

        while (true) {
            int yi = y - minY;
            //  y 범위 내에서만 업데이트
            if (yi >= 0 && yi < xmin.length) {
                if (x < xmin[yi]) xmin[yi] = x;
                if (x > xmax[yi]) xmax[yi] = x;
            }
            if (x == x1 && y == y1) break;
            int e2 = err << 1;
            if (e2 > -dy) { err -= dy; x += sx; }
            if (e2 <  dx) { err += dx; y += sy; }
        }
    }*/


    public static int packARGB(int a,int r,int g,int b) {
        return ((a&0xFF)<<24)|((r&0xFF)<<16)|((g&0xFF)<<8)|(b&0xFF);
    }
    public int[] getRaster() {
    	return pixels;
    }
    public Graphics getGraphics() {
    	return buf.getGraphics();
    }
    public Graphics getFrontGraphics() {
    	return fbuf.getGraphics();
    }

    /** 한 번에 화면에 출력 */
    public void flush(Graphics g) {
        g.drawImage(buf, 0, 0, null);
    }
    public void frontFlush(Graphics g) {
    	g.drawImage(fbuf, 0, 0, null);
    }
}
