package hyolib;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Jama.*;
import pw.common.RegistryValue;
import pw.kit.*;
public class MainAdvance2 extends GameManager {
	Vector3d moving;
	Vector3d camPos, camRot;
	ArrayList<Vertex> vs = new ArrayList<Vertex>();
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Integer> inds = new ArrayList<Integer>();
	double[][] depth;
	public MainAdvance2() {
		super("Hyowon 3D", new Point(720, 480));
		depth = new double[720][480];
		splashScreen = false;
		setRegistryValue(RegistryValue.MSDELAY, "5");
		moving = new Vector3d(0, 0, 0);
		/*vs.add(new Vertex(new Vector3d(1, 1, 0)));
		vs.add(new Vertex(new Vector3d(-1, 1, 0)));
		vs.add(new Vertex(new Vector3d(-1, -1, 0)));
		vs.add(new Vertex(new Vector3d(1, -1, 0)));

		vs.add(new Vertex(new Vector3d(1, 1, 2)));
		vs.add(new Vertex(new Vector3d(-1, 1, 2)));
		vs.add(new Vertex(new Vector3d(-1, -1, 2)));
		vs.add(new Vertex(new Vector3d(1, -1, 2)));
		
		inds = new ArrayList<>(Arrays.asList(
			    // bottom face (z = 0)
			    0, 1, 2,    // triangle 0
			    0, 2, 3,    // triangle 1

			    // top face (z = 2)
			    4, 7, 6,    // triangle 2
			    4, 6, 5,    // triangle 3

			    // front face (y = +1)
			    0, 4, 5,    // triangle 4
			    0, 5, 1,    // triangle 5

			    // back face (y = -1)
			    3, 2, 6,    // triangle 6
			    3, 6, 7,    // triangle 7

			    // right face (x = +1)
			    0, 3, 7,    // triangle 8
			    0, 7, 4,    // triangle 9

			    // left face (x = -1)
			    1, 5, 6,    // triangle 10
			    1, 6, 2     // triangle 11
			));*/
		try {
			ObjModel model = RLoader.loadObj("terrain.obj").get(0);
			vs = model.vertices;
			inds = model.indices;
		} catch (IOException e) {
			e.printStackTrace();
		}
		camPos = new Vector3d(0, 0, -5);
		camRot = new Vector3d(0, 0, 0);
		Point m = getMousePosition();
		if (m != null) {
			double xm = (double)m.x/720;
			double ym = (double)m.y/480;
			pmx = xm;
			pmy = ym;
		}
	}
	Matrix rotate(Vector3d pivot, Vector3d pv, Vector3d theta) {
		Matrix pos = new Matrix(new double[][] {
			{pv.getX()-pivot.getX()},
			{pv.getY()-pivot.getY()},
			{pv.getZ()-pivot.getZ()}
		});
		double tx = theta.getX();
		double ty = theta.getY();
		double tz = theta.getZ();
		Matrix rotXY = new Matrix(new double[][] {
			{Math.cos(tz), -Math.sin(tz),0},
			{Math.sin(tz), Math.cos(tz), 0},
			{0,              0,          1}
		});
		Matrix rotXZ = new Matrix(new double[][] {
			{Math.cos(ty), 0, Math.sin(ty)},
			{0,            1,            0},
			{-Math.sin(ty),0, Math.cos(ty)}
		});
		Matrix rotYZ = new Matrix(new double[][] {
			{1,               0,         0},
			{0, Math.cos(tx),-Math.sin(tx)},
			{0, Math.sin(tx), Math.cos(tx)}
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

	public static void main(String[] args) {
		new MainAdvance2();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'w' ||
			e.getKeyChar() == 'a' ||
			e.getKeyChar() == 's' ||
			e.getKeyChar() == 'd' ||
			e.getKeyChar() == ' ' ||
				e.getKeyChar() == 'q')
			moving = new Vector3d(0, 0, 0);
		if (e.getKeyChar() == ' ') {
			moving = Vector3d.add(moving, new Vector3d(0, 1, 0));
		}
		if (e.getKeyChar() == 'q') {
			moving = Vector3d.add(moving, new Vector3d(0, -1, 0));
		}
		if (e.getKeyChar() == 'w') {
			moving = Vector3d.add(moving, new Vector3d(0, 0, 1));
		}
		if (e.getKeyChar() == 's') {
			moving = Vector3d.add(moving, new Vector3d(0, 0, -1));
		}
		if (e.getKeyChar() == 'a') {
			moving = Vector3d.add(moving, new Vector3d(-1, 0, 0));
		}
		if (e.getKeyChar() == 'd') {
			moving = Vector3d.add(moving, new Vector3d(1, 0, 0));
		}
		if (e.getKeyChar() == 'z') {
			camRot = Vector3d.add(camRot, new Vector3d(0, Math.toRadians(15f), 0));
		}
		if (e.getKeyChar() == 'x') {
			camRot = Vector3d.add(camRot, new Vector3d(0, Math.toRadians(-15f), 0));
		}
		if (e.getKeyChar() == 'w' ||
			e.getKeyChar() == 'a' ||
			e.getKeyChar() == 's' ||
			e.getKeyChar() == 'd' ||
			e.getKeyChar() == ' ' ||
			e.getKeyChar() == 'q')
			moving = Vector3d.normalize(moving);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == 'w' ||
				e.getKeyChar() == 'a' ||
				e.getKeyChar() == 's' ||
				e.getKeyChar() == 'd' ||
				e.getKeyChar() == ' ' ||
				e.getKeyChar() == 'q')
			moving = new Vector3d(0, 0, 0);
	}
	double t = 0f;
	double pmx, pmy;
	@Override
	public void MainLoop() {
		double dt = 1D/100D;
		if (camPos==null || dt > 1f) return;
		Vector3d move = moving.c();
		Matrix mm = rotate(new Vector3d(0, 0, 0), move, camRot);
		move = new Vector3d(mm.get(0, 0), mm.get(1, 0), mm.get(2, 0));
		//System.out.println(new Vector3d(Math.toDegrees(camRot.getX()), Math.toDegrees(camRot.getY()), Math.toDegrees(camRot.getZ())));
		camPos = Vector3d.add(camPos, Vector3d.scala(move, dt*5D));
		Point m = getMousePosition();
		if (m != null) {
			double xm = (double)m.x/720;
			double ym = (double)m.y/480;
			double dxm = (xm-pmx)*480D;
			double dym = (ym-pmy)*600D;
			camRot = Vector3d.add(camRot, Vector3d.scala(new Vector3d(dym, dxm, 0), dt));
			pmx = xm;
			pmy = ym;
		}
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		for (Vertex v : vs) {
			Matrix vm = rotate(new Vector3d(0, 0, 0), v.v, new Vector3d(0, t, 0));
			verts.add(new Vertex(new Vector3d(vm.get(0, 0), vm.get(1, 0), vm.get(2, 0))));
		}
		t += dt;
		vertices = verts;
	}
	Vector2d perspective(Vertex v) {
		Matrix r = rotate(camPos, v.v, Vector3d.scala(camRot, -1));
		Vector3d p = Vector3d.subtract(new Vector3d(r.get(0, 0), r.get(1, 0), r.get(2, 0)), camPos);
		double x = p.getX();
		double y = p.getY();
		double z = p.getZ();
		double fov = Math.toRadians(60f);
		double d = 1 / Math.tan(fov/2);
		Vector2d pn = new Vector2d(d * x / z+0.5f, d * y / z-0.5f);
		Vector2d rp = new Vector2d(pn.getX() * 720, pn.getY() * 720);
		return rp;
	}
	@Override
	public void DrawScreen(Graphics g) {
		int j = 0;
		for (int i = 0; i < inds.size(); i++) {
			Vertex v = vertices.get(inds.get(i));
			Vertex vi2 = null;
			Vertex vi3 = null;
			if (i < inds.size()-1) vi2 = vertices.get(inds.get(i+1));
			if (i < inds.size()-2) vi3 = vertices.get(inds.get(i+2));
			Vector2d rp = perspective(v);
			g.fillRect((int)rp.getX(), -(int)rp.getY(), 8, 8);
			
			if (vi2 != null) {
				Vector2d rp2 = perspective(vi2);
				g.setColor(new Color((i * 0x2174F) % 0xFFFFFF));
				if (vi3 != null) {
					Vector2d rp3 = perspective(vi3);
					if (j % 3 == 0) {
						int[] xs = new int[] {(int)rp.getX(), (int)rp2.getX(), (int)rp3.getX()};
						int[] ys = new int[] {-(int)rp.getY(), -(int)rp2.getY(), -(int)rp3.getY()};
						g.fillPolygon(xs, ys, 3);
					}
				}
				g.setColor(Color.red);
				g.drawLine((int)rp.getX(), -(int)rp.getY(), (int)rp2.getX(), -(int)rp2.getY());
			}
			j++;
		}
	}

}
