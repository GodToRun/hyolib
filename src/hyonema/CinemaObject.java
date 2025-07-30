package hyonema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import hyolib.Vector3d;
import hyolib.anim.Animation;
import hyolib.element.Element;
import hyonema.scene.Constants;

public class CinemaObject implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 10;
	public Element e;
	public String name;
	public boolean created;
	public Object[] tags = new Object[256];
	private boolean active;
	public CinemaObject parent;
	public ArrayList<CinemaObject> childs = new ArrayList<>();
	public long uuid;
	public CinemaObject(long id) {
		this.uuid = id;
	}
	public CinemaObject(long id, Element e, Vector3d pos, Vector3d rot, Vector3d scale) {
		this(id);
		this.e = e;
		setPosition(pos);
		setRotation(rot);
		setScale(scale);
	}
	public void setScale(Vector3d s) {
		e.setScale(s);
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		e.setActive(active);
		this.active = active;
	}
	public void setPosition(Vector3d v) {
		e.setPos(v);
	}
	public void setRotation(Vector3d r) {
		e.setRot(r);
	}
	public Vector3d getPosition() {
		return e.getPos();
	}
	public Vector3d getRotation() {
		return e.getRot();
	}
	public Vector3d getScale() {
		return e.getScale();
	}
}
