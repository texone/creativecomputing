package cc.creativecomputing.math.interpolate;

public enum CCInterpolators {
	
	LINEAR(new CCLinearInterpolator()),
	CUBIC(new CCCubicInterpolator()),
	HERMITE(new CCHermiteInterpolator());

	private final CCInterpolator _myInterpolator;
	
	private CCInterpolators(final CCInterpolator theInterpolator){
		_myInterpolator = theInterpolator;
	}
	
	public double blend(double theV0, double theV1, double theV2, double theV3, double theBlend){
		return _myInterpolator.interpolate(theV0, theV1, theV2, theV3, theBlend);
	}
	
	public CCInterpolator interpolator(){
		return _myInterpolator;
	}
}
