package hyolib.hyoair;

import java.io.Serializable;

import hyolib.Constants;
import hyolib.Vector3d;

public abstract class Integrator implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 24;
	public Vector3d acceleration, velocity, position;
	public Integrator() {
		position = new Vector3d(0, 0, 0);
		velocity = new Vector3d(0, 0, 0);
		acceleration = new Vector3d(0, 0, 0);
	}
	public abstract void integrate(double dt);
}
