package cc.creativecomputing.math.color;

import cc.creativecomputing.math.CCColor;

public interface CCColorMode {

	CCColor toRGB(double... args);
	
	double[] fromRGB(CCColor theColor);
}
