package hyolib.hyoair;

import hyolib.Vector3d;

public class BoxHitbox extends Hitbox {

	@Override
	public boolean intersects(Vector3d other) {
		double x = other.getX();
		double y = other.getY();
		double z = other.getZ();
		return  x >= min.getX() && x <= max.getX() &&
				y >= min.getY() && y <= max.getY() &&
				z >= min.getZ() && z <= max.getZ();
	}

}
