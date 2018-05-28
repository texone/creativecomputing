/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.simulation.particles.emit.gpu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram.CCGLTextureUniform;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.emit.CCIParticleEmitter;

public class CCParticleGPUEmitter implements CCIParticleEmitter{
	
	@CCProperty(name = "shader")
	private CCParticleEmitShader _myEmitShader;
	protected String _myVelocityTextureParameter;
	protected String _myPositionTextureParameter;
	protected String _myInfoTextureParameter;

	protected String _myEmitPropabilityParameter;
	protected String _myRandomSeedParameter;
	
	private CCParticles _myParticles;
	

	@CCProperty(name = "emitter")
	private Map<String, CCEmitter> _myEmitterMap = new LinkedHashMap<>();
	
	public CCParticleGPUEmitter(final CCGraphics g, CCParticles theParticles, List<CCEmitter> theEmitter){

		_myParticles = theParticles;
		
		for(CCEmitter myEmitter:theEmitter) {
			myEmitter.setSize(g, theParticles.width(), theParticles.height());
			_myEmitterMap.put(myEmitter.parameter("emitter"), myEmitter);
		}
		
		_myPositionTextureParameter = "positionTexture";
		_myInfoTextureParameter = "infoTexture";
		_myVelocityTextureParameter = "velocityTexture";
		_myRandomSeedParameter = "randomSeed";
		_myEmitPropabilityParameter = "emitProb";
		

		_myEmitShader = new CCParticleEmitShader(theParticles, theEmitter);
		_myEmitShader.setTextureUniform("positionTexture", _myParticles.dataBuffer().attachment(0));
		_myEmitShader.setTextureUniform("infoTexture", _myParticles.dataBuffer().attachment(1));
		_myEmitShader.setTextureUniform("velocityTexture", _myParticles.dataBuffer().attachment(2));
		_myEmitShader.setTextureUniform("colorTexture", _myParticles.dataBuffer().attachment(3));
	}
	
	
	@Override
	public void setData(CCGraphics g) {
		for(CCEmitter myEmitter:_myEmitterMap.values()) {
			myEmitter.preDisplay(g);
		}
		
		_myEmitShader.start();
		int myTextureUnit = 0;
		for(CCGLTextureUniform myTextureUniform:_myEmitShader.textures()){
			if(myTextureUniform.texture == null)continue;
				
			g.texture(myTextureUnit, myTextureUniform.texture);
			_myEmitShader.uniform1i(myTextureUniform.parameter, myTextureUnit);
			myTextureUnit++;
		}
		_myEmitShader.uniform2f(_myRandomSeedParameter, CCMath.random(-10000,10000), CCMath.random(-10000,10000));
		_myParticles.destinationDataTexture().draw(g);
		g.noTexture();
		_myEmitShader.end();
		
		_myParticles.swapDataTextures();
	}
	
	public void positions(final CCShaderBuffer thePositionTexture){
		
	}
	
	@Override
	public void reset() {
	}

	@Override
	public void update(CCAnimator theDeltaTime) {
		
	}

}
