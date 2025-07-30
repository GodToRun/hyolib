package hyonema;

import java.io.Serializable;

import hyonema.scene.Constants;

public class Sound implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 12;
	private double volume = 1.0D;
	private String path;
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Sound() {
		volume = 1.0D;
	}
}
