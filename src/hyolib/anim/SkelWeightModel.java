package hyolib.anim;

import java.io.Serializable;

import hyolib.Constants;
import hyolib.ObjModel;
import hyolib.Vector3d;

public class SkelWeightModel implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 6;
	public String name;
	public Vector3d point;
}
