package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import hyolib.Constants;

public class Animation implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 9;
	private ArrayList<KeyRow> keyrows;
	/* Must be not duplicated */
	public String name;
	public double time;
	
	public Animation(String name) {
		this.name = name;
		keyrows = new ArrayList<KeyRow>();
	}
	public double getLength() {
		double length = 0D;
		for (int i = 0; i < keyrows(); i++) {
			length = Math.max(length, get(i).length);
		}
		return length;
	}
	public KeyRow add(Keyframe key) {
		KeyRow kr = get(key.getName());
		if (kr == null) {
			kr = addRow(key.getName());
		}
		kr.add(key);
		return kr;
	}
	public KeyRow addRow(String name) {
		KeyRow kr = new KeyRow(name);
		keyrows.add(kr);
		return kr;
	}
	public Keyframe get(KeyRow k, int i) {
		return k.get(i);
	}
	public KeyRow get(int i) {
		return keyrows.get(i);
	}
	public int keyrows() {
		return keyrows.size();
	}
	public int keyframes() {
		int k = 0;
		for (int i = 0; i < keyrows.size(); i++) {
			for (int j = 0; j < keyrows.get(i).keyframes(); j++) {
				k++;
			}
		}
		return k;
	}
	// get Column of keyframes by time.
	public ArrayList<Keyframe> get(double time) {
		ArrayList<Keyframe> column = new ArrayList<Keyframe>();
		for (KeyRow kr : keyrows) {
			Keyframe kf = kr.get(time);
			column.add(kf);
		}
		return column;
	}
	public void remove(Collection<Keyframe> keyframes) {
		for (int i = 0; i < keyrows.size(); i++) {
			keyrows.get(i).remove(keyframes);
		}
	}
	public void removeRows(Collection<?> keyframes) {
		for (int i = 0; i < keyrows.size(); i++) {
			keyrows.removeAll(keyframes);
		}
	}
	public KeyRow get(String name) {
		for (KeyRow key : keyrows) {
			if (key.name.equals(name)) return key;
		}
		return null;
	}
	// Create if any row isn't exists
	public KeyRow getCreate(String name) {
		KeyRow kr = get(name);
		if (kr != null) return kr;
		return addRow(name);
	}
}
