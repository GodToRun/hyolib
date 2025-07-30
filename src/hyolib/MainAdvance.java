package hyolib;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Jama.*;
import pw.common.RegistryValue;
import pw.kit.*;
public class MainAdvance extends GameManager {
	Vector3d moving;
	Vector3d camPos, camRot;
	ArrayList<Vertex> vs = new ArrayList<Vertex>();
	ArrayList<Integer> inds = new ArrayList<Integer>();
	public MainAdvance() {
		super("Hyowon 3D", new Point(720, 480));
		splashScreen = false;
		setRegistryValue(RegistryValue.MSDELAY, "5");
		moving = new Vector3d(0, 0, 0);
		vs.add(new Vertex(new Vector3d(1, 1, 0)));
		vs.add(new Vertex(new Vector3d(-1, 1, 0)));
		vs.add(new Vertex(new Vector3d(-1, -1, 0)));
		vs.add(new Vertex(new Vector3d(1, -1, 0)));
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
		new MainAdvance();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'w' ||
			e.getKeyChar() == 'a' ||
			e.getKeyChar() == 's' ||
			e.getKeyChar() == 'd')
			moving = new Vector3d(0, 0, 0);
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
			e.getKeyChar() == 'd')
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
			e.getKeyChar() == 'd')
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
	}
	@Override
	public void DrawScreen(Graphics g) {
		for (Vertex v : vs) {
			Matrix r = rotate(camPos, v.v, Vector3d.scala(camRot, -1));
			Vector3d p = Vector3d.subtract(new Vector3d(r.get(0, 0), r.get(1, 0), r.get(2, 0)), camPos);
			double x = p.getX();
			double y = p.getY();
			double z = p.getZ();
			double fov = Math.toRadians(90f);
			double d = 1 / Math.tan(fov/2);
			Vector2d pn = new Vector2d(d * x / z+0.5f, d * y / z-0.5f);
			Vector2d rp = new Vector2d(pn.getX() * 720, pn.getY() * 480);
			g.fillRect((int)rp.getX(), -(int)rp.getY(), 8, 8);
		}
	}

}
