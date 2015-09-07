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
package cc.creativecomputing.image;

/**
 * Each pixel is represented by one or more byte. The color layout of these 
 * data elements is specified by one of the pixel formats.
 * @author christian riekoff
 *
 */
public enum CCPixelFormat{
	/**
	 * Colors are in red, green order.
	 */
	RG(2,0,1,-1,-1),
	/**
	 * Colors are in red, green order.
	 */
	RG_INTEGER(2,0,1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB_INTEGER(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA_INTEGER(4,0,1,2,3),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR(3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR_INTEGER(3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA(4,2,1,0,3),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA_INTEGER(4,2,1,0,3),
	/**
	 * Colors are in alpha, blue, green, red order.
	 */
	ABGR(4,3,2,1,0),
	/**
	 * Each pixel contains a single red component.
	 */
	RED(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	RED_INTEGER(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single blue component.
	 */
	BLUE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single blue component.
	 */
	BLUE_INTEGER(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single green component.
	 */
	GREEN(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single green component.
	 */
	GREEN_INTEGER(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE_INTEGER(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA_INTEGER(2,0,1,-1,-1),
	/**
	 * Each pixel contains a single stencil value.
	 */
	STENCIL_INDEX(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT(1,0,-1,-1,-1),
	DEPTH_STENCIL(2,0,1,-1,-1),
	COMPRESSED_RGB_S3TC_DXT1_EXT(3,0,1,2,-1),
	COMPRESSED_RGBA_S3TC_DXT1_EXT(4,0,1,2,3),
	COMPRESSED_RGBA_S3TC_DXT3_EXT(4,0,1,2,3),
	COMPRESSED_RGBA_S3TC_DXT5_EXT(4,0,1,2,3);
	
	
	public final int numberOfChannels;
	public final int[] offsets;
	
	private CCPixelFormat(final int theNumberOfBytes, final int theOffset1, final int theOffset2, final int theOffset3, final int theOffset4){
		numberOfChannels = theNumberOfBytes;
		offsets = new int[4];
		offsets[0] = theOffset1;
		offsets[1] = theOffset2;
		offsets[2] = theOffset3;
		offsets[3] = theOffset4;
	}
	
	public CCPixelInternalFormat internalPixelFormat() {
		switch(this){
		case BGR:return CCPixelInternalFormat.BGR;
		case BGRA:return CCPixelInternalFormat.BGRA;
		case RGB:return CCPixelInternalFormat.RGB;
		case RGBA:return CCPixelInternalFormat.RGBA;
		case LUMINANCE:return CCPixelInternalFormat.LUMINANCE;
		case LUMINANCE_ALPHA:return CCPixelInternalFormat.LUMINANCE_ALPHA;
		case COMPRESSED_RGB_S3TC_DXT1_EXT:return CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT1_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT3_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case COMPRESSED_RGBA_S3TC_DXT5_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
		default:
		}
		throw new RuntimeException("Unavailable Pixel Format:"+this);
	}

}
