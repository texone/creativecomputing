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
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * This force creates a texture based force field, every pixel of the
 * texture holds a direction which acts as a force on the particle. 
 * To read out the force from the texture it can be placed and scaled
 * on the xy plane. The xy coords of the particles will than be used to
 * read the force from the texture.
 * @author christian riekoff
 *
 */
public class CCGPUTexture3DConstraint extends CCGPUConstraint{
	
	protected CCTexture3D _myTexture;
	private CCVector3f _myTextureScale;
	private CCVector3f _myTextureOffset;

	private CCVector3f _myMinCut = new CCVector3f(0,0,0);
	private CCVector3f _myMaxCut = new CCVector3f(1,1,1);
	
	private float _myMinForce = 0;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTextureScaleParameter;
	private CGparameter _myTextureOffsetParameter;
	
	private CGparameter _myMinCutParameter;
	private CGparameter _myMaxCutParameter;
	private CGparameter _myMinForceParameter;
	
	public CCGPUTexture3DConstraint(
		final CCTexture3D theTexture,
		final CCVector3f theTextureScale,
		final CCVector3f theTextureOffset,
		final float theResilience, final float theFriction, final float theMinimalVelocity
	){
		super("Texture3DConstraint", theResilience, theFriction, theMinimalVelocity);
		_myTexture = theTexture;
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		_myTextureParameter = parameter("texture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myMinCutParameter = parameter("minCut");
		_myMaxCutParameter = parameter("maxCut");
		_myMinForceParameter = parameter("minForce");
		
		texture(_myTexture);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myVelocityShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myVelocityShader.parameter(_myMinCutParameter, _myMinCut);
		_myVelocityShader.parameter(_myMaxCutParameter, _myMaxCut);
		_myVelocityShader.parameter(_myMinForceParameter, _myMinForce);
		
	}
	
	public boolean addToForceArray(){
		return true;
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
}
