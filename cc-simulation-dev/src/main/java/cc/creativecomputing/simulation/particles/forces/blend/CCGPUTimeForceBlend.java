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

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


public class CCGPUTimeForceBlend extends CCForce{
	
	private static final int MAX_STATES = 10;
	
	private CGparameter _myStartTimeParameter;
	private CGparameter _myEndTimeParameter;
	
	private CCTexture2D _myBlendInfos;
	private CCTextureData _myBlendInfoData;
	private CGparameter _myBlendInfosParameter;
	
	private float _myStartTime;
	private float _myEndTime;
	
	private CGparameter _myPowerParameter;

	private float _myPower;
	
	private CCForce _myForce1;
	private CCForce _myForce2;
	
	public CCGPUTimeForceBlend(
		final float theStartTime,
		final float theEndTime,
		final CCForce theForce1,
		final CCForce theForce2
	){
		super("TimeForceBlend");
		
		_myStartTime = theStartTime;
		_myEndTime = theEndTime;
		_myPower = 1;
		
		_myForce1 = theForce1;
		_myForce2 = theForce2;
		
		_myBlendInfoData = new CCTextureData(MAX_STATES,1);
		for(int i = 0; i < MAX_STATES;i++) {
			CCColor myColor = new CCColor(0.0f,1.0f,0.0f,0.0f);
			_myBlendInfoData.setPixel(i, 0, myColor);
		}
		_myBlendInfos = new CCTexture2D(_myBlendInfoData, CCTextureTarget.TEXTURE_RECT);
	}

	@Override
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, int theIndex, final int theWidth, final int theHeight) {
		_myVelocityShader = theShader;
		_myParameterIndex = "forces["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(theWidth, theHeight);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce1.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
		_myForce2.setShader(theParticles,theShader, _myParameterIndex + ".force2",theWidth, theHeight);
	}
	
	@Override
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, final String theIndex, final int theWidth, final int theHeight) {
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
		_myStartTimeParameter = parameter("start");
		_myEndTimeParameter = parameter("end");
		
		_myBlendInfosParameter = parameter("blendInfos");
		
		_myPowerParameter = parameter("power");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce1.setSize(theG, theWidth, theHeight);
		_myForce2.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myStartTimeParameter, _myStartTime);
		_myVelocityShader.parameter(_myEndTimeParameter, _myEndTime);
		
		_myVelocityShader.texture(_myBlendInfosParameter, _myBlendInfos.id());
		
		_myVelocityShader.parameter(_myPowerParameter, _myPower);
		
		_myForce1.update(theDeltaTime);
		_myForce2.update(theDeltaTime);
	}
	
	public void startTime(final float theStartTime) {
		_myStartTime = theStartTime;
	}
	
	public void endTime(final float theEndTime) {
		_myEndTime = theEndTime;
	}
	
	public void blend(int theState, final float theMinBlend, final float theMaxBlend) {
		CCColor myData = new CCColor(0f);
		myData.r = theMinBlend;
		myData.g = theMaxBlend;
		
		_myBlendInfoData.setPixel(theState, 0, myData);
		_myBlendInfos.data(_myBlendInfoData);
	}
	
	public void blend(final float theMinBlend, final float theMaxBlend) {
		blend(0, theMinBlend, theMaxBlend);
	}
	
	
	public void power(final float thePower) {
		_myPower = thePower;
	}
}
