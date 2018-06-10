package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;

public class CCSignalSettings{

	@CCProperty(name = "scale", min = 0, max = 10, defaultValue = 1, digits = 4)
	private double _myScale = 1;
	@CCProperty(name = "octaves", min = 1, max = 10, defaultValue = 2)
	private double _myOctaves = 1;
	@CCProperty(name = "gain", min = 0, max = 1, defaultValue = 0.5)
	private double _myGain = 0.5f;
	@CCProperty(name = "lacunarity", min = 0, max = 10, defaultValue = 2)
	private double _myLacunarity = 2;
	@CCProperty(name = "norm")
	private boolean _myIsNormed = true;
	
	public double scale(){
		return _myScale;
	}
	
	public void scale(double theScale){
		_myScale = theScale;
	}
	
	/**
	 * The minimum value is one. You can also set floating numbers to blend
	 * between the result of 2 or three bands.
	 */
	public double octaves(){
		return _myOctaves;
	}
	
	public void octaves(double theOctaves){
		_myOctaves = theOctaves;
	}
	
	/**
	 * Controls amplitude change between each band. The default gain
	 * is 0.5 meaning that the influence of every higher band is half as
	 * high as the one from the previous.
	 */
	public double gain(){
		return _myGain;
	}
	
	/**
	 * Lacunarity controls frequency change between each band. The default value
	 * is 2.0 meaning the frequency of every band is twice as high as the previous
	 */
	public double lacunarity(){
		return _myLacunarity;
	}
	
	public boolean isNormed(){
		return _myIsNormed;
	}
}