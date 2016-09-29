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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUMultiTargetForce extends CCForce {

	private CCShaderBuffer _myTargetPositionTexture;

	private CGparameter _myCenterParameter;
	private CGparameter _myLookAheadParameter;
	private CGparameter _myMaxForceParameter;
	private CGparameter _myStateInfosParameter;
	private CGparameter _myStateIDsParameter;

	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;

	private CCCGShader _myInitValueShader;

	private CCShaderBuffer _myStateInfosTexture;
	private CCShaderBuffer _myStateIDsTexture;
	
	public CCGPUMultiTargetForce(int theWidth, int theHeight) {
		super("MultiTargetForce");
		_myTargetPositionTexture = new CCShaderBuffer(16,4,theWidth, theHeight);
	}

	@Override
	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myCenterParameter = parameter("center");
		_myLookAheadParameter = parameter("lookAhead");
		_myMaxForceParameter = parameter("maxForce");
		_myStateInfosParameter = parameter("stateInfoTexture");
		_myStateIDsParameter = parameter("stateIDTexture");
		_myStateInfosTexture = new CCShaderBuffer(16,4,theWidth, theHeight);
		_myStateIDsTexture = new CCShaderBuffer(16,4,theWidth, theHeight);
		_myVelocityShader.texture(_myStateInfosParameter, _myStateInfosTexture.attachment(0).id());
		_myVelocityShader.texture(_myStateIDsParameter, _myStateIDsTexture.attachment(0).id());
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
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

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class, "shader/initvalue01.fp"));
		_myInitValueShader.load();
	}

	public void targetSetup(final CCGPUTargetSetup theSetup) {
		
		_myTargetPositionTexture.beginDraw();
		_myInitValueShader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
		theSetup.setParticleTargets(_myGraphics, 0, 0, _myWidth, _myHeight);
		_myGraphics.endShape();

		_myInitValueShader.end();
		_myTargetPositionTexture.endDraw();
	}
	
	public void updateSetup(CCGPUTargetSetup theSetup) {
		
		_myTargetPositionTexture.beginDraw();
		_myInitValueShader.start();
		_myGraphics.clear();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		theSetup.setParticleTargets(_myGraphics, 0, 0, _myWidth, _myHeight);
		_myGraphics.endShape();

		_myInitValueShader.end();
		_myTargetPositionTexture.endDraw();
	}
	

}
