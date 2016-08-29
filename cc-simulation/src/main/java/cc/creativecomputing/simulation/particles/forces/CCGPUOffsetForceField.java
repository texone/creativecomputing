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

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUOffsetForceField extends CCForce{
	
	private float _myNoiseScale;
	private CCVector3f _myNoiseOffset;
	
	private CGparameter _myNoiseScaleParameter;
	private CGparameter _myNoiseOffsetParameter;
	private CGparameter _myNoiseLengthScalesParameter;
	private CGparameter _myNoiseGainsParameter;
	private CGparameter _myOffsetTextureSizeParameter;
	private CGparameter _myOffsetTextureParameter;
	
	private final CCTexture2D _myOffsetTexture;
	
	public CCGPUOffsetForceField(CCTexture2D theOffsetTexture, final float theNoiseScale, final float theStrength, final CCVector3f theNoiseOffset){
		super("OffsetNoiseForceField");
		_myNoiseScale = theNoiseScale;
		_cStrength = theStrength;
		_myNoiseOffset = theNoiseOffset;
		_myOffsetTexture = theOffsetTexture;
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myNoiseScaleParameter = parameter("noiseScale");
		_myNoiseOffsetParameter = parameter("noiseOffset");
		_myNoiseLengthScalesParameter = parameter("noiseLengthScales");
		_myNoiseGainsParameter = parameter("noiseGains");
		_myOffsetTextureSizeParameter = parameter("texSize");
		_myOffsetTextureParameter = parameter("offsets");
		
		_myVelocityShader.parameter1(_myNoiseLengthScalesParameter, 0.4f,0.23f,0.11f);
		_myVelocityShader.parameter1(_myNoiseGainsParameter, 1f,0.5f,0.25f);
		_myVelocityShader.texture(_myOffsetTextureParameter, _myOffsetTexture.id());
		_myVelocityShader.parameter(_myOffsetTextureSizeParameter, _myOffsetTexture.width(), _myOffsetTexture.height());
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myNoiseOffsetParameter, _myNoiseOffset);
		_myVelocityShader.parameter(_myNoiseScaleParameter, _myNoiseScale);
	}
	
	public void noiseOffset(final CCVector3f theNoiseOffset){
		_myNoiseOffset = theNoiseOffset;
	}
	
	public CCVector3f noiseOffset(){
		return _myNoiseOffset;
	}
	
	public void noiseScale(final float theNoiseScale){
		_myNoiseScale = theNoiseScale;
	}
	
	public float noiseScale(){
		return _myNoiseScale;
	}
	
}
