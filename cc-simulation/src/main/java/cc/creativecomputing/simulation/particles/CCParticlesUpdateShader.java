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

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShader;
import cc.creativecomputing.graphics.shader.CCGLShaderNoise;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.CCShaderSource;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.impulses.CCGPUImpulse;



/**
 * @invisible
 * @author info
 *
 */
@SuppressWarnings("unused")
public class CCParticlesUpdateShader extends CCGLProgram{

	protected String _myVelocityTextureParameter;
	protected String _myPositionTextureParameter;
	protected String _myInfoTextureParameter;
	protected String _myColorTextureParameter;
	protected String _myStaticPositionTextureParameter;
	protected String _myNoiseTextureParameter;
	protected String _myStaticPositionBlendParameter;
	protected String _myDeltaTimeParameter;
	protected String _myEnvelopeTextureParameter;
	
	protected String _myForcesParameter;
	protected String _myConstraintsParameter;
	protected String _myImpulsesParameter;
	
	private List<CCForce> _myForces;
	private List<CCConstraint> _myConstraints;
	
	private CCTexture2D _myRandomTexture;
	
	private CCShaderBuffer _myEvelopeData;
	
	private CCGLWriteDataShader _myWriteDataShader;
	
	protected CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces , 
		final List<CCConstraint> theConstraints,
		final List<CCGPUImpulse> theImpulses,
		final Path[] theShaderFile,
		final int theWidth,
		final int theHeight
	){
		super();
		
		_myWriteDataShader = new CCGLWriteDataShader();
		
		_myEnvelopeTextureParameter = "lifeTimeBlends";
		_myEvelopeData = new CCShaderBuffer(100,theForces.size());

		CCShaderSource shaderSource = CCGLShader.buildSourceObject(theShaderFile);
		
		int myIndex = 0;
		
		StringBuffer myForcesBuffer = new StringBuffer();
		StringBuffer myForcesApplyBuffer = new StringBuffer();
		
		_myForces = theForces;
		for(CCForce myForce:_myForces){
			myForce.index(myIndex);
			myForce.setShader(this);
			myForce.setParticles(theParticles);
			myForcesBuffer.append(myForce.shaderSource());
			myForcesApplyBuffer.append("	acceleration = acceleration + " + myForce.parameter("function") + "(position,velocity,texID,deltaTime);\n");
			
			myIndex++;
		}
		shaderSource.setDefine("noise", CCGLShaderNoise.source);
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
		
		
		_myRandomTexture = new CCTexture2D(CCGLShaderNoise.randomData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		int myImpulseIndex = 0;
		for(CCGPUImpulse myImpulse:theImpulses){
//			myImpulse.setShader(this, myImpulseIndex++, theWidth, theHeight);
		}
		
		_myPositionTextureParameter = "positionTexture";
		_myInfoTextureParameter = "infoTexture";
		_myVelocityTextureParameter = "velocityTexture";
		_myColorTextureParameter = "colorTexture";
		_myStaticPositionTextureParameter = "staticPositions";
		_myNoiseTextureParameter = CCGLShaderNoise.textureUniform;
		_myStaticPositionBlendParameter = "staticPositionBlend";
		_myDeltaTimeParameter = "deltaTime";
		
		setTextureUniform(CCGLShaderNoise.textureUniform, _myRandomTexture);
		setTextureUniform(_myEnvelopeTextureParameter, _myEvelopeData.attachment(0));
	}
	
	public CCParticlesUpdateShader(
		final CCParticles theParticles,
		final CCGraphics theGraphics, 
		final List<CCForce> theForces, 
		final List<CCConstraint> theConstrains,
		final List<CCGPUImpulse> theImpulses,
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
	
	public CCTexture2D envelopeTexture(){
		return _myEvelopeData.attachment(0);
	}
	
	public void preDisplay(CCGraphics g){
		for(CCForce myForce:_myForces){
			myForce.preDisplay(g);
		}
		for(CCConstraint myConstraint:_myConstraints){
			myConstraint.preDisplay(g);
		}
		_myEvelopeData.beginDraw();
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(CCForce myForce:_myForces){
			for(int i = 0; i < 100; i++){
				double myVal = myForce.lifetimeBlend().value(i / 100d);
				g.textureCoords4D(0, myVal, myVal, myVal, 1d);
				g.vertex(i + 0.5, myForce.index() + 1);
			}
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		_myEvelopeData.endDraw();
	}
	
	@Override
	public void start() {
		super.start();
		
		int myTextureUnit = 0;
		
		uniform1i(_myPositionTextureParameter, myTextureUnit++);
		uniform1i(_myInfoTextureParameter, myTextureUnit++);
		uniform1i(_myVelocityTextureParameter, myTextureUnit++);
		uniform1i(_myColorTextureParameter, myTextureUnit++);
		uniform1i(_myStaticPositionTextureParameter, myTextureUnit++);
		
		uniform1i(_myNoiseTextureParameter, myTextureUnit++);
		uniform1i(_myEnvelopeTextureParameter, myTextureUnit++);

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
