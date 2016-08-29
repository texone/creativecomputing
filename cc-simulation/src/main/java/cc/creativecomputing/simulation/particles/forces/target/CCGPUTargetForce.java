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
package cc.creativecomputing.simulation.particles.forces.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;


public class CCGPUTargetForce extends CCForce {

	private List<Object> _myTargetPositionTextures;

	private CGparameter _myTargetPositionTextureParameter;
	private CGparameter _myCenterParameter;
	private CGparameter _myScaleParameter;
	private CGparameter _myLookAheadParameter;
	private CGparameter _myMaxForceParameter;
	private CGparameter _myNearDistanceParameter;
	private CGparameter _myNearMaxForceParameter;

	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;
	
	private float _myScale;

	private CCCGShader _myInitValueShader;

	private int _myCurrentIndex = 0;
	private int _myInitialTargetTextures;

	public CCGPUTargetForce(int theTargetTextures) {
		super("TargetForce");
		_myTargetPositionTextures = new ArrayList<Object>();
		_myInitialTargetTextures = theTargetTextures;
		_myScale = 1f;
	}
	
	public CCGPUTargetForce() {
		this(0);
	}

	@Override
	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myTargetPositionTextureParameter = parameter("targetPositionTexture");
		_myCenterParameter = parameter("center");
		_myScaleParameter = parameter("scale");
		_myLookAheadParameter = parameter("lookAhead");
		_myMaxForceParameter = parameter("maxForce");
		_myNearDistanceParameter = parameter("nearDistance");
		_myNearMaxForceParameter = parameter("nearMaxForce");
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myScaleParameter, _myScale);
	}
	
	public void scale(float theScale){
		_myScale = theScale;
	}
	
	public void lookAhead(float theLookAhead) {
		_myVelocityShader.parameter(_myLookAheadParameter, theLookAhead);
	}
	
	public void maxForce(float theMaxForce) {
		_myVelocityShader.parameter(_myMaxForceParameter, theMaxForce);
	}

	public void center(final CCVector3f theCenter) {
		_myVelocityShader.parameter(_myCenterParameter, theCenter);
	}

	public void center(final float theX, final float theY, final float theZ) {
		_myVelocityShader.parameter(_myCenterParameter, theX, theY, theZ);
	}
	
	public void nearDistance(float theNearMaxDistance) {
		_myVelocityShader.parameter(_myNearDistanceParameter, theNearMaxDistance);
	}
	
	public void nearMaxForce(float theNearMaxForce) {
		_myVelocityShader.parameter(_myNearMaxForceParameter, theNearMaxForce);
	}

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class, "shader/initvalue01.fp"));
		_myInitValueShader.load();
		
		for(int i = 0; i < _myInitialTargetTextures;i++) {
			CCShaderBuffer myTargets = new CCShaderBuffer(16,4,theWidth, theHeight);
			myTargets.beginDraw();
			_myGraphics.clearColor(0,0,0,0);
			_myGraphics.clear();
			myTargets.endDraw();
			_myTargetPositionTextures.add(myTargets);
		}
	}
	
	private void setTargets(CCShaderBuffer theTargetTexture, CCGPUTargetSetup theSetup, int theX, int theY, int theWidth, int theHeight) {
		_myGraphics.pushAttribute();
		_myGraphics.noBlend();
		theTargetTexture.beginDraw();
		_myInitValueShader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
		theSetup.setParticleTargets(_myGraphics, theX, theY, theWidth, theHeight);
		_myGraphics.endShape();

		_myInitValueShader.end();
		theTargetTexture.endDraw();
		_myGraphics.popAttribute();
//		FloatBuffer myData = theTargetTexture.getData(theX, theY, theWidth, theHeight);
//		while(myData.hasRemaining()) {
//			CCLog.info(myData.get()+","+myData.get()+","+myData.get()+","+myData.get());
//		}
	}
	
	private void setTargets(CCShaderBuffer theTargetTexture, CCGPUTargetSetup theSetup, CCGPUIndexParticleEmitter theGroup) {
		_myGraphics.pushAttribute();
		_myGraphics.noBlend();
		theTargetTexture.beginDraw();
		_myInitValueShader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
		theSetup.setParticleTargets(_myGraphics, theGroup);
		_myGraphics.endShape();

		_myInitValueShader.end();
		theTargetTexture.endDraw();
		_myGraphics.popAttribute();
//		FloatBuffer myData = theTargetTexture.getData(theX, theY, theWidth, theHeight);
//		while(myData.hasRemaining()) {
//			CCLog.info(myData.get()+","+myData.get()+","+myData.get()+","+myData.get());
//		}
	}
	
	public void beginSetTargets(int theIndex){
		_myGraphics.pushAttribute();
		_myGraphics.noBlend();
		targetBuffer(theIndex).beginDraw();
		_myInitValueShader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
	}
	
	public void setTarget(CCGPUParticle theParticle,CCVector4f theTarget){
		_myGraphics.textureCoords(0, theTarget.x, theTarget.y, theTarget.z, theTarget.w);
		_myGraphics.vertex(theParticle.x() + 0.5f, theParticle.y() + 0.5f);
	}
	
	public void endSetTargets(int theIndex){
		_myGraphics.endShape();

		_myInitValueShader.end();
		targetBuffer(theIndex).endDraw();
		_myGraphics.popAttribute();
	}
	
	public CCShaderBuffer targetBuffer(int theIndex){
		 return (CCShaderBuffer)_myTargetPositionTextures.get(theIndex);
	}

	public void addTargetSetup(final CCGPUTargetSetup theSetup) {
		CCShaderBuffer myTexture = new CCShaderBuffer(16, 4, _myWidth, _myHeight);
		_myTargetPositionTextures.add(myTexture);
		
		setTargets(myTexture, theSetup, 0, 0, _myWidth, _myHeight);
		
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderBuffer)_myTargetPositionTextures.get(_myCurrentIndex)).attachment(0).id());
			}
		}
	}
	
	public void updateSetup(int theIndex, final CCGPUTargetSetup theSetup, int theX, int theY, int theWidth, int theHeight) {
		CCShaderBuffer myTexture = (CCShaderBuffer)_myTargetPositionTextures.get(theIndex);
		
		setTargets(myTexture, theSetup, theX, theY, theWidth, theHeight);
	}
	
	public void updateSetup(int theIndex, final CCGPUTargetSetup theSetup, CCGPUIndexParticleEmitter theParticleGroup) {
		CCShaderBuffer myTexture = (CCShaderBuffer)_myTargetPositionTextures.get(theIndex);
		
		setTargets(myTexture, theSetup, theParticleGroup);
	}
	
	public void updateSetup(int theIndex, CCGPUTargetSetup theSetup) {
		CCShaderBuffer myTexture = (CCShaderBuffer)_myTargetPositionTextures.get(theIndex);
		
		setTargets(myTexture, theSetup, 0, 0, _myWidth, _myHeight);
	}
	
	public void addTargetSetup(final CCShaderBuffer theShaderTexture) {
		_myTargetPositionTextures.add(theShaderTexture);
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderBuffer)_myTargetPositionTextures.get(_myCurrentIndex)).attachment(0).id());
			}
		}
	}
	
	public void addTargetSetup(final int theTextureID) {
		_myTargetPositionTextures.add(theTextureID);
		if(_myTargetPositionTextures.size() == 1) {
			Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
			if(myObject instanceof Integer) {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
			}else {
				_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderBuffer)_myTargetPositionTextures.get(_myCurrentIndex)).attachment(0).id());
			}
		}
	}

	public void changeSetup(int theIndex) {
		_myCurrentIndex = theIndex;
		Object myObject = _myTargetPositionTextures.get(_myCurrentIndex);
		if(myObject instanceof Integer) {
			_myVelocityShader.texture(_myTargetPositionTextureParameter, (Integer)myObject);
		}else {
			_myVelocityShader.texture(_myTargetPositionTextureParameter, ((CCShaderBuffer)_myTargetPositionTextures.get(_myCurrentIndex)).attachment(0).id());
		}
	}

}
