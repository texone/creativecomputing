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
package cc.creativecomputing.simulation.particles;

import com.jogamp.opengl.cg.CGparameter;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;

public class CCGPUParticleGBufferEmitter implements CCGPUParticleEmitter{
	
	private CCCGShader _myEmitShader;
	protected CGparameter _myInverseViewMatrixParameter;
	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;
	protected CGparameter _myColorTextureParameter;
	protected CGparameter _myGeometryTextureParameter;
	protected CGparameter _myGeometryColorTextureParameter;
	protected CGparameter _myGBufferSizeParameter;

	protected CGparameter _myEmitPropabilityParameter;
	protected CGparameter _myMinLifeTimeParameter;
	protected CGparameter _myMaxLifeTimeParameter;
	protected CGparameter _myLifeTimeSpreadPowParameter;
	protected CGparameter _myRandomSeedParameter;
	
	private CCParticles _myParticles;
	private CCGeometryBuffer _myGBBuffer;
	
	private CCMatrix4f _myInverseViewMatrix;
	
	public CCGPUParticleGBufferEmitter(CCParticles theParticles, CCGeometryBuffer theGBuffer){
		_myEmitShader = new CCCGShader(
			null, 
			CCIOUtil.classPath(CCGPUParticleGBufferEmitter.class, "shader/emit/gbuffer_emit.fp")
		);
		_myPositionTextureParameter = _myEmitShader.fragmentParameter("positionTexture");
		_myInverseViewMatrixParameter = _myEmitShader.fragmentParameter("inverseView");
		_myInfoTextureParameter = _myEmitShader.fragmentParameter("infoTexture");
		_myVelocityTextureParameter = _myEmitShader.fragmentParameter("velocityTexture");
		_myColorTextureParameter = _myEmitShader.fragmentParameter("colorTexture");
		_myGeometryTextureParameter = _myEmitShader.fragmentParameter("geometryTexture");
		_myGeometryColorTextureParameter = _myEmitShader.fragmentParameter("geometryColorTexture");
		_myGBufferSizeParameter = _myEmitShader.fragmentParameter("gBufferSize");
		_myRandomSeedParameter = _myEmitShader.fragmentParameter("randomSeed");
		_myEmitPropabilityParameter = _myEmitShader.fragmentParameter("emitProb");
		_myMaxLifeTimeParameter = _myEmitShader.fragmentParameter("maxLifeTime");
		_myMinLifeTimeParameter = _myEmitShader.fragmentParameter("minLifeTime");
		_myLifeTimeSpreadPowParameter = _myEmitShader.fragmentParameter("lifeTimeSpreadPow");
		_myEmitShader.load();
		
		_myParticles = theParticles;
		_myGBBuffer = theGBuffer;
		_myInverseViewMatrix = new CCMatrix4f();
	}
	
	public void inverseViewMatrix(CCMatrix4f theMatrix){
		_myInverseViewMatrix = theMatrix;
	}
	
	public void emitPropability(float theEmitPropability){
		_myEmitShader.parameter(_myEmitPropabilityParameter, theEmitPropability);
	}
	
	public void lifeTime(float theLifeTime){
		_myEmitShader.parameter(_myMinLifeTimeParameter, theLifeTime);
		_myEmitShader.parameter(_myMaxLifeTimeParameter, theLifeTime);
	}
	
	public void lifeTime(float theMinLifeTime, float theMaxLifeTime){
		_myEmitShader.parameter(_myMinLifeTimeParameter, theMinLifeTime);
		_myEmitShader.parameter(_myMaxLifeTimeParameter, theMaxLifeTime);
	}
	
	public void lifeTimeSpread(float theLifeTimeSpread){
		_myEmitShader.parameter(_myLifeTimeSpreadPowParameter, theLifeTimeSpread);
	}
	
	@Override
	public void setData(CCGraphics g) {
		_myEmitShader.start();
		_myEmitShader.texture(_myPositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myEmitShader.texture(_myInfoTextureParameter, _myParticles.dataBuffer().attachment(1).id());
		_myEmitShader.texture(_myVelocityTextureParameter, _myParticles.dataBuffer().attachment(2).id());
		_myEmitShader.texture(_myColorTextureParameter, _myParticles.dataBuffer().attachment(3).id());
		_myEmitShader.texture(_myGeometryTextureParameter, _myGBBuffer.positions().id());
		_myEmitShader.texture(_myGeometryColorTextureParameter, _myGBBuffer.colors().id());
		_myEmitShader.parameter(_myRandomSeedParameter, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
		_myEmitShader.parameter(_myGBufferSizeParameter, _myGBBuffer.width(), _myGBBuffer.height());
		_myEmitShader.matrix(_myInverseViewMatrixParameter, _myInverseViewMatrix);
		_myParticles.destinationDataTexture().draw();
		_myEmitShader.end();
		
		_myParticles.swapDataTextures();
	}
	
	public void positions(final CCShaderBuffer thePositionTexture){
		
	}
	
	@Override
	public void reset() {
	}

	@Override
	public void update(float theDeltaTime) {
		
	}

}
