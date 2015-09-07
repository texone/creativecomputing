package cc.creativecomputing.controlui.timeline.controller.quantize;

public interface Quantizer{
	public double quantize(double theTime);
	
	public int drawRaster();
}