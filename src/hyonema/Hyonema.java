package hyonema;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import hyolib.Vector3d;
import hyolib.anim.Animation;
import hyolib.anim.AnimationLauncher;
import hyolib.anim.Keyframe;
import hyolib.anim.KeyframeFunction;
import hyolib.anim.SkelConductor;
import hyolib.element.Element;
import hyonema.scene.Scene;
import hyonema.scene.SceneLoader;
import hyonema.scene.SceneObject;

public class Hyonema extends Thread {
	CinemaObject cam;
	HyonemaGameWnd game;
	ToolWnd toolWnd;
	KeyframeWnd keyWnd;
	Scene scene = null;
	public long idTop;
	//public List<Element> eles = new ArrayList<Element>();
	public List<CinemaObject> cos = new ArrayList<CinemaObject>();
	public List<SkelConductor> skels = new ArrayList<SkelConductor>();
	public Hashtable<String, Animation> alt = new Hashtable<>();
	public boolean recording = false;
	public Animation current;
	public AnimationLauncher currentLauncher;
	public double curTime = 0D;
	public List<Keyframe> selectedKeyframes = new ArrayList<>();
	public Hyonema() {
		
	}
	public long genId() {
		return idTop++;
	}
	private void initCam() {
		cam = new CinemaObject(-1L, new Element(), new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1));
		cam.name = cam.e.name = "Camera";
		toolWnd.addObject(cam, false);
		cos.add(cam);
	}
	@Override
	public void run() {
		game = new HyonemaGameWnd(this);
		toolWnd = new ToolWnd(this);
		toolWnd.start();
		keyWnd = new KeyframeWnd(this);
		keyWnd.start();
		scene = new Scene("Default Project", null/*"openxl.engine.script"*/);
		initCam();
	}
	public void reloadScene() {
		cam = null;
		toolWnd.clear();
		SceneLoader.load(this, scene);
		for (CinemaObject go : cos) {
			if (go.name != null && go.name.equals("Camera")) cam = go;
			toolWnd.selectedObject = go;
			toolWnd.addObject(go, false);
		}
		if (scene.alt == null) {
			scene.alt = new Hashtable<String, Animation>();
		}
		alt = scene.alt;
		idTop = scene.idTop;
		skels = scene.skels;
		toolWnd.f1.revalidate();
		
		for (String key : alt.keySet()) {
			Animation ani = alt.get(key);
			for (int i = 0; i < ani.keyrows(); i++) {
				for (int j = 0; j < ani.get(i).keyframes(); j++) {
					Keyframe k = ani.get(i).get(j);
					k.beforeLerp = k.afterLerp = k;
					if (k.functions == null) k.functions = new ArrayList<KeyframeFunction>();
					if (SceneKeyframe.class.isAssignableFrom(k.getClass())) {
						SceneKeyframe mm = (SceneKeyframe)k;
						if (mm.getName() != null)
							mm.init(this); // Temp
					}
				}
			}
		}
		
		if (cam == null) initCam();
	}
	public void exportScene() {
		scene.cos = cos;
		scene.idTop = idTop;
		scene.alt = alt;
		scene.skels = skels;
	}
	public CinemaObject cinemaFindByName(String name) {
		for (CinemaObject c : cos) {
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}
	public static void main(String[] args) {
		new Hyonema().start();
	}
}
