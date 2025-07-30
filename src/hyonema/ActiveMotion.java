package hyonema;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import hyolib.Vector3d;
import hyolib.anim.Keyframe;
import hyonema.scene.Constants;

public class ActiveMotion extends SceneKeyframe {
	private static final long serialVersionUID = Constants.SRBASE + 18;
	transient double pt = 0D;
	public boolean active = true;
	public CinemaObject to;
	public ActiveMotion(double time) {
		super(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1), time, "");
	}
	@Override
	public boolean lerpTransform() {
		return false;
	}
	@Override
	public void init(Hyonema hyonema) {
		super.init(hyonema);
	}
	@Override
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		ActiveMotion key = new ActiveMotion(a.time + (b.time-a.time)*t);
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
		
		key.active = active;
		key.to = to;
		key.pt = pt;
		return key;
	}
	@Override
	public void apply(double t) {
		super.apply(t);
		ActiveMotion before = (ActiveMotion)beforeLerp;
		double time = t-before.time;
		double ptime = before.pt-before.time;
		if ((time > 0 && ptime < 0) || t == 0) {
			before.to.setActive(active);
		}
		
		before.pt = t;
		if (afterLerp != null)
			((ActiveMotion)afterLerp).pt = t;
	}
}
