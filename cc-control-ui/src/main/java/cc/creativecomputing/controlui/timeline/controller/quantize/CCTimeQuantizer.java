package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.TransportController;
import cc.creativecomputing.math.CCMath;

public class CCTimeQuantizer implements CCQuantizer {

	private double _myRaster;

	public CCTimeQuantizer(double theRaster) {
		_myRaster = theRaster;
	}

	@Override
	public double quantize(TransportController theTransport, double theTime) {
		if (_myRaster <= 0)
			return theTime;
		return CCMath.round(theTime / _myRaster) * _myRaster;
	}

	@Override
	public int drawRaster(TransportController theTransport) {
		return (int) (theTransport.rulerInterval().interval() / _myRaster);
	}

}