package hyolib;

public class IColor {
	public int argb;
	public IColor(boolean a, int argb) {
		if (a)
			this.argb = argb;
		else {
			this.argb = argb | 0xFF000000;
		}
	}
	public IColor(int rgb) {
		this.argb = rgb | 0xFF000000;
	}
	public IColor(int r, int g, int b) {
		argb = packARGB(255, r, g, b);
	}
	public IColor(int a, int r, int g, int b) {
		argb = packARGB(a, r, g, b);
	}
	public static int packARGB(int a, int r, int g, int b) {
	    return ((a & 0xFF) << 24)
	         | ((r & 0xFF) << 16)
	         | ((g & 0xFF) <<  8)
	         |  (b & 0xFF);
	}
}
