package hyonema;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.*;
import hyonema.scene.*;
import hyolib.*;
import hyonema.scene.Constants;
import hyolib.anim.*;
import hyolib.element.Element;

import java.util.*;

public class ToolWnd extends Thread {
	ArrayList<JButton> buttons = new ArrayList<JButton>();
	Hashtable<JButton, CinemaObject> table = new Hashtable<JButton, CinemaObject>();
	public CinemaObject selectedObject;
	Hyonema engine;
	JButton play = new JButton("Play");
    JButton tex = new JButton("Load Texture");
    JButton shd = new JButton("Load Shader");
    JButton mde = new JButton("Load Normal");
    JButton update = new JButton("Refresh");
    JButton delete = new JButton("Delete");
    JButton set = new JButton("Pos Set");
    JButton rset = new JButton("Rot Set");
    JButton sset = new JButton("Scl Set");
    JButton save = new JButton("Save");
    JButton load = new JButton("Load");
    JButton name = new JButton("Name");
    JButton gloss = new JButton("Gloss");
    JButton child = new JButton("Set Parent");
    JButton act = new JButton("Active");
    JTextField nf = new JTextField();
    JTextField x = new JTextField();
    JTextField y = new JTextField();
    JTextField z = new JTextField();
    JTextField rx = new JTextField();
    JTextField ry = new JTextField();
    JTextField rz = new JTextField();
    JTextField sx = new JTextField();
    JTextField sy = new JTextField();
    JTextField sz = new JTextField();
    final JPanel gridButton = new JPanel(new GridLayout(0,1,2,2));
	public ToolWnd(Hyonema engine) {
		this.engine = engine;
	}
	public void addObj() {
		JButton btn = new JButton(selectedObject.name);
    	btn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	selectedObject = table.get(btn);
            	nf.setText(selectedObject.name);
            	x.setText(selectedObject.getPosition().getX() + "");
            	y.setText(selectedObject.getPosition().getY() + "");
            	z.setText(selectedObject.getPosition().getZ() + "");
            	
            	rx.setText(Math.toDegrees(selectedObject.getRotation().getX()) + "");
            	ry.setText(Math.toDegrees(selectedObject.getRotation().getY()) + "");
            	rz.setText(Math.toDegrees(selectedObject.getRotation().getZ()) + "");
            	
            	sx.setText(selectedObject.getScale().getX() + "");
            	sy.setText(selectedObject.getScale().getY() + "");
            	sz.setText(selectedObject.getScale().getZ() + "");
            }
        });
    	table.put(btn, selectedObject);
        gridButton.add(btn);
	}
	public JFileChooser choose(boolean s) {
		JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    	int result;
    	if (s)
    		result = fileChooser.showSaveDialog(null);
    	else 
    		result = fileChooser.showOpenDialog(null);
    	if (result == JFileChooser.APPROVE_OPTION)
    		return fileChooser;
    	else
    		return null;
	}
	public JFrame f1;
	@Override
	public void run() {
		f1 = new JFrame();
		final JPanel gui = new JPanel(new BorderLayout());
		f1.getContentPane().add(gui);
		f1.setTitle("Hyowon Cinema: Tools");

        final Box boxButton = Box.createVerticalBox();
        //boxButton.add(new JButton("Button 1"));
        
        //play.setBounds(230, 50, 140, 40);
        set.setBounds(135, 50, 80, 40);
        x.setBounds(225, 50, 40, 40);
        y.setBounds(275, 50, 40, 40);
        z.setBounds(325, 50, 40, 40);
        
        rset.setBounds(135, 100, 80, 40);
        rx.setBounds(225, 100, 40, 40);
        ry.setBounds(275, 100, 40, 40);
        rz.setBounds(325, 100, 40, 40);
        
        sset.setBounds(135, 150, 80, 40);
        sx.setBounds(225, 150, 40, 40);
        sy.setBounds(275, 150, 40, 40);
        sz.setBounds(325, 150, 40, 40);
        
        tex.setBounds(230, 200, 140, 40);
        shd.setBounds(230, 300, 140, 40);
        update.setBounds(240, 400, 130, 40);
        delete.setBounds(250, 500, 120, 40);
        mde.setBounds(230, 590, 140, 40);
        child.setBounds(110, 590, 110, 40);
        load.setBounds(100+10, 680, 120, 35);
        save.setBounds(230+10, 680, 120, 35);
        name.setBounds(55+70, 255, 90, 35);
        nf.setBounds(230, 255, 140, 35);
        gloss.setBounds(110, 500, 130, 40);
        act.setBounds(55+50, 400, 130, 40);
        gloss.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedObject.e.specularness = Double.parseDouble(JOptionPane.showInputDialog("Specularness?:", selectedObject.e.specularness + ""));
				selectedObject.e.specularable = Double.parseDouble(JOptionPane.showInputDialog("Specularable?:", selectedObject.e.specularable + ""));
			}
		});
        child.addActionListener( new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedObject == null) return;
				CinemaObject co = engine.keyWnd.selectObject();
				CinemaObject preC = selectedObject.parent;
				Element pre = selectedObject.e.getParent();
				if (preC != null && pre != null) {
					preC.childs.remove(selectedObject);
					pre.removeChild(selectedObject.e);
				}
				selectedObject.parent = co;
				if (co != null)
					selectedObject.e.setParent(co.e);
				else
					selectedObject.e.setParent(null);
				co.e.addChild(selectedObject.e);
			}
		});
        tex.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	JFileChooser fileChooser = choose(false);
            	if (fileChooser != null) {
            	    File selectedFile = fileChooser.getSelectedFile();
            	    String pth = selectedFile.getAbsolutePath();
            	    selectedObject.tags[Constants.TS_TEX] = pth;
            	    selectedObject.e.texture(Engine.loadTexture(pth));
            	    //selectedObject.setMaterial(new Material(pth));
            	    //selectedObject.created = false;
            	}
            	
            }
        });
        name.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		selectedObject.name = selectedObject.e.name = nf.getText();
            	}
            }
        });
        set.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		Vector3d pos = new Vector3d(Double.parseDouble(x.getText()), Double.parseDouble(y.getText()), Double.parseDouble(z.getText()));
            		if (engine.recording) {
            			Keyframe k = engine.keyWnd.pointAtTime("hyolib.anim.Keyframe", selectedObject.name);
            			k.setPos(pos);
            			engine.keyWnd.refreshGraphics();
            		}
            		selectedObject.setPosition(pos);
            	}
            }
        });
        rset.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		Vector3d rot = new Vector3d(Math.toRadians(Double.parseDouble(rx.getText())), Math.toRadians((Double.parseDouble(ry.getText()))), Math.toRadians(Double.parseDouble(rz.getText())));
            		if (engine.recording) {
            			Keyframe k = engine.keyWnd.pointAtTime("hyolib.anim.Keyframe", selectedObject.name);
            			k.setRot(rot);
            			engine.keyWnd.refreshGraphics();
            		}
            		selectedObject.setRotation(rot);
            	}
            }
        });
        sset.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		Vector3d scale = new Vector3d(Double.parseDouble(sx.getText()), Double.parseDouble(sy.getText()), Double.parseDouble(sz.getText()));
            		if (engine.recording) {
            			Keyframe k = engine.keyWnd.pointAtTime("hyolib.anim.Keyframe", selectedObject.name);
            			k.setScale(scale);
            			engine.keyWnd.refreshGraphics();
            		}
            		selectedObject.setScale(scale);
            	}
            }
        });
        update.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
	            	/*Shader shader = selectedObject.getShader();
	            	selectedObject.setShader(new Shader(shader.vertexPath, shader.fragmentPath));
	            	selectedObject.created = false;
	            	selectedObject.shaderCreated = false;*/
            	}
            }
        });
        delete.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	removeRec(selectedObject);
            	gui.revalidate();
            }
        });
        load.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	JFileChooser fileChooser = choose(false);
            	if (fileChooser != null) {
            	    File selectedFile = fileChooser.getSelectedFile();
            	    engine.scene = SceneLoader.scene(selectedFile.getAbsolutePath());
            	    engine.reloadScene();
            	}
        	}
        });
        save.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	/*if (engine.scene.projectPackage.equals("openxl.engine.script")) {
            		String p = JOptionPane.showInputDialog("project package: ");
            		engine.scene.projectPackage = p;
            	}*/
            	JFileChooser fileChooser = choose(true);
            	if (fileChooser != null) {
            	    File selectedFile = fileChooser.getSelectedFile();
            	    File file = new File(selectedFile.getAbsolutePath());
            	    if (file != null) {
        				FileOutputStream fos;
        				try {
        					fos = new FileOutputStream(file);
        					ObjectOutputStream outStream = new ObjectOutputStream(fos);
        					engine.exportScene();
        					outStream.writeObject(engine.scene);
        					outStream.close();
        				} catch (IOException ex) {
        					ex.printStackTrace();
        				}
        			}
            	}
        	}
        });
        shd.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		String frag = "", vertex = "";
                	JFileChooser fileChooser = choose(false);
                	if (fileChooser != null) {
                	    File selectedFile = fileChooser.getSelectedFile();
                	    frag = selectedFile.getAbsolutePath();
                	}
                	JFileChooser fileChooser2 = choose(false);
                	if (fileChooser2 != null) {
                	    File selectedFile = fileChooser2.getSelectedFile();
                	    vertex = selectedFile.getAbsolutePath();
                	}
                	//selectedObject.setShader(new Shader(vertex, frag));
                	//selectedObject.shaderCreated = false;
            	}
            }
        });
        mde.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	if (selectedObject != null) {
            		String path = "";
                	JFileChooser fileChooser = choose(false);
                	if (fileChooser != null) {
                	    File selectedFile = fileChooser.getSelectedFile();
                	    path = selectedFile.getAbsolutePath();
                	    selectedObject.e.normal(Engine.loadTexture(path));
                	}
            	}
            }
        });
        act.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedObject != null) {
					selectedObject.e.setActive(!selectedObject.e.isActive());
				}
			}
        	
        });
        gui.add(play);
        gui.add(set);
        gui.add(x);
        gui.add(y);
        gui.add(z);
        gui.add(rset);
        gui.add(rx);
        gui.add(ry);
        gui.add(rz);
        gui.add(sset);
        gui.add(sx);
        gui.add(sy);
        gui.add(sz);
        gui.add(tex);
        gui.add(shd);
        gui.add(save);
        gui.add(load);
        gui.add(update);
        gui.add(delete);
        gui.add(mde);
        gui.add(name);
        gui.add(nf);
        gui.add(gloss);
        gui.add(child);
        gui.add(act);
        
        JPanel gridConstrain = new JPanel(new BorderLayout());
        gridConstrain.add(gridButton, BorderLayout.NORTH);
        JSplitPane sp = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(gridConstrain),
            new JScrollPane(boxButton));
        gui.add(sp, BorderLayout.CENTER);
        
        JButton b = new JButton("New");
        b.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	String[] types = {"Primitive","Model","SkelModel", "Terrain","UI","Particle"};
            	String sel = (String)JOptionPane.showInputDialog(null, "", "Select object type", JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
            	int i = Arrays.asList(types).indexOf(sel);
            	SceneObject so = new SceneObject();
            	so.mipmap = true;
            	so.type = SOType.values()[i];
            	so.pos = so.rot = new Vector3d(0, 0, 0);
            	if (so.type == SOType.Primitive) so.prmitive = 0;
            	else if (so.type == SOType.Terrain)
            		so.modelPath = JOptionPane.showInputDialog("Terrain heightmap File:");
            	else if (so.type == SOType.Model)
            		so.modelPath = JOptionPane.showInputDialog("OBJ File:");
            	else if (so.type == SOType.SkelModel) {
            		so.modelPath = JOptionPane.showInputDialog("OBJ File:");
            		so.skel = true;
            	}
            	else if (so.type == SOType.UI) so.pos = new Vector3d(0, 0, -1.2f);
            	so.scale = new Vector3d(1, 1, 1);
            	CinemaObject obj = SceneLoader.obj(engine, so);
            	addObject(obj, true);
            	selectedObject = obj;
                gui.revalidate();
            }
        });

        gui.add(b, BorderLayout.NORTH);
        
        gui.setSize(400,800);
        f1.setLocationRelativeTo(null);
        f1.pack();
		f1.setSize(400, 800);
		f1.setLocation(20, 40);
        f1.setVisible(true);
        /*engine.scene = SceneLoader.scene("hyonema/scenes/a.scene");
	    engine.reloadScene();*/
	}
	public void clear() {
		table.forEach((k, v) -> {
			gridButton.remove(k);
    	});
		table.clear();
	}
	JButton key;
	void removeRec(CinemaObject selectedObject) {
		table.forEach((k, v) -> {
    		if (v == selectedObject) {
    			gridButton.remove(k);
    			key = k;
    		}
    	});
		table.remove(key);
    	//if (selectedObject != null)
    		//selectedObject.setActive(false);
    	engine.cos.remove(selectedObject);
    	ArrayList<SkelConductor> tr = new ArrayList<SkelConductor>();
    	for (SkelConductor skel : engine.skels) {
    		if (skel.presElement.equals(selectedObject.e)) tr.add(skel);
    	}
    	engine.skels.removeAll(tr);
    	for (CinemaObject child : selectedObject.childs)
    		removeRec(child);
	}
	void addObject(CinemaObject co, boolean addChilds) {
		selectedObject = co;
		selectedObject.name = co.name;
    	//engine.eles.add(selectedObject.e);
    	addObj();
		if (addChilds) {
			for (CinemaObject child : co.childs) {
				addObject(child, true);
			}
		}
	}
}