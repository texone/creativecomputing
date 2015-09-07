package cc.creativecomputing.gl4.texture;

import java.nio.Buffer;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLPixelDataFormat;
import cc.creativecomputing.gl4.GLPixelDataInternalFormat;
import cc.creativecomputing.gl4.GLPixelDataType;

import com.jogamp.opengl.GL4;

public class GLTexture1D extends GLTexture{

	public GLTexture1D(int theMipMapLevel, GLPixelDataInternalFormat theFormat, int theWidth){
		super(GLTextureTarget.TEXTURE_1D);
		bind();
		texStorage1D(theMipMapLevel, theFormat, theWidth);
	}
	
	/**
	 * Specify the storage requirements for all levels of a one-dimensional
	 * texture simultaneously. Once a texture is specified with this command,
	 * the format and dimensions of all levels become immutable unless it is a
	 * proxy texture. The contents of the image may still be modified, however,
	 * its storage requirements may not change. Such a texture is referred to as
	 * an immutable-format texture.
	 * 
	 * @param theLevels Specify the number of texture levels.
	 * @param theFormat Specifies the sized internal format to be used to store texture image data.
	 * @param theWidth Specifies the width of the texture, in texels.
	 */
	public void texStorage1D(int theLevels, GLPixelDataInternalFormat theFormat, int theWidth){
		GL4 gl = GLGraphics.currentGL();
		gl.glTexStorage1D(_myTarget.glID(), theLevels, theFormat.glID(), theWidth);
	}
	
	/**
	 * redefine a contiguous subregion of an existing one-dimensional texture
	 * image. The texels referenced by pixels replace the portion of the
	 * existing texture array with x indices xoffset and xoffset + width - 1 ,
	 * inclusive. This region may not include any texels outside the range of
	 * the texture array as it was originally specified. It is not an error to
	 * specify a subtexture with width of 0, but such a specification has no
	 * effect.
	 * 
	 * @param theLevel Specifies the level-of-detail number. Level 0 is the base image level. Level n is the nth mipmap reduction image.
	 * @param theXOffset Specifies a texel offset in the x direction within the texture array.
	 * @param theWidh Specifies the width of the texture subimage.
	 * @param theFormat Specifies the format of the pixel data. 
	 * @param theType Specifies the data type of the pixel data.
	 * @param thePixels Specifies a pointer to the image data in memory.
	 */
	public void texSubImage1D(int theLevel, int theXOffset, int theWidh, GLPixelDataFormat theFormat, GLPixelDataType theType, Buffer thePixels){
		GL4 gl = GLGraphics.currentGL();
		gl.glTexSubImage1D(_myTarget.glID(), theLevel, theXOffset, theWidh, theFormat.glID(), theType.glID(), thePixels);
	}
	
	/**
	 * If a non-zero named buffer object is bound to the GL_PIXEL_UNPACK_BUFFER
	 * target (see glBindBuffer) while a texture image is specified, offset is
	 * treated as a byte offset into the buffer object's data store.
	 * 
	 * @param theLevel Specifies the level-of-detail number. Level 0 is the base image level. Level n is the nth mipmap reduction image.
	 * @param theXOffset Specifies a texel offset in the x direction within the texture array.
	 * @param theWidh Specifies the width of the texture subimage.
	 * @param theFormat Specifies the format of the pixel data. 
	 * @param theType Specifies the data type of the pixel data.
	 * @param theOffset byte offset into the buffer object's data store.
	 */
	public void texSubImage1D(int theLevel, int theXOffset, int theWidh, GLPixelDataFormat theFormat, GLPixelDataType theType, long theOffset){
		GL4 gl = GLGraphics.currentGL();
		gl.glTexSubImage1D(_myTarget.glID(), theLevel, theXOffset, theWidh, theFormat.glID(), theType.glID(), theOffset);
	}
}
