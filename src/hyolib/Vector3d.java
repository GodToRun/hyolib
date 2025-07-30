package hyolib;

import java.io.Serializable;

import Jama.Matrix;

public class Vector3d implements Serializable {
private double x, y, z;
private static final long serialVersionUID = Constants.BASE + 0;
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	// change matrix to column vector(Vector3d)
	public static Vector3d from(Matrix matrix) {
		return new Vector3d(matrix.get(0, 0), matrix.get(1, 0), matrix.get(2, 0));
	}
	public static double dist(Vector3d a, Vector3d b) {
		return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z));
	}
	public Matrix to() {
		Matrix ret = new Matrix(3, 1);
		ret.set(0, 0, getX());
		ret.set(1, 0, getY());
		ret.set(2, 0, getZ());
		return ret;
	}
	/// t -> 0~1 
	public static Vector3d lerp(Vector3d v1, Vector3d v2, double t) {
		double x = v1.getX() + (v2.getX()-v1.getX()) * t;
		double y = v1.getY() + (v2.getY()-v1.getY()) * t;
		double z = v1.getZ() + (v2.getZ()-v1.getZ()) * t;
		return new Vector3d(x, y, z);
	}
	public Vector3d c() {
		return new Vector3d(getX(), getY(), getZ());
	}
	
	public static Vector3d add(Vector3d vector1, Vector3d vector2) {
		return new Vector3d(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public static Vector3d subtract(Vector3d vector1, Vector3d vector2) {
		return new Vector3d(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ());
	}
	
	public static Vector3d multiply(Vector3d vector1, Vector3d vector2) {
		return new Vector3d(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY(), vector1.getZ() * vector2.getZ());
	}
	public static Vector3d scala(Vector3d vector1, double scala) {
		return new Vector3d(vector1.getX() * scala, vector1.getY() * scala, vector1.getZ() * scala);
	}
	
	public static Vector3d divide(Vector3d vector1, Vector3d vector2) {
		return new Vector3d(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY(), vector1.getZ() / vector2.getZ());
	}
	
	public static double length(Vector3d vector) {
		return (double) Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ());
	}
	
	public static Vector3d normalize(Vector3d vector) {
		double len = Vector3d.length(vector);
		return Vector3d.divide(vector, new Vector3d(len, len, len));
	}
	
	public static double dot(Vector3d vector1, Vector3d vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
	}
	public static double angle(Vector3d v1, Vector3d v2) {
		return Math.acos(dot(v1, v2) / length(v1) / length(v2));
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
		Vector3d other = (Vector3d) obj;
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
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}