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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.graphics.texture.CCTexture2D;

/**
 * This force creates a texture based force field, every pixel of the
 * texture holds a direction which acts as a force on the particle. 
 * To read out the force from the texture it can be placed and scaled
 * on the xy plane. The xy coords of the particles will than be used to
 * read the force from the texture.
 * @author christian riekoff
 *
 */
public class CCGPUTextureForceField extends CCForce{
	
	public static enum CCGPUTextureForceFieldPlane{
		XY, XZ;
	}
	
	protected CCTexture2D _myTexture;
	private CCVector2f _myTextureScale;
	private CCVector2f _myTextureOffset;
	
	private String _myTextureParameter;
	private String _myTextureScaleParameter;
	private String _myTextureOffsetParameter;
	private String _myTextureSizeParameter;
	
	public CCGPUTextureForceField(
		final CCTexture2D theTexture,
		final CCVector2f theTextureScale,
		final CCVector2f theTextureOffset,
		final CCGPUTextureForceFieldPlane thePlane
	){
		super("TextureForceField"+thePlane);
		_myTexture = theTexture;
		_myTextureScale = theTextureScale;
		_myTextureOffset = theTextureOffset;
	}
	
	public CCGPUTextureForceField(
		final CCTexture2D theTexture,
		final CCVector2f theTextureScale,
		final CCVector2f theTextureOffset
	){
		this(theTexture, theTextureScale, theTextureOffset, CCGPUTextureForceFieldPlane.XY);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myTextureParameter = parameter("texture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myTextureSizeParameter = parameter("textureSize");
		
		texture(_myTexture);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.texture(_myTextureParameter, _myTexture.id());
		_myVelocityShader.parameter(_myTextureScaleParameter, _myTextureScale);
		_myVelocityShader.parameter(_myTextureOffsetParameter, _myTextureOffset);
		_myVelocityShader.parameter(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
		
	}
	
	public boolean addToForceArray(){
		return true;
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
	}
	
	public CCTexture2D texture(){
		return _myTexture;
	}
	
	public CCVector2f textureScale() {
		return _myTextureScale;
	}
	
	public CCVector2f textureOffset() {
		return _myTextureOffset;
	}
	
	
}
