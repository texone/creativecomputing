package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.TransportController;

public interface CCQuantizer{
	public double quantize(TransportController theTransport, double theTime);
	
	public int drawRaster(TransportController theTransport);
}