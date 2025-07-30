package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hyolib.Constants;

public class Skeleton implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 3;
	public List<Joint> joints;
	public Skeleton() {
		joints = new ArrayList<Joint>();
	}
	public void refresh() {
		for (Joint j : joints) {
			j.init();
		}
		for (Joint j : joints) {
			j.apply(j);
		}
	}
}
