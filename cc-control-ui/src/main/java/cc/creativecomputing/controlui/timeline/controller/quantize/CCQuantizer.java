package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;

public interface CCQuantizer{
	public double quantize(CCTransportController theTransport, double theTime);
	
	public int drawRaster(CCTransportController theTransport);
}