package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;

public class CCSubStepQuantizer implements CCQuantizer{
	private int _myRaster;
	
	public CCSubStepQuantizer(int theRaster){
		_myRaster = theRaster;
	}

	@Override
	public double quantize(CCTransportController theTransport, double theTime) {
		if(_myRaster <= 0)return 0;
		return theTransport.rulerInterval().quantize(theTime, _myRaster);
	}

	@Override
	public int drawRaster(CCTransportController theTransport) {
		return _myRaster;
	}

}