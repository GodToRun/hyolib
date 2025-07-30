package hyolib.anim;

import java.io.Serializable;

import hyolib.Constants;
import hyolib.element.Element;

public abstract class KeyframeFunction implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 21;
	public Keyframe keyframe;
	public abstract KeyframeFunction lerp(KeyframeFunction b, double t);
	public abstract void apply(double t, Element e);
	public abstract KeyframeFunction copy();
	
}
