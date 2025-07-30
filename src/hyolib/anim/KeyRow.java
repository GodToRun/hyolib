package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import hyolib.Constants;
// Row for same name keyframes.
public class KeyRow implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 16;
	static final Comparator<Keyframe> c;
	static {
		c = new Comparator<Keyframe>() {
			
			@Override
			public int compare(Keyframe o1, Keyframe o2) {
				return (int)Math.signum(o1.time-o2.time);
			}
		};
	}
	public ArrayList<Keyframe> keyframes;
	public String name;
	public double length;
	public KeyRow(String name) {
		keyframes = new ArrayList<Keyframe>();
		this.name = name;
	}
	/*public KeyRow(ArrayList<Keyframe> keyframes) {
		this.keyframes = keyframes;
		if (keyframes.size() > 0)
			this.name = keyframes.get(0).getName();
		this.keyframes.sort(c);
	}
	public KeyRow(Keyframe[] keyframes) {
		for (Keyframe keyframe : keyframes)
			this.keyframes.add(keyframe);
		this.keyframes.sort(c);
	}*/
	public void add(Keyframe k) {
		keyframes.add(k);
		keyframes.sort(c);
		calcLength();
	}
	public void calcLength() {
		length = 0D;
		for (Keyframe k : keyframes)
			length = Math.max(length, k.time);
	}
	public void addFunction(KeyframeFunction f) {
		for (Keyframe k : keyframes) {
			k.addFunction(f);
		}
	}
	public void removeFunction(KeyframeFunction f) {
		for (Keyframe k : keyframes)
			k.functions.remove(f);
	}
	public void remove(Keyframe k) {
		keyframes.remove(k);
	}
	public void remove(Collection<Keyframe> keyframes) {
		this.keyframes.removeAll(keyframes);
	}
	public int keyframes() {
		return keyframes.size();
	}
	public Keyframe get(int index) {
		return keyframes.get(index);
	}
	// Get lerped keyframe.
	public Keyframe get(double time) {
		double epsilon = 0.00001D;
		Keyframe before = null, after = null;
		Keyframe lerped;
		int i;
		for (i = -1; i < keyframes.size()-1; i++) {
			after = keyframes.get(i+1);
			if (Math.abs(after.time-time) < epsilon) return after;
			if (i >= 0 && after.time > time) {
				before = keyframes.get(i);
				break;
			}
		}
		/*if (i+1 < keyframes.size())
			after = keyframes.get(i+1);*/
		if (before == null && keyframes.size() > 0 && keyframes.get(0).time < time) {
			before = keyframes.get(keyframes.size()-1);
		}
		if (before == null) return null;
		if (after == null || before == after) {
			return before.lerp(before, 0);
		}
		double t = (time-before.time)/(after.time-before.time);
		lerped = before.lerp(after, t);
		return lerped;
	}
}
