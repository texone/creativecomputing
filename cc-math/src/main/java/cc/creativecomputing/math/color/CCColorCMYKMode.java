package cc.creativecomputing.math.color;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCColorCMYKMode implements CCColorMode{

	@Override
	public CCColor toRGB(double... args) {
		double c = args[0];
		double m = args[1];
		double y = args[2];
		double k = args[3];
		double alpha =  args.length > 4 ? args[4] : 1;
		
		if (k == 1) return new CCColor(0,0,0,alpha);
		
		double r = c >= 1 ? 0 : 255 * (1-c) * (1-k);
		double g = m >= 1 ? 0 : 255 * (1-m) * (1-k);
		double b = y >= 1 ? 0 : 255 * (1-y) * (1-k);
		
		return new CCColor(r,g,b,alpha);
	}

	@Override
	public double[] fromRGB(CCColor theColor) {
		double k = 1 - CCMath.max(theColor.r,theColor.g,theColor.b);
		double f = k < 1 ? 1 / (1-k) : 0;
		double c = (1-theColor.r-k) * f;
		double m = (1-theColor.g-k) * f;
		double y = (1-theColor.b-k) * f;
		
		return new double[]{c,m,y,k};
	}

}
