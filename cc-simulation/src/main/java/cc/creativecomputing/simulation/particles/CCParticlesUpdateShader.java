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
//import cc.creativecomputing.core.logging.CCLog;
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
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.blends.CCBlend;
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

	
	protected String _myForcesParameter;
	protected String _myConstraintsParameter;
	protected String _myImpulsesParameter;
	
	private List<CCForce> _myForces;
	private List<CCBlend> _myBlends;
	private List<CCConstraint> _myConstraints;
	
	private CCTexture2D _myRandomTexture;
	
	protected CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces, 
		final List<CCBlend> theBlends, 
		final List<CCConstraint> theConstraints,
		final List<CCImpulse> theImpulses,
		final Path[] theShaderFile,
		final int theWidth,
		final int theHeight
	){
		super();

		CCShaderSourceTemplate shaderSource = CCGLShader.buildSourceObject(theShaderFile);

		
		StringBuffer myBlendsBuffer = new StringBuffer();
		StringBuffer myBlendsApplyBuffer = new StringBuffer();
		
		_myBlends = theBlends;
		for(CCBlend myBlend:_myBlends){
			myBlend.setShader(this);
			myBlend.setParticles(theParticles);
			myBlendsBuffer.append(myBlend.shaderSource());
			myBlendsApplyBuffer.append("	blendInfo = ");
			myBlendsApplyBuffer.append(myBlend.parameter("function"));
			myBlendsApplyBuffer.append("(thePosition.xyz, theVelocity, theInfos, theGroupInfos, theTexID, theDeltaTime, theBlend);\n");
			
			myBlendsApplyBuffer.append("	myBlend += blendInfo.x * blendInfo.y;\n");
			myBlendsApplyBuffer.append("	myAmount += blendInfo.y;\n");
			myBlendsApplyBuffer.append("	\n");
		}
		
		shaderSource.setDefine("blends", myBlendsBuffer.toString());
		shaderSource.setApply("blends", myBlendsApplyBuffer.toString());
		
		int myIndex = 0;
		
		StringBuffer myForcesBuffer = new StringBuffer();
		StringBuffer myForcesApplyBuffer = new StringBuffer();
		
		_myForces = theForces;
		for(CCForce myForce:_myForces){
			myForce.setShader(this);
			myForce.setParticles(theParticles);
			myForcesBuffer.append(myForce.shaderSource());
			myForcesApplyBuffer.append("	acceleration = acceleration + ");
			myForcesApplyBuffer.append(myForce.parameter("function"));
			myForcesApplyBuffer.append("(position.xyz, velocity, infos, groupInfos, texID, deltaTime) * blend(position.xyz, velocity, infos, groupInfos, texID, deltaTime, ");
			myForcesApplyBuffer.append(myForce.parameter("blend"));
			myForcesApplyBuffer.append(", ");
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
			myConstraintApplyBuffer.append("	velocity = " + myConstraint.parameter("function") + "(velocity, position.xyz,texID, deltaTime);");
		}
		
		shaderSource.setDefine("constraints", myConstraintBuffer.toString());
		shaderSource.setApply("constraints", myConstraintApplyBuffer.toString());
		
		//CCLog.info(shaderSource.source());
		init(null, null, shaderSource.source());
		
		
		_myRandomTexture = new CCTexture2D(CCGLShaderUtil.randomRGBAData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		int myImpulseIndex = 0;
		for(CCImpulse myImpulse:theImpulses){
//			myImpulse.setShader(this, myImpulseIndex++, theWidth, theHeight);
		}
		
		setTextureUniform(CCGLShaderUtil.textureUniform, _myRandomTexture);
	}
	
	public CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces, 
		final List<CCBlend> theBlends, 
		final List<CCConstraint> theConstrains,
		final List<CCImpulse> theImpulses,
		final int theWidth,
		final int theHeight
	){
		this(
			theParticles,
			theGraphics, 
			theForces, 
			theBlends,
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
	
	private CCVector3 _myMoveAll = new CCVector3();
	
	public void moveAll(CCVector3 theMove) {
		_myMoveAll.set(theMove);
	}
	
	@Override
	public void start() {
		super.start();

		uniform1f("deltaTime", _myDeltaTime);
		uniform1f("staticPositionBlend", _myStaticPositionBlend);
		uniform3f("moveAll", _myMoveAll);
		_myMoveAll.set(0,0,0);
		
		for(CCForce myForce:_myForces){
			myForce.setUniforms();
		}
		
		for(CCBlend myBlend:_myBlends) {
			myBlend.setUniforms();
		}
		
		for(CCConstraint myConstraint:_myConstraints){
			myConstraint.setUniforms();
		}
	}
}
