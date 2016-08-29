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
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;

/**
 * This force considers the position and velocity of a particle and tries to find
 * the nearest free point on a grid. This can be useful to let particles fill a grid
 * of pixels or similar things. Be aware that the grid texture is change in one pass
 * so it is possible that two or more particle get attached to the same grid point
 * inside one pass dependent on the number of particles this should rarely happen though
 * and be barely noticeable. 
 * @author christian riekoff
 *
 */
public class CCGPUNearestGridPositionTargetForce extends CCForce {

	private CGparameter _myTargetPositionTextureParameter;
	private CGparameter _myForceTextureOffsetParameter;
	private CGparameter _myForceTextureScaleParameter;
	private CGparameter _myTargetTimeParameter;

	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;

	private CCCGShader _myInitValueShader;
	
	private CCCGShader _myNearestTargetShader;
	private CGparameter _myGridDimensionParameter;
	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;
	private CGparameter _myLookAheadParameter;
	private CGparameter _myDeltaTimeParameter;
	private CGparameter _myNearestTargetTimeParameter;
	
	private CGparameter _myTargetsTextureParameter;
	private CGparameter _myParticleTargetInfoTextureParameter;
	
	private CGparameter _myParticleInfoTextureParameter;
	private CGparameter _myParticlePositionTextureParameter;
	private CGparameter _myParticleVelocityTextureParameter;
	
	private CGparameter _myUseMaskParameter;
	private CGparameter _myMaskTextureParameter;
	
	private CCShaderBuffer _myCurrentTargetInfos;
	
	private CCShaderBuffer _myCurrentParticleInfos;
	private CCShaderBuffer _myDestinationParticleInfos;
	
	private CCCGShader _myNearestTargetChangeShader;
	private CGparameter _myParticleTargetInfoTexture2Parameter;
	
	private CCMesh _myIndexMesh;
	
	private int _myGridWidth;
	private int _myGridHeight;

	public CCGPUNearestGridPositionTargetForce(final int theGridWidth, final int theGridHeight) {
		super("NearestTargetForce");
		
		_myGridWidth = theGridWidth;
		_myGridHeight = theGridHeight;
	}
	
	public void setMask(final CCTexture theMaskTexture) {
		_myNearestTargetShader.parameter(_myUseMaskParameter, true);
		_myNearestTargetShader.texture(_myMaskTextureParameter, theMaskTexture.id());
	}

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myIndexMesh = new CCVBOMesh(CCDrawMode.POINTS, _myWidth * _myHeight);
		for(int x = 0; x < _myWidth;x++) {
			for(int y = 0; y < _myHeight;y++) {
				_myIndexMesh.addVertex(x, y, 0);
			}
		}
		
		_myCurrentTargetInfos = new CCShaderBuffer(32,4,_myGridWidth, _myGridHeight);
		
		_myCurrentParticleInfos = new CCShaderBuffer(32,4,2,theWidth, theHeight);
		_myCurrentParticleInfos.clear();
		_myDestinationParticleInfos = new CCShaderBuffer(32,4,2,theWidth, theHeight);
		_myDestinationParticleInfos.clear();

		_myInitValueShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class,"shader/initvalue01.fp"));
		_myInitValueShader.load();
		
		_myNearestTargetShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class,"shader/nearestTarget.fp"));
		
		_myGridDimensionParameter = _myNearestTargetShader.fragmentParameter("textureSize");
		_myTextureScaleParameter = _myNearestTargetShader.fragmentParameter("textureScale");
		_myTextureOffsetParameter = _myNearestTargetShader.fragmentParameter("textureOffset");
		_myLookAheadParameter = _myNearestTargetShader.fragmentParameter("lookAhead");
		_myDeltaTimeParameter = _myNearestTargetShader.fragmentParameter("deltaTime");
		_myNearestTargetTimeParameter = _myNearestTargetShader.fragmentParameter("targetTime");
		
		_myTargetsTextureParameter = _myNearestTargetShader.fragmentParameter("targets");
		_myParticleTargetInfoTextureParameter = _myNearestTargetShader.fragmentParameter("particleTargetInfos");
		
		_myParticleInfoTextureParameter = _myNearestTargetShader.fragmentParameter("particleInfos");
		_myParticlePositionTextureParameter = _myNearestTargetShader.fragmentParameter("particlePositions");
		_myParticleVelocityTextureParameter = _myNearestTargetShader.fragmentParameter("particleVelocities");
		
		_myUseMaskParameter = _myNearestTargetShader.fragmentParameter("useMask");
		_myMaskTextureParameter = _myNearestTargetShader.fragmentParameter("mask");
		_myNearestTargetShader.load();
		
		_myNearestTargetShader.parameter(_myGridDimensionParameter, _myGridWidth, _myGridHeight);
		_myNearestTargetShader.parameter(_myTextureScaleParameter, 1, 1);
		_myNearestTargetShader.parameter(_myTextureOffsetParameter, 0, 0);
		
		_myNearestTargetChangeShader = new CCCGShader(
			CCIOUtil.classPath(CCParticles.class,"shader/nearestTargetChange.vp"),
			CCIOUtil.classPath(CCParticles.class,"shader/nearestTargetChange.fp")
		);
		_myParticleTargetInfoTexture2Parameter = _myNearestTargetChangeShader.vertexParameter("particleTargetInfos");
		_myNearestTargetChangeShader.load();
	}

	@Override
	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myTargetPositionTextureParameter = parameter("targetPositionTexture");
		_myForceTextureOffsetParameter = parameter("textureOffset");
		_myForceTextureScaleParameter = parameter("textureScale");
		_myTargetTimeParameter = parameter("targetTime");
		
		targetTime(Float.MAX_VALUE);
	}

	int count = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		
		_myGraphics.noBlend();
		
		_myGraphics.pushAttribute();
//		if(count < 100) {
		_myNearestTargetShader.start();
		_myNearestTargetShader.texture(_myTargetsTextureParameter, _myCurrentTargetInfos.attachment(0).id());
		_myNearestTargetShader.texture(_myParticleTargetInfoTextureParameter, _myCurrentParticleInfos.attachment(0).id());
		
		_myNearestTargetShader.texture(_myParticleInfoTextureParameter, _myParticles.dataBuffer().attachment(1).id());
		_myNearestTargetShader.texture(_myParticlePositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myNearestTargetShader.texture(_myParticleVelocityTextureParameter, _myParticles.dataBuffer().attachment(2).id());
		
		_myDestinationParticleInfos.draw();
		_myNearestTargetShader.end();

		CCShaderBuffer myTemp = _myCurrentParticleInfos;
		_myCurrentParticleInfos = _myDestinationParticleInfos;
		_myDestinationParticleInfos = myTemp;
		
		_myNearestTargetShader.start();
		_myNearestTargetShader.parameter(_myDeltaTimeParameter, theDeltaTime);
		_myNearestTargetShader.texture(_myTargetsTextureParameter, _myCurrentTargetInfos.attachment(0).id());
		_myNearestTargetShader.texture(_myParticleTargetInfoTextureParameter, _myCurrentParticleInfos.attachment(0).id());
		
		_myNearestTargetShader.texture(_myParticleInfoTextureParameter, _myParticles.dataBuffer().attachment(1).id());
		_myNearestTargetShader.texture(_myParticlePositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myNearestTargetShader.texture(_myParticleVelocityTextureParameter, _myParticles.dataBuffer().attachment(2).id());
		
		_myDestinationParticleInfos.draw();
		_myNearestTargetShader.end();

		myTemp = _myCurrentParticleInfos;
		_myCurrentParticleInfos = _myDestinationParticleInfos;
		_myDestinationParticleInfos = myTemp;
		count++;
//		}
//		
		_myVelocityShader.texture(_myTargetPositionTextureParameter, _myCurrentParticleInfos.attachment(0).id());
		
		_myNearestTargetChangeShader.start();
		_myCurrentTargetInfos.beginDraw();
		_myGraphics.clearColor(0);
		_myGraphics.clear();
		_myNearestTargetChangeShader.texture(_myParticleTargetInfoTexture2Parameter, _myCurrentParticleInfos.attachment(0).id());
		_myIndexMesh.draw(_myGraphics);
		_myCurrentTargetInfos.endDraw();
		_myNearestTargetChangeShader.end();

		
		_myGraphics.popAttribute();
		
//		myTemp = _myCurrentTargetInfos;
//		_myCurrentTargetInfos = _myDestinationTargetInfos;
//		_myDestinationTargetInfos = myTemp;
	}
	
	public CCShaderBuffer targetInfos() {
		return _myCurrentTargetInfos;
	}
	
	public CCShaderBuffer particleTargetInfos() {
		return _myCurrentParticleInfos;
	}

	public void textureOffset(final float theX, final float theY) {
		_myNearestTargetShader.parameter(_myTextureOffsetParameter, theX, theY);
		_myVelocityShader.parameter(_myForceTextureOffsetParameter, theX, theY);
	}
	
	public void lookAhead(final float theLookAhead) {
		_myNearestTargetShader.parameter(_myLookAheadParameter, theLookAhead);
	}
	
	public void targetTime(final float theTargetTime) {
		_myVelocityShader.parameter(_myTargetTimeParameter, theTargetTime);
		_myNearestTargetShader.parameter(_myNearestTargetTimeParameter, theTargetTime);
	}
	
	public void textureScale(final float theXScale, final float theYScale) {
		_myNearestTargetShader.parameter(_myTextureScaleParameter, theXScale, theYScale);
		_myVelocityShader.parameter(_myForceTextureScaleParameter, theXScale, theYScale);
	}
}
