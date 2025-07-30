package hyonema.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import hyolib.Engine;
import hyolib.ObjModel;
import hyolib.RLoader;
import hyolib.Vector3d;
import hyolib.anim.Joint;
import hyolib.anim.SkelConductor;
import hyolib.anim.SkelModelLoader;
import hyolib.anim.SkeletonModel;
import hyolib.element.Element;
import hyolib.element.JointElement;
import hyolib.element.ParticleElement;
import hyonema.CinemaObject;
import hyonema.Hyonema;

public class SceneLoader {
	public static void load(Hyonema hyonema, Scene scene) {
		hyonema.cos.clear();
		/*for (SceneObject so : scene.sos) {
			if (so.type == SOType.ModelEle) {
				CinemaObject f = null;
				for (CinemaObject co : hyonema.cos) {
					if (co.name.equals(so.name)) {
						f = co;
						break;
					}
				}
				if (f != null) {
					f.e.setPos(so.pos);
					f.e.setRot(so.rot);
					f.e.setScale(so.scale);
				}
			}
		}*/
		hyonema.cos = scene.cos;
	}
	public static SceneObject export(CinemaObject go) {
		SceneObject so = new SceneObject();
		so.pos = go.getPosition();
		so.rot = go.getRotation();
		so.scale = go.getScale();
		so.name = go.name;
		so.type = SOType.values()[(int)go.tags[Constants.TI_TYPE]];
		so.mipmap = (boolean)go.tags[Constants.TB_TEXMIPMAP];
		so.texPath = (String)go.tags[Constants.TS_TEX];
		switch (so.type) {
			case Primitive:
				//so.prmitive = Mesh.primitives.indexOf(go.getMesh());
				break;
			case Terrain:
			case Model:
			case SkelModel:
			default:
				so.modelPath = (String)go.tags[1];
				so.prmitive = -1;
				break;
		}
		return so;
	}
	static int count = 1;
	public static CinemaObject obj(Hyonema hyonema, SceneObject so) {
		Element e = new Element();
		long id = hyonema.genId();
		e.name = "Model " + id;
		count++;
		CinemaObject go = new CinemaObject(id, e, so.pos, so.rot, so.scale/*, mesh, new Material(tex), Shader.standard*/);
		go.name = e.name;
		//Mesh mesh;
		switch (so.type) {
			case Primitive:
				//mesh = Mesh.primitives.get(so.prmitive);
				break;
			case Terrain:
				if (so.modelPath != null) {
					//mesh = Terrain.createMesh(so.modelPath, so.texPath);
					break;
				}
			case Particle:
				e = new ParticleElement();
				e.setPos(go.getPosition());
				e.setRot(go.getRotation());
				e.setScale(go.getScale());
				e.name = go.name;
				go.e = e;
				break;
			case Model:
				if (so.modelPath != null) {
					List<Element> ret = new ArrayList<>();
					try {
						List<ObjModel> models = RLoader.loadObj(so.modelPath);
						for (ObjModel model : models) {
							Element el = new Element();
							el.name = model.name;
							el.vertices = model.vertices;
							el.indices = model.indices;
							if (model.mtl != null && model.mtl.tex != null && new File(model.mtl.tex).exists()) {
								el.texture(Engine.loadTexture(model.mtl.tex));
							}
							ret.add(el);
						}
					} catch (IOException el) {
						el.printStackTrace();
					}
					if (ret.size() > 0) {
						for (Element ele : ret) {
							CinemaObject co = new CinemaObject(hyonema.genId(), ele, new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1));
							co.name = ele.name;
							go.childs.add(co);
							e.addChild(ele);
							hyonema.cos.add(co);
						}
					}
					//mesh = new OBJLoader().load(so.modelPath);
				}
				break;
			case SkelModel:
				if (so.modelPath != null) {
					SkelConductor conductor = null;
					SkeletonModel sModel = null;
					try {
						sModel = SkelModelLoader.loadObj(so.modelPath);
						conductor = new SkelConductor(sModel);
						conductor.presElement = go.e;
					} catch (IOException el) {
						el.printStackTrace();
					}
					List<Element> ret = conductor.elements;
					ret.addAll(conductor.jointElements);
					if (ret.size() > 0) {
						for (Element ele : ret) {
							CinemaObject co = new CinemaObject(hyonema.genId(), ele, new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(1, 1, 1));
							co.tags[Constants.TI_TYPE] = SOType.ModelEle.ordinal();
							co.name = ele.name;
							go.childs.add(co);
							e.addChild(ele);
							hyonema.cos.add(co);
						}
					}
					hyonema.skels.add(conductor);
					//mesh = new OBJLoader().load(so.modelPath);
					break;
				}
			case ModelEle:
				return null;
			default:
				//mesh = Mesh.cube;
				break;
		}
		String tex = "resources/textures/white.png";
		if (so.texPath != null) tex = so.texPath;
		if (so.type == SOType.UI) {
			//go = new UIObject(so.pos, so.rot, so.scale, new Material(tex));
		}
		go.tags[Constants.TI_TYPE] = so.type.ordinal();
		go.tags[Constants.TS_MODEL] = so.modelPath;
		go.tags[Constants.TS_TEX] = so.texPath;
		go.tags[Constants.TB_TEXMIPMAP] = so.mipmap;
		if (so.name != null && so.name.length() > 0)
		go.name = so.name;
		hyonema.cos.add(go);
		//go.getMaterial().mipmap = so.mipmap;
		return go;
	}
	public static Scene scene(String path) {
		File file = new File(path);
	    if (file != null) {
			FileInputStream fos;
			try {
				fos = new FileInputStream(file);
				ObjectInputStream inStream = new ObjectInputStream(fos);
				Scene s = (Scene)inStream.readObject();
				inStream.close();
				return s;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	    return null;
	}
}
