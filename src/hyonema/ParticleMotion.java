package hyonema;

import hyolib.Constants;
import hyolib.Vector3d;
import hyolib.anim.Keyframe;
import hyolib.element.ParticleElement;

public class ParticleMotion extends SceneKeyframe {
	private static final long serialVersionUID = Constants.BASE + 17;
	public CinemaObject ps;
	double pt;
	public int emit = 10;
	public ParticleMotion(double time) {
		super(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), time, "Particle");
	}
	@Override
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		ParticleMotion key = new ParticleMotion(a.time + (b.time-a.time)*t);
		key.setName(getName());
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
		
		key.ps = ps;
		key.pt = pt;
		key.emit = emit;
		return key;
	}
	@Override
	public void apply(double t) {
		super.apply(t);
		ParticleMotion before = (ParticleMotion)beforeLerp;
		double time = t-before.time;
		double ptime = before.pt-before.time;
		if ((time > 0 && ptime < 0)) {
			if (ps != null) {
				ParticleElement pe = (ParticleElement)ps.e;
				pe.clear();
				pe.emit(emit);
			}
		}
		
		before.pt = t;
		if (afterLerp != null)
			((ParticleMotion)afterLerp).pt = t;
	}
}
