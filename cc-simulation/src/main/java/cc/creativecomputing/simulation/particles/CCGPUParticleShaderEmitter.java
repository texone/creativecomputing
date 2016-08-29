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
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCGPUParticleShaderEmitter implements CCGPUParticleEmitter{
	
	private CCCGShader _myEmitShader;
	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;

	protected CGparameter _myEmitPropabilityParameter;
	protected CGparameter _myRandomSeedParameter;
	
	private CCParticles _myParticles;
	
	public CCGPUParticleShaderEmitter(CCParticles theParticles){
		_myEmitShader = new CCCGShader(
			null, 
			CCIOUtil.classPath(CCGPUParticleShaderEmitter.class, "shader/emit/simple_shader_emit.fp")
		);
		_myPositionTextureParameter = _myEmitShader.fragmentParameter("positionTexture");
		_myInfoTextureParameter = _myEmitShader.fragmentParameter("infoTexture");
		_myVelocityTextureParameter = _myEmitShader.fragmentParameter("velocityTexture");
		_myRandomSeedParameter = _myEmitShader.fragmentParameter("randomSeed");
		_myEmitPropabilityParameter = _myEmitShader.fragmentParameter("emitProb");
		_myEmitShader.load();
		
		_myParticles = theParticles;
	}
	
	public void emitPropability(float theEmitPropability){
		_myEmitShader.parameter(_myEmitPropabilityParameter, theEmitPropability);
	}
	
	@Override
	public void setData(CCGraphics g) {
		_myEmitShader.start();
		_myEmitShader.texture(_myPositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myEmitShader.texture(_myInfoTextureParameter, _myParticles.dataBuffer().attachment(1).id());
		_myEmitShader.texture(_myVelocityTextureParameter, _myParticles.dataBuffer().attachment(2).id());
		_myEmitShader.parameter(_myRandomSeedParameter, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
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
