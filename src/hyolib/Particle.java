package hyolib;

import java.io.Serializable;
import java.util.Random;

import hyolib.hyoair.VerletIntegrator;

public class Particle implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 26;
	public VerletIntegrator integrator;
	public Particle(Vector3d p) {
		integrator = new VerletIntegrator();
		Random r = new Random();
		double d = 0.5D;
		integrator.position = Vector3d.add(p, new Vector3d(r.nextDouble() * d-d/2, r.nextDouble() * d-d/2, r.nextDouble() * d-d/2));
	}
	public void update(double dt) {
		integrator.integrate(dt);
	}
	public Vector3d getPosition() {
		return integrator.position;
	}
}
