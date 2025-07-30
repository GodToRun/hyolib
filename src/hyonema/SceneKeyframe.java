package hyonema;

import hyolib.Vector3d;
import hyolib.anim.Keyframe;

public abstract class SceneKeyframe extends Keyframe {
	private static final long serialVersionUID = 16L;
	transient Hyonema hyonema;
	public SceneKeyframe(Vector3d pos, Vector3d rot, Vector3d scale, double time, String name) {
		super(pos, rot, scale, time, name);
	}
	public void init(Hyonema hyonema) {
		this.hyonema = hyonema;
	}

}
