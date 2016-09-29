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
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

public class CCGPUTimedTextureForceBlend extends CCForce{
	
	private CCShaderBuffer _myOldBlends;
	private CCShaderBuffer _myBlends;
	private CCTexture2D _myTexture;
	private CCVector2f _myTextureScale;
	private CCVector2f _myTextureOffset;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myBlendParameter;
	
	private CCForce _myForce1;
	private CCForce _myForce2;
	
	private CCCGShader _myBlendShader;
	private CGparameter _myPositionsTextureParameter;
	private CGparameter _myBlendTextureParameter;
	private CGparameter _myOldBlendsParameter;

	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;

	private CGparameter _myDeltaTimeParameter;
	private CGparameter _myPowerParameter;
	
	private float _cPower = 1;
	
	private float _cBlend = 1;
	
	public CCGPUTimedTextureForceBlend(
		final CCTexture2D theTexture,
		final CCVector2f theTextureScale,
		final CCVector2f theTextureOffset,
		final CCForce theForce1,
		final CCForce theForce2
	){
		super("IDTextureForceBlend");
		_myTexture = theTexture;
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
		_cBlend = 1;
		
		_myForce1 = theForce1;
		_myForce2 = theForce2;
		
		_myBlendShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class,"shader/timedtextureblend.fp"));
		_myPositionsTextureParameter = _myBlendShader.fragmentParameter("positions");
		_myBlendTextureParameter = _myBlendShader.fragmentParameter("blendTexture");
		_myOldBlendsParameter = _myBlendShader.fragmentParameter("oldBlends");

		_myTextureScaleParameter = _myBlendShader.fragmentParameter("textureScale");
		_myTextureOffsetParameter = _myBlendShader.fragmentParameter("textureOffset");

		_myDeltaTimeParameter = _myBlendShader.fragmentParameter("deltaTime");
		_myBlendShader.load();
	}
	
	public CCGPUTimedTextureForceBlend(
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
		_myBlendParameter = parameter("blend");
		_myPowerParameter = parameter("power");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce1.setSize(theG, theWidth, theHeight);
		_myForce2.setSize(theG, theWidth, theHeight);
		
		_myBlends = new CCShaderBuffer(theWidth, theHeight);
		_myOldBlends = new CCShaderBuffer(theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		CCShaderBuffer myTmp = _myOldBlends;
		_myOldBlends = _myBlends;
		_myBlends = myTmp;
		
		_myBlendShader.start();
		_myBlendShader.texture(_myPositionsTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myBlendShader.texture(_myBlendTextureParameter, _myTexture.id());
		_myBlendShader.texture(_myOldBlendsParameter, _myOldBlends.attachment(0).id());
		_myBlendShader.parameter(_myDeltaTimeParameter, theDeltaTime);
		_myBlendShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myBlendShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myBlends.draw();
		_myBlendShader.end();
		
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myBlends.attachment(0).id());
		_myVelocityShader.parameter(_myBlendParameter, _cBlend);
		_myVelocityShader.parameter(_myPowerParameter, _cPower);
		
		_myForce1.update(theDeltaTime);
		_myForce2.update(theDeltaTime);
	}
	
	public CCShaderBuffer blendTexture() {
		return _myBlends;
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
		_cBlend = theBlend;
	}
}
