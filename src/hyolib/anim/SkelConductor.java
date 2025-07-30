package hyolib.anim;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hyolib.Constants;
import hyolib.Engine;
import hyolib.ObjModel;
import hyolib.RLoader;
import hyolib.Vector3d;
import hyolib.element.Element;
import hyolib.element.JointElement;

public class SkelConductor implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 18;
	private SkeletonModel sModel;
	public Element presElement;
	public List<Element> elements = new ArrayList<Element>();
	public List<JointElement> jointElements = new ArrayList<JointElement>();
	public SkelConductor(SkeletonModel sModel) {
		this.sModel = sModel;
		for (ObjModel model : sModel.models) {
			Element e = new Element();
			e.vertices = model.vertices;
			e.indices = model.indices;
			e.name = model.name;
			if (model.mtl != null && model.mtl.tex != null && new File(model.mtl.tex).exists()) {
				e.texture(Engine.loadTexture(model.mtl.tex));
			}
			elements.add(e);
		}
		for (Joint j : sModel.skeleton.joints) {
			JointElement el = new JointElement(j);
			el.setPos(Vector3d.from(j.transform));
			el.setRot(j.rot);
			el.name = j.name;
			jointElements.add(el);
		}
	}
	public void draw(Graphics g, Engine engine) {
		for (Element e : elements) {
			e.draw(g, engine);
		}
	}
	public SkeletonModel getModel() {
		return sModel;
	}
}
