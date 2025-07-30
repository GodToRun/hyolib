package hyonema.scene;

import java.io.Serializable;

import hyolib.Vector3d;

public class SceneParticle implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 3;
	public Vector3d pos, rot, scale;
	public Vector3d velocity;
	public float gravityEffect;
	public float lifeTime;
	//public float 
}
