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
package cc.creativecomputing.simulation.particles;

import java.nio.file.Path;
import java.util.List;

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
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.impulses.CCImpulse;



/**
 * @invisible
 * @author info
 *
 */
@SuppressWarnings("unused")
public class CCParticlesUpdateShader extends CCGLProgram{

	protected String _myStaticPositionBlendParameter;
	protected String _myDeltaTimeParameter;
	
	protected String _myForcesParameter;
	protected String _myConstraintsParameter;
	protected String _myImpulsesParameter;
	
	private List<CCForce> _myForces;
	private List<CCConstraint> _myConstraints;
	
	private CCTexture2D _myRandomTexture;
	
	protected CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces , 
		final List<CCConstraint> theConstraints,
		final List<CCImpulse> theImpulses,
		final Path[] theShaderFile,
		final int theWidth,
		final int theHeight
	){
		super();

		CCShaderSourceTemplate shaderSource = CCGLShader.buildSourceObject(theShaderFile);
		
		int myIndex = 0;
		
		StringBuffer myForcesBuffer = new StringBuffer();
		StringBuffer myForcesApplyBuffer = new StringBuffer();
		
		_myForces = theForces;
		for(CCForce myForce:_myForces){
			myForce.index(myIndex);
			myForce.setShader(this);
			myForce.setParticles(theParticles);
			myForcesBuffer.append(myForce.shaderSource());
			myForcesApplyBuffer.append("	acceleration = acceleration + ");
			myForcesApplyBuffer.append(myForce.parameter("function"));
			myForcesApplyBuffer.append("(position,velocity,infos,groupInfos,texID,deltaTime) * lifeTimeBlend(infos, groupInfos, ");
			myForcesApplyBuffer.append(myForce.parameter("index"));
			myForcesApplyBuffer.append(");\n");
			
			myIndex++;
		}
		shaderSource.setDefine("noise", CCGLShaderUtil.source);
		shaderSource.setDefine("forces", myForcesBuffer.toString());
		shaderSource.setApply("forces", myForcesApplyBuffer.toString());
		
		StringBuffer myConstraintBuffer = new StringBuffer();
		StringBuffer myConstraintApplyBuffer = new StringBuffer();
		
		_myConstraints = theConstraints;
		for(CCConstraint myConstraint:_myConstraints){
			myConstraint.setShader(this);
			myConstraint.setParticles(theParticles);
			myConstraintBuffer.append(myConstraint.shaderSource());
			myConstraintApplyBuffer.append("	velocity = " + myConstraint.parameter("function") + "(velocity, position,texID, deltaTime);");
		}
		
		shaderSource.setDefine("constraints", myConstraintBuffer.toString());
		shaderSource.setApply("constraints", myConstraintApplyBuffer.toString());
		
		CCLog.info(shaderSource.source());
		init(null, null, shaderSource.source());
		
		
		_myRandomTexture = new CCTexture2D(CCGLShaderUtil.randomRGBAData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		int myImpulseIndex = 0;
		for(CCImpulse myImpulse:theImpulses){
//			myImpulse.setShader(this, myImpulseIndex++, theWidth, theHeight);
		}
		
		_myStaticPositionBlendParameter = "staticPositionBlend";
		_myDeltaTimeParameter = "deltaTime";
		
		setTextureUniform(CCGLShaderUtil.textureUniform, _myRandomTexture);
	}
	
	public CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces, 
		final List<CCConstraint> theConstrains,
		final List<CCImpulse> theImpulses,
		final int theWidth,
		final int theHeight
	){
		this(
			theParticles,
			theGraphics, 
			theForces, 
			theConstrains, 
			theImpulses,
			new Path[] {
//				CCNIOUtil.classPath(CCGPUUpdateShader.class,"shader/simplex.fp"),
//				CCNIOUtil.classPath(CCGPUUpdateShader.class,"shader/forces.fp"),
//				CCNIOUtil.classPath(CCGPUUpdateShader.class,"shader/constraints.fp"),
//				CCNIOUtil.classPath(CCGPUUpdateShader.class,"shader/impulses.fp"),
				CCNIOUtil.classPath(CCParticlesUpdateShader.class,"update.glsl")
			},
			theWidth, theHeight
		);
	}
	
	private double _myStaticPositionBlend = 0;
	
	public void staticPositionBlend(float thePositionBlend){
		_myStaticPositionBlend = thePositionBlend;
	}
	
	private double _myDeltaTime;
	
	public void deltaTime(final double theDeltaTime){
		_myDeltaTime = theDeltaTime;
	}
	
	public CCTexture2D randomTexture(){
		return _myRandomTexture;
	}
	
	@Override
	public void start() {
		super.start();

		uniform1f(_myDeltaTimeParameter, _myDeltaTime);
		uniform1f(_myStaticPositionBlendParameter, _myStaticPositionBlend);
		
		for(CCForce myForce:_myForces){
			myForce.setUniforms();
		}
		
		for(CCConstraint myConstraint:_myConstraints){
			myConstraint.setUniforms();
		}
	}
}
