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

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector2;

/**
 * This force creates a texture based force field, every pixel of the
 * texture holds a direction which acts as a force on the particle. 
 * To read out the force from the texture it can be placed and scaled
 * on the xy plane. The xy coords of the particles will than be used to
 * read the force from the texture.
 * @author christian riekoff
 *
 */
public class CCTextureForceField extends CCForce{
	
	protected CCTexture2D _myTexture;
	
	private CCVector2 _myTextureScale;
	private CCVector2 _myTextureOffset;
	
	private String _myTextureParameter;
	private String _myTextureScaleParameter;
	private String _myTextureOffsetParameter;
	private String _myTextureSizeParameter;
	
	public CCTextureForceField(
		final CCTexture2D theTexture,
		final CCVector2 theTextureScale,
		final CCVector2 theTextureOffset
	){
		super("textureForceField");
		_myTexture = theTexture;
		_myTextureScale = new CCVector2(theTextureScale);
		_myTextureOffset = new CCVector2(theTextureOffset);


		_myTextureParameter = parameter("forceFieldTexture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myTextureSizeParameter = parameter("textureSize");
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myTextureParameter, _myTexture);
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform2f(_myTextureScaleParameter, _myTextureScale);
		_myShader.uniform2f(_myTextureOffsetParameter, _myTextureOffset);
		_myShader.uniform2f(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
	}
	
	public boolean addToForceArray(){
		return true;
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
		_myShader.setTextureUniform(_myTextureParameter, theTexture);
	}
	
	public CCTexture2D texture(){
		return _myTexture;
	}
	
	public CCVector2 textureScale() {
		return _myTextureScale;
	}
	
	public CCVector2 textureOffset() {
		return _myTextureOffset;
	}
	
	
}
