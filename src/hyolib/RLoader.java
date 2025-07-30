package hyolib;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class RLoader {
    /**
     * OBJ 파일을 읽어서 vs, inds 를 채웁니다.
     * - vs: Vertex(pos, uv, normal, col) 리스트
     * - inds: 삼각형 인덱스 리스트
     */
    public static List<ObjModel> loadObj(String filename) throws IOException {
    	List<ObjModel> models = new ArrayList<>();
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
            		if (model != null && model.vertices.size() > 0) {
            			models.add(model);
            		}
            		model = new ObjModel();
        			model.name = line.substring(2);
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
            }
        }
        if (model != null) {
        	models.add(model);
        	model = null;
        }
        return models;
    }
}
