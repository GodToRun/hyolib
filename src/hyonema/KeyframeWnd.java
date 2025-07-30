package hyonema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import hyonema.scene.*;
import hyolib.*;
import hyonema.scene.Constants;
import hyolib.anim.*;
import hyolib.element.Element;
import hyolib.element.ParticleElement;

import java.util.*;
import java.util.Timer;
class AnimThread extends Thread {
	KeyframeWnd wnd;
	public AnimThread(KeyframeWnd wnd) {
		this.wnd = wnd;
	}
	Vector3d pp = new Vector3d(0, 0, 0);
	@Override
	public void run() {
        long last = System.nanoTime();
        while (true) {
        	long time = System.nanoTime();
        	double dt = ((double)(time - last))/1000000000D;
        	dt = 20D / 1000D;
        	last = time;
        	
        	// Refresh editor TRS
        	CinemaObject co = wnd.engine.toolWnd.selectedObject;
        	if (co != null) {
        		if (Vector3d.dist(co.getPosition(), pp) > 0.001D) {
        			wnd.engine.toolWnd.x.setText(Math.round(co.getPosition().getX()*100D)/100D + "");
                	wnd.engine.toolWnd.y.setText(Math.round(co.getPosition().getY()*100D)/100D + "");
                	wnd.engine.toolWnd.z.setText(Math.round(co.getPosition().getZ()*100D)/100D + "");
        			
                	wnd.engine.toolWnd.rx.setText(Math.round(Math.toDegrees(co.getRotation().getX())*100D)/100D + "");
        			wnd.engine.toolWnd.ry.setText(Math.round(Math.toDegrees(co.getRotation().getY())*100D)/100D + "");
        			wnd.engine.toolWnd.rz.setText(Math.round(Math.toDegrees(co.getRotation().getZ())*100D)/100D + "");
        			
        			wnd.engine.toolWnd.sx.setText(Math.round(co.getScale().getX()*100D)/100D + "");
        			wnd.engine.toolWnd.sy.setText(Math.round(co.getScale().getY()*100D)/100D + "");
        			wnd.engine.toolWnd.sz.setText(Math.round(co.getScale().getZ()*100D)/100D + "");
        		}
    			pp = co.getPosition().c();
        	}
        	
        	if (wnd.isPlaying) {
        		AnimationLauncher tR = null;
        		for (int i = 0; i < wnd.playingAnimations.size(); i++) {
        			if (i >= wnd.playingAnimations.size()) continue;
        			AnimationLauncher anim = wnd.playingAnimations.get(i);
        			wnd.view(anim.time, wnd.engine.currentLauncher.dependent, anim.anim.get(anim.time));
        			
        			anim.time += dt;
        			if (anim == wnd.engine.currentLauncher) wnd.engine.curTime = anim.time;
            		if (anim.time >= anim.anim.getLength()) {
            			if (anim.loop)
            				anim.time = 0;
            			else tR = anim;
            		}
        		}
        		wnd.refreshGraphics();	
        		if (tR != null) wnd.playingAnimations.remove(tR);
        	}
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
}
class KeyframePanel extends JPanel {
	KeyframeWnd kw;
	public KeyframePanel(KeyframeWnd kw) {
		//super(new BorderLayout());
		super();
		this.kw = kw;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(kw.buf, 0, 0, kw.buf.getWidth(), kw.buf.getHeight(), null);
	}
}
public class KeyframeWnd extends Thread implements KeyListener, MouseMotionListener, MouseListener {
	double clipboardTime;
	List<Keyframe> clipboardKeyframes = new ArrayList<Keyframe>();
	ArrayList<JButton> buttons = new ArrayList<JButton>();
	Hashtable<JButton, CinemaObject> table = new Hashtable<JButton, CinemaObject>();
	KeyframePanel gui;
	Hyonema engine;
	JButton play = new JButton("Play");
    JButton newAni = new JButton("New");
    JButton particle = new JButton("Emit");
    JButton deleteAni = new JButton("Delete Animation");
    JButton record = new JButton("Record");
    JButton mmset = new JButton("Set Meta Motion");
    JButton sset = new JButton("Set Subtitle");
    JButton save = new JButton("Save");
    JButton load = new JButton("Load");
    JButton soset = new JButton("Set Sound");
    JButton actmo = new JButton("Active Motion");
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
    BufferedImage buf;
    Graphics bG;
    double xPerTime = 120D;
    double indicator; // time
    int yIndicator;
    boolean freeMove = false;
    boolean isPlaying = false;
    ArrayList<AnimationLauncher> playingAnimations = new ArrayList<AnimationLauncher>();
	public KeyframeWnd(Hyonema engine) {
		this.engine = engine;
		engine.current = new Animation("");
		
		buf = new BufferedImage(1000, 200, BufferedImage.TYPE_INT_RGB);
		bG = buf.getGraphics();
	}
	int timeToSX(double time) {
		return 40+(int)((time-indicator)*xPerTime);
	}
	double sxToTime(int x) {
		return (double)(x-40)/xPerTime+indicator;
	}
	int rowToY(int i) {
		return 40+i*20-yIndicator;
	}
	void refreshGraphics() {
		bG.setColor(new Color(85, 85, 85));
		bG.fillRect(0, 0, buf.getWidth(), buf.getHeight()-35);
		bG.setColor(new Color(200, 200, 200));
		bG.fillRect(0, buf.getHeight()-35, buf.getWidth(), buf.getHeight());
		for (int i = 0; i < engine.current.keyrows(); i++) {
			int y = rowToY(i);
			KeyRow row = engine.current.get(i);
			bG.setColor(new Color(255, 255, 0));
			String tag = "";
			if (row.keyframes() > 0)
				tag = row.get(0).getTag();
			if (tag == null) tag = "";
			bG.drawString(tag, 0, y);
			for (int j = 0; j < row.keyframes(); j++) {
				Keyframe k = row.get(j);
				boolean isSelected = engine.selectedKeyframes.contains(k);
				if (isSelected)
					bG.setColor(new Color(255, 255, 0));
				else
					bG.setColor(new Color(255, 255, 255));
				bG.fillOval(timeToSX(k.time)-7, y-15, 15, 15);
			}
		}
		bG.setColor(new Color(255, 255, 255));
		bG.drawLine(timeToSX(engine.curTime), 0, timeToSX(engine.curTime), buf.getHeight());
		gui.repaint();
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
	public void play() {
		isPlaying = true;
		engine.currentLauncher = new AnimationLauncher(engine.current, true);
		playingAnimations.add(engine.currentLauncher);
		engine.currentLauncher.time = engine.curTime;
	}
	public JFrame f1;
	@Override
	public void run() {
		f1 = new JFrame();
		gui = new KeyframePanel(this);
		gui.setLayout(null);
		f1.getContentPane().add(gui);
		f1.pack();
		f1.setSize(1000, 400);
		f1.setTitle("Hyowon Cinema: Animation Editor");
		f1.addKeyListener(this);
		f1.addMouseMotionListener(this);
		f1.addMouseListener(this);
		f1.setResizable(false);
        final Box boxButton = Box.createVerticalBox();
        play.setFocusable(false);
        record.setFocusable(false);
        x.setFocusable(false);
        y.setFocusable(false);
        z.setFocusable(false);
        mmset.setFocusable(false);
        rx.setFocusable(false);
        ry.setFocusable(false);
        rz.setFocusable(false);
        sset.setFocusable(false);
        sx.setFocusable(false);
        sy.setFocusable(false);
        sz.setFocusable(false);
        newAni.setFocusable(false);
        particle.setFocusable(false);
        deleteAni.setFocusable(false);
        save.setFocusable(false);
        load.setFocusable(false);
        soset.setFocusable(false);
        nf.setFocusable(false);
        actmo.setFocusable(false);
        //boxButton.add(new JButton("Button 1"));
        
        play.setBounds(230, 254, 140, 40);
        record.setBounds(410, 254, 140, 40);
        x.setBounds(225, 50, 40, 40);
        y.setBounds(275, 50, 40, 40);
        z.setBounds(325, 50, 40, 40);
        
        mmset.setBounds(815, 207, 160, 40);
        rx.setBounds(225, 100, 40, 40);
        ry.setBounds(275, 100, 40, 40);
        rz.setBounds(325, 100, 40, 40);
        
        sset.setBounds(410, 207, 140, 40);
        sx.setBounds(225, 150, 40, 40);
        sy.setBounds(275, 150, 40, 40);
        sz.setBounds(325, 150, 40, 40);
        
        newAni.setBounds(230, 207, 140, 40);
        particle.setBounds(230, 301, 140, 40);
        load.setBounds(70+600, 280, 120, 35);
        save.setBounds(230+600, 280, 120, 35);
        soset.setBounds(410+160, 207, 140, 40);
        nf.setBounds(160, 255, 160, 35);
        actmo.setBounds(50, 254+50, 140, 40);
        play.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isPlaying = !isPlaying;
				if (isPlaying) {
					play();
					play.setText("Stop");
				}
				else {
					playingAnimations.remove(engine.currentLauncher);
					play.setText("Play");
				}
			}
		});
        sset.addActionListener( new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String dn = JOptionPane.showInputDialog("Sub Number? (Default: 0)");
				if (dn == null || (dn != null && dn.trim().length() == 0)) dn = "0";
				int num = Integer.parseInt(dn);
				SubMotion sm = (SubMotion)pointAtTime("hyonema.SubMotion", "_Sub" + num);
				String str = JOptionPane.showInputDialog("Subtitle?:");
				sm.length = Double.parseDouble(JOptionPane.showInputDialog("Length?:"));
				sm.subtitle = new Subtitle(str, 0, 40, str.length() * 30, 40);
				sm.init(engine);
				sm.setTag("Subtitle");
				refreshGraphics();
			}
		});
        particle.addActionListener( new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				CinemaObject co = engine.toolWnd.selectedObject;
				if (co != null && co.e.getClass().isAssignableFrom(ParticleElement.class)) {
					ParticleMotion sm = (ParticleMotion)pointAtTime("hyonema.ParticleMotion", "_PP" + co.uuid);
					sm.emit = Integer.parseInt(JOptionPane.showInputDialog("Emit?:"));
					sm.ps = co;
					sm.init(engine);
					sm.setTag("Emit");
					refreshGraphics();
				}
			}
		});
        soset.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	Sound sound = new Sound();
            	sound.setPath(JOptionPane.showInputDialog("Audio (WAV) file path:"));
            	sound.setVolume(1.0D);
            	
            	SoundMotion sm = (SoundMotion)pointAtTime("hyonema.SoundMotion", "_Sound" + sound.getPath());
				sm.sound = sound;
				sm.init(engine);
				sm.setTag(sound.getPath());
				refreshGraphics();
            }
        });
        record.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	engine.recording = !engine.recording;
            	if (engine.recording) {
            		record.setText("Stop recording");
            	}
            	else record.setText("Record");
            }
        });
        newAni.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	String an = JOptionPane.showInputDialog("Animation name:");
            	engine.current = new Animation(an);
            	engine.scene.alt.put(engine.current.name, engine.current);
            	refreshGraphics();
            }
        });
        load.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	Animation anim = selectAnimation();
        		if (anim != null) {
        			engine.current = anim;
        			refreshGraphics();
        		}
        	}
        });
        save.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	engine.alt.put(engine.current.name, engine.current);
        	}
        });
        mmset.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Animation anim = selectAnimation();
				if (anim != null && anim != engine.current) {
					MetaMotion mm = (MetaMotion)pointAtTime("hyonema.MetaMotion", "_MM" + anim.name);
					mm.dependent = selectObject();
					mm.loop = JOptionPane.showInputDialog("It is Loop? (Y/N)").toLowerCase().equals("y");
					mm.init(engine);
					mm.setTag(anim.name);
					refreshGraphics();
				}
			}
		});
        actmo.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CinemaObject o = selectObject();
				ActiveMotion mm = (ActiveMotion)pointAtTime("hyonema.ActiveMotion", "_ACT" + o.e.getPath());
				mm.to = o;
				mm.active = JOptionPane.showInputDialog("Active? (Y/N)").toLowerCase().equals("y");
				mm.init(engine);
				mm.setTag("active::"+mm.to.e.getPath());
				refreshGraphics();
			}
		});
        deleteAni.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Animation a = selectAnimation();
				engine.alt.remove(a.name);
				if (engine.current.name.equals(a.name)) {
					engine.current = new Animation("");
				}
				refreshGraphics();
			}
		});
        gui.add(play);
        gui.add(record);
        gui.add(mmset);
        /*gui.add(x);
        gui.add(y);
        gui.add(z);
        gui.add(rset);
        /*gui.add(rx);
        gui.add(ry);
        gui.add(rz);
        /*gui.add(sx);
        gui.add(sy);
        gui.add(sz);*/
        gui.add(save);
        gui.add(load);
        gui.add(newAni);
        gui.add(particle);
        gui.add(soset);
        gui.add(sset);
        gui.add(deleteAni);
        gui.add(actmo);
        JButton b = new JButton("Dependent");
        b.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
        		engine.currentLauncher.dependent = selectObject();
            }
        });
        b.setFocusable(false);
        b.setBounds(50, 254, 140, 40);
        deleteAni.setBounds(50, 207, 140, 40);
        gui.add(b);

        gui.setSize(800,400);
        f1.setLocation(400, 40);
        f1.setVisible(true);
        refreshGraphics();
        new AnimThread(this).start();
	}
	CinemaObject selectObject() {
		ArrayList<String> strs = new ArrayList<String>();
    	strs.add("None");
    	for (int i = 0; i < engine.cos.size(); i++) {
    		strs.add(engine.cos.get(i).e.getPath());
    	}
    	if (strs.size() == 0) return null;
    	String sel = (String)JOptionPane.showInputDialog(null, "", "Select Object", JOptionPane.QUESTION_MESSAGE, null, strs.toArray(), strs.get(0));
    	if (sel.equals(strs.get(0))) {
    		return null;
    	}
    	else
    		return engine.cos.get(strs.indexOf(sel)-1); // -1 Because of 'None'.
	}
	Animation selectAnimation() {
		ArrayList<String> types = new ArrayList<String>();
    	engine.alt.forEach((k, v) -> {
    		types.add(k);
    	});
    	if (types.size() == 0) return null;
    	String sel = (String)JOptionPane.showInputDialog(null, "", "Select animation", JOptionPane.QUESTION_MESSAGE, null, types.toArray(), types.get(0));
		Animation anim = engine.alt.get(sel);
		return anim;
	}
	int dragBx;
	Keyframe snap(double time, ArrayList<Keyframe> except) {
		Keyframe snap = null;
		if (!freeMove) {
			for (int i = 0; i < engine.current.keyrows(); i++) {
				KeyRow kr = engine.current.get(i);
				for (int j = 0; j < kr.keyframes(); j++) {
					Keyframe k = kr.get(j);
					if (Math.abs(k.time-time) < 0.06D && !except.contains(k)) {
						snap = k;
						break;
					}
				}
			}	
		}
		return snap;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		Keyframe snap = null;
		double time = Math.max(0, sxToTime(e.getX()));
		if (e.getY() >= 193 && e.getY() <= 233) {
			snap = snap(time, new ArrayList<Keyframe>());
			if (snap != null)
				engine.curTime = snap.time;
			else engine.curTime = time;
			
			ArrayList<Keyframe> kfs = engine.current.get(engine.curTime);
			if (kfs == null) return;
			if (engine.currentLauncher == null) engine.currentLauncher = new AnimationLauncher(engine.current, true);
			engine.currentLauncher.time = engine.curTime;
			view(engine.curTime, engine.currentLauncher.dependent, kfs);
			refreshGraphics();
		}
		else if (e.getY() <= 193) {
			int x = e.getX();
			snap = snap(time, (ArrayList<Keyframe>)engine.selectedKeyframes);
			if (snap != null) x = timeToSX(snap.time)+7;
			
			if (dragBx == 0) dragBx = x;
			int dx = x - dragBx;
			dragBx = x;
			for (Keyframe k : engine.selectedKeyframes)
				k.time = Math.max(0, k.time + sxToTime(dx+40)-indicator);
			
			// recalculate length
			for (int i = 0; i < engine.current.keyrows(); i++)
				engine.current.get(i).calcLength();
			
			refreshGraphics();
		}
	}
	public void view(double time, CinemaObject dependent, ArrayList<Keyframe> kfs) {
		for (Keyframe kf : kfs) {
			if (kf == null) continue;
			kf.apply(time);
			for (CinemaObject co : engine.cos) {
				if (dependent != null) {
					boolean find = false;
					for (Element e : co.e.parS) {
						if (e.name.equals(dependent.name)) {
							find = true;
							break;
						}
					}
					if (!find) continue;
				}
				if (co.name != null && (co.name).equals(kf.getName())) {
					if (kf.lerpTransform()) {
						co.setPosition(kf.getPos());
						co.setRotation(kf.getRot());
						co.setScale(kf.getScale());	
					}
					if (kf.functions != null) {
						for (KeyframeFunction f : kf.functions) {
							f.apply(time, co.e);
						}
					}
					break;
				}
			}
		}
	}
	Keyframe keyframe(String classname) {
		try {
			Constructor<?> constructor = Class.forName(classname).getDeclaredConstructor(Vector3d.class, Vector3d.class, Vector3d.class, double.class, String.class);
			Keyframe k = (Keyframe)constructor.newInstance(engine.toolWnd.selectedObject.getPosition().c(), engine.toolWnd.selectedObject.getRotation().c(), engine.toolWnd.selectedObject.getScale().c(), engine.curTime, engine.toolWnd.selectedObject.name);
			k.setTag(engine.toolWnd.selectedObject.name);
			return k;
		} catch (NoSuchMethodException e) {
			Constructor<?> constructor;
			try {
				constructor = Class.forName(classname).getDeclaredConstructor(double.class);
				Keyframe k = (Keyframe)constructor.newInstance(engine.curTime);
				return k;
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		catch (ClassNotFoundException |  SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	public Keyframe pointAtTime(String classname, String name) {
		KeyRow r = engine.current.getCreate(name);
		Keyframe k = r.get(engine.curTime);
		if (k == null || (k != null && k.isLerped)) {
			k = keyframe(classname);
			
			r.add(k);
		}
		k.setName(name);
		return k;
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		refreshGraphics();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'd') {
			engine.current.remove(engine.selectedKeyframes);
			ArrayList<KeyRow> tr = new ArrayList<KeyRow>();
			for (int i = 0; i < engine.current.keyrows(); i++) {
				if (engine.current.get(i).keyframes() == 0) tr.add(engine.current.get(i));
			}
			engine.current.removeRows(tr);
			engine.selectedKeyframes.clear();
		}
		if (e.getKeyChar() == 'c') {
			clipboardTime = Double.POSITIVE_INFINITY;
			clipboardKeyframes.clear();
			for (Keyframe k : engine.selectedKeyframes) {
				clipboardKeyframes.add(k);
				clipboardTime = Math.min(clipboardTime, k.time);
			}
		}
		if (e.getKeyChar() == 'v') {
			ArrayList<Keyframe> news = new ArrayList<Keyframe>();
			for (Keyframe k : clipboardKeyframes) {
				Keyframe newKeyframe = k.lerp(k, 0);
				newKeyframe.time += engine.curTime-clipboardTime;
				engine.current.add(newKeyframe);
				news.add(newKeyframe);
			}
			engine.selectedKeyframes = news;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			indicator -= 0.5D;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			indicator += 0.5D;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			yIndicator -= 15;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			yIndicator += 15;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			freeMove = true;
		}
		
		refreshGraphics();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			freeMove = false;
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			engine.selectedKeyframes.clear();
			refreshGraphics();
			return;
		}
		for (int i = 0; i < engine.current.keyrows(); i++) {
			int y = rowToY(i)+20;
			KeyRow kr = engine.current.get(i);
			for (int j = 0; j < kr.keyframes(); j++) {
				Keyframe k = kr.get(j);
				int x = timeToSX(k.time);
				/*System.out.println(x + ", " + y + " : " + e.getX() + " , " + e.getY() + " = " + 
						Math.abs(e.getX()-x) + ", " + Math.abs(e.getY()-y));*/
				if (Math.abs(e.getX()-x) < 14 &&
						Math.abs(e.getY()-y) < 10) {
					if (engine.selectedKeyframes.contains(k))
						engine.selectedKeyframes.remove(k);
					else
						engine.selectedKeyframes.add(k);
					refreshGraphics();
					break;
				}
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		dragBx = 0;
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}