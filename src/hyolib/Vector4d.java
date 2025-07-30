package hyolib;

import java.io.Serializable;

public class Vector4d implements Serializable {
private double x, y, z, t;
private static final long serialVersionUID = Constants.BASE + 2;
	public Vector4d(double x, double y, double z, double t) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}
	
	public void set(double x, double y, double z, double t) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}
	
	public static Vector4d add(Vector4d vector1, Vector4d vector2) {
		return new Vector4d(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ(), vector1.getT() + vector2.getT());
	}
	
	public static Vector4d subtract(Vector4d vector1, Vector4d vector2) {
		return new Vector4d(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ(), vector1.getT() - vector2.getT());
	}
	
	public static Vector4d multiply(Vector4d vector1, Vector4d vector2) {
		return new Vector4d(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY(), vector1.getZ() * vector2.getZ(), vector1.getT() * vector2.getT());
	}
	
	public static Vector4d divide(Vector4d vector1, Vector4d vector2) {
		return new Vector4d(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY(), vector1.getZ() / vector2.getZ(), vector1.getT() / vector2.getT());
	}
	public static Vector4d scala(Vector4d vector1, double scala) {
		return new Vector4d(vector1.getX() * scala, vector1.getY() * scala, vector1.getZ() * scala, vector1.getT() * scala);
	}
	public static double length(Vector4d vector) {
		return (double) Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ() + vector.getT() * vector.getT());
	}
	
	public static Vector4d normalize(Vector4d vector) {
		double len = Vector4d.length(vector);
		return Vector4d.divide(vector, new Vector4d(len, len, len, len));
	}
	
	public static double dot(Vector4d vector1, Vector4d vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ() + vector1.getT() * vector2.getT();
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector4d other = (Vector4d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}
}