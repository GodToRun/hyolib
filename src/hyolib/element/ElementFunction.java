package hyolib.element;

import java.io.Serializable;

import hyolib.Constants;

public abstract class ElementFunction implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 13;
	protected Element e;
	public ElementFunction(Element e) {
		this.e = e;
	}
	public abstract void update(double dt);
}
