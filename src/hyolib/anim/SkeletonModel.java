package hyolib.anim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hyolib.Constants;
import hyolib.ObjModel;

public class SkeletonModel implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 4;
	public Skeleton skeleton;
	public List<ObjModel> models;
	public SkeletonModel(List<ObjModel> models, Skeleton skeleton) {
		this.models = models;
		this.skeleton = skeleton;
	}
}
