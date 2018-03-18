/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCForceField extends CCForce{
	
	@CCProperty(name = "scale", min = 0, max = 10)
	private double _myScale = 1;
	@CCProperty(name = "octaves", min = 0, max = 5)
	private int _cOctaves = 1;
	@CCProperty(name = "gain", min = 0, max = 1)
	private double _cGain = 0.5;
	@CCProperty(name = "lacunarity", min = 0, max = 4)
	private double _cLacunarity = 2;
	
	@CCProperty(name = "speed", min = 0, max = 3)
	private double _cSpeed = 0;
	
	private CCVector3 _myOffset = new CCVector3();
	
	private String _myNoiseScaleParameter;
	private String _myNoiseOffsetParameter;
	
	private String _myOctavesParameter;
	private String _myGainParameter;
	private String _myLacunarityParameter;
	
	public CCForceField(){
		super("NoiseForceField");
		
		_myNoiseScaleParameter = parameter("scale");
		_myNoiseOffsetParameter = parameter("offset");
		
		_myOctavesParameter = parameter("octaves");
		_myGainParameter = parameter("gain");
		_myLacunarityParameter = parameter("lacunarity");
	}
	
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myNoiseOffsetParameter, _myOffset);
		_myShader.uniform1f(_myNoiseScaleParameter, _myScale * 0.01);

		_myShader.uniform1i(_myOctavesParameter, _cOctaves);
		_myShader.uniform1f(_myGainParameter, _cGain);
		_myShader.uniform1f(_myLacunarityParameter, _cLacunarity);
	}
	
	public void offset(final CCVector3 theNoiseOffset){
		_myOffset.set(theNoiseOffset);
	}
	
	public CCVector3 offset(){
		return _myOffset;
	}
	
	public void scale(final double theNoiseScale){
		_myScale = theNoiseScale;
	}
	
	public double scale(){
		return _myScale;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(_cSpeed == 0)return;
		
		_myOffset.z += theAnimator.deltaTime() * _cSpeed;
	}
	
}
