package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;

public class CCOffQuantizer implements CCQuantizer{

	@Override
	public double quantize(CCTransportController theTransport, double theTime) {
		return theTime;
	}

	@Override
	public int drawRaster(CCTransportController theTransport) {
		return 0;
	}
	
}