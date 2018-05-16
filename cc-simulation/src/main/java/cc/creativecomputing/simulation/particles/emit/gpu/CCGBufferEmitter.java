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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.math.CCMatrix4x4;

public class CCGBufferEmitter extends CCEmitter{
	
	protected String _myInverseViewMatrixParameter;
	
	protected String _myGeometryTextureParameter;
	protected String _myGeometryColorTextureParameter;
	protected String _myGeometryDepthTextureParameter;
	protected String _myGBufferSizeParameter;

	protected String _myEmitPropabilityParameter;
	protected String _myMinLifeTimeParameter;
	protected String _myMaxLifeTimeParameter;
	protected String _myLifeTimeSpreadPowParameter;
	
	private CCGeometryBuffer _myGBBuffer;
	
	private CCMatrix4x4 _myInverseViewMatrix;
	
	@CCProperty(name = "min lifetime", min = 0, max = 30)
	private double _cMinLifeTime = 1;
	@CCProperty(name = "max lifetime", min = 0, max = 30)
	private double _cMaxLifeTime = 1;
	@CCProperty(name = "lifetime spread pow", min = 0, max = 10)
	private double _cLifeTimeSpreadPow = 1;
	
	public CCGBufferEmitter(CCGeometryBuffer theGBuffer){
		super("gBuffer");
		
		_myInverseViewMatrixParameter = parameter("inverseView");
		_myGeometryTextureParameter = parameter("geometryTexture");
		_myGeometryColorTextureParameter = parameter("geometryColorTexture");
		_myGeometryDepthTextureParameter = parameter("geometryDepthTexture");
		
		_myGBufferSizeParameter = parameter("gBufferSize");
		_myEmitPropabilityParameter = parameter("emitProb");
		_myMaxLifeTimeParameter = parameter("maxLifeTime");
		_myMinLifeTimeParameter = parameter("minLifeTime");
		_myLifeTimeSpreadPowParameter = parameter("lifeTimeSpreadPow");
		
		_myGBBuffer = theGBuffer;
		_myInverseViewMatrix = new CCMatrix4x4();
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myGeometryTextureParameter, _myGBBuffer.positions());
		_myShader.setTextureUniform(_myGeometryColorTextureParameter, _myGBBuffer.colors());
		_myShader.setTextureUniform(_myGeometryDepthTextureParameter, _myGBBuffer.depth());
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		
		_myShader.uniform1f(_myMinLifeTimeParameter, _cMinLifeTime);
		_myShader.uniform1f(_myMaxLifeTimeParameter, _cMaxLifeTime);
		_myShader.uniform1f(_myLifeTimeSpreadPowParameter, _cLifeTimeSpreadPow);
		_myShader.uniform2f(_myGBufferSizeParameter, _myGBBuffer.width(), _myGBBuffer.height());
		_myShader.uniformMatrix4f(_myInverseViewMatrixParameter, _myInverseViewMatrix);
	}
	
	public void inverseViewMatrix(CCMatrix4x4 theMatrix){
		_myInverseViewMatrix = theMatrix;
	}
	
	@Override
	public void update(CCAnimator theDeltaTime) {
		
	}

}
