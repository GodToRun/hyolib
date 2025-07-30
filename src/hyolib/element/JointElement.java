package hyolib.element;

import hyolib.Constants;
import hyolib.Vector3d;
import hyolib.anim.Joint;

public class JointElement extends Element {
	private static final long serialVersionUID = Constants.BASE + 12;
	Joint j;
	public JointElement(Joint j) {
		super();
		this.j = j;
	}
	@Override
	public void setPos(Vector3d pos) {
		super.setPos(pos);
		j.transform = pos.to();
	}
	@Override
	public void setRot(Vector3d rot) {
		j.rotate(Vector3d.subtract(rot, getRot()));
		super.setRot(rot);
	}
}
