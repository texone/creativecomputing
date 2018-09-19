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
package cc.creativecomputing.gl4;


import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.GL_ADD;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_DECAL;
import static org.lwjgl.opengl.GL11.GL_DONT_CARE;
import static org.lwjgl.opengl.GL11.GL_FASTEST;
import static org.lwjgl.opengl.GL11.GL_INTENSITY;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_LUMINANCE;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_PACK_LSB_FIRST;
import static org.lwjgl.opengl.GL11.GL_PACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_PACK_SWAP_BYTES;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_INTERNAL_FORMAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNPACK_LSB_FIRST;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SWAP_BYTES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glGetTexParameterf;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteriv;
import static org.lwjgl.opengl.GL11.glGetTexParameteri;
import static org.lwjgl.opengl.GL11.glGetTexParameteriv;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexEnvfv;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTexImage1D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameterfv;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_PACK_IMAGE_HEIGHT;
import static org.lwjgl.opengl.GL12.GL_PACK_SKIP_IMAGES;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL12.GL_UNPACK_IMAGE_HEIGHT;
import static org.lwjgl.opengl.GL12.GL_UNPACK_SKIP_IMAGES;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL13.GL_COMBINE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_COMPRESSED;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_COMPRESSION_HINT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl4.GLTextureAttributes.GLTextureFilter;
import cc.creativecomputing.gl4.GLTextureAttributes.GLTextureMipmapFilter;
import cc.creativecomputing.gl4.GLTextureAttributes.GLTextureWrap;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelStorageModes;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christian riekoff
 *
 */
public abstract class GLTexture{
	
	/**
	 * When textures are compressed OpenGL chooses the most appropriate texture 
	 * compression format. You can use CCTextureCompressionHint to specify whether 
	 * you want OpenGL to choose based on the fastest or highest quality algorithm.
	 * @author christianriekoff
	 *
	 */
	public enum GLTextureCompressionHint{
		/**
		 * choose fastest algorithm for compression
		 */
		FASTEST(GL_FASTEST),
		/**
		 * choose highest quality algorithm
		 */
		NICEST(GL_NICEST), 
		/**
		 * let opengl decide for the compression algorithm
		 */
		DONT_CARE(GL_DONT_CARE);
        
        public int glID;
		
		GLTextureCompressionHint(final int theGLID) {
			glID = theGLID;
		}
	}
	
	
	
	
	public enum GLTextureTarget{
		TEXTURE_1D(GL_TEXTURE_1D),
		TEXTURE_2D(GL_TEXTURE_2D),
		TEXTURE_3D(GL_TEXTURE_3D),
		TEXTURE_RECT(GL_TEXTURE_RECTANGLE),
		TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP);
        
        public int glID;
		
		GLTextureTarget(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The environment mode defines how the colors of the texels are combined 
	 * with the color of the underlying geometry.
	 * @author Christian Riekoff
	 *
	 */
	public enum GLTextureEnvironmentMode{
		/**
		 * Texel color values are multiplied by the geometry fragment color values.
		 */
		MODULATE(GL_MODULATE), 
		/**
		 * Texel values are applied to geometry fragment values. If blending 
		 * is enabled and the texture contains an alpha channel, the geometry 
		 * blends through the texture according to the current blend function.
		 */
		DECAL(GL_DECAL),
		/**
		 * Texel values replace geometry fragment values. If blending is enabled 
		 * and the texture contains an alpha channel, the texture's alpha values 
		 * are used to replace the geometry fragment colors in the color buffer.
		 */
		REPLACE(GL_REPLACE),
		/**
		 * Texel color values are added to the geometry color values.
		 */
		ADD(GL_ADD), 
		/**
		 * Texel color values are multiplied by the texture environment color.
		 */
		BLEND(GL_BLEND), 
		/**
		 * Texel color values are combined with a second texture unit according 
		 * to the texture combine function.
		 */
		COMBINE(GL_COMBINE);
		
		private final int glID;
		
		GLTextureEnvironmentMode(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @author christianriekoff
	 *
	 */
	public enum GLDepthTextureMode{
		LUMINANCE(GL_LUMINANCE), 
		INTENSITY(GL_INTENSITY),
		ALPHA(GL_ALPHA);
		
		private final int glID;
		
		GLDepthTextureMode(final int theGlID){
			glID = theGlID;
		}
	}

	protected GLTextureTarget _myTarget;
	protected GLTextureEnvironmentMode _myEnvironmentMode;
	
	protected GLTextureFilter _myTextureFilter = GLTextureFilter.LINEAR;
	protected GLTextureMipmapFilter _myTextureMipmapFilter = GLTextureMipmapFilter.LINEAR;
	
	protected CCColor _myBlendColor = new CCColor();
	
	protected GLPixelDataInternalFormat _myInternalFormat;
	protected GLPixelDataFormat _myFormat;
	protected GLPixelDataType _myPixelType;
	
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
	public GLTexture(final GLTextureTarget theTarget, final GLTextureAttributes theAttributes, final int theNumberOfTextures) {
		_myTarget = theTarget;
		_myEnvironmentMode = GLTextureEnvironmentMode.MODULATE;
		_myTextureIDs = createTextureIds(theNumberOfTextures);
		_myTextureID = 0;
		
		_myWidth = 0;
		_myHeight = 1;
		_myDepth = 1;
		
		_myInternalFormat = theAttributes.internalFormat;
		_myFormat = theAttributes.format;
		_myPixelType = theAttributes.type;
		
		textureFilter(theAttributes.filter);
		wrapS(theAttributes.wrapS);
		wrapT(theAttributes.wrapT);
		
		generateMipmaps(theAttributes.generateMipmaps);
	}
	
	public GLTexture(final GLTextureTarget theTarget, final GLTextureAttributes theAttributes) {
		this(theTarget, theAttributes, 1);
	}
	
	public GLTexture(final GLTextureTarget theTarget) {
		this(theTarget, new GLTextureAttributes());
	}
	
	/**
	 * Sets whether mipmaps should be generated for the texture data. 
	 * @param theGenerateMipmaps indicates whether mipmaps should be autogenerated for the resulting texture. 
	 * 		  Currently if generateMipmaps is true then dataIsCompressed may not be true.
	 */
	@CCProperty(name = "generate mipmaps")
	public void generateMipmaps(final boolean theGenerateMipmaps) {
		_myGenerateMipmaps = theGenerateMipmaps;
		_myHasMipmaps = theGenerateMipmaps;
		if(!_myGenerateMipmaps)return;
	
		_myStorageModes = new CCPixelStorageModes();
		_myStorageModes.alignment(1);
		bind();
		glGenerateMipmap(_myTarget.glID);
		unbind();
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
		

		glPixelStorei(GL_UNPACK_SWAP_BYTES, theStorageModes.swapBytes() ? 1 : 0);
		glPixelStorei(GL_UNPACK_LSB_FIRST, theStorageModes.isLSBFirst() ? 1 : 0);
		
		glPixelStorei(GL_UNPACK_ROW_LENGTH, theStorageModes.rowLength());
		glPixelStorei(GL_UNPACK_ALIGNMENT, theStorageModes.alignment());
		glPixelStorei(GL_UNPACK_IMAGE_HEIGHT, theStorageModes.imageHeight());
		
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, theStorageModes.skipPixels());
		glPixelStorei(GL_UNPACK_SKIP_ROWS, theStorageModes.skipRows());
		glPixelStorei(GL_UNPACK_SKIP_IMAGES, theStorageModes.skipImages());
	}
	
	/**
	 * Applies the pixel storage mode for pack operations. This means for
	 * operations that receive data to the graphics card.
	 * @see #defaultUnpackStorage()
	 */
	public void packStorage(CCPixelStorageModes theStorageModes) {
		

		glPixelStorei(GL_PACK_SWAP_BYTES, theStorageModes.swapBytes() ? 1 : 0);
		glPixelStorei(GL_PACK_LSB_FIRST, theStorageModes.isLSBFirst() ? 1 : 0);
		
		glPixelStorei(GL_PACK_ROW_LENGTH, theStorageModes.rowLength());
		glPixelStorei(GL_PACK_ALIGNMENT, theStorageModes.alignment());
		glPixelStorei(GL_PACK_IMAGE_HEIGHT, theStorageModes.imageHeight());
		
		glPixelStorei(GL_PACK_SKIP_PIXELS, theStorageModes.skipPixels());
		glPixelStorei(GL_PACK_SKIP_ROWS, theStorageModes.skipRows());
		glPixelStorei(GL_PACK_SKIP_IMAGES, theStorageModes.skipImages());
	}
	
	public static GLPixelDataFormat pixelFormat(CCImage theImage){
		switch(theImage.pixelFormat()){
		case RG: return GLPixelDataFormat.RGB;
		case RG_INTEGER: return GLPixelDataFormat.RG_INTEGER;
		case RGB: return GLPixelDataFormat.RGB;
		case RGB_INTEGER: return GLPixelDataFormat.RGB_INTEGER;
		case RGBA: return GLPixelDataFormat.RGBA;
		case RGBA_INTEGER: return GLPixelDataFormat.RGBA_INTEGER;
		case BGR: return GLPixelDataFormat.BGR;
		case BGR_INTEGER: return GLPixelDataFormat.BGR_INTEGER;
		case BGRA: return GLPixelDataFormat.BGRA;
		case BGRA_INTEGER: return GLPixelDataFormat.BGRA_INTEGER;
		case RED: return GLPixelDataFormat.RED;
		case RED_INTEGER: return GLPixelDataFormat.RED_INTEGER;
		case BLUE: return GLPixelDataFormat.BLUE;
		case BLUE_INTEGER: return GLPixelDataFormat.BLUE_INTEGER;
		case GREEN: return GLPixelDataFormat.GREEN;
		case GREEN_INTEGER: return GLPixelDataFormat.GREEN_INTEGER;
		case ALPHA: return GLPixelDataFormat.ALPHA;
		case LUMINANCE: return GLPixelDataFormat.LUMINANCE;
		case LUMINANCE_ALPHA: return GLPixelDataFormat.LUMINANCE_ALPHA;
		case STENCIL_INDEX: return GLPixelDataFormat.STENCIL_INDEX;
		case DEPTH_COMPONENT: return GLPixelDataFormat.DEPTH_COMPONENT;
		case DEPTH_STENCIL: return GLPixelDataFormat.DEPTH_STENCIL;
		case COMPRESSED_RGB_S3TC_DXT1_EXT: return GLPixelDataFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT1_EXT: return GLPixelDataFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT3_EXT: return GLPixelDataFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case COMPRESSED_RGBA_S3TC_DXT5_EXT: return GLPixelDataFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
		}
		throw new RuntimeException("no pixel format for image pixelformat:" + theImage.pixelFormat());
	}
	
	public static GLPixelDataInternalFormat internalFormat(CCImage theImage){
		switch(theImage.internalFormat()){
		case ALPHA: return GLPixelDataInternalFormat.ALPHA;
		case ALPHA4: return GLPixelDataInternalFormat.ALPHA4;
		case ALPHA8: return GLPixelDataInternalFormat.ALPHA8;
		case ALPHA12: return GLPixelDataInternalFormat.ALPHA12;
		case ALPHA16: return GLPixelDataInternalFormat.ALPHA16;
		case COMPRESSED_ALPHA: return GLPixelDataInternalFormat.COMPRESSED_ALPHA;
		case LUMINANCE: return GLPixelDataInternalFormat.LUMINANCE;
		case LUMINANCE4: return GLPixelDataInternalFormat.LUMINANCE4;
		case LUMINANCE8: return GLPixelDataInternalFormat.LUMINANCE8;
		case LUMINANCE12: return GLPixelDataInternalFormat.LUMINANCE12;
		case LUMINANCE16: return GLPixelDataInternalFormat.LUMINANCE16;
		case COMPRESSED_LUMINANCE: return GLPixelDataInternalFormat.COMPRESSED_LUMINANCE;
		case LUMINANCE_ALPHA: return GLPixelDataInternalFormat.LUMINANCE_ALPHA;
		case LUMINANCE4_ALPHA4: return GLPixelDataInternalFormat.LUMINANCE4_ALPHA4;
		case LUMINANCE6_ALPHA2: return GLPixelDataInternalFormat.LUMINANCE6_ALPHA2;
		case LUMINANCE8_ALPHA8: return GLPixelDataInternalFormat.LUMINANCE8_ALPHA8;
		case LUMINANCE12_ALPHA4: return GLPixelDataInternalFormat.LUMINANCE12_ALPHA4;
		case LUMINANCE12_ALPHA12: return GLPixelDataInternalFormat.LUMINANCE12_ALPHA12;
		case LUMINANCE16_ALPHA16: return GLPixelDataInternalFormat.LUMINANCE16_ALPHA16;
		case COMPRESSED_LUMINANCE_ALPHA: return GLPixelDataInternalFormat.COMPRESSED_LUMINANCE_ALPHA;
		case INTENSITY: return GLPixelDataInternalFormat.INTENSITY;
		case INTENSITY4: return GLPixelDataInternalFormat.INTENSITY4;
		case INTENSITY8: return GLPixelDataInternalFormat.INTENSITY8;
		case INTENSITY12: return GLPixelDataInternalFormat.INTENSITY12;
		case INTENSITY16: return GLPixelDataInternalFormat.INTENSITY16;
		case COMPRESSED_INTENSITY: return GLPixelDataInternalFormat.COMPRESSED_INTENSITY;
		case DEPTH_COMPONENT: return GLPixelDataInternalFormat.DEPTH_COMPONENT;
		case DEPTH_COMPONENT16: return GLPixelDataInternalFormat.DEPTH_COMPONENT16;
		case DEPTH_COMPONENT24: return GLPixelDataInternalFormat.DEPTH_COMPONENT24;
		case DEPTH_COMPONENT32: return GLPixelDataInternalFormat.DEPTH_COMPONENT32;
		case RG: return GLPixelDataInternalFormat.RG;
		case RGB: return GLPixelDataInternalFormat.RGB;
		case RGB4: return GLPixelDataInternalFormat.RGB4;
		case RGB5: return GLPixelDataInternalFormat.RGB5;
		case RGB8: return GLPixelDataInternalFormat.RGB8;
		case RGB10: return GLPixelDataInternalFormat.RGB10;
		case RGB12: return GLPixelDataInternalFormat.RGB12;
		case RGB16: return GLPixelDataInternalFormat.RGB16;
		case RGB16I: return GLPixelDataInternalFormat.RGB16I;
		case RGB16UI: return GLPixelDataInternalFormat.RGB16UI;
		case RGB32I: return GLPixelDataInternalFormat.RGB32I;
		case RGB32UI: return GLPixelDataInternalFormat.RGB32UI;
		case R3_G3_B2: return GLPixelDataInternalFormat.R3_G3_B2;
		case COMPRESSED_RGB: return GLPixelDataInternalFormat.COMPRESSED_RGB;
		case RGBA: return GLPixelDataInternalFormat.RGBA;
		case RGBA2: return GLPixelDataInternalFormat.RGBA2;
		case RGBA4: return GLPixelDataInternalFormat.RGBA4;
		case RGB5_A1: return GLPixelDataInternalFormat.RGB5_A1;
		case RGBA8: return GLPixelDataInternalFormat.RGBA8;
		case RGB10_A2: return GLPixelDataInternalFormat.RGB10_A2;
		case RGBA12: return GLPixelDataInternalFormat.RGBA12;
		case RGBA16: return GLPixelDataInternalFormat.RGBA16;
		case RGBA16I: return GLPixelDataInternalFormat.RGBA16I;
		case RGBA16UI: return GLPixelDataInternalFormat.RGBA16UI;
		case COMPRESSED_RGBA: return GLPixelDataInternalFormat.COMPRESSED_RGBA;
		case SLUMINANCE: return GLPixelDataInternalFormat.SLUMINANCE;
		case SLUMINANCE8: return GLPixelDataInternalFormat.SLUMINANCE8;
		case SLUMINANCE_ALPHA: return GLPixelDataInternalFormat.SLUMINANCE_ALPHA;
		case SLUMINANCE8_ALPHA8: return GLPixelDataInternalFormat.SLUMINANCE8_ALPHA8;
		case SRGB: return GLPixelDataInternalFormat.SRGB;
		case SRGB8: return GLPixelDataInternalFormat.SRGB8;
		case SRGB_ALPHA: return GLPixelDataInternalFormat.SRGB_ALPHA;
		case SRGB8_ALPHA8: return GLPixelDataInternalFormat.SRGB8_ALPHA8;
		case BGR: return GLPixelDataInternalFormat.BGR;
		case BGRA: return GLPixelDataInternalFormat.BGRA;
		case RED: return GLPixelDataInternalFormat.RED;
		case BLUE: return GLPixelDataInternalFormat.BLUE;
		case GREEN: return GLPixelDataInternalFormat.GREEN;
		case STENCIL_INDEX: return GLPixelDataInternalFormat.STENCIL_INDEX;
		case COMPRESSED_RGB_S3TC_DXT1_EXT: return GLPixelDataInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT1_EXT: return GLPixelDataInternalFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case COMPRESSED_RGBA_S3TC_DXT3_EXT: return GLPixelDataInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case COMPRESSED_RGBA_S3TC_DXT5_EXT: return GLPixelDataInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
//		case ATC_RGBA_EXPLICIT_ALPHA_AMD: return GLPixelDataInternalFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD;
//		case ATC_RGBA_INTERPOLATED_ALPHA_AMD: return GLPixelDataInternalFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD;
//		case ETC1_RGB8_OES: return GLPixelDataInternalFormat.ETC1_RGB8_OES;
		case FLOAT_R16_NV: return GLPixelDataInternalFormat.FLOAT_R16_NV;
		case FLOAT_RG16_NV: return GLPixelDataInternalFormat.FLOAT_RG16_NV;
		case FLOAT_RGB16_NV: return GLPixelDataInternalFormat.FLOAT_RGB16_NV;
		case FLOAT_RGBA16_NV: return GLPixelDataInternalFormat.FLOAT_RGBA16_NV;
		case FLOAT_R32_NV: return GLPixelDataInternalFormat.FLOAT_R32_NV;
		case FLOAT_RG32_NV: return GLPixelDataInternalFormat.FLOAT_RG32_NV;
		case FLOAT_RGB32_NV: return GLPixelDataInternalFormat.FLOAT_RG32_NV;
		case FLOAT_RGBA32_NV: return GLPixelDataInternalFormat.FLOAT_RGBA32_NV;
		case RGB16F: return GLPixelDataInternalFormat.RGB16F;
		case RGBA16F: return GLPixelDataInternalFormat.RGBA16F;
		case RGB32F: return GLPixelDataInternalFormat.RGB32F;
		case RGBA32F: return GLPixelDataInternalFormat.RGBA32F;
		case RGBA32I: return GLPixelDataInternalFormat.RGBA32I;
		case RGBA32UI: return GLPixelDataInternalFormat.RGBA32UI;
		default:
		}
		throw new RuntimeException("no pixel internal format for image pixel internal format:" + theImage.internalFormat());
	}
	
	public static GLPixelDataType pixelType(CCImage theImage){
		switch(theImage.pixelType()){
		case UNSIGNED_BYTE: return GLPixelDataType.UNSIGNED_BYTE;
		case BYTE: return GLPixelDataType.BYTE;
		case BITMAP: return GLPixelDataType.BITMAP;
		case UNSIGNED_SHORT: return GLPixelDataType.UNSIGNED_SHORT;
		case SHORT: return GLPixelDataType.SHORT;
		case UNSIGNED_INT: return GLPixelDataType.UNSIGNED_INT;
		case INT: return GLPixelDataType.INT;
		case FIXED: return GLPixelDataType.FIXED;
		case FLOAT: return GLPixelDataType.FLOAT;
		case HALF_FLOAT: return GLPixelDataType.HALF_FLOAT;
		case DOUBLE: return GLPixelDataType.DOUBLE;
		case UNSIGNED_BYTE_2_3_3_REV: return GLPixelDataType.UNSIGNED_BYTE_2_3_3_REV;
		case UNSIGNED_BYTE_3_3_2: return GLPixelDataType.UNSIGNED_BYTE_3_3_2;
		case UNSIGNED_SHORT_5_6_5: return GLPixelDataType.UNSIGNED_SHORT_5_6_5;
		case UNSIGNED_SHORT_5_6_5_REV: return GLPixelDataType.UNSIGNED_SHORT_5_6_5_REV;
		case UNSIGNED_SHORT_4_4_4_4: return GLPixelDataType.UNSIGNED_SHORT_4_4_4_4;
		case UNSIGNED_SHORT_4_4_4_4_REV: return GLPixelDataType.UNSIGNED_SHORT_4_4_4_4_REV;
		case UNSIGNED_SHORT_5_5_5_1: return GLPixelDataType.UNSIGNED_SHORT_5_5_5_1;
		case UNSIGNED_SHORT_1_5_5_5_REV: return GLPixelDataType.UNSIGNED_SHORT_1_5_5_5_REV;
		case UNSIGNED_INT_8_8_8_8: return GLPixelDataType.UNSIGNED_INT_8_8_8_8;
		case UNSIGNED_INT_8_8_8_8_REV: return GLPixelDataType.UNSIGNED_INT_8_8_8_8_REV;
		case UNSIGNED_INT_10_10_10_2: return GLPixelDataType.UNSIGNED_INT_10_10_10_2;
		case UNSIGNED_INT_2_10_10_10_REV: return GLPixelDataType.UNSIGNED_INT_2_10_10_10_REV;
		case UNSIGNED_INT_24_8: return GLPixelDataType.UNSIGNED_INT_24_8;
		case UNSIGNED_INT_10F_11F_11F_REV: return GLPixelDataType.UNSIGNED_INT_10F_11F_11F_REV;
		case UNSIGNED_INT_5_9_9_9_REV: return GLPixelDataType.UNSIGNED_INT_5_9_9_9_REV;
		case FLOAT_32_UNSIGNED_INT_24_8_REV: return GLPixelDataType.FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		throw new RuntimeException("no pixel type for image pixel type:" + theImage.pixelType());
	}
	
	/**
	 * Applies the default pixel storage settings for unpack operations.
	 * @see #unpackStorage()
	 */
	protected void defaultUnpackStorage() {
		
		
		glPixelStorei(GL_UNPACK_SWAP_BYTES, 0);
		glPixelStorei(GL_UNPACK_LSB_FIRST, 0);
		
		glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
		glPixelStorei(GL_UNPACK_IMAGE_HEIGHT, 0);
		
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
		glPixelStorei(GL_UNPACK_SKIP_IMAGES, 0);
	}
	
	/**
	 * Applies the default pixel storage settings for pack operations.
	 * @see #packStorage()
	 */
	public void defaultPackStorage() {
		

		glPixelStorei(GL_PACK_SWAP_BYTES, 0);
		glPixelStorei(GL_PACK_LSB_FIRST, 0);
		
		glPixelStorei(GL_PACK_ROW_LENGTH, 0);
		glPixelStorei(GL_PACK_ALIGNMENT, 4);
		glPixelStorei(GL_PACK_IMAGE_HEIGHT, 0);
		
		glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
		glPixelStorei(GL_PACK_SKIP_ROWS, 0);
		glPixelStorei(GL_PACK_SKIP_IMAGES, 0);
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

		
		unpackStorage(theImage.pixelStorageModes());
		dataImplementation(theImage);
		defaultUnpackStorage();
		
		textureFilter(GLTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);
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
	public boolean compressData(final GLTextureCompressionHint theHint, final CCImage theImage) {
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
			_myInternalFormat = GLPixelDataInternalFormat.COMPRESSED_RGB;
			break;
		case RGBA:
		case RGBA2:
		case RGBA4:
		case RGBA8:
		case RGBA12:
		case RGBA16:
		case RGB5_A1:
		case RGB10_A2:
			_myInternalFormat = GLPixelDataInternalFormat.COMPRESSED_RGBA;
			break;
		default:
		}
		
		bind();
		
		glHint(GL_TEXTURE_COMPRESSION_HINT, theHint.glID);
		unpackStorage(theImage.pixelStorageModes());
		switch(_myTarget) {
		case TEXTURE_1D:
			if(theImage.buffer() instanceof ByteBuffer){
				glTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, 
					_myFormat.glID, _myPixelType.glID, (ByteBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof ShortBuffer){
				glTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, 
					_myFormat.glID, _myPixelType.glID, (ShortBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof IntBuffer){
				glTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, 
					_myFormat.glID, _myPixelType.glID, (IntBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof FloatBuffer){
				glTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, 
					_myFormat.glID, _myPixelType.glID, (FloatBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof DoubleBuffer){
				glTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, 
					_myFormat.glID, _myPixelType.glID, (DoubleBuffer)theImage.buffer()
				);
			}
			
			break;
		case TEXTURE_2D:
		case TEXTURE_RECT:
			if(theImage.buffer() instanceof ByteBuffer){
				glTexImage2D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), 0, 
					_myFormat.glID, _myPixelType.glID, (ByteBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof ShortBuffer){
				glTexImage2D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), 0, 
					_myFormat.glID, _myPixelType.glID, (ShortBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof IntBuffer){
				glTexImage2D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), 0, 
					_myFormat.glID, _myPixelType.glID, (IntBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof FloatBuffer){
				glTexImage2D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), 0, 
					_myFormat.glID, _myPixelType.glID, (FloatBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof DoubleBuffer){
				glTexImage2D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), 0, 
					_myFormat.glID, _myPixelType.glID, (DoubleBuffer)theImage.buffer()
				);
			}
			
			break;
		default:
		}
		defaultUnpackStorage();
		
		textureFilter(GLTextureFilter.LINEAR);
		
		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);
		
		int[] myData = new int[1];
		
		glGetTexLevelParameteriv(_myTarget.glID, 0, GL_TEXTURE_COMPRESSED, myData);
		
		boolean myResult = myData[0] > 0;
		
		if(myResult) {
			glGetTexLevelParameteriv(_myTarget.glID, 0, GL_TEXTURE_COMPRESSED_IMAGE_SIZE, myData);
			_myEstimatedMemorySize = myData[0];
			glGetTexLevelParameteriv(_myTarget.glID, 0, GL_TEXTURE_INTERNAL_FORMAT, myData);
		}
		
		return myResult;
	}
	
	public boolean compressData(final CCImage theImage) {
		return compressData(GLTextureCompressionHint.DONT_CARE, theImage);
	}
	
	public abstract void updateData(final CCImage theImage);
	
	protected int[] createTextureIds(final int theNumberOfIds) {
		
		int[] tmp = new int[theNumberOfIds];
		glGenTextures(tmp);
		return tmp;
	}
	
	public void bind() {
		bind(_myTextureID);
	}
	
	public void bind(final int theID) {
		
		glBindTexture(_myTarget.glID, _myTextureIDs[theID]);
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, _myEnvironmentMode.glID);
		
		if(_myEnvironmentMode == GLTextureEnvironmentMode.BLEND) {
			float[] myColor = {(float)_myBlendColor.r, (float)_myBlendColor.g, (float)_myBlendColor.b, (float)_myBlendColor.a};
			glTexEnvfv(GL_TEXTURE_ENV, GL_TEXTURE_ENV_COLOR, myColor);
		}
	}
	
	public void unbind() {
		
		glBindTexture(_myTarget.glID, 0);
	}
	
	public int id() {
		return _myTextureIDs[_myTextureID];
	}
	
	public int id(final int theLevel) {
		return _myTextureIDs[theLevel];
	}
	
	@Override
	public void finalize() {
//		glDeleteTextures(_myTextureIDs.length, _myTextureIDs, 0);
	}
	
	/**
	 * Returns the target of the texture can be 1D, 2D, 3D, RECT and CUBEMAP.
	 * @return target of the texture
	 */
	public GLTextureTarget target() {
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
	@CCProperty(name = "must flip vertically")
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
	public GLPixelDataFormat format() {
		return _myFormat;
	}
	
	/**
	 * 
	 * @return
	 */
	public GLPixelDataInternalFormat internalFormat() {
		return _myInternalFormat;
	}
	
	
	public GLPixelDataType pixelType() {
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
			glTexParameteri(_myTarget.glID, theType, theValue);
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
			glTexParameterf(_myTarget.glID, theType, theValue);
			
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
			glTexParameterfv(_myTarget.glID, theType, theValues);
		}
	}
	
	/**
	 * Shortcut to get a texture parameter
	 * @param theGLID the gl id of the parameter to get
	 * @return the value for the given parameter
	 */
	protected int parameteri(final int theGLID) {
		return glGetTexParameteri(_myTarget.glID, theGLID);
	}
	protected float parameterf(final int theGLID) {
		return glGetTexParameterf(_myTarget.glID, theGLID);
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. Using this method you can set the wrap mode for each coordinate 
	 * individually. The wrap mode can then be set to one of the following values:
	 * <ul>
	 * <li>{@link GLTextureWrap#REPEAT} simply causes the texture to repeat in the direction in which the texture 
	 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
	 * of the texture size. This mode is very useful for applying a small tiled texture to large 
	 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
	 * larger texture, but at the cost of a much smaller texture image.</li>
	 * <li>{@link GLTextureWrap#MIRRORED_REPEAT} works like {@link GLTextureWrap#REPEAT} but mirrors 
	 * the texture for more seamless results on repeating the texture</li>
	 * <li>{@link GLTextureWrap#CLAMP} clamps the texture if you use values going over the image range. The needed
	 * texels are taken from the texture border.
	 * results on repeating the texture</li>
	 * <li>{@link GLTextureWrap#CLAMP_TO_EDGE} simply ignores texel samples that go over the edge and does not include them in the average</li>
	 * <li>{@link GLTextureWrap#CLAMP_TO_BORDER} uses only border texels whenever the texture coordinates fall outside the of the texture.</li>
	 * </ul>
	 * @param theTextureWrap mode for texture wrapping 
	 */
	public void wrap(final GLTextureWrap theTextureWrap){
		parameter(GL_TEXTURE_WRAP_R, theTextureWrap.glID);
		parameter(GL_TEXTURE_WRAP_S, theTextureWrap.glID);
		parameter(GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	@CCProperty(name = "wrap r")
	public void wrapR(final GLTextureWrap theTextureWrap){
		parameter(GL_TEXTURE_WRAP_R, theTextureWrap.glID);
	}
	
	public GLTextureWrap wrapR(){
		return GLTextureWrap.byGLID(parameteri(GL_TEXTURE_WRAP_R));
	}
	
	/**
	 * Sets the horizontal wrapping behavior when a texture coordinate falls outside the range of [0,1].
	 * @see #wrap(GLTextureWrap)
	 * @param theTextureWrap
	 */
	@CCProperty(name = "wrap s")
	public void wrapS(final GLTextureWrap theTextureWrap){
		parameter(GL_TEXTURE_WRAP_S, theTextureWrap.glID);
	}
	
	public GLTextureWrap wrapS(){
		return GLTextureWrap.byGLID(parameteri(GL_TEXTURE_WRAP_S));
	}
	
	/**
	 * 
	 * @param theTextureWrap
	 */
	@CCProperty(name = "wrap t")
	public void wrapT(final GLTextureWrap theTextureWrap) {
		parameter(GL_TEXTURE_WRAP_T, theTextureWrap.glID);
	}
	
	public GLTextureWrap wrapT(){
		return GLTextureWrap.byGLID(parameteri(GL_TEXTURE_WRAP_T));
	}
	
	public void textureBorderColor(final CCColor theColor) {
		float[] myColor = {(float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a};
		parameter(GL_TEXTURE_BORDER_COLOR, myColor);
	}
	
	private void updateFilter() {
		// set mag filter first as this has no impact on mipmapping
		parameter(GL_TEXTURE_MAG_FILTER, _myTextureFilter.glID);
			
		if(!_myHasMipmaps) {
			parameter(GL_TEXTURE_MIN_FILTER, _myTextureFilter.glID);
			return;
		}
			
		if(_myTextureFilter == GLTextureFilter.NEAREST) {
			if(_myTextureMipmapFilter == GLTextureMipmapFilter.NEAREST) {
				parameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
			}else {
				parameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
			}
		}else {
			if(_myTextureMipmapFilter == GLTextureMipmapFilter.NEAREST) {
				parameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
			}else {
				parameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
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
	@CCProperty(name = "anisotropic filtering", min = 0, max = 1)
	public void anisotropicFiltering(final float theAmount) {
		try(MemoryStack stack = MemoryStack.stackPush()){
			FloatBuffer floatBuff = stack.mallocFloat(1);
			glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, floatBuff);
			float GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = floatBuff.get(0);
			parameter(GL_TEXTURE_MAX_ANISOTROPY_EXT, CCMath.blend(1, GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, theAmount));
		}
	}
	
	public float anisotropicFiltering(){
		return parameterf(GL_TEXTURE_MAX_ANISOTROPY_EXT);
	}
	
	/**
	 * <p>
	 * The texture filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element. There are two 
	 * defined filter functions. 
	 * <ul>
	 * <li>{@link GLTextureFilter#NEAREST} uses the nearest pixel</li>
	 * <li>{@link GLTextureFilter#LINEAR} uses the nearest four texture elements to compute the texture value.</li>
	 * </ul>
	 * </p>
	 * The default is {@link GLTextureFilter#LINEAR}.       
	 * @param theFilter
	 */
	@CCProperty(name = "filter")
	public void textureFilter(final GLTextureFilter theFilter){
		_myTextureFilter = theFilter;
		updateFilter();
	}
	
	public GLTextureFilter textureFilter(){
		return _myTextureFilter;
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
	@CCProperty(name = "mip map filter")
	public void textureMipmapFilter(final GLTextureMipmapFilter theFilter){
		_myTextureMipmapFilter = theFilter;
		updateFilter();
	}
	
	public GLTextureMipmapFilter textureMipmapFilter(){
		return _myTextureMipmapFilter;
	}
	
	/**
	 * Defines how colors from the texels are combined with the color of the
	 * underlying geometry.
	 * @param theMode
	 */
	@CCProperty(name = "environment mode")
	public void textureEnvironmentMode(final GLTextureEnvironmentMode theMode) {
		_myEnvironmentMode = theMode;
	}
	
	public GLTextureEnvironmentMode textureEnvironmentMode() {
		return _myEnvironmentMode;
	}
	
	/**
	 * Specifies a single symbolic constant indicating how depth values should be 
	 * treated during filtering and texture application.
	 * @param theMode
	 */
	public void depthTextureMode(final GLDepthTextureMode theMode) {
		parameter(GL_DEPTH_TEXTURE_MODE, theMode.glID);
	}
	
	/**
	 * Textures can also be blended with a constant blending color using the 
	 * <code>BLEND</code> texture environment mode. If you set this environment mode, 
	 * you must also set the texture environment color with this method.
	 * @param theBlendColor blend color for the blend mode
	 */
	@CCProperty(name = "blend color")
	public void blendColor(final CCColor theBlendColor) {
		_myBlendColor = theBlendColor;
	}
	
	public CCColor blendColor() {
		return _myBlendColor;
	}
	
	
}
