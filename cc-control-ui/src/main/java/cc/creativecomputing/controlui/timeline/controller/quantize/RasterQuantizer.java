package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.math.CCMath;

public class RasterQuantizer implements Quantizer{
	
	private float _myRaster;
	
	public RasterQuantizer(float theRaster){
		_myRaster = theRaster;
	}

	@Override
	public double quantize(double theTime) {
		if(_myRaster <= 0)return theTime;
		return CCMath.round(theTime / _myRaster) * _myRaster;
	}

	@Override
	public int drawRaster() {
		return 0;
	}
	
}