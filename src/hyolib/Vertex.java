package hyolib;

import java.io.Serializable;

public class Vertex implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 10;
	public Vector3d v, n;
	public Vector2d uv;
	public int col;
	public double diffuse, specular;
	public Vertex(Vector3d v, Vector2d uv) {
		this(v, uv, null, 0xFFFFFFFF);
	}
	public Vertex(Vector3d v, Vector2d uv, int col) {
		this(v, uv, null, col);
	}
	public Vertex(Vector3d v, Vector2d uv, Vector3d n, int col) {
		this.v = v;
		this.uv = uv;
		this.col = col;
		this.n = n;
	}
	public Vertex(Vector3d v) {
		this(v, null, null, 0xFFFFFFFF);
	}
}
