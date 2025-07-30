package hyonema.scene;

import java.io.Serializable;
import java.util.ArrayList;

import hyolib.Vector3d;

public class SceneObject implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 1;
	public int code;
	public String name;
	public SOType type;
	public String modelPath, texPath, frag, vert;
	public int prmitive = -1; // modelPath == null, load primitive type.
	public Vector3d pos, rot, scale;
	public boolean mipmap, skel;
}
