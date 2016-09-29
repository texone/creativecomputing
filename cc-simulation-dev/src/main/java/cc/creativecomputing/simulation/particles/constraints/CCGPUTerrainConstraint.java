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

import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Use the height
 * @author Christian Riekoff
 *
 */
public class CCGPUTerrainConstraint extends CCGPUConstraint{
	
	private CCTexture _myTexture;
	private CCVector3f _myScale;
	private CCVector3f _myOffset;
	
	private CGparameter _myTextureParameter;
	private CGparameter _myTextureSizeParameter;
	private CGparameter _myScaleParameter;
	private CGparameter _myOffsetParameter;
	private CGparameter _myExponentParameter;
	
	public CCGPUTerrainConstraint(
		final CCTexture theTexture, final CCVector3f theScale, final CCVector3f theOffset,
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		super("TerrainConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myTexture = theTexture;
		_myScale = theScale;
		_myOffset = theOffset;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myTextureParameter = parameter("texture");
		_myTextureSizeParameter = parameter("textureSize");
		_myScaleParameter = parameter("scale");
		_myOffsetParameter = parameter("offset");
		_myExponentParameter = parameter("exponent");
		
		texture(_myTexture);
		textureScale(_myScale);
		textureOffset(_myOffset);
		exponent(1f);
	}
	
	public void texture(final CCTexture theTexture) {
		_myVelocityShader.texture(_myTextureParameter, theTexture.id());
		_myVelocityShader.parameter(_myTextureSizeParameter, theTexture.width(), theTexture.height());
	}
	
	public void textureScale(final CCVector3f theTextureScale) {
		_myVelocityShader.parameter(_myScaleParameter, theTextureScale);
	}
	
	public void textureScale(final float theXScale, final float theYScale, final float theZScale){
		_myVelocityShader.parameter(_myScaleParameter, theXScale, theYScale, theZScale);
	}
	
	public void textureOffset(final CCVector3f theTextureOffset) {
		_myVelocityShader.parameter(_myOffsetParameter, theTextureOffset);
	}
	
	public void textureOffset(final float theXOffset, final float theYOffset, final float theZOffset) {
		_myVelocityShader.parameter(_myOffsetParameter, theXOffset, theYOffset, theZOffset);
	}
	
	/**
	 * Sets the exponent to change the values of the height map. All pixel values
	 * are read as brightness between 0 and 1. Where 0 is low and 1 is high. Setting 
	 * the exponent you can have different increases in height. The default value is 1.
	 * @param theExponent exponent to control the height increase
	 */
	public void exponent(final float theExponent){
		_myVelocityShader.parameter(_myExponentParameter, theExponent);
	}
}
