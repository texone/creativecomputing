package cc.creativecomputing.math.color;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCColorLABMode implements CCColorMode{
	
	private static double Kn = 18;

	// D65 standard referent
	private static double Xn = 0.950470;
	private static double Yn = 1;
	private static double Zn = 1.088830;

	private static double t0 = 4d / 29d;
	private static double t1 = 6d / 29d;
	private static double t2 = 3d * t1 * t1;
	private static double t3 = t1 * t1 * t1;
	

	private static double lab_xyz( double t) {
		return t > t1 ? t * t * t : t2 * (t - t0);
	}
	

	private static double xyz_rgb (double r) {
		return 255 * ( r <= 0.00304 ? 12.92 * r : 1.055 * CCMath.pow(r, 1 / 2.4) - 0.055);
	}
		    
	public CCColor toRGB  (double...args) {
    	double l = args[0];
    	double a = args[1];
    	double b = args[2];

    	double y = (l + 16) / 116d;
    	double x = Double.isNaN(a) ? y : y + a / 500d;
    	double z = Double.isNaN(b) ? y : y - b / 200d;

    	y = Yn * lab_xyz(y);
    	x = Xn * lab_xyz(x);
    	z = Zn * lab_xyz(z);

    	CCColor myResult = new CCColor();
    	myResult.r = xyz_rgb (3.2404542) * x - 1.5371385 * y - 0.4985314 * z;  // D65 -> sRGB
    	myResult.g = xyz_rgb (-0.9692660) * x + 1.8760108 * y + 0.0415560 * z;
    	myResult.b = xyz_rgb (0.0556434) * x - 0.2040259 * y + 1.0572252 * z;
    	myResult.a = args.length > 3 ? args[3] : 1;
    	
    	return myResult;
	}
	
	private static double rgb_xyz (double r) {
    	return  (r /= 255) <= 0.04045 ? r / 12.92 : CCMath.pow((r + 0.055) / 1.055, 2.4);
	}
	
	private static double xyz_lab (double t) {
		return t > t3 ? CCMath.pow(t, 1 / 3) : t / t2 + t0;
	}

	public double[] fromRGB(CCColor theColor){
		double r = rgb_xyz (theColor.r);
    	double g = rgb_xyz (theColor.g);
    	double b = rgb_xyz (theColor.b);
    	double x = xyz_lab (0.4124564 * r + 0.3575761 * g + 0.1804375 * b) / Xn;
    	double y = xyz_lab (0.2126729 * r + 0.7151522 * g + 0.0721750 * b) / Yn;
    	double z = xyz_lab (0.0193339 * r + 0.1191920 * g + 0.9503041 * b) / Zn;
    	
    	return new double[]{116 * y - 16, 500 * (x - y), 200 * (y - z)};
	}
}
