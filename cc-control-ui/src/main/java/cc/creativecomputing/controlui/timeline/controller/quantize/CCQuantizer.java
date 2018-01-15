package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;

public interface CCQuantizer{
	double quantize(CCTransportController theTransport, double theTime);
	
	int drawRaster(CCTransportController theTransport);
}