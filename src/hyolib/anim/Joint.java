package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.Matrix;
import hyolib.Constants;
import hyolib.Engine;
import hyolib.Vector3d;
import hyolib.Vertex;

public class Joint implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 8;
	public Matrix transform, curTransform;
	public Vector3d rot, rRot;
	public Joint parent;
	public ArrayList<Joint> childs;
	public SkelBoundModel bounds;
	public SkelWeightModel weight;
	public String name, parentName;
	public double weightForce = 25D;
	private List<Joint> parS = null; 
	public Joint(Matrix transform) {
		if (transform == null) transform = new Matrix(3, 0);
		this.transform = transform;
		rot = new Vector3d(0, 0, 0);
		rRot = new Vector3d(0, 0, 0);
		curTransform = transform.copy();
		childs = new ArrayList<Joint>();
	}
	void getS(Joint j) {
		if (j.parent != null) {
			parS.add(j.parent);
			getS(j.parent);
		}
	}
	public void rotate(Vector3d amount) {
		rot = Vector3d.add(rot, amount);
		rRot = Vector3d.add(rRot, amount);
		rotRec(this, amount);
	}
	void rotRec(Joint j, Vector3d amount) {
		for (Joint child : j.childs) {
			Vector3d pos = Vector3d.from(j.curTransform);
			child.curTransform = Engine.rotate(pos, Vector3d.from(child.transform), j.rot);
			child.rRot = Vector3d.add(child.rRot, amount);
			rotRec(child, amount);
		}
	}
	public void apply(Joint first) {
		for (Joint j : parS) {
			rotV(Vector3d.from(j.curTransform), /*first.*/weight.point, j.rot);	
		}
		/*if (first.parent != null)
			apply(first.parent);*/
	}
	void rotV(Vector3d center, Vector3d wp, Vector3d rot) {
		for (int i = 0; i < bounds.vertices.size(); i++) {
			Vertex v = bounds.vertices.get(i);
			double dist = Vector3d.dist(wp, v.v);
			//Vector3d center = Vector3d.from(curTransform);
			Vector3d pivot = Vector3d.lerp(center, v.v, Math.min(1, Math.max(0, dist * (1D/weightForce))));
			v.v = Vector3d.from(Engine.rotate(pivot, v.v, rot));
		}
	}
	public void init() {
		if (parS == null) {
			parS = new ArrayList<Joint>();
			parS.add(this);
			getS(this);
			
			Collections.reverse(parS);
		}
		//rRot = rot.c();
		//curTransform = transform.copy();
		for (int i = 0; i < bounds.vertices.size(); i++) {
			Vector3d v = bounds.vertices.get(i).v;
			v.setX(bounds.bindVertices.get(i).v.getX());
			v.setY(bounds.bindVertices.get(i).v.getY());
			v.setZ(bounds.bindVertices.get(i).v.getZ());
		}
	}
}
