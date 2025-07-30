package hyonema;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import hyolib.Vector3d;
import hyolib.anim.Keyframe;
import hyonema.scene.Constants;
// Name : number
public class SubMotion extends SceneKeyframe {
	private static final long serialVersionUID = Constants.SRBASE + 14;
	public Subtitle subtitle;
	public int number;
	public double length;
	transient boolean show = false;
	transient Font font;
	public SubMotion(double time) {
		super(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), time, "Subtitle");
	}
	@Override
	public boolean lerpTransform() {
		return false;
	}
	@Override
	public void init(Hyonema engine) {
		super.init(engine);
		font = new Font("굴림", Font.PLAIN, 24);
	}
	@Override
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		SubMotion key = new SubMotion(a.time + (b.time-a.time)*t);
		key.setName(getName());
		key.isLerped = true;
		// Assume that a's functions equals to b's functions
		if (a.functions != null) {
			for (int i = 0; i < a.functions.size(); i++) {
				key.functions.add(a.functions.get(i).lerp(b.functions.get(i), t));
			}
		}
		key.beforeLerp = a;
		key.afterLerp = b;
		key.hyonema = hyonema;
		key.show = show;
		key.font = font;
		
		key.subtitle = subtitle;
		key.number = number;
		key.length = length;
		return key;
	}
	@Override
	public void apply(double t) {
		super.apply(t);
		SubMotion before = (SubMotion)beforeLerp;
		double time = t-beforeLerp.time;
		boolean tl = time > length || time < 0; // true : don't draw
		Graphics g = hyonema.game.engine.frontFramebuffer();
		Graphics2D g2d = (Graphics2D)g;
		if (tl && before.show) {
			hyonema.game.frontQueue = false;
			before.show = false;
			g2d.setComposite(AlphaComposite.Clear);
			g.fillRect(subtitle.getX(), subtitle.getY()-subtitle.getHeight()/2, subtitle.getWidth(), subtitle.getHeight());
			g2d.setComposite(AlphaComposite.SrcOver);
		}
		else if (!tl) {
			g.setColor(new Color(0, 0, 0, 255));
			g.fillRect(subtitle.getX(), subtitle.getY()-subtitle.getHeight()/2, subtitle.getWidth(), subtitle.getHeight());
		}
		hyonema.game.frontQueue = true;
		if (tl) return;
		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(subtitle.getText(), subtitle.getX(), subtitle.getY());
		before.show = true;
	}
	
}
