package hyolib.hyoair;

import hyolib.Constants;
import hyolib.Vector3d;

public class VerletIntegrator extends Integrator {
	private static final long serialVersionUID = Constants.BASE + 25;
	Vector3d force = new Vector3d(0, 0, 0);
	public transient double groundLevel = -100D;
	double mass = 1D;
	public double gravity = -9.81D;
	@Override
	public void integrate(double dt) {
		// s = s0 + v0t + 1/2at^2
		position = Vector3d.add(position, Vector3d.scala(velocity, dt));
		position = Vector3d.add(position, Vector3d.scala(acceleration, dt * dt));
		
		// v = v0t + at
		velocity = Vector3d.add(velocity, Vector3d.scala(acceleration, dt));
		
		// a = const.
		acceleration = new Vector3d(0, gravity, 0);
		acceleration = Vector3d.add(acceleration, Vector3d.scala(force, 1D/mass));
		force = new Vector3d(0, 0, 0);
		groundLevel = -100;
		if (position.getY() < groundLevel) {
			//force(new Vector3d(0, 250, 0));
			velocity = new Vector3d(0, 0, 0);
			position.setY(groundLevel);
		}
		System.out.println(position.getY());
	}
	public void force(Vector3d f) {
		force = Vector3d.add(force, f);
	}

}
