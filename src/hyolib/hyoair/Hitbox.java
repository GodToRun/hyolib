package hyolib.hyoair;

import hyolib.Vector3d;

public abstract class Hitbox {
	public Vector3d min = new Vector3d(0, 0, 0), max = new Vector3d(0, 0, 0);
	public abstract boolean intersects(Vector3d other);
}
