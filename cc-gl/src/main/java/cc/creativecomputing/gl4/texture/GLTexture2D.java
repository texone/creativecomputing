package cc.creativecomputing.gl4.texture;

import java.nio.Buffer;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLPixelDataFormat;
import cc.creativecomputing.gl4.GLPixelDataInternalFormat;
import cc.creativecomputing.gl4.GLPixelDataType;
import cc.creativecomputing.image.CCImage;

import com.jogamp.opengl.GL4;

public class GLTexture2D extends GLTexture{
	
	public GLTexture2D(){
		super(GLTextureTarget.TEXTURE_2D);
	}
	
	public GLTexture2D(int theMipMapLevel, GLPixelDataInternalFormat theFormat, int theWidth, int theHeight){
		this();
		bind();
		
		texStorage2D( theMipMapLevel, theFormat, theWidth, theHeight);
	}
	
	public GLTexture2D(CCImage theImage){
		super(GLTextureTarget.TEXTURE_2D);
		bind();
		
		GL4 gl = GLGraphics.currentGL();
		gl.glTexImage2D(
			_myTarget.glID(), 
			0, 
			GLPixelDataInternalFormat.fromCC(theImage.internalFormat()).glID(), 
			theImage.width(), theImage.height(), 0, 
			GLPixelDataFormat.fromCC(theImage.pixelFormat()).glID(), 
			GLPixelDataType.fromCC(theImage.pixelType()).glID(),
			theImage.buffer()
		);
	}
	
	/**
	 * Specify the storage requirements for all levels of a two-dimensional
	 * texture or one-dimensional texture array simultaneously. Once a texture
	 * is specified with this command, the format and dimensions of all levels
	 * become immutable unless it is a proxy texture. The contents of the image
	 * may still be modified, however, its storage requirements may not change.
	 * Such a texture is referred to as an immutable-format texture.
	 * 
	 * @param theLevels Specify the number of texture levels.
	 * @param theFormat Specifies the sized internal format to be used to store texture image data.
	 * @param theWidth Specifies the width of the texture, in texels.
	 * @param theHeight Specifies the height of the texture, in texels.
	 */
	public void texStorage2D(int theLevels, GLPixelDataInternalFormat theFormat, int theWidth, int theHeight){
		GL4 gl = GLGraphics.currentGL();
		gl.glTexStorage2D(_myTarget.glID(), theLevels, theFormat.glID(), theWidth, theHeight);
	}
	
	/**
	 * Texturing allows elements of an image array to be read by shaders.
	 * <p>
	 * To define texture images, call texImage2D. The arguments describe the
	 * parameters of the texture image, such as height, width, width of the
	 * border, level-of-detail number (see glTexParameter), and number of color
	 * components provided. The last three arguments describe how the image is
	 * represented in memory.
	 * <p>
	 * If target is {@linkplain GLTextureTarget#PROXY_TEXTURE_2D},
	 * {@linkplain GLTextureTarget#PROXY_TEXTURE_1D_ARRAY},
	 * {@linkplain GLTextureTarget#PROXY_TEXTURE_CUBE_MAP} or
	 * {@linkplain GLTextureTarget#PROXY_TEXTURE_RECTANGLE} no data is read from
	 * data, but all of the texture image state is recalculated, checked for
	 * consistency, and checked against the implementation's capabilities. If
	 * the implementation cannot handle a texture of the requested texture size,
	 * it sets all of the image state to 0, but does not generate an error (see
	 * glGetError). To query for an entire mipmap array, use an image array
	 * level greater than or equal to 1.
	 * <p>
	 * If target is {@linkplain GLTextureTarget#TEXTURE_2D},
	 * {@linkplain GLTextureTarget#TEXTURE_RECTANGLE} or one of the
	 * {@linkplain GLTextureTarget#TEXTURE_CUBE_MAP} targets,
	 * <p>
	 * data is read from data as a sequence of signed or unsigned bytes, shorts,
	 * or longs, or single-precision floating-point values, depending on type.
	 * These values are grouped into sets of one, two, three, or four values,
	 * depending on format, to form elements. Each data byte is treated as eight
	 * 1-bit elements, with bit ordering determined by GL_UNPACK_LSB_FIRST (see
	 * glPixelStore).
	 * <p>
	 * If target is {@linkplain GLTextureTarget#TEXTURE_1D_ARRAY}, data is
	 * interpreted as an array of one-dimensional images.
	 * <p>
	 * If a non-zero named buffer object is bound to the GL_PIXEL_UNPACK_BUFFER
	 * target (see glBindBuffer) while a texture image is specified, data is
	 * treated as a byte offset into the buffer object's data store.
	 * <p>
	 * The first element corresponds to the lower left corner of the texture
	 * image. Subsequent elements progress left-to-right through the remaining
	 * texels in the lowest row of the texture image, and then in successively
	 * higher rows of the texture image. The final element corresponds to the
	 * upper right corner of the texture image.
	 * <p>
	 * format determines the composition of each element in data.
	 * 
	 * @param theLevel
	 *            Specifies the level-of-detail number. Level 0 is the base
	 *            image level. Level n is the nth mipmap reduction image. If
	 *            target is {@linkplain GLTextureTarget#TEXTURE_RECTANGLE} or
	 *            {@linkplain GLTextureTarget#PROXY_TEXTURE_RECTANGLE}, level
	 *            must be 0.
	 * @param theInternalFormat
	 *            Specifies the number of color components in the texture. Must
	 *            be one of base internal formats
	 * @param theWidth
	 *            Specifies the width of the texture image. All implementations
	 *            support texture images that are at least 1024 texels wide.
	 * @param theHeight
	 *            Specifies the height of the texture image, or the number of
	 *            layers in a texture array, in the case of the
	 *            {@linkplain GLTextureTarget#TEXTURE_1D_ARRAY} and
	 *            {@linkplain GLTextureTarget#PROXY_TEXTURE_1D_ARRAY} targets.
	 *            All implementations support 2D texture images that are at
	 *            least 1024 texels high, and texture arrays that are at least
	 *            256 layers deep.
	 * @param theBorder
	 *            This value must be 0.
	 * @param theFormat
	 *            Specifies the format of the pixel data.
	 * @param theType
	 *            Specifies the data type of the pixel data.
	 * @param thePixels
	 *            Specifies a pointer to the image data in memory.
	 */
	public void texImage2D(
		int theLevel,
		GLPixelDataInternalFormat theInternalFormat,
		int theWidth, int theHeight,
		int theBorder, 
		GLPixelDataFormat theFormat,
		GLPixelDataType theType,
		Buffer thePixels
	){
		GL4 gl = GLGraphics.currentGL();
		gl.glTexImage2D(_myTarget.glID(), theLevel, theInternalFormat.glID(), theWidth, theHeight, theBorder, theFormat.glID(), theType.glID(), thePixels);
	}
	
	/**
	 * Redefine a contiguous subregion of an existing two-dimensional or
	 * one-dimensional array texture image. The texels referenced by pixels
	 * replace the portion of the existing texture array with x indices xoffset
	 * and xoffset + width - 1 , inclusive, and y indices yoffset and yoffset +
	 * height - 1 , inclusive. This region may not include any texels outside
	 * the range of the texture array as it was originally specified. It is not
	 * an error to specify a subtexture with zero width or height, but such a
	 * specification has no effect.
	 * 
	 * @param theLevel
	 *            Specifies the level-of-detail number. Level 0 is the base
	 *            image level. Level n is the nth mipmap reduction image.
	 * @param theXOffset
	 *            Specifies a texel offset in the x direction within the texture
	 *            array.
	 * @param theYOffset
	 *            Specifies a texel offset in the y direction within the texture
	 *            array.           
	 * @param theWidh
	 *            Specifies the width of the texture subimage.
	 * @param theHeight
	 *            Specifies the height of the texture subimage.
	 * @param theFormat
	 *            Specifies the format of the pixel data.
	 * @param theType
	 *            Specifies the data type of the pixel data.
	 * @param thePixels
	 *            Specifies a pointer to the image data in memory.
	 */
	public void texSubImage2D(
		int theLevel, 
		int theXOffset, int theYOffset, 
		int theWidh, int theHeight, 
		GLPixelDataFormat theFormat, GLPixelDataType theType,
		Buffer thePixels
	) {
		GL4 gl = GLGraphics.currentGL();
		gl.glTexSubImage2D(_myTarget.glID(), theLevel, theXOffset, theYOffset, theWidh, theHeight, theFormat.glID(), theType.glID(), thePixels);
	}
	
	public void texSubImage2D(int theLevel, int theXOffset, int theYOffset, int theWidh, int theHeight, CCImage theImage){
		texSubImage2D(
			theLevel, 
			theXOffset, theYOffset,
			theWidh, theHeight, 
			GLPixelDataFormat.fromCC(theImage.pixelFormat()), 
			GLPixelDataType.fromCC(theImage.pixelType()),
			theImage.buffer()
		);
	}
	
	public void texSubImage2D(int theLevel,int theXOffset, int theYOffset, CCImage theImage){
		texSubImage2D(theLevel, theXOffset, theYOffset, theImage.width(), theImage.height(), theImage);
	}
	
	public void texSubImage2D(int theXOffset, int theYOffset, CCImage theImage){
		texSubImage2D(0,theXOffset, theYOffset, theImage);
	}
	
	public void texImage2D(CCImage theImage){
		texSubImage2D(0, 0, 0, theImage);
	}

	/**
	 * If a non-zero named buffer object is bound to the GL_PIXEL_UNPACK_BUFFER
	 * target (see glBindBuffer) while a texture image is specified, pixels is
	 * treated as a byte offset into the buffer object's data store.
	 * 
	 * @param theLevel
	 *            Specifies the level-of-detail number. Level 0 is the base
	 *            image level. Level n is the nth mipmap reduction image.
	 * @param theXOffset
	 *            Specifies a texel offset in the x direction within the texture
	 *            array.
	 * @param theYOffset
	 *            Specifies a texel offset in the y direction within the texture
	 *            array.
	 * @param theWidh
	 *            Specifies the width of the texture subimage.
	 * @param theHeight
	 *            Specifies the height of the texture subimage.
	 * @param theFormat
	 *            Specifies the format of the pixel data.
	 * @param theType
	 *            Specifies the data type of the pixel data.
	 * @param theOffset
	 *            byte offset into the buffer object's data store.
	 */
	public void texSubImage2D(
		int theLevel, 
		int theXOffset, int theYOffset, 
		int theWidh, int theHeight, 
		GLPixelDataFormat theFormat, GLPixelDataType theType,
		long theOffset
	) {
		GL4 gl = GLGraphics.currentGL();
		gl.glTexSubImage2D(_myTarget.glID(), theLevel, theXOffset, theYOffset, theWidh, theHeight, theFormat.glID(), theType.glID(), theOffset);
	}
}
