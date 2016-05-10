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


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelStorageModes;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

/**
 * @author christian riekoff
 *
 */
public abstract class CCTexture{
	
	/**
	 * When textures are compressed OpenGL chooses the most appropriate texture 
	 * compression format. You can use CCTextureCompressionHint to specify whether 
	 * you want OpenGL to choose based on the fastest or highest quality algorithm.
	 * @author christianriekoff
	 *
	 */
	public static enum CCTextureCompressionHint{
		/**
		 * choose fastest algorithm for compression
		 */
		FASTEST(GL.GL_FASTEST),
		/**
		 * choose highest quality algorithm
		 */
		NICEST(GL.GL_NICEST), 
		/**
		 * let opengl decide for the compression algorithm
		 */
		DONT_CARE(GL.GL_DONT_CARE);
        
        public int glID;
		
		private CCTextureCompressionHint(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. This enum holds the possible modes.
	 * @author christian riekoff
	 *
	 */
	public static enum CCTextureWrap{
		/**
		 * clamps the texture if you use values going over the image range. The needed
		 * texels are taken from the texture border.
		 */
		CLAMP(GL2.GL_CLAMP),
		/**
		 * uses only border texels whenever the texture coordinates fall outside the of the texture.
		 */
		CLAMP_TO_BORDER(GL2.GL_CLAMP_TO_BORDER), 
		/**
		 * simply ignores texel samples that go over the edge and does not include them in the average
		 */
		CLAMP_TO_EDGE(GL.GL_CLAMP_TO_EDGE), 
		/**
		 * works like {@link CCTextureWrap#REPEAT} but mirrors 
		 * the texture for more seamless results on repeating the texture
		 */
		MIRRORED_REPEAT(GL.GL_MIRRORED_REPEAT),
		/**
		 * simply causes the texture to repeat in the direction in which the texture 
		 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
		 * of the texture size. This mode is very useful for applying a small tiled texture to large 
		 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
		 * larger texture, but at the cost of a much smaller texture image.
		 */
		REPEAT(GL.GL_REPEAT);
        
        public int glID;
		
		private CCTextureWrap(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public static enum CCTextureFilter{
		/**
		 * Returns the value of the texture element that is nearest 
		 * (in Manhattan distance) to the center of the pixel being textured.
		 */
		NEAREST(GL.GL_NEAREST),

		/**
		 * Returns the weighted average of the four texture elements
		 * that are closest to the center of the pixel being textured.
		 * These can include border texture elements, depending on the 
		 * values of TEXTURE_WRAP, and on the exact mapping.
		 */
        LINEAR(GL.GL_LINEAR);

        public int glID;
		
		private CCTextureFilter(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The texture mipmap filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element and mipmap data is defined. 
	 * @author christianriekoff
	 *
	 */
	public static enum CCTextureMipmapFilter{
		/**
         * Chooses the mipmap that most closely matches the size of the pixel 
         * being textured.
         */
        NEAREST,

        /**
         * Chooses the two mipmaps that most closely match the size of the pixel
         * being textured and uses the NEAREST criterion (the texture element 
         * nearest to the center of the pixel) to produce a texture value from 
         * each mipmap. The final texture value is a weighted average of those two values.
         */
        LINEAR;
	}
	
	public static enum CCTextureTarget{
		TEXTURE_1D(GL2.GL_TEXTURE_1D),
		TEXTURE_2D(GL.GL_TEXTURE_2D),
		TEXTURE_3D(GL2.GL_TEXTURE_3D),
		TEXTURE_RECT(GL2.GL_TEXTURE_RECTANGLE_ARB),
		TEXTURE_CUBE_MAP(GL.GL_TEXTURE_CUBE_MAP);
        
        public int glID;
		
		private CCTextureTarget(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The environment mode defines how the colors of the texels are combined 
	 * with the color of the underlying geometry.
	 * @author Christian Riekoff
	 *
	 */
	public static enum CCTextureEnvironmentMode{ 
		/**
		 * Texel color values are multiplied by the geometry fragment color values.
		 */
		MODULATE(GL2.GL_MODULATE), 
		/**
		 * Texel values are applied to geometry fragment values. If blending 
		 * is enabled and the texture contains an alpha channel, the geometry 
		 * blends through the texture according to the current blend function.
		 */
		DECAL(GL2.GL_DECAL),
		/**
		 * Texel values replace geometry fragment values. If blending is enabled 
		 * and the texture contains an alpha channel, the texture's alpha values 
		 * are used to replace the geometry fragment colors in the color buffer.
		 */
		REPLACE(GL.GL_REPLACE),
		/**
		 * Texel color values are added to the geometry color values.
		 */
		ADD(GL2.GL_ADD), 
		/**
		 * Texel color values are multiplied by the texture environment color.
		 */
		BLEND(GL.GL_BLEND), 
		/**
		 * Texel color values are combined with a second texture unit according 
		 * to the texture combine function.
		 */
		COMBINE(GL2.GL_COMBINE);
		
		private final int glID;
		
		private CCTextureEnvironmentMode(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @author christianriekoff
	 *
	 */
	public static enum CCDepthTextureMode{
		LUMINANCE(GL.GL_LUMINANCE), 
		INTENSITY(GL2.GL_INTENSITY),
		ALPHA(GL.GL_ALPHA);
		
		private final int glID;
		
		private CCDepthTextureMode(final int theGlID){
			glID = theGlID;
		}
	}

	protected CCTextureTarget _myTarget;
	protected CCTextureEnvironmentMode _myEnvironmentMode;
	
	protected CCTextureFilter _myTextureFilter;
	protected CCTextureMipmapFilter _myTextureMipmapFilter;
	
	protected CCColor _myBlendColor = new CCColor();
	
	protected CCPixelInternalFormat _myInternalFormat;
	protected CCPixelFormat _myFormat;
	protected CCPixelType _myPixelType;
	
	protected CCPixelStorageModes _myStorageModes;
	
	protected int[] _myTextureIDs;
	protected int _myTextureID;
	
	protected int _myWidth;
	protected int _myHeight;
	protected int _myDepth;
	
	protected int _myEstimatedMemorySize = 0;
	
	protected boolean _myMustFlipVertically;
	
	/**
	 * indicates whether mipmaps should be generated
	 * (ignored if mipmaps are supplied from the file)
	 */
	protected boolean _myGenerateMipmaps;
	
	/**
	 * indicates whether mipmaps are not only present
	 * but should also be used
	 */
	private boolean _myHasMipmaps;
	
	/**
	 * indicates whether the data of a texture is compressed
	 */
	protected boolean _myIsCompressed = false;
	
	/**
	 * Creates a new texture for the specified target. This object can also
	 * contain multiple textures to create texture sequences and do multitexturing.
	 * @param theTarget
	 * @param theGenerateMipmaps if <code>true</code> this automatically generate mipmaps when texture data is passed
	 * @param theNumberOfTextures number of textures to create
	 */
	public CCTexture(final CCTextureTarget theTarget, final CCTextureAttributes theAttributes, final int theNumberOfTextures) {
		_myTarget = theTarget;
		_myEnvironmentMode = CCTextureEnvironmentMode.MODULATE;
		_myTextureIDs = createTextureIds(theNumberOfTextures);
		_myTextureID = 0;
		
		_myWidth = 0;
		_myHeight = 1;
		_myDepth = 1;
		
		_myInternalFormat = theAttributes.internalFormat();
		_myFormat = theAttributes.format();
		_myPixelType = theAttributes.pixelType();
		
		textureFilter(theAttributes.filter());
		wrapS(theAttributes.wrapS());
		wrapT(theAttributes.wrapT());
		
		generateMipmaps(theAttributes.generateMipmaps());
	}
	
	public CCTexture(final CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		this(theTarget, theAttributes, 1);
	}
	
	public CCTexture(final CCTextureTarget theTarget) {
		this(theTarget, new CCTextureAttributes());
	}
	
	/**
	 * Sets whether mipmaps should be generated for the texture data. 
	 * @param theGenerateMipmaps indicates whether mipmaps should be autogenerated for the resulting texture. 
	 * 		  Currently if generateMipmaps is true then dataIsCompressed may not be true.
	 */
	public void generateMipmaps(final boolean theGenerateMipmaps) {
		_myGenerateMipmaps = theGenerateMipmaps;
		_myHasMipmaps = theGenerateMipmaps;
		_myStorageModes = new CCPixelStorageModes();
		_myStorageModes.alignment(1);
		
		GL2 gl = CCGraphics.currentGL();
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
	}

	/** 
	 * Returns whether mipmaps should be generated for the texture data. 
	 **/
	public boolean generateMipmaps() {
		return _myGenerateMipmaps;
	}
	
	/**
	 * Returns whether the data of this texture is compressed.
	 * @return <code>true</code> if the texture is compressed
	 */
	public boolean isCompressed() {
		return _myIsCompressed;
	}
	
	public abstract void dataImplementation(CCImage theImage);
		
	/**
	 * Applies the pixel storage mode for unpack operations. This means for
	 * operations that send data to the graphics card.
	 * @see #defaultUnpackStorage()
	 */
	protected void unpackStorage(CCPixelStorageModes theStorageModes) {
		GL gl = CCGraphics.currentGL();

		gl.glPixelStorei(GL2.GL_UNPACK_SWAP_BYTES, theStorageModes.swapBytes() ? 1 : 0);
		gl.glPixelStorei(GL2.GL_UNPACK_LSB_FIRST, theStorageModes.isLSBFirst() ? 1 : 0);
		
		gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, theStorageModes.rowLength());
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, theStorageModes.alignment());
		gl.glPixelStorei(GL2.GL_UNPACK_IMAGE_HEIGHT, theStorageModes.imageHeight());
		
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, theStorageModes.skipPixels());
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_ROWS, theStorageModes.skipRows());
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_IMAGES, theStorageModes.skipImages());
	}
	
	/**
	 * Applies the pixel storage mode for pack operations. This means for
	 * operations that receive data to the graphics card.
	 * @see #defaultUnpackStorage()
	 */
	public void packStorage(CCPixelStorageModes theStorageModes) {
		GL gl = CCGraphics.currentGL();

		gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, theStorageModes.swapBytes() ? 1 : 0);
		gl.glPixelStorei(GL2.GL_PACK_LSB_FIRST, theStorageModes.isLSBFirst() ? 1 : 0);
		
		gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, theStorageModes.rowLength());
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, theStorageModes.alignment());
		gl.glPixelStorei(GL2.GL_PACK_IMAGE_HEIGHT, theStorageModes.imageHeight());
		
		gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, theStorageModes.skipPixels());
		gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, theStorageModes.skipRows());
		gl.glPixelStorei(GL2.GL_PACK_SKIP_IMAGES, theStorageModes.skipImages());
	}
	
	protected CCPixelFormat pixelFormat(CCImage theImage){
		switch(theImage.pixelFormat()){
		case RG: return CCPixelFormat.RGB;
		case RG_INTEGER: return CCPixelFormat.RG_INTEGER;
		case RGB: return CCPixelFormat.RGB;
		case RGB_INTEGER: return CCPixelFormat.RGB_INTEGER;
		case RGBA: return CCPixelFormat.RGBA;
		case RGBA_INTEGER: return CCPixelFormat.RGBA_INTEGER;
		case BGR: return CCPixelFormat.BGR;
		case BGR_INTEGER: return CCPixelFormat.BGR_INTEGER;
		case BGRA: return CCPixelFormat.BGRA;
		case BGRA_INTEGER: return CCPixelFormat.BGRA_INTEGER;
		case ABGR: return CCPixelFormat.ABGR;
		case RED: return CCPixelFormat.RED;
		case RED_INTEGER: return CCPixelFormat.RED_INTEGER;
		case BLUE: return CCPixelFormat.BLUE;
		case BLUE_INTEGER: return CCPixelFormat.BLUE_INTEGER;
		case GREEN: return CCPixelFormat.GREEN;
		case GREEN_INTEGER: return CCPixelFormat.GREEN_INTEGER;
		case ALPHA: return CCPixelFormat.ALPHA;
		case LUMINANCE: return CCPixelFormat.LUMINANCE;
		case LUMINANCE_INTEGER: return CCPixelFormat.LUMINANCE_INTEGER;
		case LUMINANCE_ALPHA: return CCPixelFormat.LUMINANCE_ALPHA;
		case LUMINANCE_ALPHA_INTEGER: return CCPixelFormat.LUMINANCE_ALPHA_INTEGER;
		case STENCIL_INDEX: return CCPixelFormat.STENCIL_INDEX;
		case DEPTH_COMPONENT: return CCPixelFormat.DEPTH_COMPONENT;
		case DEPTH_STENCIL: return CCPixelFormat.DEPTH_STENCIL;
		case COMPRESSED_RGB_S3TC_DXT1_EXT: return CCPixelFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT1_EXT: return CCPixelFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT3_EXT: return CCPixelFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case COMPRESSED_RGBA_S3TC_DXT5_EXT: return CCPixelFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		throw new RuntimeException("no pixel format for image pixelformat:" + theImage.pixelFormat());
	}
	
	protected CCPixelInternalFormat internalFormat(CCImage theImage){
		switch(theImage.internalFormat()){
		case ALPHA: return CCPixelInternalFormat.ALPHA;
		case ALPHA4: return CCPixelInternalFormat.ALPHA4;
		case ALPHA8: return CCPixelInternalFormat.ALPHA8;
		case ALPHA12: return CCPixelInternalFormat.ALPHA12;
		case ALPHA16: return CCPixelInternalFormat.ALPHA16;
		case COMPRESSED_ALPHA: return CCPixelInternalFormat.COMPRESSED_ALPHA;
		case LUMINANCE: return CCPixelInternalFormat.LUMINANCE;
		case LUMINANCE4: return CCPixelInternalFormat.LUMINANCE4;
		case LUMINANCE8: return CCPixelInternalFormat.LUMINANCE8;
		case LUMINANCE12: return CCPixelInternalFormat.LUMINANCE12;
		case LUMINANCE16: return CCPixelInternalFormat.LUMINANCE16;
		case COMPRESSED_LUMINANCE: return CCPixelInternalFormat.COMPRESSED_LUMINANCE;
		case LUMINANCE_ALPHA: return CCPixelInternalFormat.LUMINANCE_ALPHA;
		case LUMINANCE4_ALPHA4: return CCPixelInternalFormat.LUMINANCE4_ALPHA4;
		case LUMINANCE6_ALPHA2: return CCPixelInternalFormat.LUMINANCE6_ALPHA2;
		case LUMINANCE8_ALPHA8: return CCPixelInternalFormat.LUMINANCE8_ALPHA8;
		case LUMINANCE12_ALPHA4: return CCPixelInternalFormat.LUMINANCE12_ALPHA4;
		case LUMINANCE12_ALPHA12: return CCPixelInternalFormat.LUMINANCE12_ALPHA12;
		case LUMINANCE16_ALPHA16: return CCPixelInternalFormat.LUMINANCE16_ALPHA16;
		case COMPRESSED_LUMINANCE_ALPHA: return CCPixelInternalFormat.COMPRESSED_LUMINANCE_ALPHA;
		case INTENSITY: return CCPixelInternalFormat.INTENSITY;
		case INTENSITY4: return CCPixelInternalFormat.INTENSITY4;
		case INTENSITY8: return CCPixelInternalFormat.INTENSITY8;
		case INTENSITY12: return CCPixelInternalFormat.INTENSITY12;
		case INTENSITY16: return CCPixelInternalFormat.INTENSITY16;
		case COMPRESSED_INTENSITY: return CCPixelInternalFormat.COMPRESSED_INTENSITY;
		case DEPTH_COMPONENT: return CCPixelInternalFormat.DEPTH_COMPONENT;
		case DEPTH_COMPONENT16: return CCPixelInternalFormat.DEPTH_COMPONENT16;
		case DEPTH_COMPONENT24: return CCPixelInternalFormat.DEPTH_COMPONENT24;
		case DEPTH_COMPONENT32: return CCPixelInternalFormat.DEPTH_COMPONENT32;
		case RG: return CCPixelInternalFormat.RG;
		case RGB: return CCPixelInternalFormat.RGB;
		case RGB4: return CCPixelInternalFormat.RGB4;
		case RGB5: return CCPixelInternalFormat.RGB5;
		case RGB8: return CCPixelInternalFormat.RGB8;
		case RGB10: return CCPixelInternalFormat.RGB10;
		case RGB12: return CCPixelInternalFormat.RGB12;
		case RGB16: return CCPixelInternalFormat.RGB16;
		case RGB16I: return CCPixelInternalFormat.RGB16I;
		case RGB16UI: return CCPixelInternalFormat.RGB16UI;
		case RGB32I: return CCPixelInternalFormat.RGB32I;
		case RGB32UI: return CCPixelInternalFormat.RGB32UI;
		case R3_G3_B2: return CCPixelInternalFormat.R3_G3_B2;
		case COMPRESSED_RGB: return CCPixelInternalFormat.COMPRESSED_RGB;
		case RGBA: return CCPixelInternalFormat.RGBA;
		case RGBA2: return CCPixelInternalFormat.RGBA2;
		case RGBA4: return CCPixelInternalFormat.RGBA4;
		case RGB5_A1: return CCPixelInternalFormat.RGB5_A1;
		case RGBA8: return CCPixelInternalFormat.RGBA8;
		case RGB10_A2: return CCPixelInternalFormat.RGB10_A2;
		case RGBA12: return CCPixelInternalFormat.RGBA12;
		case RGBA16: return CCPixelInternalFormat.RGBA16;
		case RGBA16I: return CCPixelInternalFormat.RGBA16I;
		case RGBA16UI: return CCPixelInternalFormat.RGBA16UI;
		case COMPRESSED_RGBA: return CCPixelInternalFormat.COMPRESSED_RGBA;
		case SLUMINANCE: return CCPixelInternalFormat.SLUMINANCE;
		case SLUMINANCE8: return CCPixelInternalFormat.SLUMINANCE8;
		case SLUMINANCE_ALPHA: return CCPixelInternalFormat.SLUMINANCE_ALPHA;
		case SLUMINANCE8_ALPHA8: return CCPixelInternalFormat.SLUMINANCE8_ALPHA8;
		case SRGB: return CCPixelInternalFormat.SRGB;
		case SRGB8: return CCPixelInternalFormat.SRGB8;
		case SRGB_ALPHA: return CCPixelInternalFormat.SRGB_ALPHA;
		case SRGB8_ALPHA8: return CCPixelInternalFormat.SRGB8_ALPHA8;
		case BGR: return CCPixelInternalFormat.BGR;
		case BGRA: return CCPixelInternalFormat.BGRA;
		case RED: return CCPixelInternalFormat.RED;
		case BLUE: return CCPixelInternalFormat.BLUE;
		case GREEN: return CCPixelInternalFormat.GREEN;
		case STENCIL_INDEX: return CCPixelInternalFormat.STENCIL_INDEX;
		case ABGR: return CCPixelInternalFormat.ABGR;
		case COMPRESSED_RGB_S3TC_DXT1_EXT: return CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT1_EXT: return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT3_EXT: return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case COMPRESSED_RGBA_S3TC_DXT5_EXT: return CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
//		case ATC_RGBA_EXPLICIT_ALPHA_AMD: return CCPixelInternalFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD;
//		case ATC_RGBA_INTERPOLATED_ALPHA_AMD: return CCPixelInternalFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD;
//		case ETC1_RGB8_OES: return CCPixelInternalFormat.ETC1_RGB8_OES;
		case FLOAT_R16_NV: return CCPixelInternalFormat.FLOAT_R16_NV;
		case FLOAT_RG16_NV: return CCPixelInternalFormat.FLOAT_RG16_NV;
		case FLOAT_RGB16_NV: return CCPixelInternalFormat.FLOAT_RGB16_NV;
		case FLOAT_RGBA16_NV: return CCPixelInternalFormat.FLOAT_RGBA16_NV;
		case FLOAT_R32_NV: return CCPixelInternalFormat.FLOAT_R32_NV;
		case FLOAT_RG32_NV: return CCPixelInternalFormat.FLOAT_RG32_NV;
		case FLOAT_RGB32_NV: return CCPixelInternalFormat.FLOAT_RG32_NV;
		case FLOAT_RGBA32_NV: return CCPixelInternalFormat.FLOAT_RGBA32_NV;
		case LUMINANCE_FLOAT16_ATI: return CCPixelInternalFormat.LUMINANCE_FLOAT16_ATI;
		case LUMINANCE_FLOAT32_ATI: return CCPixelInternalFormat.LUMINANCE_FLOAT32_ATI;
		case LUMINANCE_ALPHA_FLOAT16_ATI: return CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT16_ATI;
		case LUMINANCE_ALPHA_FLOAT32_ATI: return CCPixelInternalFormat.LUMINANCE_ALPHA_FLOAT32_ATI;
		case RGB16F: return CCPixelInternalFormat.RGB16F;
		case RGBA16F: return CCPixelInternalFormat.RGBA16F;
		case RGB32F: return CCPixelInternalFormat.RGB32F;
		case RGBA32F: return CCPixelInternalFormat.RGBA32F;
		case RGBA32I: return CCPixelInternalFormat.RGBA32I;
		case RGBA32UI: return CCPixelInternalFormat.RGBA32UI;
		default:
		}
		throw new RuntimeException("no pixel internal format for image pixel internal format:" + theImage.internalFormat());
	}
	
	protected CCPixelType pixelType(CCImage theImage){
		switch(theImage.pixelType()){
		case UNSIGNED_BYTE: return CCPixelType.UNSIGNED_BYTE;
		case BYTE: return CCPixelType.BYTE;
		case BITMAP: return CCPixelType.BITMAP;
		case UNSIGNED_SHORT: return CCPixelType.UNSIGNED_SHORT;
		case SHORT: return CCPixelType.SHORT;
		case UNSIGNED_INT: return CCPixelType.UNSIGNED_INT;
		case INT: return CCPixelType.INT;
		case FIXED: return CCPixelType.FIXED;
		case FLOAT: return CCPixelType.FLOAT;
		case HALF_FLOAT: return CCPixelType.HALF_FLOAT;
		case DOUBLE: return CCPixelType.DOUBLE;
		case UNSIGNED_BYTE_2_3_3_REV: return CCPixelType.UNSIGNED_BYTE_2_3_3_REV;
		case UNSIGNED_BYTE_3_3_2: return CCPixelType.UNSIGNED_BYTE_3_3_2;
		case UNSIGNED_SHORT_5_6_5: return CCPixelType.UNSIGNED_SHORT_5_6_5;
		case UNSIGNED_SHORT_5_6_5_REV: return CCPixelType.UNSIGNED_SHORT_5_6_5_REV;
		case UNSIGNED_SHORT_4_4_4_4: return CCPixelType.UNSIGNED_SHORT_4_4_4_4;
		case UNSIGNED_SHORT_4_4_4_4_REV: return CCPixelType.UNSIGNED_SHORT_4_4_4_4_REV;
		case UNSIGNED_SHORT_5_5_5_1: return CCPixelType.UNSIGNED_SHORT_5_5_5_1;
		case UNSIGNED_SHORT_1_5_5_5_REV: return CCPixelType.UNSIGNED_SHORT_1_5_5_5_REV;
		case UNSIGNED_INT_8_8_8_8: return CCPixelType.UNSIGNED_INT_8_8_8_8;
		case UNSIGNED_INT_8_8_8_8_REV: return CCPixelType.UNSIGNED_INT_8_8_8_8_REV;
		case UNSIGNED_INT_10_10_10_2: return CCPixelType.UNSIGNED_INT_10_10_10_2;
		case UNSIGNED_INT_2_10_10_10_REV: return CCPixelType.UNSIGNED_INT_2_10_10_10_REV;
		case UNSIGNED_INT_24_8: return CCPixelType.UNSIGNED_INT_24_8;
		case UNSIGNED_INT_10F_11F_11F_REV: return CCPixelType.UNSIGNED_INT_10F_11F_11F_REV;
		case UNSIGNED_INT_5_9_9_9_REV: return CCPixelType.UNSIGNED_INT_5_9_9_9_REV;
		case FLOAT_32_UNSIGNED_INT_24_8_REV: return CCPixelType.FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		throw new RuntimeException("no pixel type for image pixel type:" + theImage.pixelType());
	}
	
	/**
	 * Applies the default pixel storage settings for unpack operations.
	 * @see #unpackStorage()
	 */
	protected void defaultUnpackStorage() {
		GL gl = CCGraphics.currentGL();
		
		gl.glPixelStorei(GL2.GL_UNPACK_SWAP_BYTES, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_LSB_FIRST, 0);
		
		gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, 0);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
		gl.glPixelStorei(GL2.GL_UNPACK_IMAGE_HEIGHT, 0);
		
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_IMAGES, 0);
	}
	
	/**
	 * Applies the default pixel storage settings for pack operations.
	 * @see #packStorage()
	 */
	public void defaultPackStorage() {
		GL gl = CCGraphics.currentGL();

		gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, 0);
		gl.glPixelStorei(GL2.GL_PACK_LSB_FIRST, 0);
		
		gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, 0);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 4);
		gl.glPixelStorei(GL2.GL_PACK_IMAGE_HEIGHT, 0);
		
		gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, 0);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL2.GL_PACK_SKIP_IMAGES, 0);
	}
	
	/**
	 * Sets or resets the data for the texture. Be aware that this method is quiet
	 * expensive. It should only be called for initialization or to totally reset a texture,
	 * meaning to also change its size.
	 * @param theImage texture information
	 */
	public void data(CCImage theImage) {
		_myMustFlipVertically = theImage.mustFlipVertically();
		
		_myInternalFormat = internalFormat(theImage);
		_myFormat = pixelFormat(theImage);
		_myPixelType = pixelType(theImage);
		_myStorageModes = theImage.pixelStorageModes();
		
		_myWidth = theImage.width();
		_myHeight = theImage.height();
		
		bind();

		GL2 gl = CCGraphics.currentGL();
		unpackStorage(theImage.pixelStorageModes());
		dataImplementation(theImage);
		defaultUnpackStorage();
		
		textureFilter(CCTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
	}
	
	/**
	 * Returns a texture image into img. 
	 * @param theLevel specifies the level-of-detail number of the desired image
	 * @return data of the texture
	 */
	public ByteBuffer dataBuffer(int theLevel) {
		GL2 gl = CCGraphics.currentGL();
		size();
		ByteBuffer myBuffer = ByteBuffer.allocate(size() * _myPixelType.bytesPerChannel * 4);
		gl.glGetTexImage(_myTarget.glID, theLevel, _myFormat.glID, _myPixelType.glID, myBuffer);
		myBuffer.rewind();
		return myBuffer;
	}
	
	/**
	 * Sets or resets the data for the texture. Different from the normal data method, calling
	 * <code>compressData()</code> uses OPENGLs internal texture compression. As a result textures
	 * use far less memory on the graphics card. Dependent on the texture data's internal pixel 
	 * format this method might fail. To check if texture compression was successful this method
	 * returns a boolean value which is <code>true</code> in case that the compression was successful
	 * or <code>false</code> otherwise. Be aware that this method is quiet
	 * expensive. It should only be called for initialization or to totally reset a texture,
	 * meaning to also change its size.
	 * @param theImage texture information
	 * @param theHint specify whether texture compression uses the fastest or highest quality algorithm
	 * @return <code>true</code> in case that the compression was successful or <code>false</code> otherwise
	 */
	public boolean compressData(final CCTextureCompressionHint theHint, final CCImage theImage) {
		_myMustFlipVertically = theImage.mustFlipVertically();
		
		_myWidth = theImage.width();
		_myHeight = theImage.height();
		
		_myInternalFormat = internalFormat(theImage);
		_myFormat = pixelFormat(theImage);
		_myPixelType = pixelType(theImage);
		
		switch(_myInternalFormat) {
		case RGB:
		case RGB4:
		case RGB5:
		case RGB8:
		case RGB10:
		case RGB12:
		case RGB16:
			_myInternalFormat = CCPixelInternalFormat.COMPRESSED_RGB;
			break;
		case RGBA:
		case RGBA2:
		case RGBA4:
		case RGBA8:
		case RGBA12:
		case RGBA16:
		case RGB5_A1:
		case RGB10_A2:
			_myInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA;
			break;
		default:
		}
		
		bind();
		GL2 gl = CCGraphics.currentGL();
		gl.glHint(GL2.GL_TEXTURE_COMPRESSION_HINT, theHint.glID);
		unpackStorage(theImage.pixelStorageModes());
		switch(_myTarget) {
		case TEXTURE_1D:
			gl.glTexImage1D(
				_myTarget.glID, 0, _myInternalFormat.glID,
				theImage.width(), 0, 
				_myFormat.glID, _myPixelType.glID, theImage.buffer()
			);
			break;
		case TEXTURE_2D:
		case TEXTURE_RECT:
			gl.glTexImage2D(
				_myTarget.glID, 0, _myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				_myFormat.glID, _myPixelType.glID, theImage.buffer()
			);
			break;
		default:
		}
		defaultUnpackStorage();
		
		textureFilter(CCTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)gl.glGenerateMipmap(_myTarget.glID);
		
		int[] myData = new int[1];
		
		gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_COMPRESSED, myData, 0);
		
		boolean myResult = myData[0] > 0;
		
		if(myResult) {
			gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_COMPRESSED_IMAGE_SIZE, myData, 0);
			_myEstimatedMemorySize = myData[0];
			gl.glGetTexLevelParameteriv(_myTarget.glID, 0, GL2.GL_TEXTURE_INTERNAL_FORMAT, myData, 0);
		}
		
		return myResult;
	}
	
	public boolean compressData(final CCImage theImage) {
		return compressData(CCTextureCompressionHint.DONT_CARE, theImage);
	}
	
	public abstract void updateData(final CCImage theImage);
	
	protected int[] createTextureIds(final int theNumberOfIds) {
		GL gl = CCGraphics.currentGL();
		int[] tmp = new int[theNumberOfIds];
		gl.glGenTextures(theNumberOfIds, tmp, 0);
		return tmp;
	}
	
	public void bind() {
		bind(_myTextureID);
	}
	
	public void bind(final int theID) {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID, _myTextureIDs[theID]);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, _myEnvironmentMode.glID);
		
		if(_myEnvironmentMode == CCTextureEnvironmentMode.BLEND) {
			float[] myColor = {(float)_myBlendColor.r, (float)_myBlendColor.g, (float)_myBlendColor.b, (float)_myBlendColor.a};
			gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, myColor,0);
		}
	}
	
	public void unbind() {
		GL2 gl = CCGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID, 0);
	}
	
	public int id() {
		return _myTextureIDs[_myTextureID];
	}
	
	public int id(final int theLevel) {
		return _myTextureIDs[theLevel];
	}
	
	@Override
	public void finalize() {
//		CCGraphics.currentGL().glDeleteTextures(_myTextureIDs.length, _myTextureIDs, 0);
	}
	
	/**
	 * Returns the target of the texture can be 1D, 2D, 3D, RECT and CUBEMAP.
	 * @return target of the texture
	 */
	public CCTextureTarget target() {
		return _myTarget;
	}
	
	/**
	 * Indicates whether the texture coordinates must be flipped vertically in 
	 * order to properly display the texture. This is handled automatically by 
	 * {@link CCGraphics#texture(cc.creativecomputing.graphics.texture.CCAbstractTexture) texture()} in
	 * {@link CCGraphics} by setting a texture transform, but applications may generate or otherwise
	 * produce texture coordinates which must be corrected.
	 *
	 * @return
	 */
	public boolean mustFlipVertically() {
		return _myMustFlipVertically;
	}
	
	/**
	 * Indicates whether the texture coordinates must be flipped vertically in 
	 * order to properly display the texture. This is handled automatically by 
	 * {@link CCGraphics#texture(cc.creativecomputing.graphics.texture.CCAbstractTexture) texture()} in
	 * {@link CCGraphics} by setting a texture transform, but applications may generate or otherwise
	 * produce texture coordinates which must be corrected.
	 *
	 * @param theMustFlipVertically true if the texture must be flipped otherwise false
	 */
	public void mustFlipVertically(boolean theMustFlipVertically) {
		_myMustFlipVertically = theMustFlipVertically;
	}
	
	/** 
	 * Returns the width of the texture.
	 *
	 * @return the width of the texture
	 */
	public int width() {
		return _myWidth;
	}

	/**
	 * Returns the height of the texture. For 1D textures
	 * this value is 1.
	 *
	 * @return the height of the texture
	 */
	public int height() {
		return _myHeight;
	}
	
	/**
	 * Returns the number of pixels of this texture.
	 * @return the number of pixels
	 */
	public int size() {
		return _myWidth * _myHeight * _myDepth;
	}
	
	/**
	 * Returns the size of the texture in pixels
	 * @return
	 */
	public CCVector2 dimension() {
		return new CCVector2(_myWidth, _myHeight);
	}
	
	/**
	 * Returns the depth of the texture. This makes only 
	 * sense for 3D texture. For all others this value is 1.
	 * 
	 * @return the depth of the texture
	 */
	public int depth() {
		return _myDepth;
	}
	
	public int border() {
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public CCPixelFormat format() {
		return _myFormat;
	}
	
	/**
	 * 
	 * @return
	 */
	public CCPixelInternalFormat internalFormat() {
		return _myInternalFormat;
	}
	
	
	public CCPixelType pixelType() {
		return _myPixelType;
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final int theValue) {
		if(_myTextureIDs == null)return;
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameteri(_myTarget.glID, theType, theValue);
		}
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final float theValue) {
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameterf(_myTarget.glID, theType, theValue);
		}
	}
	
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(final int theType, final float[] theValues) {
		for(int i = 0; i < _myTextureIDs.length;i++) {
			bind(i);
			CCGraphics.currentGL().glTexParameterfv(_myTarget.glID, theType, theValues,0);
		}
	}
	
	/**
	 * Shortcut to get a texture parameter
	 * @param theGLID the gl id of the parameter to get
	 * @return the value for the given parameter
	 */
	protected int parameter(final int theGLID) {
		int[] myResult = new int[1];
		CCGraphics.currentGL().glGetTexLevelParameteriv(_myTarget.glID, 0, theGLID, myResult, 0);
		return myResult[0];
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. Using this method you can set the wrap mode for each coordinate 
	 * individually. The wrap mode can then be set to one of the following values:
	 * <ul>
	 * <li>{@link CCTextureWrap#REPEAT} simply causes the texture to repeat in the direction in which the texture 
	 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
	 * of the texture size. This mode is very useful for applying a small tiled texture to large 
	 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
	 * larger texture, but at the cost of a much smaller texture image.</li>
	 * <li>{@link CCTextureWrap#MIRRORED_REPEAT} works like {@link CCTextureWrap#REPEAT} but mirrors 
	 * the texture for more seamless results on repeating the texture</li>
	 * <li>{@link CCTextureWrap#CLAMP} clamps the texture if you use values going over the image range. The needed
	 * texels are taken from the texture border.
	 * results on repeating the texture</li>
	 * <li>{@link CCTextureWrap#CLAMP_TO_EDGE} simply ignores texel samples that go over the edge and does not include them in the average</li>
	 * <li>{@link CCTextureWrap#CLAMP_TO_BORDER} uses only border texels whenever the texture coordinates fall outside the of the texture.</li>
	 * </ul>
	 * @param theTextureWrap mode for texture wrapping 
	 */
	public void wrap(final CCTextureWrap theTextureWrap){
		parameter(GL2.GL_TEXTURE_WRAP_R, theTextureWrap.glID);
		parameter(GL.GL_TEXTURE_WRAP_S, theTextureWrap.glID);
		parameter(GL.GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	public void wrapR(final CCTextureWrap theTextureWrap){
		parameter(GL2.GL_TEXTURE_WRAP_R, theTextureWrap.glID);
	}
	
	/**
	 * Sets the horizontal wrapping behavior when a texture coordinate falls outside the range of [0,1].
	 * @see #wrap(CCTextureWrap)
	 * @param theTextureWrap
	 */
	public void wrapS(final CCTextureWrap theTextureWrap){
		parameter(GL.GL_TEXTURE_WRAP_S, theTextureWrap.glID);
	}
	
	/**
	 * 
	 * @param theTextureWrap
	 */
	public void wrapT(final CCTextureWrap theTextureWrap) {
		parameter(GL.GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	public void textureBorderColor(final CCColor theColor) {
		float[] myColor = {(float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a};
		parameter(GL2.GL_TEXTURE_BORDER_COLOR, myColor);
	}
	
	private void updateFilter() {
		// set mag filter first as this has no impact on mipmapping
		parameter(GL.GL_TEXTURE_MAG_FILTER, _myTextureFilter.glID);
			
		if(!_myHasMipmaps) {
			parameter(GL.GL_TEXTURE_MIN_FILTER, _myTextureFilter.glID);
			return;
		}
			
		if(_myTextureFilter == CCTextureFilter.NEAREST) {
			if(_myTextureMipmapFilter == CCTextureMipmapFilter.NEAREST) {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
			}else {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_LINEAR);
			}
		}else {
			if(_myTextureMipmapFilter == CCTextureMipmapFilter.NEAREST) {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);
			}else {
				parameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
			}
		}
	}
	
	/**
	 * Applies anisotropic filtering to the texture. THis is helpful to avoid
	 * blurring in more oblique angle of the geometry to the view. You have to 
	 * provide an amount between 0 and 1 be aware that the maximum amount of filtering
	 * is dependent on the current hardware. So 0 stands for no anisotropic filtering
	 * 1 for the maximum available amount.
	 * @param theAmount the amount of filtering number between 0 and 1
	 */
	public void anisotropicFiltering(final float theAmount) {
		GL2 gl = CCGraphics.currentGL();
		FloatBuffer floatBuff = FloatBuffer.allocate(1);
		gl.glGetFloatv(GL2GL3.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, floatBuff);
		float GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = floatBuff.get(0);
		if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			
		} else {
			GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0;
		}
		parameter(GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, CCMath.blend(1, GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, theAmount));
	}
	
	/**
	 * <p>
	 * The texture filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element. There are two 
	 * defined filter functions. 
	 * <ul>
	 * <li>{@link CCTextureFilter#NEAREST} uses the nearest pixel</li>
	 * <li>{@link CCTextureFilter#LINEAR} uses the nearest four texture elements to compute the texture value.</li>
	 * </ul>
	 * </p>
	 * The default is {@link CCTextureFilter#LINEAR}.       
	 * @param theFilter
	 */
	public void textureFilter(final CCTextureFilter theFilter){
		_myTextureFilter = theFilter;
		updateFilter();
	}
	
	/**
	 * <p>
	 * The texture mipmap filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element and mipmap data is defined. 
	 * </p>
	 * <p>A mipmap is an ordered set of arrays representing the same image at 
	 * progressively lower resolutions. If the texture has dimensions 2n x 2m,
	 * there are max(n,m) + 1 mipmaps.</p>
	 * <ul>
	 * <li><code>NEAREST</code> uses the nearest mipmap</li>
	 * <li><code>LINEAR</code> uses the nearest two mipmaps to compute the texture value.</li>
	 * </ul>
	 * The default is <code>NEAREST</code>.
	 *              
	 * @param theFilter
	 */
	public void textureMipmapFilter(final CCTextureMipmapFilter theFilter){
		_myTextureMipmapFilter = theFilter;
		updateFilter();
	}
	
	/**
	 * Defines how colors from the texels are combined with the color of the
	 * underlying geometry.
	 * @param theMode
	 */
	public void textureEnvironmentMode(final CCTextureEnvironmentMode theMode) {
		_myEnvironmentMode = theMode;
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @param theMode
	 */
	public void depthTextureMode(final CCDepthTextureMode theMode) {
		parameter(GL2.GL_DEPTH_TEXTURE_MODE, theMode.glID);
	}
	
	/**
	 * Textures can also be blended with a constant blending color using the 
	 * <code>BLEND</code> texture environment mode. If you set this environment mode, 
	 * you must also set the texture environment color with this method.
	 * @param theBlendColor blend color for the blend mode
	 */
	public void blendColor(final CCColor theBlendColor) {
		_myBlendColor = theBlendColor;
	}
	
	
}
