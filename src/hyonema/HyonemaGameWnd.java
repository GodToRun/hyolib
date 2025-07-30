package hyonema;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.python.apache.commons.compress.harmony.unpack200.SegmentUtils;

import Jama.Matrix;
import hyolib.Engine;
import hyolib.TRS;
import hyolib.Vector3d;
import hyolib.anim.Keyframe;
import hyolib.anim.SkelConductor;
import hyolib.element.Element;
import pw.common.RegistryValue;
import pw.kit.GameManager;
enum ObjectMode {
	FREE,MOVE,ROTATE,SCALE
}
public class HyonemaGameWnd extends GameManager implements MouseMotionListener {
	Vector3d moving;
	Engine engine;
	boolean load = false;
	Hyonema hyonema;
	ObjectMode mode = ObjectMode.FREE;
	public boolean frontQueue;
	double v = 1D;
	protected HyonemaGameWnd(Hyonema hyonema) {
		super("Hyowon Cinema: Scene", new Point(800, 600));
		setLocation(400, 430);
		setRegistryValue(RegistryValue.MSDELAY, "4");
		addMouseMotionListener(this);
		this.hyonema = hyonema;
		splashScreen = false;
		engine = new Engine();
		engine.init(Math.toRadians(60D));
		engine.lightDir(Vector3d.normalize(new Vector3d(1, -1, 0)));
		load = true;
		moving = new Vector3d(0, 0, 0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		if (e.getKeyChar() == '1') {
			mode = ObjectMode.FREE;
		}
		if (e.getKeyChar() == '2') {
			mode = ObjectMode.MOVE;
		}
		if (e.getKeyChar() == '3') {
			mode = ObjectMode.ROTATE;
		}
		if (e.getKeyChar() == '4') {
			mode = ObjectMode.SCALE;
		}
		
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
		if (e.getKeyChar() == 'p') {
			hyonema.keyWnd.play();
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
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			if (v >= 9.9D)
				v = 1D;
			else
				v = 10D;
		}
		
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
	TRS axis;
	void drawAxis(Graphics g, Vector3d p, Vector3d r) {
		engine.identity(Engine.IDENTITY_ALL);
		Vector3d zero = new Vector3d(p.getX(), p.getY(), p.getZ());
		engine.line(g, zero, Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX()+1, p.getY(), p.getZ()), r)), 0xFFFF0000);
		engine.line(g, zero, Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX(), p.getY()+1, p.getZ()), r)), 0xFF00FF00);
		engine.line(g, zero, Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX(), p.getY(), p.getZ()+1), r)), 0xFF0000FF);
	}
	@Override
	public void DrawScreen(Graphics g) {
		super.DrawScreen(g);
		if (!load) return;
		engine.initDraw(g);
		for (CinemaObject e : hyonema.cos) {
			//e.e.specularness = 850;
			//e.e.specularable = 1D/15D;
			e.e.draw(g, engine);
		}
		if (mode != ObjectMode.FREE && hyonema.toolWnd != null && hyonema.toolWnd.selectedObject != null) {
			TRS trs = hyonema.toolWnd.selectedObject.e.localToWorld();
			axis = trs;
			drawAxis(engine.framebuffer(), trs.p, trs.r);
		}
		engine.flush(g);
		if (frontQueue) {
			engine.frontFlush(g);
		}
	}
	double pmx, pmy;
	double dpt = 0D;
	@Override
	public void MainLoop() {
		super.MainLoop();
		double dt = getDelta();
		dpt += dt;
		if (dpt >= 1D) {
			dpt -= 1D;
			System.out.println(getFps());
		}
		if (dt < 0.0001D || dt > 0.9D) dt = 0.01D;
		if (!load || hyonema.cam == null) return;
		Vector3d move = moving.c();
		Matrix mm = Engine.rotate(new Vector3d(0, 0, 0), move, new Vector3d(0, hyonema.cam.getRotation().getY(), 0));
		move = Vector3d.from(mm);
		hyonema.cam.setPosition(Vector3d.add(hyonema.cam.getPosition(), Vector3d.scala(move, dt*5D*v)));
		Point m = getMousePosition();
		double xm = 0, ym = 0;
		if (m != null) {
			xm = (double)m.x/720;
			ym = (double)m.y/480;	
		}
		if (m != null && Vector3d.length(moving) != 0) {
			double dxm = (xm-pmx)*480D;
			double dym = (ym-pmy)*600D;
			hyonema.cam.setRotation(Vector3d.add(hyonema.cam.getRotation(), Vector3d.scala(new Vector3d(dym, dxm, 0), dt)));
		}
		pmx = xm;
		pmy = ym;
		for (SkelConductor skel : hyonema.skels) {
			skel.getModel().skeleton.refresh();
		}
		for (CinemaObject co : hyonema.cos) {
			co.e.update(dt);
		}
		
		engine.camPos(hyonema.cam.getPosition());
		engine.camRot(hyonema.cam.getRotation());
	}
	public double pDistance(double x, double y, double x1, double y1, double x2, double y2) {

	      double A = x - x1; // position of point rel one end of line
	      double B = y - y1;
	      double C = x2 - x1; // vector along line
	      double D = y2 - y1;
	      double E = -D; // orthogonal vector
	      double F = C;

	      double dot = A * E + B * F;
	      double len_sq = E * E + F * F;

	      return Math.abs(dot) / Math.sqrt(len_sq);
    }
	int dragBx = 0, dragBy;
	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragBx == 0) dragBx = e.getX();
		if (dragBy == 0) dragBy = e.getY();
		if (axis != null) {
			Vector3d p = axis.p;
			Vector3d r = axis.r;
			Vector3d zero = new Vector3d(p.getX(), p.getY(), p.getZ());
			r = new Vector3d(0, 0, 0);
			Vector3d x = Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX()+1, p.getY(), p.getZ()), r));
			Vector3d y = Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX(), p.getY()+1, p.getZ()), r));
			Vector3d z = Vector3d.from(Engine.rotate(zero, new Vector3d(p.getX(), p.getY(), p.getZ()+1), r));
			Vector3d zp = engine.corePerspective(engine.camProject(engine.project(zero)), zero);
			Vector3d xl = Vector3d.subtract(engine.corePerspective(engine.camProject(engine.project(x)), x), zp);
			Vector3d yl = Vector3d.subtract(engine.corePerspective(engine.camProject(engine.project(y)), y), zp);
			Vector3d zl = Vector3d.subtract(engine.corePerspective(engine.camProject(engine.project(z)), z), zp);
			xl.setZ(0);yl.setZ(0);zl.setZ(0); // Z is used to Z-buffering, but we don't need in this situation. 
			Vector3d mp = new Vector3d(e.getX()-dragBx, e.getY()-dragBy, 0);
			mp.setY(mp.getY()*-1);
			
			double xDot = Vector3d.dot(Vector3d.normalize(xl), mp);
			double yDot = Vector3d.dot(Vector3d.normalize(yl), mp);
			double zDot = Vector3d.dot(Vector3d.normalize(zl), mp);
			double a = 80D;
			
			if (mode == ObjectMode.MOVE)
				hyonema.toolWnd.selectedObject.e.setPos(Vector3d.add(hyonema.toolWnd.selectedObject.e.getPos(), new Vector3d(xDot/a, yDot/a, zDot/a)));
			else if (mode == ObjectMode.ROTATE)
				hyonema.toolWnd.selectedObject.e.setRot(Vector3d.add(hyonema.toolWnd.selectedObject.e.getRot(), new Vector3d(/*Math.toRadians*/(xDot/a), /*Math.toRadians*/(yDot/a), /*Math.toRadians*/(zDot/a))));
			else if (mode == ObjectMode.SCALE)
				hyonema.toolWnd.selectedObject.e.setScale(Vector3d.add(hyonema.toolWnd.selectedObject.e.getScale(), new Vector3d(xDot/a, yDot/a, zDot/a)));
			//System.out.println(r.toString());
		}
		//System.out.println("A");
		for (Keyframe k : hyonema.current.get(hyonema.curTime)) {
			if (k==null) continue;
			k = k.beforeLerp;
			if (SubMotion.class.isAssignableFrom(k.getClass())) {
				SubMotion s = (SubMotion)k;
				//System.out.println("B");
				if (e.getX() >= s.subtitle.getX() &&
						e.getY() >= s.subtitle.getY() && e.getX() <= s.subtitle.getX()+s.subtitle.getWidth() &&
						 e.getY() <= s.subtitle.getY()+s.subtitle.getHeight()+30) {
					//System.out.println("C");
					Graphics2D g2d = (Graphics2D)engine.frontFramebuffer();
					g2d.setComposite(AlphaComposite.Clear);
					g2d.fillRect(0, 0, getWidth(), getHeight());
					g2d.setComposite(AlphaComposite.SrcOver);
					if (SwingUtilities.isLeftMouseButton(e)) {
						s.subtitle.setX(e.getX()-dragBx + s.subtitle.getX());
						s.subtitle.setY(e.getY()-dragBy + s.subtitle.getY());
					}
					else {
						s.subtitle.setWidth(e.getX()-dragBx + s.subtitle.getWidth());
						s.subtitle.setHeight(e.getY()-dragBy + s.subtitle.getHeight());
					}
					s.apply(hyonema.curTime);
					break;
				}
			}
		}
		dragBx = e.getX();
		dragBy = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		dragBx = e.getX();
		dragBy = e.getY();
	}
}
