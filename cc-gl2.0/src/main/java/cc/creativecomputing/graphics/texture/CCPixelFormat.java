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

import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * Each pixel is represented by one or more byte. The color layout of these 
 * data elements is specified by one of the pixel formats.
 * @author christian riekoff
 *
 */
public enum CCPixelFormat{
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB(GL.GL_RGB,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RG_INTEGER(GL2.GL_RG_INTEGER,2,0,1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB_INTEGER(GL2.GL_RGB_INTEGER,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA(GL.GL_RGBA,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA_INTEGER(GL2.GL_RGBA_INTEGER,4,0,1,2,3),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR(GL2.GL_BGR,3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR_INTEGER(GL2.GL_BGR_INTEGER,3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA(GL2.GL_BGRA,4,2,1,0,3),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA_INTEGER(GL2.GL_BGRA_INTEGER,4,2,1,0,3),
	/**
	 * Colors are in alpha, blue, green, red order.
	 */
	ABGR(GL2.GL_ABGR_EXT,4,3,2,1,0),
	/**
	 * Each pixel contains a single red component.
	 */
	RED(GL2.GL_RED,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	RED_INTEGER(GL2.GL_RED_INTEGER,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single blue component.
	 */
	BLUE(GL2.GL_BLUE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single blue component.
	 */
	BLUE_INTEGER(GL2.GL_BLUE_INTEGER,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single green component.
	 */
	GREEN(GL2.GL_GREEN,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single green component.
	 */
	GREEN_INTEGER(GL2.GL_GREEN_INTEGER,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA(GL.GL_ALPHA,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE(GL.GL_LUMINANCE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE_INTEGER(GL2.GL_LUMINANCE_INTEGER,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA(GL.GL_LUMINANCE_ALPHA,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA_INTEGER(GL2.GL_LUMINANCE_ALPHA_INTEGER,2,0,1,-1,-1),
	/**
	 * Each pixel contains a single stencil value.
	 */
	STENCIL_INDEX(GL2.GL_STENCIL_INDEX,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT(GL2.GL_DEPTH_COMPONENT,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_STENCIL(GL2.GL_DEPTH_STENCIL,1,0,1,-1,-1),
	COMPRESSED_RGB_S3TC_DXT1_EXT(GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT,3,0,1,2,-1),
	COMPRESSED_RGBA_S3TC_DXT1_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT,4,0,1,2,3),
	COMPRESSED_RGBA_S3TC_DXT3_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,4,0,1,2,3),
	COMPRESSED_RGBA_S3TC_DXT5_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT,4,0,1,2,3);
	
	public static CCPixelFormat getPixelFormat(){
		int[] tmp = new int[1];
		GL2 gl = CCGraphics.currentGL();
		gl.glGetTexLevelParameteriv(GL.GL_TEXTURE_2D, 0, GL2.GL_TEXTURE_INTERNAL_FORMAT, tmp, 0);
		
		return pixelFormat(tmp[0]);
	}
	
	public static CCPixelFormat pixelFormat(int theGLId) {
		switch(theGLId){
		case GL2.GL_BGR:return BGR;
		case GL2.GL_BGRA:return BGRA;
		case GL.GL_RGB:return RGB;
		case GL.GL_RGBA:return RGBA;
		case GL.GL_LUMINANCE:return LUMINANCE;
		case GL.GL_LUMINANCE_ALPHA:return LUMINANCE_ALPHA;
		case GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT:return COMPRESSED_RGB_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT:return COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT:return COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT:return COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		throw new RuntimeException("Unavailable Pixel Format:"+theGLId);
	}
	
	public static CCPixelInternalFormat internalPixelFormat(int theGLId) {
		switch(theGLId){
		case GL2.GL_BGR:return CCPixelInternalFormat.BGR;
		case GL2.GL_BGRA:return CCPixelInternalFormat.BGRA;
		case GL.GL_RGB:return CCPixelInternalFormat.RGB;
		case GL.GL_RGBA:return CCPixelInternalFormat.RGBA;
		case GL.GL_LUMINANCE:return CCPixelInternalFormat.LUMINANCE;
		case GL.GL_LUMINANCE_ALPHA:return CCPixelInternalFormat.LUMINANCE_ALPHA;
		case GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT:return CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT:return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		throw new RuntimeException("Unavailable Pixel Format:"+theGLId);
	}
	
	public final int glID;
	public final int numberOfChannels;
	public final int[] offsets;
	
	CCPixelFormat(final int theGLid, final int theNumberOfBytes, final int theOffset1, final int theOffset2, final int theOffset3, final int theOffset4){
		glID = theGLid;
		numberOfChannels = theNumberOfBytes;
		offsets = new int[4];
		offsets[0] = theOffset1;
		offsets[1] = theOffset2;
		offsets[2] = theOffset3;
		offsets[3] = theOffset4;
	}
}
