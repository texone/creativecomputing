/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCNoiseHeightMapForce extends CCForce{
	@CCProperty(name = "noise scale", min = 0, max = 1)
	private double _myNoiseScale = 1;
	@CCProperty(name = "octaves", min = 0, max = 5)
	private int _cOctaves = 1;
	@CCProperty(name = "gain", min = 0, max = 1)
	private double _cGain = 0.5;
	@CCProperty(name = "lacunarity", min = 0, max = 4)
	private double _cLacunarity = 2;

	@CCProperty(name = "height", min = -1000, max = 1000)
	private double _cHeight = 1;
	
	private CCVector3 _myNoiseOffset = new CCVector3();
	
	private String _myNoiseScaleParameter;
	private String _myNoiseOffsetParameter;
	
	private String _myOctavesParameter;
	private String _myGainParameter;
	private String _myLacunarityParameter;
	private String _myHeightParameter;
	
	public CCNoiseHeightMapForce(){
		super("NoiseHeightMap");
		
		_myNoiseScaleParameter = parameter("scale");
		_myNoiseOffsetParameter = parameter("offset");
		
		_myOctavesParameter = parameter("octaves");
		_myGainParameter = parameter("gain");
		_myLacunarityParameter = parameter("lacunarity");
		
		_myHeightParameter = parameter("height");
	}
	
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myNoiseOffsetParameter, _myNoiseOffset);
		_myShader.uniform1f(_myNoiseScaleParameter, _myNoiseScale * 0.01);

		_myShader.uniform1i(_myOctavesParameter, _cOctaves);
		_myShader.uniform1f(_myGainParameter, _cGain);
		_myShader.uniform1f(_myLacunarityParameter, _cLacunarity);

		_myShader.uniform1f(_myHeightParameter, _cHeight);
	}
	
	public void noiseOffset(final CCVector3 theNoiseOffset){
		_myNoiseOffset.set(theNoiseOffset);
	}
	
	public CCVector3 noiseOffset(){
		return _myNoiseOffset;
	}
	
	public void noiseScale(final float theNoiseScale){
		_myNoiseScale = theNoiseScale;
	}
	
	public double noiseScale(){
		return _myNoiseScale;
	}
	

	
	public void height(final double theHeight){
		_cHeight = theHeight;
	}
	
	public double height(){
		return _cHeight;
	}
}
