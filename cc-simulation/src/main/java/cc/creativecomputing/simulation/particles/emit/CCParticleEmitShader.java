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

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShader;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.CCShaderSourceTemplate;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesUpdateShader;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.impulses.CCImpulse;



/**
 * @invisible
 * @author info
 *
 */
@SuppressWarnings("unused")
public class CCParticleEmitShader extends CCGLProgram{
	
	protected String _myEmitterParameter;
	
	private List<CCEmitter> _myEmitter;
	
	private CCTexture2D _myRandomTexture;
	
	protected CCParticleEmitShader(
		final CCParticles theParticles,
		final List<CCEmitter> theEmitter
	){
		super();

		CCShaderSourceTemplate shaderSource = CCGLShader.buildSourceObject(CCNIOUtil.classPath(CCParticleEmitShader.class,"emit.glsl"));
		
		int myIndex = 0;
		
		StringBuffer myEmitterBuffer = new StringBuffer();
		StringBuffer myEmitterApplyBuffer = new StringBuffer();
		
		_myEmitter = theEmitter;
		for(CCEmitter myEmitter:_myEmitter){
			myEmitter.setShader(this);
			myEmitter.setParticles(theParticles);
			myEmitterBuffer.append(myEmitter.shaderSource());
			
			myEmitterApplyBuffer.append("if(" + myEmitter.parameter("function") + "(position, info, velocity, color, texID, newPosition, newInfo, newVelocity, newColor)){\n");
			myEmitterApplyBuffer.append("	gl_FragData[0] = newPosition;\n");
			myEmitterApplyBuffer.append("	gl_FragData[1] = newInfo;\n");
			myEmitterApplyBuffer.append("	gl_FragData[2] = newVelocity;\n");
			myEmitterApplyBuffer.append("	gl_FragData[3] = newColor;\n");
			myEmitterApplyBuffer.append("	return;\n");
			myEmitterApplyBuffer.append("}\n");
			
			myIndex++;
		}
		shaderSource.setDefine("emitter", myEmitterBuffer.toString());
		shaderSource.setApply("emitter", myEmitterApplyBuffer.toString());
		
		CCLog.info(shaderSource.source());
		init(null, null, shaderSource.source());
		
		
		_myRandomTexture = new CCTexture2D(CCGLShaderUtil.randomRGBAData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		setTextureUniform(CCGLShaderUtil.textureUniform, _myRandomTexture);
	}
	
	private double _myStaticPositionBlend = 0;
	
	private double _myTime;
	
	public void update(CCAnimator theAnimator) {
		_myTime = theAnimator.time();
	}
	
	@Override
	public void start() {
		super.start();

		uniform1f("time", _myTime);
		
		for(CCEmitter myEmitter:_myEmitter){
			myEmitter.setUniforms();
		}
	}
}
