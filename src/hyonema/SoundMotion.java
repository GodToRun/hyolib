package hyonema;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import hyolib.Vector3d;
import hyolib.anim.Keyframe;
import hyonema.scene.Constants;
// Name : sound file path
public class SoundMotion extends SceneKeyframe {
	private static final long serialVersionUID = Constants.SRBASE + 15;
	public Sound sound;
	private transient Clip clip;
	transient double pt = 0D;
	public SoundMotion(double time) {
		super(new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), time, "Sound");
	}
	@Override
	public boolean lerpTransform() {
		return false;
	}
	@Override
	public void init(Hyonema hyonema) {
		super.init(hyonema);
		if (sound == null || clip != null) return;
		try {
			clip = AudioSystem.getClip();
	        AudioInputStream inputStream;
			inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(sound.getPath()))));
			clip.open(inputStream);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public void initCt() {
	}
	@Override
	public Keyframe lerp(Keyframe b, double t) {
		Keyframe a = this;
		SoundMotion key = new SoundMotion(a.time + (b.time-a.time)*t);
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
		key.clip = clip;
		
		key.sound = sound;
		//key.pt = pt;
		return key;
	}
	@Override
	public void apply(double t) {
		super.apply(t);
		SoundMotion before = (SoundMotion)beforeLerp;
		double time = t-before.time;
		double ptime = before.pt-before.time;
		if (time > 0 && time*1000000 < clip.getMicrosecondLength()) {
			clip.setMicrosecondPosition((long)(time * 1000000));
			if (!clip.isRunning())
				clip.start();
		}
		else if (t != 0 && clip.isRunning() && time*1000000 >= clip.getMicrosecondLength()) {
			clip.stop();
		}
		if (((time > 0 && ptime < 0) || t == 0) && !clip.isRunning()) {
			clip.setFramePosition(0);
			clip.start();
		}
		
		before.pt = t;
		if (afterLerp != null)
			((SoundMotion)afterLerp).pt = t;
	}
}
