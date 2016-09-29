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

public class CCGPUTextureForceBlend extends CCForce{
	
	private CCTexture2D _myTexture;
	private CCVector2f _myTextureScale;
	private CCVector2f _myTextureOffset;
	private float _myBlend;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;
	private CGparameter _myBlendParameter;
	
	private CCForce _myForce1;
	private CCForce _myForce2;
	
	public CCGPUTextureForceBlend(
		final CCTexture2D theTexture,
		final CCVector2f theTextureScale,
		final CCVector2f theTextureOffset,
		final CCForce theForce1,
		final CCForce theForce2
	){
		super("TextureForceBlend");
		_myTexture = theTexture;
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
		_myBlend = 1;
		
		_myForce1 = theForce1;
		_myForce2 = theForce2;
	}
	
	public CCGPUTextureForceBlend(
		final CCTexture2D theTexture,
		final CCForce theForce1,
		final CCForce theForce2
	){
		this(theTexture, new CCVector2f(1,1), new CCVector2f(), theForce1, theForce2);
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
		
		_myForce1.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
		_myForce2.setShader(theParticles,theShader, _myParameterIndex + ".force2",theWidth, theHeight);
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
		
		_myForce1.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
		_myForce2.setShader(theParticles,theShader, _myParameterIndex + ".force2",theWidth, theHeight);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myTextureParameter = parameter("texture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myBlendParameter = parameter("blend");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce1.setSize(theG, theWidth, theHeight);
		_myForce2.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myVelocityShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myVelocityShader.parameter(_myBlendParameter, _myBlend);
		
		_myForce1.update(theDeltaTime);
		_myForce2.update(theDeltaTime);
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
	}
	
	public CCVector2f textureScale() {
		return _myTextureScale;
	}
	
	public CCVector2f textureOffset() {
		return _myTextureOffset;
	}
	
	public void blend(final float theBlend) {
		_myBlend = theBlend;
	}
}
