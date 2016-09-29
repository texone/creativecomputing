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
package cc.creativecomputing.simulation.particles.forces.blend;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


/**
 * this force has an extra texture containing texture coords for every 
 * particle to get a blend value from another texture
 * @author christianriekoff
 *
 */
public class CCGPUTexcoordTextureBlendForce extends CCForce{
	
	private CCTexture2D _myBlendTexture;
	private CCTexture2D _myTexCoordTexture;
	private float _myPower;
	private CCVector2f _myScale = new CCVector2f(1,1);
	private CCVector2f _myOffset = new CCVector2f(0,0);
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTexCoordTextureParameter;
	private CGparameter _myPowerParameter;
	private CGparameter _myScaleParameter;
	private CGparameter _myOffsetParameter;
	
	
	private CCForce _myForce;
	
	public CCGPUTexcoordTextureBlendForce(
		final CCTexture2D theTexture,
		final CCTexture2D theTexCoordTexture,
		final CCForce theForce
	){
		super("TexCoordTextureBlendForce");
		_myBlendTexture = theTexture;
		_myTexCoordTexture = theTexCoordTexture;
		_myPower = 1;
		
		_myForce = theForce;
	}

	@Override
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, int theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = "forces["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(0, 0);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
	}
	
	@Override
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(0, 0);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myTexCoordTextureParameter = parameter("texCoordsTexture");
		_myTextureParameter = parameter("texture");
		_myPowerParameter = parameter("power");
		_myScaleParameter = parameter("scale");
		_myOffsetParameter = parameter("offset");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTexCoordTextureParameter, _myTexCoordTexture.id());
		_myVelocityShader.texture(_myTextureParameter, _myBlendTexture.id());
		_myVelocityShader.parameter(_myPowerParameter, _myPower);
		_myVelocityShader.parameter(_myScaleParameter, _myScale);
		_myVelocityShader.parameter(_myOffsetParameter, _myOffset);
		_myForce.update(theDeltaTime);
	}
	
	public void blendTexture(final CCTexture2D theTexture){
		_myBlendTexture = theTexture;
	}
	
	public void power(final float thePower) {
		_myPower = thePower;
	}
	
	public void scale(final float theScaleX, final float theScaleY) {
		_myScale = new CCVector2f(theScaleX, theScaleY);
	}
	
	public void offset(final float theX, final float theY) {
		_myOffset = new CCVector2f(theX, theY);
	}
}
