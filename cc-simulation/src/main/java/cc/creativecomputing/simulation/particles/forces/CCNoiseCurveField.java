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
import cc.creativecomputing.gl.app.CCGLTimer;

public class CCNoiseCurveField extends CCForce{
	
	@CCProperty(name = "octaves", min = 0, max = 5)
	private int _cOctaves = 1;
	@CCProperty(name = "gain", min = 0, max = 1)
	private double _cGain = 0.5;
	@CCProperty(name = "lacunarity", min = 0, max = 4)
	private double _cLacunarity = 2;
	
	@CCProperty(name = "prediction", min = 0, max = 1)
	private double _myPrediction = 0;
	   
	private double _myOffset = 0;
	@CCProperty(name = "scale", min = 0, max = 1)
	private double _myScale = 1;
	@CCProperty(name = "curveOutputScale", min = 0, max = 200)
	private double _myOutputScale = 1;
	
	@CCProperty(name = "radius", min = 0, max = 400)
	private double _myRadius = 1;
	
	@CCProperty(name = "speed", min = 0, max = 1)
	private double _mySpeed = 1;
	
	private String _myPredictionParameter;
	private String _myOffsetParameter;
	private String _myScaleParameter;
	private String _myOutputScaleParameter;
	private String _myRadiusParameter;
	
	private String _myOctavesParameter;
	private String _myGainParameter;
	private String _myLacunarityParameter;
	
	public CCNoiseCurveField(){
		super("NoiseCurveForceFieldFollow");
		
		_myPredictionParameter = parameter("prediction");
		_myOffsetParameter = parameter("offset");
		_myScaleParameter = parameter("scale");
		_myOutputScaleParameter = parameter("outputScale");
		_myRadiusParameter = parameter("radius");

		_myOctavesParameter = parameter("octaves");
		_myGainParameter = parameter("gain");
		_myLacunarityParameter = parameter("lacunarity");
	}
	
	public void prediction(final double thePrediction) {
		_myPrediction = thePrediction;
	}

	public void scale(double theScale) {
		_myScale = theScale;
	}

	public void outputScale(double theOutputScale) {
		_myOutputScale = theOutputScale;
	}
	
	public void radius(double theRadius) {
		_myRadius = theRadius;
	}

	public void speed(double theSpeed) {
		_mySpeed = theSpeed;
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myOffsetParameter, _myOffset);
		_myShader.uniform1f(_myOutputScaleParameter, _myOutputScale);
		_myShader.uniform1f(_myScaleParameter, _myScale);
		_myShader.uniform1f(_myRadiusParameter, _myRadius);
		_myShader.uniform1f(_myPredictionParameter, _myPrediction);

		_myShader.uniform1i(_myOctavesParameter, _cOctaves);
		_myShader.uniform1f(_myGainParameter, _cGain);
		_myShader.uniform1f(_myLacunarityParameter, _cLacunarity);
	}

	@Override
	public void update(final CCGLTimer theAnimator) {
		_myOffset += theAnimator.deltaTime() * _mySpeed;	
	}
	
}
