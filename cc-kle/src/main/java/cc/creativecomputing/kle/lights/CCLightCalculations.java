package cc.creativecomputing.kle.lights;

import cc.creativecomputing.core.CCProperty;

public class CCLightCalculations {

	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 1;
	
	@CCProperty(name = "hsb")
	private boolean _cHSB = false;
	
	public double alpha() {
		return _cAlpha;
	}
	
	public boolean hsb() {
		return _cHSB;
	}
}
