package hyolib.anim;

import java.io.*;
import java.util.*;

import hyolib.Mtllib;
import hyolib.ObjModel;
import hyolib.ObjMtl;
import hyolib.Vector2d;
import hyolib.Vector3d;
import hyolib.Vertex;

public class SkelModelLoader {
    /**
     * OBJ 파일을 읽어서 vs, inds 를 채웁니다.
     * - vs: Vertex(pos, uv, normal, col) 리스트
     * - inds: 삼각형 인덱스 리스트
     */
    public static SkeletonModel loadObj(String filename) throws IOException {
    	Skeleton skeleton = new Skeleton();
    	List<ObjModel> models = new ArrayList<>();
    	List<SkelBoundModel> bounds = new ArrayList<SkelBoundModel>();
    	List<SkelWeightModel> weights = new ArrayList<SkelWeightModel>();
    	List<Joint> joints = new ArrayList<Joint>();
    	SkeletonModel sModel = new SkeletonModel(models, skeleton);
    	ObjModel model = new ObjModel();
        List<Vector3d>  positions = new ArrayList<>();
        List<Vector2d>  texcoords = new ArrayList<>();
        List<Vector3d>  normals   = new ArrayList<>();
        // 중복 (v/vt/vn) -> vs 인덱스 맵핑
        Map<String,Integer> cache = new HashMap<>();
        Mtllib mtllib = null;
        String path = new File(filename).getParent() + "\\";
        if (path == "null\\") path = "";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = br.readLine()) != null) {
            	if (line.startsWith("o ")) {
            		String[] tok = line.split(" ");
            		String name = tok[1];
            		String[] tokBar = tok[1].split("_");
            		if (model != null && model.vertices.size() > 0) {
            			models.add(model);
            			
            			model = new ObjModel();
            			model.name = name;
            			if (name.startsWith("!p")) {
            				//System.out.println(name);
            				model.parent = tokBar[1];
            				name = name.substring(tokBar[0].length()+1+tokBar[1].length()+1);
            				//System.out.println("AFTER " + name);
            			}
            			if (name.startsWith("$bb")) {
            				model.type = ObjModel.BOUNDS;
            				model.name = name.substring(3+1);
            			}
            			if (name.startsWith("$w")) {
            				model.type = ObjModel.WEIGHTS;
            				model.name = name.substring(2+1);
            			}
            		}
            	}
            	else if (line.startsWith("mtllib ")) {
            		mtllib = new Mtllib(path, path + line.substring(7));
            	}
            	else if (line.startsWith("usemtl ")) {
            		for (String key:mtllib.mtlTable.keySet()) {
            			if (key.equals(line.substring(7))) {
            				model.mtl = mtllib.mtlTable.get(key);
            				break;
            			}
            		}
            	}
                else if(line.startsWith("v ")) {
                    // vertex position
                    String[] tok = line.split("\\s+");
                    double x = Double.parseDouble(tok[1]);
                    double y = Double.parseDouble(tok[2]);
                    double z = Double.parseDouble(tok[3]);
                    positions.add(new Vector3d(x,y,z));

                } else if(line.startsWith("vt ")) {
                    // texture coord
                    String[] tok = line.split("\\s+");
                    double u = Double.parseDouble(tok[1]);
                    double v = Double.parseDouble(tok[2]);
                    texcoords.add(new Vector2d(u,v));

                } else if(line.startsWith("vn ")) {
                    // normal
                    String[] tok = line.split("\\s+");
                    double nx = Double.parseDouble(tok[1]);
                    double ny = Double.parseDouble(tok[2]);
                    double nz = Double.parseDouble(tok[3]);
                    normals.add(new Vector3d(nx,ny,nz));

                } else if(line.startsWith("f ")) {
                    // face (assume triangles)
                    String[] tok = line.split("\\s+");
                    for(int i=1; i<=3; i++){
                        String key = tok[i];          // e.g. "12/34/56"
                        Integer idx = cache.get(key);
                        if(idx == null){
                            String[] part = key.split("/");
                            int vi  = Integer.parseInt(part[0]) - 1;
                            int ti  = part.length>1 && !part[1].isEmpty()
                                      ? Integer.parseInt(part[1]) - 1
                                      : -1;
                            int ni  = part.length>2
                                      ? Integer.parseInt(part[2]) - 1
                                      : -1;

                            Vector3d pos = positions.get(vi);
                            Vector2d uv  = ti >= 0 ? texcoords.get(ti) : new Vector2d(0, 0);
                            Vector3d nrm = ni >= 0 ? normals.get(ni)  : new Vector3d(0,0,1);
                            //System.out.println(nrm.toString());

                            model.vertices.add(new Vertex(pos, uv, nrm, 0xFFFFFFFF));
                            idx = model.vertices.size() - 1;
                            cache.put(key, idx);
                        }
                        model.indices.add(idx);
                    }
                }
            }
        }
        if (model != null) {
        	models.add(model);
        	model = null;
        }
        for (ObjModel m : sModel.models) {
        	switch (m.type) {
        		case ObjModel.BOUNDS:
        			Vector3d min = new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        			Vector3d max = new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        			for (Vertex v : m.vertices) {
        				min = new Vector3d(Math.min(min.getX(), v.v.getX()), Math.min(min.getY(), v.v.getY()), Math.min(min.getZ(), v.v.getZ()));
        				max = new Vector3d(Math.max(max.getX(), v.v.getX()), Math.max(max.getY(), v.v.getY()), Math.max(max.getZ(), v.v.getZ()));
        			}
        			
        			// Select Bound vertices
        			List<Vertex> sel = new ArrayList<Vertex>();
        			for (ObjModel pm : sModel.models) {
        				if (pm.type != 0) continue;
        				for (Vertex v : pm.vertices) {
        					double x = v.v.getX();
        					double y = v.v.getY();
        					double z = v.v.getZ();
        					if (x >= min.getX() && y >= min.getY() && z >= min.getZ() &&
        							x <= max.getX() && y <= max.getY() && z <= max.getZ()) {
        						sel.add(v);
        					}
        				}
        			}
        			SkelBoundModel bound = new SkelBoundModel();
        			bound.vertices = sel;
        			List<Vertex> selCopy = new ArrayList<Vertex>();
        			for (Vertex b : bound.vertices) {
        				selCopy.add(new Vertex(b.v.c(), b.uv.c(), b.n.c(), 0xFFFFFFFF));
        			}
        			bound.bindVertices = selCopy;
        			bound.min = min;
        			bound.max = max;
        			bound.name = m.name;
        			bound.parentName = m.parent;
        			bounds.add(bound);
        			
        			break;
        		case ObjModel.WEIGHTS:
        			SkelWeightModel weight = new SkelWeightModel();
        			weight.point = m.vertices.get(0).v.c();
        			weight.name = m.name;
        			weights.add(weight);
        			break;
        		default:
        			break;
        	}
        }
        List<ObjModel> toR = new ArrayList<ObjModel>();
        for (ObjModel o : sModel.models) {
        	if (o.type != 0) toR.add(o);
        }
        sModel.models.removeAll(toR);
        for (SkelBoundModel bound : bounds) {
        	Joint j = new Joint(new Vector3d(0, 0, 0).to());
        	j.name = bound.name;
        	j.parentName = bound.parentName;
        	SkelWeightModel weightFind = null;
        	for (SkelWeightModel weight : weights) {
        		if (weight.name.equals(bound.name)) {
        			weightFind = weight;
        			break;
        		}
        	}
        	if (weightFind != null) {
        		j.weight = weightFind;
        		j.transform = j.weight.point.to();
        		j.curTransform = j.transform.copy();
        	}
        	j.bounds = bound;
        	joints.add(j);
        }
        for (Joint j : joints) {
        	if (j.parentName != null) {
        		j.parent = findByName(joints, j.parentName);
        		j.parent.childs.add(j);
        	}
        }
        skeleton.joints = joints;
        return sModel;
    }
    static Joint findByName(List<Joint> joints, String name) {
    	Joint find = null;
    	for (Joint j : joints) {
    		if (j.name.equals(name)) {
    			find = j;
    			break;
    		}
    	}
    	return find;
    }
}
