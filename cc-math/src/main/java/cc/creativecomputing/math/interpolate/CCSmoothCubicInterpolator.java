package cc.creativecomputing.math.interpolate;

import cc.creativecomputing.math.CCMath;

public class CCSmoothCubicInterpolator implements CCInterpolator{

	@Override
	public double interpolate(double theV0, double theV1, double theV2, double theV3, double theBlend, double... theparam) {
		return CCMath.smoothCubicBlend(theV0, theV1, theV2, theV3, theBlend);
	}

}
