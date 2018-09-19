package cc.creativecomputing.math.interpolate;

import cc.creativecomputing.math.CCMath;

public class CCPowInterpolator implements CCInterpolator{

	private double _myPow;
	
	public CCPowInterpolator(double thePow) {
		_myPow = thePow;
	}
	
	public CCPowInterpolator() {
		_myPow = 1;
	}
	
	@Override
	public double interpolate(double theV0, double theV1, double theV2, double theV3, double theBlend, double... theparam) {
		return CCMath.blend(theV1, theV2, CCMath.pow(theBlend, theparam.length > 0 ? theparam[0] : _myPow));
	}

}
