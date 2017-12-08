package cc.creativecomputing.math.color;

import cc.creativecomputing.math.CCColor;

public interface CCColorMode {

	public CCColor toRGB  (double...args);
	
	public double[] fromRGB(CCColor theColor);
}
