package cc.creativecomputing.math.interpolate;

public enum CCInterpolators {
	
	LINEAR(new CCLinearInterpolator()),
	CUBIC(new CCCubicInterpolator()),
	HERMITE(new CCHermiteInterpolator()),
	SMOOTH_CUBIC(new CCSmoothCubicInterpolator()),
	CATMULLROM(new CCCatmullroomInterpolator()),
	POW(new CCPowInterpolator()),
	QUADRATIC_POW(new CCPowInterpolator(2)),
	CUBIC_POW(new CCPowInterpolator(3)),
	QUARTIC_POW(new CCPowInterpolator(4)),
	QUINTIC_POW(new CCPowInterpolator(5));

	private final CCInterpolator _myInterpolator;
	
	CCInterpolators(final CCInterpolator theInterpolator){
		_myInterpolator = theInterpolator;
	}
	
	public double blend(double theV0, double theV1, double theV2, double theV3, double theBlend){
		return _myInterpolator.interpolate(theV0, theV1, theV2, theV3, theBlend, 0);
	}
	
	public CCInterpolator interpolator(){
		return _myInterpolator;
	}
}
