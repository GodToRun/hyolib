package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;

import hyolib.Constants;
import hyolib.Vector3d;
import hyolib.element.Element;

public class Keyframe implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 15;
	private Vector3d pos, rot, scale;
	private String name, tag;
	public boolean isLerped;
	public ArrayList<KeyframeFunction> functions = new ArrayList<>();
	public transient Keyframe beforeLerp;
	public transient Keyframe afterLerp;
	public boolean lerpTransform() {
		return true;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vector3d getPos() {
		return pos;
	}
	public void setPos(Vector3d pos) {
		this.pos = pos;
	}
	public Vector3d getRot() {
		return rot;
	}
	public void setRot(Vector3d rot) {
		this.rot = rot;
	}
	public Vector3d getScale() {
		return scale;
	}
	public void setScale(Vector3d scale) {
		this.scale = scale;
	}
	public double time;
	public Keyframe(Vector3d pos, Vector3d rot, Vector3d scale, double time, String name) {
		this.pos = pos;
		this.rot = rot;
		this.scale = scale;
		this.time = time;
		this.name = name;
		beforeLerp = afterLerp = this;
	}
	public void apply(double t) {
		
	}
	// t = 0~1
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		Keyframe key = new Keyframe(Vector3d.lerp(a.getPos(), b.getPos(), t), Vector3d.lerp(a.getRot(), b.getRot(), t), Vector3d.lerp(a.getScale(), b.getScale(), t), a.time + (b.time-a.time)*t, a.name);
		key.isLerped = true;
		// Assume that a's functions equals to b's functions
		if (a.functions == null) return key;
		for (int i = 0; i < a.functions.size(); i++) {
			key.functions.add(a.functions.get(i).lerp(b.functions.get(i), t));
		}
		key.beforeLerp = a;
		key.afterLerp = b;
		return key; 
	}
	public void addFunction(KeyframeFunction f) {
		KeyframeFunction kfc = f.copy();
		kfc.keyframe = this;
		functions.add(kfc);
	}
}
