package hyonema;

import hyonema.scene.Constants;
import hyolib.Vector3d;
import hyolib.anim.Animation;
import hyolib.anim.AnimationLauncher;
import hyolib.anim.Keyframe;
import hyolib.anim.KeyframeFunction;
import hyolib.element.Element;

public class MetaMotion extends SceneKeyframe {
	private static final long serialVersionUID = Constants.SRBASE + 13;
	transient AnimationLauncher launcher;
	CinemaObject dependent;
	boolean loop = false;
	public MetaMotion(Vector3d p, Vector3d r, Vector3d s, double time, String name) {
		super(p, r, s, time, name);
	}
	@Override
	public void init(Hyonema hyonema) {
		super.init(hyonema);
		launcher = new AnimationLauncher(hyonema.alt.get(getName().substring(3)), loop);
		launcher.dependent = dependent;
	}
	@Override
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		MetaMotion key = new MetaMotion(Vector3d.lerp(a.getPos(), b.getPos(), t), Vector3d.lerp(a.getRot(), b.getRot(), t), Vector3d.lerp(a.getScale(), b.getScale(), t), a.time + (b.time-a.time)*t, a.getName());
		key.isLerped = true;
		// Assume that a's functions equals to b's functions
		if (a.functions != null) {
			for (int i = 0; i < a.functions.size(); i++) {
				key.functions.add(a.functions.get(i).lerp(b.functions.get(i), t));
			}
		}
		key.beforeLerp = a;
		key.afterLerp = b;
		key.hyonema = hyonema;
		key.launcher = launcher;
		key.dependent = dependent;
		key.loop = loop;
		return key;
	}
	@Override
	public boolean lerpTransform() {
		return false;
	}

	@Override
	public void apply(double t) {
		if (launcher == null || (launcher != null && launcher.anim == null) || beforeLerp == null) return;
		double length = launcher.anim.getLength();
		double time = (t-beforeLerp.time);
		if (time > length && !loop) return;
		time %= length;
		if (time < 0) return;
		hyonema.keyWnd.view(time, launcher.dependent, launcher.anim.get(time));
		/*if (!hyonema.keyWnd.playingAnimations.contains(launcher))
			hyonema.keyWnd.playingAnimations.add(launcher);*/
	}

}
