package cc.creativecomputing.math.interpolate;

import cc.creativecomputing.math.CCMath;

public class CCCubicInterpolator implements CCInterpolator{

	@Override
	public double interpolate(double theV0, double theV1, double theV2, double theV3, double theBlend) {
		return CCMath.cubicBlend(theV0, theV1, theV2, theV3, theBlend);
	}

}
