package hyolib.element;

import java.awt.Graphics;
import java.util.ArrayList;

import hyolib.Constants;
import hyolib.Engine;
import hyolib.Particle;
import hyolib.Vector2d;
import hyolib.Vector3d;
import hyolib.Vertex;

public class ParticleElement extends Element {
	private static final long serialVersionUID = Constants.BASE + 14;
	public ArrayList<Particle> particles;
	public ParticleElement() {
		super();
		vertices = new ArrayList<>();
		particles = new ArrayList<Particle>();
		vertices.add(new Vertex(new Vector3d(-0.5, -0.5, 0), new Vector2d(0, 0), new Vector3d(0, 0, 1), 0xFFFFFFFF));
		vertices.add(new Vertex(new Vector3d(0.5, -0.5, 0), new Vector2d(1, 0), new Vector3d(0, 0, 1), 0xFFFFFFFF));
		vertices.add(new Vertex(new Vector3d(0.5, 0.5, 0), new Vector2d(1, 1), new Vector3d(0, 0, 1), 0xFFFFFFFF));
		vertices.add(new Vertex(new Vector3d(-0.5, 0.5, 0), new Vector2d(0, 1), new Vector3d(0, 0, 1), 0xFFFFFFFF));
		
		indices.add(3);
		indices.add(2);
		indices.add(1);
		indices.add(1);
		indices.add(0);
		indices.add(3);
		
		indices.add(1);
		indices.add(2);
		indices.add(3);
		indices.add(3);
		indices.add(0);
		indices.add(1);
	}
	public void clear() {
		particles.clear();
	}
	public void emit(int e) {
		for (int i = 0; i < e; i++) {
			Particle p = new Particle(new Vector3d(0, 0.2D, 0));
			particles.add(p);
		}
		force(new Vector3d(0, -1D, 0), 180D);
	}
	public void force(Vector3d center, double force) {
		for (Particle particle : particles) {
			particle.integrator.force(Vector3d.scala(Vector3d.subtract(particle.getPosition(), center), force));
		}
	}
	@Override
	public void update(double dt) {
		super.update(dt);
		for (Particle p : particles) {
			p.update(dt);
		}
	}
	@Override
	public void draw(Graphics g, Engine e) {
		super.initDraw(e);
		super.localToWorldBind(e);
		e.linkVertices(vertices);
		e.linkIndices(indices);
		e.bindTexture(tex, texW, texH);
		Vector3d amount = new Vector3d(0, 0, 0);
		Particle tr = null;
		for (Particle p : particles) {
			//e.identity(Engine.IDENTITY_TRANSLATE);
			e.translate(-amount.getX(), -amount.getY(), -amount.getZ());
			amount = p.getPosition();
			e.translate(amount.getX(), amount.getY(), amount.getZ());
			e.draw(g);
			if (p.getPosition().getY() <= p.integrator.groundLevel) {
				tr = p;
			}
		}
		if (tr != null)
			particles.remove(tr);
	}
}
