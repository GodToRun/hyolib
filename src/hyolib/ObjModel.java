package hyolib;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjModel implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 5;
	public ArrayList<Vertex> vertices;
	public ArrayList<Integer> indices;
	public int type = 0;
	public String parent;
	public static final int DEFAULT = 0,
			BOUNDS = 1,
			WEIGHTS = 2;
	public String name;
	public ObjMtl mtl;
	public ObjModel(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
	}
	public ObjModel() {
		vertices = new ArrayList<Vertex>();
		indices = new ArrayList<Integer>();
	}
}
