package cc.creativecomputing.math.interpolate;

import cc.creativecomputing.math.CCMath;

public class CCCatmullroomInterpolator implements CCInterpolator{

	@Override
	public double interpolate(double theV0, double theV1, double theV2, double theV3, double theBlend, double... theparam) {
		return CCMath.catmullRomBlend(theV0, theV1, theV2, theV3, theBlend,theparam.length > 0 ? theparam[0]:0);
	}

}
