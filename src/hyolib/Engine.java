package hyolib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Jama.Matrix;
import pw.common.RegistryValue;

public class Engine {
	Vector3d camPos, camRot;
	ArrayList<Vertex> vertices;
	ArrayList<Integer> inds;
	FastGraphics fg = null;
	Vector3d lightDir;
	Vector3d translate;
	Vector3d rotate;
	Vector3d scale;
	double fov, specRadius, specular;
	public static final int IDENTITY_ROTATE = 0B1;
	public static final int IDENTITY_TRANSLATE = 0B10;
	public static final int IDENTITY_VERTICES = 0B100;
	public static final int IDENTITY_INDICES = 0B1000;
	public static final int IDENTITY_SCALE = 0B10000;
	public static final int IDENTITY_SPECRADIUS = 0B100000;
	public static final int IDENTITY_SPECULAR = 0B1000000;
	//public static final int IDENTITY_NORMAL = 0B10000000;
	
	public static final int IDENTITY_ALL = 0xFFFFFFFF;
	double d, hfov;
	private Matrix camMatrixXY, camMatrixXZ, camMatrixYZ,
	rotateXY, rotateXZ, rotateYZ;
	
	public void init(double fov) {
		this.fov = fov;
		fg = new FastGraphics();
		camPos = new Vector3d(0, 0, 0);
		camRot = new Vector3d(0, 0, 0);
		identity(IDENTITY_ALL);
		hfov = 2*Math.atan(Math.tan(fov/2)*720/480);
		d = 1 / Math.tan(hfov/2);
	}
	private void refreshRotateMatrix() {
		Vector3d theta = rotate;
		double tx = theta.getX();
		double ty = theta.getY();
		double tz = theta.getZ();
		rotateXY = new Matrix(new double[][] {
			{Math.cos(tz), -Math.sin(tz),0},
			{Math.sin(tz), Math.cos(tz), 0},
			{0,              0,          1}
		});
		rotateXZ = new Matrix(new double[][] {
			{Math.cos(ty), 0, Math.sin(ty)},
			{0,            1,            0},
			{-Math.sin(ty),0, Math.cos(ty)}
		});
		rotateYZ = new Matrix(new double[][] {
			{1,               0,         0},
			{0, Math.cos(tx),-Math.sin(tx)},
			{0, Math.sin(tx), Math.cos(tx)}
		});
	}
	private void refreshCamMatrix() {
		Vector3d theta = Vector3d.scala(camRot, -1);
		double tx = theta.getX();
		double ty = theta.getY();
		double tz = theta.getZ();
		camMatrixXY = new Matrix(new double[][] {
			{Math.cos(tz), -Math.sin(tz),0},
			{Math.sin(tz), Math.cos(tz), 0},
			{0,              0,          1}
		});
		camMatrixXZ = new Matrix(new double[][] {
			{Math.cos(ty), 0, Math.sin(ty)},
			{0,            1,            0},
			{-Math.sin(ty),0, Math.cos(ty)}
		});
		camMatrixYZ = new Matrix(new double[][] {
			{1,               0,         0},
			{0, Math.cos(tx),-Math.sin(tx)},
			{0, Math.sin(tx), Math.cos(tx)}
		});
	}
	public void camPos(Vector3d camPos) {
		this.camPos = camPos;
	}
	public void camRot(Vector3d camRot) {
		this.camRot = camRot;
		refreshCamMatrix();
	}
	public void lightDir(Vector3d v) {
		lightDir = v;
	}
	public void identity(int mode) {
		if ((mode & IDENTITY_ROTATE) != 0) {
			rotate = new Vector3d(0, 0, 0);
		}
		if ((mode & IDENTITY_TRANSLATE) != 0) {
			translate = new Vector3d(0, 0, 0);
		}
		if ((mode & IDENTITY_SCALE) != 0) {
			scale = new Vector3d(1, 1, 1);
		}
		if ((mode & IDENTITY_VERTICES) != 0) {
			vertices = new ArrayList<Vertex>();
		}
		if ((mode & IDENTITY_INDICES) != 0) {
			inds = new ArrayList<Integer>();
		}
		if ((mode & IDENTITY_SPECRADIUS) != 0) {
			specRadius(1D/10D);
		}
		if ((mode & IDENTITY_SPECULAR) != 0) {
			specular(200);
		}
	}
	public void linkVertices(ArrayList<Vertex> tb) {
		vertices = tb;
	}
	public void linkIndices(ArrayList<Integer> tb) {
		inds = tb;
	}
	public void translate(double x, double y, double z) {
		translate = Vector3d.add(translate, new Vector3d(x, y, z));
	}
	public void rotate(double x, double y, double z) {
		rotate = Vector3d.add(rotate, new Vector3d(x, y, z));
		refreshRotateMatrix();
	}
	public void scale(double x, double y, double z) {
		scale = Vector3d.multiply(scale, new Vector3d(x, y, z));
	}
	public void specRadius(double specRadius) {
		this.specRadius = specRadius;
	}
	public void specular(double specular) {
		this.specular = specular;
	}
	public Engine() {
		camPos = new Vector3d(0, 0, 0);
		camRot = new Vector3d(0, 0, 0);
	}
	public void bindTexture(int[] tex, int w, int h) {
		fg.setTex(tex, w, h);
	}
	public void bindNormal(int[] tex, int w, int h) {
		fg.setNormal(tex, w, h);
	}
	public Graphics framebuffer() {
		return fg.getGraphics();
	}
	public Graphics frontFramebuffer() {
		return fg.getFrontGraphics();
	}
	public static Texture loadTexture(String file) {
		try {
			BufferedImage img = ImageIO.read(new File(file));
			int w = img.getWidth();
			int h = img.getHeight();
			int[] col = new int[w * h];
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int clr = img.getRGB(x, h-y-1);
					col[x + y * w] = clr;
				}
			}
			return new Texture(col, w, h);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Texture(new int[] {}, 0, 0);
	}
	public static Matrix rotate(Vector3d pivot, Vector3d pv, Matrix rotXY, Matrix rotXZ, Matrix rotYZ) {
		Matrix pos = new Matrix(new double[][] {
			{pv.getX()-pivot.getX()},
			{pv.getY()-pivot.getY()},
			{pv.getZ()-pivot.getZ()}
		});
		Matrix result = rotYZ.times(rotXZ.times(rotXY.times(pos)));
		/*for (int r = 0; r < 2; r++) {
			for (int c = 0; c < 1; c++) {
				System.out.print(result.get(r, c)+ " ");
			}
			System.out.println();
		}*/
		result.set(0, 0, result.get(0, 0)+pivot.getX());
		result.set(1, 0, result.get(1, 0)+pivot.getY());
		result.set(2, 0, result.get(2, 0)+pivot.getZ());
		return result;
	}
	public static Matrix rotate(Vector3d pivot, Vector3d pv, Vector3d theta) {
		double tx = theta.getX();
		double ty = theta.getY();
		double tz = theta.getZ();
		double sinTz = Math.sin(tz);
		double cosTz = Math.cos(tz);
		
		double sinTx = Math.sin(tx);
		double cosTx = Math.cos(tx);
		
		double sinTy = Math.sin(ty);
		double cosTy = Math.cos(ty);
		Matrix rotXY = new Matrix(new double[][] {
			{cosTz, -sinTz,0},
			{sinTz, cosTz, 0},
			{0,              0,          1}
		});
		Matrix rotXZ = new Matrix(new double[][] {
			{cosTy, 0, sinTy},
			{0,            1,            0},
			{-sinTy,0, cosTy}
		});
		Matrix rotYZ = new Matrix(new double[][] {
			{1,               0,         0},
			{0, cosTx,-sinTx},
			{0, sinTx, cosTx}
		});
		
		return rotate(pivot, pv, rotXY, rotXZ, rotYZ);
	}
	public Vector3d project(Vector3d v) {
		Vector3d rot = Vector3d.multiply(v, scale);
		if (rotate.getX() != 0 || rotate.getY() != 0 || rotate.getZ() != 0) {
			Matrix rv = rotate(new Vector3d(0, 0, 0), rot, rotateXY, rotateXZ, rotateYZ);
			rot = Vector3d.from(rv);
		}
		rot = Vector3d.add(translate, rot);
		return rot;
	}
	public Vector3d camProject(Vector3d project) {
		Vector3d rot = project;
		Matrix r = rotate(camPos, rot, camMatrixXY, camMatrixXZ, camMatrixYZ);
		Vector3d p = Vector3d.subtract(new Vector3d(r.get(0, 0), r.get(1, 0), r.get(2, 0)), camPos);
		return p;
	}
	public Vector3d corePerspective(Vector3d camProject, Vector3d v) {
		//Vector3d p = camProject(project);
		Vector3d p = camProject;
		double x = p.getX();
		double y = p.getY();
		double z = p.getZ();
		//System.out.println(z);
		Vector2d pn = new Vector2d(d * x / z+0.5f, d * y / z-0.5f);
		Vector3d rp = new Vector3d(pn.getX() * 720, pn.getY() * 720, z);
		return rp;
	}
	// rot = project(v)
	Vector3d perspective(Vector3d p, Vector3d rot, Vertex v) {
		Vector3d rp = corePerspective(rot, v.v);
		
		Vector3d normal = v.n;
		if (rotate.getX() != 0 || rotate.getY() != 0 || rotate.getZ() != 0) {
			Matrix rn = rotate(new Vector3d(0, 0, 0), v.n, rotateXY, rotateXZ, rotateYZ);
			normal = Vector3d.from(rn);	
		}
		// Lighting
		Vector3d l = Vector3d.scala(lightDir, -1);
		Vector3d nn = Vector3d.normalize(normal);
		double diffuse = Math.max(0, Vector3d.dot(nn, l));
		v.diffuse = Math.min(1, diffuse + 0.25D);
		if (diffuse > 0 && specular > 1) {
			Vector3d reflect = Vector3d.subtract(Vector3d.scala(nn, 2 * diffuse), l);
			v.specular = specular*Math.pow(Vector3d.dot(Vector3d.normalize(Vector3d.subtract(camPos, p)), reflect), 1D/specRadius);
			v.specular *= diffuse;
		} else v.specular = 0;
		return rp;
	}
	public static int packARGB(int a, int r, int g, int b) {
	    return ((a & 0xFF) << 24)
	         | ((r & 0xFF) << 16)
	         | ((g & 0xFF) <<  8)
	         |  (b & 0xFF);
	}
	double sec = 0.0D;
	int draw;
	public boolean vsync(double dt) {
		draw++;
		sec += dt;
		if (sec >= 1D) draw = 0;
		return draw < 120;
	}
	public void initDraw(Graphics g) {
		fg.init(g, 720, 480);
	}
	public void line(Graphics g, Vector3d p1, Vector3d p2, int color) {
		Vector3d s1 = corePerspective(camProject(project(p1)), p1);
		Vector3d s2 = corePerspective(camProject(project(p2)), p2);
		g.setColor(new Color(color));
		g.drawLine((int)s1.getX(), -(int)s1.getY(), (int)s2.getX(), -(int)s2.getY());
	}
	void drawTriangle(Vertex v, Vertex vi2, Vertex vi3, boolean clip) {
		Vector3d pv = project(v.v);
		Vector3d pv2 = project(vi2.v);
		Vector3d pv3 = project(vi3.v);
		Vector3d cpv = camProject(pv);
		Vector3d cpv2 = camProject(pv2);
		Vector3d cpv3 = camProject(pv3);
		
		Vector3d rp = perspective(pv, cpv, v);
		
		if (vi2 != null) {
			Vector3d rp2 = perspective(pv2, cpv2, vi2);
			if (vi3 != null/* && j % 3 == 0*/) {
				Vector3d rp3 = perspective(pv3, cpv3, vi3);
				double near = 0.1D; 
				if (rp.getZ() < near || rp2.getZ() < near || rp3.getZ() < near) {
					if (clip) {
						for (ClippedTriangle t : TriangleClipper.clipTriangle(near, v, vi2, vi3, cpv, cpv2, cpv3)) {
							drawTriangle(t.vertices.get(0), t.vertices.get(1), t.vertices.get(2), false);
						}
					}
					return;
				}
				
				/*double nearZ = 0.1D;
				Vector3d p0 = cpv;
				Vector3d p1 = cpv2;
				Vector3d p2 = cpv3;
				if (p0.getZ() < nearZ || p1.getZ() < nearZ || p2.getZ() < nearZ)
				    return; // 투영 불가, 찢어짐 방지*/
				
				fg.fillTriangle((int)rp.getX(), -(int)rp.getY(), rp.getZ(), (int)rp2.getX(), -(int)rp2.getY(), rp2.getZ(), (int)rp3.getX(), -(int)rp3.getY(), rp3.getZ(), 0xFFFFFFFF, v, vi2, vi3, 0);
				//System.out.println((int)rp.getX() + ", " + -(int)rp.getY() + ", " + (int)rp2.getX() + ", " + -(int)rp2.getY() + ", " + (int)rp3.getX() + ", " + -(int)rp3.getY());
				
				/*int[] xs = new int[] {(int)rp.getX(), (int)rp2.getX(), (int)rp3.getX()};
				int[] ys = new int[] {-(int)rp.getY(), -(int)rp2.getY(), -(int)rp3.getY()};
				g.fillPolygon(xs, ys, 3);*/
			}
			//g.setColor(Color.red);
		}
	}
	public void draw(Graphics g) {
		int j = 0;
		for (int i = 0; i < inds.size(); i += 3) {
			Vertex v = vertices.get(inds.get(i));
			Vertex vi2 = null;
			Vertex vi3 = null;
			/*if (i < inds.size()-1)*/ vi2 = vertices.get(inds.get(i+1));
			/*if (i < inds.size()-2)*/ vi3 = vertices.get(inds.get(i+2));
			drawTriangle(v, vi2, vi3, true);
		}
	}
	public void frontFlush(Graphics g) {
		fg.frontFlush(g);
	}
	public void flush(Graphics g) {
		fg.flush(g);
	}
}
