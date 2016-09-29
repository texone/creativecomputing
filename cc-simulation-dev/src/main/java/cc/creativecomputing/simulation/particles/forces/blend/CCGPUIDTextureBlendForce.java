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
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

/**
 * This force takes a forces and a texture to define the influence of the force per particle. 
 * The texture needs to be in the same size as the data textures of the particle system.
 * @author christianriekoff
 *
 */
public class CCGPUIDTextureBlendForce extends CCForce{
	
	private CCTexture2D _myTexture;
	
	private float _myBlendRangeStart = 0;
	private float _myBlendRangeEnd = 1;
	private float _myPow = 1;
	
	private CGparameter _myTextureParameter;
	
	private CGparameter _myBlendRangeStartParameter;
	private CGparameter _myBlendRangeEndParameter;
	
	private CGparameter _myPowParameter;
	
	private CCForce _myForce;
	
	public CCGPUIDTextureBlendForce(
		final CCTexture2D theTexture,
		final CCForce theForce
	){
		super("IDTextureBlendForce");
		_myTexture = theTexture;
		
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
		_myTextureParameter = parameter("texture");
		_myBlendRangeStartParameter = parameter("blendRangeStart");
		_myBlendRangeEndParameter = parameter("blendRangeEnd");
		_myPowParameter = parameter("power");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myPowParameter, _myPow);
		_myVelocityShader.parameter(_myBlendRangeStartParameter, _myBlendRangeStart);
		_myVelocityShader.parameter(_myBlendRangeEndParameter, _myBlendRangeEnd);
		_myForce.update(theDeltaTime);
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
	}
	
	public void pow(final float thePow) {
		_myPow = thePow;
	}
	
	public void blendRangeStart(final float theBlendRangeStart) {
		_myBlendRangeStart = theBlendRangeStart;
	}
	
	public void blendRangeEnd(final float theBlendRangeEnd) {
		_myBlendRangeEnd = theBlendRangeEnd;
	}
	
}
