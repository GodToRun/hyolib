package hyolib;

// Triangle clipping for Vertex class (Java 8 compatible)

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ClippedTriangle {
    public List<Vertex> vertices;
    public ClippedTriangle(List<Vertex> verts) {
        this.vertices = verts;
    }
}

public class TriangleClipper {

    public static List<ClippedTriangle> clipTriangle(
        double NEAR_Z, Vertex v0, Vertex v1, Vertex v2,
        Vector3d pv0, Vector3d pv1, Vector3d pv2
    ) {
        NEAR_Z += 0.1D;
        List<Vertex> inputVerts = Arrays.asList(v0, v1, v2);
        List<Vector3d> inputView = Arrays.asList(pv0, pv1, pv2);

        List<Vertex> outputVerts = new ArrayList<Vertex>();
        List<Vector3d> outputView = new ArrayList<Vector3d>();

        for (int i = 0; i < 3; i++) {
            Vertex a = inputVerts.get(i);
            Vertex b = inputVerts.get((i + 1) % 3);
            Vector3d pa = inputView.get(i);
            Vector3d pb = inputView.get((i + 1) % 3);

            boolean aIn = pa.getZ() >= NEAR_Z;
            boolean bIn = pb.getZ() >= NEAR_Z;

            if (aIn && bIn) {
                outputVerts.add(b);
                outputView.add(pb);
            } else if (aIn && !bIn) {
                InterpResult ir = intersectZ(NEAR_Z, a, b, pa, pb);
                outputVerts.add(ir.v);
                outputView.add(ir.pv);
            } else if (!aIn && bIn) {
                InterpResult ir = intersectZ(NEAR_Z, a, b, pa, pb);
                outputVerts.add(ir.v);
                outputView.add(ir.pv);
                outputVerts.add(b);
                outputView.add(pb);
            }
        }

        if (outputVerts.size() < 3) return new ArrayList<ClippedTriangle>();

        sortClockwiseByProjected(outputVerts, outputView);

        List<ClippedTriangle> result = new ArrayList<ClippedTriangle>();
        if (outputVerts.size() == 3) {
            result.add(new ClippedTriangle(outputVerts));
        } else if (outputVerts.size() == 4) {
            result.add(new ClippedTriangle(Arrays.asList(outputVerts.get(0), outputVerts.get(1), outputVerts.get(2))));
            result.add(new ClippedTriangle(Arrays.asList(outputVerts.get(0), outputVerts.get(2), outputVerts.get(3))));
        }
        return result;
    }

    private static List<Vertex> sortClockwiseByProjected(List<Vertex> verts, List<Vector3d> viewSpace) {
        if (verts.size() != 3) return verts;

        double cx = 0, cy = 0;
        for (int i = 0; i < 3; i++) {
            Vector3d p = viewSpace.get(i);
            cx += p.getX() / p.getZ();
            cy += p.getY() / p.getZ();
        }
        cx /= 3;
        cy /= 3;

        final double centerX = cx;
        final double centerY = cy;

        List<Integer> indices = Arrays.asList(0, 1, 2);
        Collections.sort(indices, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                Vector3d p1 = viewSpace.get(i1);
                Vector3d p2 = viewSpace.get(i2);

                double ax = p1.getX() / p1.getZ() - centerX;
                double ay = p1.getY() / p1.getZ() - centerY;
                double bx = p2.getX() / p2.getZ() - centerX;
                double by = p2.getY() / p2.getZ() - centerY;

                double angleA = Math.atan2(ay, ax);
                double angleB = Math.atan2(by, bx);
                return Double.compare(angleA, angleB);
            }
        });

        List<Vertex> sortedVerts = new ArrayList<Vertex>();
        for (int i : indices) {
            sortedVerts.add(verts.get(i));
        }

        for (int i = 0; i < 3; i++) {
            verts.set(i, sortedVerts.get(i));
        }

        return verts;
    }

    private static class InterpResult {
        public Vertex v;
        public Vector3d pv;
        public InterpResult(Vertex v, Vector3d pv) {
            this.v = v;
            this.pv = pv;
        }
    }

    private static InterpResult intersectZ(double NEAR_Z, Vertex a, Vertex b, Vector3d pa, Vector3d pb) {
        double t = (NEAR_Z - pa.getZ()) / (pb.getZ() - pa.getZ());

        double ix = pa.getX() + t * (pb.getX() - pa.getX());
        double iy = pa.getY() + t * (pb.getY() - pa.getY());
        Vector3d ipv = new Vector3d(ix, iy, NEAR_Z);

        Vector3d va = a.v;
        Vector3d vb = b.v;
        double wx = va.getX() + t * (vb.getX() - va.getX());
        double wy = va.getY() + t * (vb.getY() - va.getY());
        double wz = va.getZ() + t * (vb.getZ() - va.getZ());
        Vector3d ipos = new Vector3d(wx, wy, wz);

        Vector2d iuv = null;
        if (a.uv != null && b.uv != null) {
            double za = pa.getZ();
            double zb = pb.getZ();

            double ua = a.uv.getX() / za;
            double ub = b.uv.getX() / zb;
            double vaUv = a.uv.getY() / za;
            double vbUv = b.uv.getY() / zb;

            double invZ = 1.0 / (1.0 / za + t * (1.0 / zb - 1.0 / za));
            double u = (ua + t * (ub - ua)) * invZ;
            double v = (vaUv + t * (vbUv - vaUv)) * invZ;

            iuv = new Vector2d(u, v);
        }

        Vector3d inorm = null;
        if (a.n != null && b.n != null) {
            double nx = a.n.getX() + t * (b.n.getX() - a.n.getX());
            double ny = a.n.getY() + t * (b.n.getY() - a.n.getY());
            double nz = a.n.getZ() + t * (b.n.getZ() - a.n.getZ());
            inorm = new Vector3d(nx, ny, nz);
        }

        int icol = blendColor(a.col, b.col, t);

        Vertex iv = new Vertex(ipos, iuv, inorm, icol);
        iv.diffuse = a.diffuse + t * (b.diffuse - a.diffuse);
        iv.specular = a.specular + t * (b.specular - a.specular);

        return new InterpResult(iv, ipv);
    }

    private static int blendColor(int c1, int c2, double t) {
        int a1 = (c1 >> 24) & 0xFF;
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8)  & 0xFF;
        int b1 = (c1)       & 0xFF;

        int a2 = (c2 >> 24) & 0xFF;
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8)  & 0xFF;
        int b2 = (c2)       & 0xFF;

        int a = (int)(a1 + t * (a2 - a1));
        int r = (int)(r1 + t * (r2 - r1));
        int g = (int)(g1 + t * (g2 - g1));
        int b = (int)(b1 + t * (b2 - b1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
