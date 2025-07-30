package hyolib;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import Jama.*;
import pw.common.RegistryValue;
import pw.kit.*;
public class Main extends GameManager {
	Vector3d pos;
	Vector3d moving;
	Vector3d cam;
	public Main() {
		super("Hyowon 3D", new Point(720, 480));
		splashScreen = false;
		setRegistryValue(RegistryValue.MSDELAY, "5");
		moving = new Vector3d(0, 0, 0);
		pos = new Vector3d(-1, 1, 5);
		cam = new Vector3d(0, 0, 0);
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
		Matrix result = rotXZ.times(rotYZ.times(rotXY.times(pos)));
		result.set(0, 0, result.get(0, 0)+pivot.getX());
		result.set(1, 0, result.get(1, 0)+pivot.getY());
		result.set(2, 0, result.get(2, 0)+pivot.getZ());
		return result;
	}

	public static void main(String[] args) {
		new Main();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'w' ||
			e.getKeyChar() == 'a' ||
			e.getKeyChar() == 's' ||
			e.getKeyChar() == 'd')
			moving = new Vector3d(0, 0, 0);
		if (e.getKeyChar() == 'w') {
			moving = Vector3d.add(moving, new Vector3d(0, 0, -1));
		}
		if (e.getKeyChar() == 's') {
			moving = Vector3d.add(moving, new Vector3d(0, 0, 1));
		}
		if (e.getKeyChar() == 'a') {
			moving = Vector3d.add(moving, new Vector3d(-1, 0, 0));
		}
		if (e.getKeyChar() == 'd') {
			moving = Vector3d.add(moving, new Vector3d(1, 0, 0));
		}
		if (e.getKeyChar() == 'z') {
			cam = Vector3d.add(cam, new Vector3d(0, Math.toRadians(15f), 0));
		}
		if (e.getKeyChar() == 'x') {
			cam = Vector3d.add(cam, new Vector3d(0, Math.toRadians(-15f), 0));
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
	@Override
	public void MainLoop() {
		double dt = 1D/100D;
		if (pos==null || dt > 1f) return;
		if (dt > 0.0005f) {
			
		}
		pos = Vector3d.add(pos, Vector3d.scala(moving, dt));
	}
	@Override
	public void DrawScreen(Graphics g) {
		Matrix r = rotate(new Vector3d(-1, 1, 4.5), pos, cam);
		Vector3d p = new Vector3d(r.get(0, 0), r.get(1, 0), r.get(2, 0));
		double x = -p.getX();
		double y = -p.getY();
		double z = -p.getZ();
		double fov = Math.toRadians(90f);
		double d = 1 / Math.tan(fov/2);
		Vector2d pn = new Vector2d(d * x / -z, d * y / -z);
		Vector2d rp = new Vector2d(pn.getX() * 720, pn.getY() * 480);
		g.fillRect((int)rp.getX(), -(int)rp.getY(), 8, 8);
	}

}
