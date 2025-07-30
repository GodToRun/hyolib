package hyonema.scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import hyolib.Vector3d;
import hyolib.anim.Animation;
import hyolib.anim.SkelConductor;
import hyonema.CinemaObject;
public class Scene implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 0;
	//public ArrayList<SceneObject> sos = new ArrayList<>();
	public List<CinemaObject> cos = new ArrayList<>();
	public List<SkelConductor> skels = new ArrayList<SkelConductor>();
	public String projectName, projectPackage;
	public SceneEnv se;
	public Vector3d camPos, camRot;
	public long idTop;
	public Hashtable<String, Animation> alt = new Hashtable<>();
	public Scene(String projectName, String projectPackage) {
		se = new SceneEnv();
		this.projectName = projectName;
		this.projectPackage = projectPackage;
		camPos = new Vector3d(0, 0, 0);
		camRot = new Vector3d(0, 0, 0);
	}
}
