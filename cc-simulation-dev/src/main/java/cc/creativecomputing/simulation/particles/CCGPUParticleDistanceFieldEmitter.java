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
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

public class CCGPUParticleDistanceFieldEmitter implements CCGPUParticleEmitter{
	
	private CCCGShader _myEmitShader;
	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;

	protected CGparameter _myEmitPropabilityParameter;
	protected CGparameter _myRandomSeedParameter;
	protected CGparameter _myDirectionOffsetParameter;
	
	private CCParticles _myParticles;
	
	protected CCTexture3D _myTexture;
	private CCVector3f _myTextureScale;
	private CCVector3f _myTextureOffset;

	private CCVector3f _myMinCut = new CCVector3f(0,0,0);
	private CCVector3f _myMaxCut = new CCVector3f(1,1,1);
	
	private float _myMinForce = 0;
	private float _myDirectionOffset = 0;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;
	
	private CGparameter _myMinCutParameter;
	private CGparameter _myMaxCutParameter;
	private CGparameter _myMinForceParameter;
	
	public CCGPUParticleDistanceFieldEmitter(
		CCParticles theParticles, 
		final CCTexture3D theTexture,
		final CCVector3f theTextureScale,
		final CCVector3f theTextureOffset
	){
		_myEmitShader = new CCCGShader(
			null, 
			CCIOUtil.classPath(CCGPUParticleDistanceFieldEmitter.class, "shader/emit/distance_shader_emit.fp")
		);
		_myTexture = theTexture;
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
		

		_myTextureParameter = _myEmitShader.fragmentParameter("texture");
		_myTextureScaleParameter = _myEmitShader.fragmentParameter("textureScale");
		_myTextureOffsetParameter = _myEmitShader.fragmentParameter("textureOffset");
		_myMinCutParameter = _myEmitShader.fragmentParameter("minCut");
		_myMaxCutParameter = _myEmitShader.fragmentParameter("maxCut");
		_myMinForceParameter = _myEmitShader.fragmentParameter("minForce");
		_myDirectionOffsetParameter = _myEmitShader.fragmentParameter("directionOffset");
		
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
		_myEmitShader.texture(_myTextureParameter, _myTexture.id());
		_myEmitShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myEmitShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myEmitShader.parameter(_myMinCutParameter, _myMinCut);
		_myEmitShader.parameter(_myMaxCutParameter, _myMaxCut);
		_myEmitShader.parameter(_myMinForceParameter, _myMinForce);
		_myEmitShader.parameter(_myDirectionOffsetParameter, _myDirectionOffset);
		_myParticles.destinationDataTexture().draw();
		_myEmitShader.end();
		
		_myParticles.swapDataTextures();
	}
	
	public void positions(final CCShaderBuffer thePositionTexture){
		
	}
	
	public void texture(final CCTexture3D theTexture){
		_myTexture = theTexture;
	}
	
	public CCVector3f textureScale() {
		return _myTextureScale;
	}
	
	public CCVector3f textureOffset() {
		return _myTextureOffset;
	}
	
	public CCVector3f minCut(){
		return _myMinCut;
	}
	
	public CCVector3f maxCut(){
		return _myMaxCut;
	}
	
	public void minForce(float theMinForce){
		_myMinForce = theMinForce;
	}
	
	public void directionOffset(float theDirectionOffset){
		_myDirectionOffset = theDirectionOffset;
	}
	
	@Override
	public void reset() {
	}

	@Override
	public void update(float theDeltaTime) {
		
	}

}
