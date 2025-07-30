package hyolib;

import java.io.Serializable;

public class ObjMtl implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 20;

	public String name, tex;
	
	// unused
	public double Ns, Ka, Kd, Ks, Ke, Ni, d;
}
