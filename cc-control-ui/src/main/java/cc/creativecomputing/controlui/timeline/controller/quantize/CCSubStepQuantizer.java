package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.TransportController;

public class CCSubStepQuantizer implements CCQuantizer{
	private int _myRaster;
	
	public CCSubStepQuantizer(int theRaster){
		_myRaster = theRaster;
	}

	@Override
	public double quantize(TransportController theTransport, double theTime) {
		if(_myRaster <= 0)return 0;
		return theTransport.rulerInterval().quantize(theTime, _myRaster);
	}

	@Override
	public int drawRaster(TransportController theTransport) {
		return _myRaster;
	}

}