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
 * Specifies the number of color components in the texture. 
 * Must be 1, 2, 3, or 4, or one of the following symbolic constants
 * @author christian riekoff
 *
 */
public enum CCPixelInternalFormat{
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA4(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA8(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA12(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	ALPHA16(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single alpha component.
	 */
	COMPRESSED_ALPHA(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE4(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE8(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE12(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	LUMINANCE16(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	COMPRESSED_LUMINANCE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE_ALPHA(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE4_ALPHA4(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE6_ALPHA2(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE8_ALPHA8(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE12_ALPHA4(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE12_ALPHA12(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	LUMINANCE16_ALPHA16(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	COMPRESSED_LUMINANCE_ALPHA(2,0,1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY4(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY8(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY12(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	INTENSITY16(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single intensity component.
	 */
	COMPRESSED_INTENSITY(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT16(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT24(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single depth value.
	 */
	DEPTH_COMPONENT32(1,0,-1,-1,-1),

	/**
	 * Each pixel contains a red and green component.
	 */
	RG(2,0,1,-1,-1),
	
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB4(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB5(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB8(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB10(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB12(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16I(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB16UI(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB32I(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	RGB32UI(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	R3_G3_B2(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	COMPRESSED_RGB(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA2(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA4(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGB5_A1(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA8(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGB10_A2(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA12(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16I(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	RGBA16UI(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	COMPRESSED_RGBA(4,0,1,2,3),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	SLUMINANCE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single luminance (intensity) component.
	 */
	SLUMINANCE8(1,0,-1,-1,-1),/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	SLUMINANCE_ALPHA(2,0,1,-1,-1),
	/**
	 * Each pixel contains a luminance followed by an alpha component.
	 */
	SLUMINANCE8_ALPHA8(2,0,1,-1,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	SRGB(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue order.
	 */
	SRGB8(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	SRGB_ALPHA(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order.
	 */
	SRGB8_ALPHA8(4,0,1,2,3),
	/**
	 * Colors are in blue, green, red order.
	 */
	BGR(3,2,1,0,-1),
	/**
	 * Colors are in blue, green, red, alpha order.
	 */
	BGRA(4,2,1,0,3),
	/**
	 * Each pixel contains a single red component.
	 */
	RED(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	BLUE(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single red component.
	 */
	GREEN(1,0,-1,-1,-1),
	/**
	 * Each pixel contains a single stencil value.
	 */
	STENCIL_INDEX(1,0,-1,-1,-1),
	
	ABGR(4,3,2,1,0),
	/**
	 * Colors are in red, green, blue order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGB_S3TC_DXT1_EXT(3,0,1,2,-1),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT1_EXT(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT3_EXT(4,0,1,2,3),
	/**
	 * Colors are in red, green, blue, alpha order. Compression in dds dxt1 format.
	 */
	COMPRESSED_RGBA_S3TC_DXT5_EXT(4,0,1,2,3),
	
	ATC_RGBA_EXPLICIT_ALPHA_AMD(4,0,1,2,3),
	
	ATC_RGBA_INTERPOLATED_ALPHA_AMD(4,0,1,2,3),
	
	ETC1_RGB8_OES(3,0,1,2,-1),
	
	FLOAT_R16_NV(1,0,-1,-1,-1),
	FLOAT_RG16_NV(2,0, 1,-1,-1),
	FLOAT_RGB16_NV(3,0, 1, 2,-1),
	FLOAT_RGBA16_NV(4,0, 1, 2, 3),

	FLOAT_R32_NV(1,0,-1,-1,-1),
	FLOAT_RG32_NV(2,0, 1,-1,-1),
	FLOAT_RGB32_NV(3,0, 1, 2,-1),
	FLOAT_RGBA32_NV(4,0, 1, 2, 3),
	
	LUMINANCE_FLOAT16_ATI(1,0,-1,-1,-1),
	LUMINANCE_FLOAT32_ATI(2,0, -1,-1,-1),
	
	LUMINANCE_ALPHA_FLOAT16_ATI(1, 0, 1,-1,-1),
	LUMINANCE_ALPHA_FLOAT32_ATI(2, 0, 1,-1,-1),

	RGB16F(3,0, 1, 2,-1),
	RGBA16F(3,0, 1, 2, 3),

	RGB32F(3,0, 1, 2,-1),
	RGBA32F(3,0, 1, 2, 3),
	RGBA32I(3,0, 1, 2, 3),
	RGBA32UI(3,0, 1, 2, 3);
	
	public final int numberOfChannels;
	public final int offset1;
	public final int offset2;
	public final int offset3;
	public final int offset4;
	
	private CCPixelInternalFormat(final int theNumberOfChannels, final int theOffset1, final int theOffset2, final int theOffset3, final int theOffset4){
		numberOfChannels = theNumberOfChannels;
		offset1 = theOffset1;
		offset2 = theOffset2;
		offset3 = theOffset3;
		offset4 = theOffset4;
	}
}
