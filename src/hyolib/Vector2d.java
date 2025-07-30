package hyolib;

import java.io.Serializable;

public class Vector2d implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 1;
	private double x, y;
	
	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2d add(Vector2d vector1, Vector2d vector2) {
		return new Vector2d(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY());
	}
	
	public static Vector2d subtract(Vector2d vector1, Vector2d vector2) {
		return new Vector2d(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY());
	}
	
	public static Vector2d multiply(Vector2d vector1, Vector2d vector2) {
		return new Vector2d(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY());
	}
	
	public static Vector2d divide(Vector2d vector1, Vector2d vector2) {
		return new Vector2d(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY());
	}
	
	public static double length(Vector2d vector) {
		return (double) Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
	}
	
	public static Vector2d normalize(Vector2d vector) {
		double len = Vector2d.length(vector);
		return Vector2d.divide(vector, new Vector2d(len, len));
	}
	public Vector2d c() {
		return new Vector2d(getX(), getY());
	}
	
	public static double dot(Vector2d vector1, Vector2d vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY();
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
		Vector2d other = (Vector2d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
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
}