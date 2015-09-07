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
package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;

/**
 * @author christianriekoff
 * 
 */
public class CCTextureAttributes {

	protected CCTextureWrap _myTextureWrapS;
	protected CCTextureWrap _myTextureWrapT;

	protected CCTextureFilter _myFilter;
	protected CCTextureMipmapFilter _myMipmapFilter;

	private CCPixelFormat _myPixelFormat;
	protected CCPixelInternalFormat _myPixelInternalFormat;
	
	private CCPixelType _myPixelType;

	protected boolean _myGenerateMipmaps;

	/**
	 * Default constructor, sets the target to \c GL_TEXTURE_2D, wrap to {@link CCTexture.CCTextureWrap#CLAMP_TO_EDGE}, disables mipmapping, 
	 * the internal format to "automatic"
	 */
	public CCTextureAttributes() {
		_myTextureWrapS = CCTextureWrap.CLAMP_TO_EDGE;
		_myTextureWrapT = CCTextureWrap.CLAMP_TO_EDGE;
		_myFilter = CCTextureFilter.LINEAR;
		_myMipmapFilter = CCTextureMipmapFilter.LINEAR;
		
		_myGenerateMipmaps = false;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.RGBA;
		_myPixelType = CCPixelType.FLOAT;
	}

	/**
	 * Enables or disables mipmapping. Default is disabled.
	 * @param theGenerateMipmaps
	 */
	public void generateMipmaps(final boolean theGenerateMipmaps) {
		_myGenerateMipmaps = theGenerateMipmaps;
	}

	/**
	 * Returns whether the texture has mipmapping enabled
	 * @return whether the texture has mipmapping enabled
	 */
	public boolean generateMipmaps() {
		return _myGenerateMipmaps;
	}

	/**
	 * Sets the Texture's internal format. A value of null implies 
	 * selecting the best format for the context.
	 * @param theInternalFormat
	 */
	public void internalFormat(CCPixelInternalFormat theInternalFormat) {
		_myPixelInternalFormat = theInternalFormat;
	}

	/**
	 * Returns the Texture's internal format. A value of null implies automatic 
	 * selection of the internal format based on the context.
	 * @return the Texture's internal format
	 */
	public CCPixelInternalFormat internalFormat() {
		return _myPixelInternalFormat;
	}
	
	/**
	 * Sets the Texture's format. A value of null implies 
	 * selecting the best format for the context.
	 * @param theFormat
	 */
	public void format(CCPixelFormat theFormat) {
		_myPixelFormat = theFormat;
	}

	/**
	 * Returns the Texture's format. A value of null implies automatic 
	 * selection of the internal format based on the context.
	 * @return the Texture's internal format
	 */
	public CCPixelFormat format() {
		return _myPixelFormat;
	}
	
	/**
	 * Sets the Texture's pixeltype.
	 * @param thePixelType
	 */
	public void pixelType(CCPixelType thePixelType) {
		_myPixelType = thePixelType;
	}

	/**
	 * Returns the Texture's pixeltype.
	 * @return the Texture's pixeltype
	 */
	public CCPixelType pixelType() {
		return _myPixelType;
	}

	/**
	 * Sets the wrapping behavior when a texture coordinate falls outside the 
	 * range of [0,1]. 
	 * @param theWrapS
	 * @param theWrapT
	 */
	public void wrap(final CCTextureWrap theWrapS, CCTextureWrap theWrapT) {
		wrapS(theWrapS);
		wrapT(theWrapT);
	}
	
	/**
	 * Sets the wrapping behavior when a texture coordinate falls outside the 
	 * range of [0,1]. 
	 * @param theWrap
	 */
	public void wrap(final CCTextureWrap theWrap) {
		wrapS(theWrap);
		wrapT(theWrap);
	}

	/**
	 * Sets the horizontal wrapping behavior when a texture coordinate falls outside the range of [0,1].
	 * @param theWrapS
	 */
	public void wrapS(CCTextureWrap theWrapS) {
		_myTextureWrapS = theWrapS;
	}

	/**
	 * Returns the horizontal wrapping behavior for the texture coordinates.
	 * @return the horizontal wrapping behavior
	 */
	public CCTextureWrap wrapS() {
		return _myTextureWrapS;
	}

	/**
	 * Sets the verical wrapping behavior when a texture coordinate falls outside the range of [0,1].
	 * @param theWrapT
	 */
	public void wrapT(CCTextureWrap theWrapT) {
		_myTextureWrapT = theWrapT;
	}

	/**
	 * Returns the vertical wrapping behavior for the texture coordinates.
	 * @return the vertical wrapping behavior
	 */
	public CCTextureWrap wrapT() {
		return _myTextureWrapT;
	}

	/**
	 * Sets the filtering behavior when a texture is displayed at a lower or higher resolution than its native resolution.
	 * Default is {@link CCTextureFilter#LINEAR} Possible values are:{@link CCTextureFilter#LINEAR}, {@link CCTextureFilter#NEAREST}
	 * @param theMinFilter filtering behavior
	 */
	public void filter(CCTextureFilter theMinFilter) {
		_myFilter = theMinFilter;
	}

	/**
	 * Returns the texture function, which is used whenever the pixel 
	 * being textured maps to an area greater or smaller than one texture element.
	 * @return the texture filter function
	 */
	public CCTextureFilter filter() {
		return _myFilter;
	}

	/**
	 * Sets the filtering behavior when a texture is displayed at a higher resolution than its native resolution.
	 * Default is {@link CCTextureFilter#LINEAR} Possible values are:{@link CCTextureFilter#LINEAR}, {@link CCTextureFilter#NEAREST}
	 * @param theMipmapFilter filtering behavior when a texture is magnified
	 */
	public void mipmapFilter(CCTextureMipmapFilter theMipmapFilter) {
		_myMipmapFilter = theMipmapFilter;
	}
	
	/**
	 * Returns the texture magnifying function, which is used whenever the pixel 
	 * being textured maps to an area less than or equal to one texture element.
	 * @return the texture magnifying function
	 */
	public CCTextureMipmapFilter mipmapFilter() {
		return _myMipmapFilter;
	}

}
