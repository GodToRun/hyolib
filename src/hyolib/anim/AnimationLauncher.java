package hyolib.anim;

import java.io.Serializable;

import hyolib.Constants;
import hyonema.CinemaObject;

public class AnimationLauncher implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 17;
	//public long applyUUID;
	public boolean loop = false;
	public Animation anim;
	public double time;
	public CinemaObject dependent;
	public AnimationLauncher(Animation anim, boolean loop) {
		this.anim = anim;
		this.loop = loop;
	}

}
