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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

/**
 * Specifies the number of color components in the texture. 
 * Must be 1, 2, 3, or 4, or one of the following symbolic constants
 * @author christian riekoff
 *
 */
public enum CCPixelInternalFormat{
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA(GL.GL_ALPHA,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA4(GL2.GL_ALPHA4,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA8(GL2.GL_ALPHA8,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA12(GL2.GL_ALPHA12,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA16(GL2.GL_ALPHA16,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	COMPRESSED_ALPHA(GL2.GL_COMPRESSED_ALPHA,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE(GL.GL_LUMINANCE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE4(GL2.GL_LUMINANCE4,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE8(GL2.GL_LUMINANCE8,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE12(GL2.GL_LUMINANCE12,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE16(GL2.GL_LUMINANCE16,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	COMPRESSED_LUMINANCE(GL2.GL_COMPRESSED_LUMINANCE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA(GL.GL_LUMINANCE_ALPHA,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE4_ALPHA4(GL2.GL_LUMINANCE4_ALPHA4,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE6_ALPHA2(GL2.GL_LUMINANCE6_ALPHA2,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE8_ALPHA8(GL2.GL_LUMINANCE8_ALPHA8,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE12_ALPHA4(GL2.GL_LUMINANCE12_ALPHA4,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE12_ALPHA12(GL2.GL_LUMINANCE12_ALPHA12,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE16_ALPHA16(GL2.GL_LUMINANCE16_ALPHA16,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	COMPRESSED_LUMINANCE_ALPHA(GL2.GL_COMPRESSED_LUMINANCE_ALPHA,2,0,1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY(GL2.GL_INTENSITY,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY4(GL2.GL_INTENSITY4,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY8(GL2.GL_INTENSITY8,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY12(GL2.GL_INTENSITY12,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY16(GL2.GL_INTENSITY16,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	COMPRESSED_INTENSITY(GL2.GL_COMPRESSED_INTENSITY,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT(GL2.GL_DEPTH_COMPONENT,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT16(GL.GL_DEPTH_COMPONENT16,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT24(GL.GL_DEPTH_COMPONENT24,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT32(GL.GL_DEPTH_COMPONENT32,1,0,-1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RG(GL2.GL_RG,3,0,1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB(GL.GL_RGB,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB4(GL2.GL_RGB4,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB5(GL2.GL_RGB5,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB8(GL.GL_RGB8,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB10(GL2.GL_RGB10,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB12(GL2.GL_RGB12,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16(GL2.GL_RGB16,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16I(GL2.GL_RGB16I,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16UI(GL2.GL_RGB16UI,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB32I(GL2.GL_RGB32I,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB32UI(GL2.GL_RGB32UI,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	R3_G3_B2(GL2.GL_R3_G3_B2,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	COMPRESSED_RGB(GL2.GL_COMPRESSED_RGB,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA(GL.GL_RGBA,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA2(GL2.GL_RGBA2,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA4(GL.GL_RGBA4,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGB5_A1(GL.GL_RGB5_A1,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA8(GL.GL_RGBA8,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGB10_A2(GL2.GL_RGB10_A2,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA12(GL2.GL_RGBA12,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16(GL2.GL_RGBA16,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16I(GL2.GL_RGBA16I,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16UI(GL2.GL_RGBA16UI,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	COMPRESSED_RGBA(GL2.GL_COMPRESSED_RGBA,4,0,1,2,3),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	SLUMINANCE(GL2.GL_SLUMINANCE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	SLUMINANCE8(GL2.GL_SLUMINANCE8,1,0,-1,-1,-1),/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	SLUMINANCE_ALPHA(GL2.GL_SLUMINANCE_ALPHA,2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	SLUMINANCE8_ALPHA8(GL2.GL_SLUMINANCE8_ALPHA8,2,0,1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	SRGB(GL2.GL_SRGB,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	SRGB8(GL2.GL_SRGB8,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	SRGB_ALPHA(GL2.GL_SRGB_ALPHA,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	SRGB8_ALPHA8(GL2.GL_SRGB8_ALPHA8,4,0,1,2,3),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR(GL2.GL_BGR,3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA(GL2.GL_BGRA,4,2,1,0,3),
	/**
	 * Each pixel contains a single red component.
	 */
	RED(GL2.GL_RED,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	BLUE(GL2.GL_BLUE,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	GREEN(GL2.GL_GREEN,1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single stencil value.
	 */
	STENCIL_INDEX(GL2.GL_STENCIL_INDEX,1,0,-1,-1,-1),
	
	ABGR(GL2.GL_ABGR_EXT,4,3,2,1,0),
	/**
	 * Colors are in red, green, blue order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGB_S3TC_DXT1_EXT(GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT,3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT1_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT3_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT,4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT5_EXT(GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT,4,0,1,2,3),
	
	FLOAT_R16_NV(GL2.GL_FLOAT_R16_NV,1,0,-1,-1,-1),
	FLOAT_RG16_NV(GL2.GL_FLOAT_RG16_NV,2,0, 1,-1,-1),
	FLOAT_RGB16_NV(GL2.GL_FLOAT_RGB16_NV,3,0, 1, 2,-1),
	FLOAT_RGBA16_NV(GL2.GL_FLOAT_RGBA16_NV,4,0, 1, 2, 3),

	FLOAT_R32_NV(GL2.GL_FLOAT_R16_NV,1,0,-1,-1,-1),
	FLOAT_RG32_NV(GL2.GL_FLOAT_R16_NV,2,0, 1,-1,-1),
	FLOAT_RGB32_NV(GL2.GL_FLOAT_R16_NV,3,0, 1, 2,-1),
	FLOAT_RGBA32_NV(GL2.GL_FLOAT_R16_NV,4,0, 1, 2, 3),
	
	LUMINANCE_FLOAT16_ATI(GL2.GL_LUMINANCE_FLOAT16_ATI,1,0,-1,-1,-1),
	LUMINANCE_FLOAT32_ATI(GL2.GL_LUMINANCE_FLOAT32_ATI,2,0, -1,-1,-1),
	
	LUMINANCE_ALPHA_FLOAT16_ATI(GL2.GL_LUMINANCE_ALPHA_FLOAT16_ATI,1, 0, 1,-1,-1),
	LUMINANCE_ALPHA_FLOAT32_ATI(GL2.GL_LUMINANCE_ALPHA_FLOAT32_ATI,2, 0, 1,-1,-1),

	RGB16F(GL2GL3.GL_RGB16F,3,0, 1, 2,-1),
	RGBA16F(GL2.GL_RGBA16F,3,0, 1, 2, 3),

	RGB32F(GL2.GL_RGB32F,3,0, 1, 2,-1),
	RGBA32F(GL2.GL_RGBA32F,3,0, 1, 2, 3),
	RGBA32I(GL2.GL_RGBA32I,3,0, 1, 2, 3),
	RGBA32UI(GL2.GL_RGBA32UI,3,0, 1, 2, 3);

	
	public static CCPixelInternalFormat valueOf(final int theGlID) {
		switch(theGlID){
		case GL.GL_ALPHA : return ALPHA;
		case GL2.GL_ALPHA4 : return ALPHA4;
		case GL2.GL_ALPHA8 : return ALPHA8;
		case GL2.GL_ALPHA12 : return ALPHA12;
		case GL2.GL_ALPHA16 : return ALPHA16;
		
		case GL.GL_LUMINANCE : return LUMINANCE;
		case GL2.GL_LUMINANCE4 : return LUMINANCE4;
		case GL2.GL_LUMINANCE8 : return LUMINANCE8;
		case GL2.GL_LUMINANCE12 : return LUMINANCE12;
		case GL2.GL_LUMINANCE16 : return LUMINANCE16;
		case GL2.GL_COMPRESSED_LUMINANCE : return COMPRESSED_LUMINANCE;
		
		case GL.GL_LUMINANCE_ALPHA : return LUMINANCE_ALPHA;
		case GL2.GL_LUMINANCE4_ALPHA4 : return LUMINANCE4_ALPHA4;
		case GL2.GL_LUMINANCE6_ALPHA2 : return LUMINANCE6_ALPHA2;
		case GL2.GL_LUMINANCE8_ALPHA8 : return LUMINANCE8_ALPHA8;
		case GL2.GL_LUMINANCE12_ALPHA4 : return LUMINANCE12_ALPHA4;
		case GL2.GL_LUMINANCE12_ALPHA12 : return LUMINANCE12_ALPHA12;
		case GL2.GL_LUMINANCE16_ALPHA16 : return LUMINANCE16_ALPHA16;
		case GL2.GL_COMPRESSED_LUMINANCE_ALPHA : return COMPRESSED_LUMINANCE_ALPHA;
		
		case GL2.GL_INTENSITY : return INTENSITY;
		case GL2.GL_INTENSITY4 : return INTENSITY4;
		case GL2.GL_INTENSITY8 : return INTENSITY8;
		case GL2.GL_INTENSITY12 : return INTENSITY12;
		case GL2.GL_INTENSITY16 : return INTENSITY16;
		case GL2.GL_COMPRESSED_INTENSITY : return COMPRESSED_INTENSITY;
		
		case GL2.GL_DEPTH_COMPONENT : return DEPTH_COMPONENT;
		case GL.GL_DEPTH_COMPONENT16 : return DEPTH_COMPONENT16;
		case GL.GL_DEPTH_COMPONENT24 : return DEPTH_COMPONENT24;
		case GL.GL_DEPTH_COMPONENT32 : return DEPTH_COMPONENT32;
		
		case GL.GL_RGB : return RGB;
		case GL2.GL_RGB4 : return RGB4;
		case GL2.GL_RGB5 : return RGB5;
		case GL.GL_RGB8 : return RGB8;
		case GL2.GL_RGB10 : return RGB10;
		case GL2.GL_RGB12 : return RGB12;
		case GL2.GL_RGB16 : return RGB16;
		case GL2.GL_R3_G3_B2 : return R3_G3_B2;
		case GL2.GL_COMPRESSED_RGB : return COMPRESSED_RGB;
		
		case GL.GL_RGBA : return RGBA;
		case GL2.GL_RGBA2 : return RGBA2;
		case GL.GL_RGBA4 : return RGBA4;
		case GL.GL_RGB5_A1 : return RGB5_A1;
		case GL.GL_RGBA8 : return RGBA8;
		case GL2.GL_RGB10_A2 : return RGB10_A2;
		case GL2.GL_RGBA12 : return RGBA12;
		case GL2.GL_RGBA16 : return RGBA16;
		case GL2.GL_COMPRESSED_RGBA : return COMPRESSED_RGBA;
		
		case GL2.GL_SLUMINANCE : return SLUMINANCE;
		case GL2.GL_SLUMINANCE8 : return SLUMINANCE8;
		
		case GL2.GL_SLUMINANCE_ALPHA : return SLUMINANCE_ALPHA;
		case GL2.GL_SLUMINANCE8_ALPHA8 : return SLUMINANCE8_ALPHA8;
		
		case GL2.GL_SRGB : return SRGB;
		case GL2.GL_SRGB8 : return SRGB8;
		
		case GL2.GL_SRGB_ALPHA : return SRGB_ALPHA;
		case GL2.GL_SRGB8_ALPHA8 : return SRGB8_ALPHA8;
		
		case GL2.GL_BGR : return BGR;
		case GL2.GL_BGRA : return BGRA;
		
		case GL2.GL_RED : return RED;
		case GL2.GL_BLUE : return BLUE;
		case GL2.GL_GREEN : return GREEN;
		case GL2.GL_STENCIL_INDEX : return STENCIL_INDEX;
		
		case GL2.GL_ABGR_EXT : return ABGR;
		
		case GL.GL_COMPRESSED_RGB_S3TC_DXT1_EXT : return COMPRESSED_RGB_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT : return COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT : return COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT : return COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		throw new CCTextureException("Unavailable Pixel Format:"+theGlID);
	}
	
	public final int glID;
	public final int numberOfChannels;
	public final int offset1;
	public final int offset2;
	public final int offset3;
	public final int offset4;
	
	CCPixelInternalFormat(final int theGLid, final int theNumberOfChannels, final int theOffset1, final int theOffset2, final int theOffset3, final int theOffset4){
		glID = theGLid;
		numberOfChannels = theNumberOfChannels;
		offset1 = theOffset1;
		offset2 = theOffset2;
		offset3 = theOffset3;
		offset4 = theOffset4;
	}
}
