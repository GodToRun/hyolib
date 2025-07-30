package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hyolib.Constants;
import hyolib.ObjModel;
import hyolib.Vector3d;
import hyolib.Vertex;

public class SkelBoundModel implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 7;
	public String name, parentName;
	public List<Vertex> bindVertices, vertices = new ArrayList<Vertex>();
	public Vector3d min, max;
}
