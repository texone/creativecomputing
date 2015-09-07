package cc.creativecomputing.controlui.timeline.controller.quantize;

public class OffQuantizer implements Quantizer{

	@Override
	public double quantize(double theTime) {
		return theTime;
	}

	@Override
	public int drawRaster() {
		return 0;
	}
	
}