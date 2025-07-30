package hyolib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;

public class Mtllib implements Serializable {
	private static final long serialVersionUID = Constants.BASE + 19;
	public Hashtable<String, ObjMtl> mtlTable = new Hashtable<String, ObjMtl>();
	public Mtllib(String par, String path) {
		if (par == "null\\") par = "";
		ObjMtl cur = null;
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while((line = br.readLine()) != null) {
            	if (line.startsWith("newmtl " )) {
            		if (cur != null) {
            			mtlTable.put(cur.name, cur);
            		}
            		cur = new ObjMtl();
            		cur.name = line.substring(7);
            	}
            	else if (line.startsWith("map_Kd ")) {
            		cur.tex = par + line.substring(7);
            	}
            }
            if (cur != null) {
            	mtlTable.put(cur.name, cur);
            }
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
