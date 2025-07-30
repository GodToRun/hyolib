package hyolib.element;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import hyolib.Constants;
import hyolib.Engine;
import hyolib.TRS;
import hyolib.Texture;
import hyolib.Vector3d;
import hyolib.Vertex;
import hyolib.anim.Joint;

public class Element implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 11;
	private Vector3d pos, rot, scale;
	public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	public ArrayList<Integer> indices = new ArrayList<Integer>();
	public int[] tex, normalTex;
	public int texW, texH, norW, norH;
	public double specularable, specularness;
	private Element parent;
	private ArrayList<Element> childs = new ArrayList<Element>();
	public ArrayList<Element> parS = new ArrayList<Element>();
	public ArrayList<ElementFunction> functions = new ArrayList<ElementFunction>();
	public String name;
	private boolean active = true;
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Element() {
		pos = new Vector3d(0, 0, 0);
		rot = new Vector3d(0, 0, 0);
		scale = new Vector3d(1, 1, 1);
		specularness = 250;
		specularable = 1D/45D;
	}
	public String getPath() {
		String path = "";
		for (Element e : parS) {
			path += e.name + "/";
		}
		path += name;
		return path;
	}
	public void update(double dt) {
		for (ElementFunction f : functions) {
			f.update(dt);
		}
	}
	public Vector3d getPos() {
		return pos;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}
	void refreshParentsRec(Element c) {
		for (Element child : c.childs) {
			child.refreshParents();
			refreshParentsRec(child);
		}
	}
	public int childs() {
		return childs.size();
	}
	public boolean removeChild(Element e) {
		return childs.remove(e);
	}
	public Element getChild(int i) {
		return childs.get(i);
	}
	public void addChild(Element e) {
		e.parent = this;
		childs.add(e);
		refreshParentsRec(this);
	}

	public Vector3d getRot() {
		return rot;
	}

	public void setRot(Vector3d rot) {
		this.rot = rot;
	}

	public Vector3d getScale() {
		return scale;
	}

	public void setScale(Vector3d scale) {
		this.scale = scale;
	}
	
	public void refreshParents() {
		parS.clear();
		//parS.add(this);
		getS(this);
		
		Collections.reverse(parS);
	}
	void getS(Element j) {
		if (j.getParent() != null) {
			parS.add(j.getParent());
			getS(j.getParent());
		}
	}
	public void texture(int[] tex, int w, int h) {
		this.tex = tex;
		this.texW = w;
		this.texH = h;
	}
	public void normal(int[] tex, int w, int h) {
		this.normalTex = tex;
		this.norW = w;
		this.norH = h;
	}
	public void normal(Texture texture) {
		this.normalTex = texture.data;
		this.norW = texture.w;
		this.norH = texture.h;
	}
	public void texture(int argb) {
		this.tex = new int[] {argb};
		texW = texH = 1;
	}
	public void texture(Texture texture) {
		this.tex = texture.data;
		this.texW = texture.w;
		this.texH = texture.h;
	}
	public TRS localToWorld() {
		TRS trs = new TRS();
		Vector3d rpos = getPos().c();
		Vector3d rrot = getRot().c();
		Vector3d rs = getScale().c();
		for (Element ele : parS) {
			//Vector3d center = Vector3d.from(curTransform);
			// I can't ensure if this is right code
			rpos = Vector3d.multiply(ele.getScale(), rpos);
			rpos = Vector3d.add(ele.getPos(), Vector3d.from(Engine.rotate(rpos/*ele.getPos()*/, rpos, ele.getRot())));
			rrot = Vector3d.add(rrot, ele.getRot());
			rs = Vector3d.multiply(rs, ele.getScale());
		}
		trs.p = rpos;
		trs.r = rrot;
		trs.s = rs;
		return trs;
	}
	protected void localToWorldBind(Engine e) {
		if (getParent() == null) {
			e.translate(pos.getX(), pos.getY(), pos.getZ());
			e.rotate(rot.getX(), rot.getY(), rot.getZ());
			e.scale(scale.getX(), scale.getY(), scale.getZ());
		}
		else {
			TRS trs = localToWorld();
			Vector3d rpos = trs.p;
			Vector3d rrot = trs.r;
			Vector3d rs = trs.s;
			e.translate(rpos.getX(), rpos.getY(), rpos.getZ());
			e.rotate(rrot.getX(), rrot.getY(), rrot.getZ());
			e.scale(rs.getX(), rs.getY(), rs.getZ());
		}
	}
	protected void initDraw(Engine e) {
		e.identity(Engine.IDENTITY_ALL);
		e.bindTexture(tex, texW, texH);
		e.bindNormal(normalTex, norW, norH);
	}
	protected void lateDraw(Graphics g, Engine e) {
		e.specRadius(specularable);
		e.specular(specularness);
		e.linkVertices(vertices);
		e.linkIndices(indices);
		e.draw(g);
	}
	public void draw(Graphics g, Engine e) {
		if (!isActive()) return;
		for (Element p : parS) {
			if (!p.isActive()) return;
		}
		initDraw(e);
		localToWorldBind(e);
		lateDraw(g, e);
	}

	public Element getParent() {
		return parent;
	}

	public void setParent(Element parent) {
		this.parent = parent;
		refreshParents();
	}
}
