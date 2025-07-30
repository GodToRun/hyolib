package hyolib;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import Jama.*;
import hyolib.anim.Animation;
import hyolib.anim.Joint;
import hyolib.anim.KeyRow;
import hyolib.anim.Keyframe;
import hyolib.anim.SkelConductor;
import hyolib.anim.SkelModelLoader;
import hyolib.anim.SkeletonModel;
import hyolib.element.Element;
import hyolib.element.JointElement;
import hyolib.element.ParticleElement;
import hyolib.hyoair.Integrator;
import hyolib.hyoair.VerletIntegrator;
import pw.common.RegistryValue;
import pw.kit.*;
public class MainAdvance3 extends GameManager {
	Vector3d moving;
	ArrayList<Element> eles;
	Engine engine;
	boolean load;
	Vector3d camPos, camRot;
	SkelConductor joints;
	Element sphere;
	Animation animation;
	public MainAdvance3() {
		super("Hyowon 3D", new Point(720, 480));
		splashScreen = false;
		setRegistryValue(RegistryValue.MSDELAY, "4");
		moving = new Vector3d(0, 0, 0);
		load = false;
		eles = new ArrayList<Element>();
		engine = new Engine();
		engine.init(Math.toRadians(60D));
		engine.lightDir(Vector3d.normalize(new Vector3d(1, -1, 0)));
		
		camPos = new Vector3d(0-5, -0.62+2, -6);
		camRot = new Vector3d(0, 0, 0);
		
		Point m = getMousePosition();
		if (m != null) {
			double xm = (double)m.x/720;
			double ym = (double)m.y/480;
			pmx = xm;
			pmy = ym;
		}
		joints = new SkelConductor(loadSkelAsElement("obj.obj"));
		
		sphere = loadAsElement("sphere.obj").get(0);
		sphere.setPos(new Vector3d(5, 3, 1));
		sphere.specularness = 850;
		sphere.specularable = 1D/15D;
		sphere.texture(Engine.loadTexture("res/bluestone.png"));
		sphere.normal(Engine.loadTexture("hyolib/normal.jpg"));
		animation = new Animation("");
		animation.add(new Keyframe(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1), 0, "leg2"));
		animation.add(new Keyframe(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1), 0, "leg2.1"));
		
		animation.add(new Keyframe(new Vector3d(0, 0, 0), new Vector3d(-1, 0, 0), new Vector3d(1, 1, 1), 20, "leg2"));
		animation.add(new Keyframe(new Vector3d(0, 0, 0), new Vector3d(Math.toRadians(65), 1, 0), new Vector3d(1, 1, 1), 20, "leg2.1"));
		
		ParticleElement pe = new ParticleElement();
		pe.setPos(new Vector3d(5, 0, 0));
		pe.emit(20);
		eles.add(pe);
		/*List<Element> woman = loadAsElement("obj.obj");
		woman.get(6).texture(engine.loadTexture("womanbody.png"), 1000, 1000);
		int[] th = engine.loadTexture("womanhair1.png");
		woman.get(5).texture(th, 1, 1);
		woman.get(4).texture(th, 1, 1);
		woman.get(3).texture(engine.loadTexture("womanuniformb.png"), 500, 500);
		woman.get(2).texture(0xFF444444);
		woman.get(1).texture(engine.loadTexture("womanheel.png"), 500, 500);
		woman.get(1).specularable = 1D/15D;
		woman.get(1).specularness = 850D;
		
		for (Element e : woman) {
			e.scale = new Vector3d(0.2D, 0.2D, 0.2D);
		}*/
		//woman.get(0).texture(engine.loadTexture("womanbody.png"), 500, 500);
		//loadAsElement("terrain.obj");
		load = true;
	}
	SkeletonModel loadSkelAsElement(String file) {
		SkeletonModel model = null;
		try {
			model = SkelModelLoader.loadObj(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	List<Element> loadAsElement(String file) {
		List<Element> ret = new ArrayList<>();
		try {
			List<ObjModel> models = RLoader.loadObj(file);
			for (ObjModel model : models) {
				Element e = new Element();
				e.vertices = model.vertices;
				e.indices = model.indices;
				eles.add(e);
				ret.add(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void main(String[] args) {
		new MainAdvance3();
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
	double t = 0D, at = 0D;
	double pmx, pmy;
	Element k;
	double pdt = 0D;
	@Override
	public void MainLoop() {
		super.MainLoop();
		double dt = getDelta();
		pdt += dt;
		if (pdt >= 1D) {
			pdt -= 1D;
			System.out.println(getFps());
		}
		if (dt < 0.0001D || dt > 0.9D) dt = 0.01D;
		/*if (model != null) {
			ColladaSkeletonAnimation.updateAnimationTransforms(model, at);
			at += getDelta();
			ColladaSkeletonAnimation.applySkinning(model);
			k.vertices = (ArrayList<Vertex>) model.vertices;
			k.indices = (ArrayList<Integer>) model.indices;
		}*/
		if (camPos==null || !load) return;
		Vector3d move = moving.c();
		Matrix mm = Engine.rotate(new Vector3d(0, 0, 0), move, new Vector3d(0, camRot.getY(), 0));
		move = Vector3d.from(mm);
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
		//joints.getModel().skeleton.joints.get(1).rotate(new Vector3d(2D * getDelta(), 0, 0));
		joints.getModel().skeleton.refresh();
		/*for (Joint j : joints.skeleton.joints) {
			for (int i = 0; i < j.bounds.vertices.size(); i++) {
				Vertex v = j.bounds.vertices.get(i);
				Vertex bindV = j.bounds.bindVertices.get(i);
				Matrix r = Engine.rotate(new Vector3d(0, -9, 0), bindV.v, new Vector3d(5*Math.sin(t), 0, 0));
				v.v = Vector3d.from(r);
			}
		}*/
		for (Element e : eles) {
			e.update(getDelta());
		}
		ArrayList<Keyframe> lerped = animation.get(t);
		t += getDelta();
		for (Keyframe k : lerped) {
			JointElement jeFind = null;
			for (JointElement je : joints.jointElements) {
				if (je.name.equals(k.getName())) {
					jeFind = je;
					break;
				}
			}
			jeFind.setRot(k.getRot());
		}
		
		engine.camPos(camPos);
		engine.camRot(camRot);
	}
	@Override
	public void DrawScreen(Graphics g) {
		if (!load || !engine.vsync(getDelta())) return;
		super.DrawScreen(g);
		engine.initDraw(g);
		joints.draw(g, engine);
		for (Element e : eles) {
			e.draw(g, engine);
		}
		engine.flush(g);
	}

}
