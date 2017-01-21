package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.TransportController;

public class CCOffQuantizer implements CCQuantizer{

	@Override
	public double quantize(TransportController theTransport, double theTime) {
		return theTime;
	}

	@Override
	public int drawRaster(TransportController theTransport) {
		return 0;
	}
	
}