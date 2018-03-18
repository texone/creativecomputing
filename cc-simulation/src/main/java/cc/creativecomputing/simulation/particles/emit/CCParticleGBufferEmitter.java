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
package cc.creativecomputing.simulation.particles.emit;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCParticleGBufferEmitter implements CCIParticleEmitter{
	
	private CCGLProgram _myEmitShader;
	protected String _myInverseViewMatrixParameter;
	protected String _myVelocityTextureParameter;
	protected String _myPositionTextureParameter;
	protected String _myInfoTextureParameter;
	protected String _myColorTextureParameter;
	protected String _myGeometryTextureParameter;
	protected String _myGeometryColorTextureParameter;
	protected String _myGBufferSizeParameter;

	protected String _myEmitPropabilityParameter;
	protected String _myMinLifeTimeParameter;
	protected String _myMaxLifeTimeParameter;
	protected String _myLifeTimeSpreadPowParameter;
	protected String _myRandomSeedParameter;
	
	private CCParticles _myParticles;
	private CCGeometryBuffer _myGBBuffer;
	
	private CCMatrix4x4 _myInverseViewMatrix;
	
	public CCParticleGBufferEmitter(CCParticles theParticles, CCGeometryBuffer theGBuffer){
		_myEmitShader = new CCGLProgram(
			null, 
			CCNIOUtil.classPath(CCParticleGBufferEmitter.class, "gbuffer_emit.glsl")
		);
		_myPositionTextureParameter = "positionTexture";
		_myInverseViewMatrixParameter = "inverseView";
		_myInfoTextureParameter = "infoTexture";
		_myVelocityTextureParameter = "velocityTexture";
		_myColorTextureParameter = "colorTexture";
		_myGeometryTextureParameter = "geometryTexture";
		_myGeometryColorTextureParameter = "geometryColorTexture";
		_myGBufferSizeParameter = "gBufferSize";
		_myRandomSeedParameter = "randomSeed";
		_myEmitPropabilityParameter = "emitProb";
		_myMaxLifeTimeParameter = "maxLifeTime";
		_myMinLifeTimeParameter = "minLifeTime";
		_myLifeTimeSpreadPowParameter = "lifeTimeSpreadPow";
		
		_myParticles = theParticles;
		_myGBBuffer = theGBuffer;
		_myInverseViewMatrix = new CCMatrix4x4();
	}
	
	public void inverseViewMatrix(CCMatrix4x4 theMatrix){
		_myInverseViewMatrix = theMatrix;
	}
	
	@CCProperty(name = "emit propability")
	private double _cEmitPropability = 0.001;
	
	public void emitPropability(double theEmitPropability){
		_cEmitPropability = theEmitPropability;
	}
	@CCProperty(name = "min lifetime", min = 0, max = 30)
	private double _cMinLifeTime = 1;
	@CCProperty(name = "max lifetime", min = 0, max = 30)
	private double _cMaxLifeTime = 1;
	
	public void lifeTime(double theLifeTime){
		_cMinLifeTime = theLifeTime;
		_cMaxLifeTime = theLifeTime;
	}
	
	public void lifeTime(double theMinLifeTime, double theMaxLifeTime){
		_cMinLifeTime = theMinLifeTime;
		_cMaxLifeTime = theMaxLifeTime;
	}

	@CCProperty(name = "lifetime spread pow", min = 0, max = 10)
	private double _cLifeTimeSpreadPow = 1;
	
	public void lifeTimeSpread(double theLifeTimeSpread){
		_cLifeTimeSpreadPow = theLifeTimeSpread;
	}
	
	@Override
	public void setData(CCGraphics g) {
		_myEmitShader.start();

		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _myParticles.dataBuffer().attachment(2));
		g.texture(3, _myParticles.dataBuffer().attachment(3));
		g.texture(4, _myGBBuffer.positions());
		g.texture(5, _myGBBuffer.colors());
		
		_myEmitShader.uniform1f(_myEmitPropabilityParameter, _cEmitPropability);
		_myEmitShader.uniform1f(_myMinLifeTimeParameter, _cMinLifeTime);
		_myEmitShader.uniform1f(_myMaxLifeTimeParameter, _cMaxLifeTime);
		_myEmitShader.uniform1f(_myLifeTimeSpreadPowParameter, _cLifeTimeSpreadPow);
		_myEmitShader.uniform3f(_myRandomSeedParameter, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
		_myEmitShader.uniform2f(_myGBufferSizeParameter, _myGBBuffer.width(), _myGBBuffer.height());
		_myEmitShader.uniformMatrix4f(_myInverseViewMatrixParameter, _myInverseViewMatrix);

		_myEmitShader.uniform1i(_myPositionTextureParameter, 0);
		_myEmitShader.uniform1i(_myInfoTextureParameter, 1);
		_myEmitShader.uniform1i(_myVelocityTextureParameter, 2);
		_myEmitShader.uniform1i(_myColorTextureParameter, 3);
		_myEmitShader.uniform1i(_myGeometryTextureParameter, 4);
		_myEmitShader.uniform1i(_myGeometryColorTextureParameter, 5);
		
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
