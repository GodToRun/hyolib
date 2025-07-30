package hyonema;

import java.io.Serializable;

import hyonema.scene.Constants;

public class Subtitle implements Serializable {
	private static final long serialVersionUID = Constants.SRBASE + 11;
	private String text;
	private int x, y, w, h;
	public Subtitle(String text, int x, int y, int w, int h) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public void setWidth(int w) {
		this.w = w;
	}
	public void setHeight(int h) {
		this.h = h;
	}
	public int getWidth() {
		return w;
	}
	public int getHeight() {
		return h;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
