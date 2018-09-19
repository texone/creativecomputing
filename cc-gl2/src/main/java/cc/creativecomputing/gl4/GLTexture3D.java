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

import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL12.glCopyTexSubImage3D;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.glCompressedTexImage3D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;


/**
 * This class represents a 3d texture. You can create textures
 * by loading all kind of images as texture data and than pass the content
 * to a texture object. 
 * @author christian riekoff
 *
 */
public class GLTexture3D extends GLTexture{

	/**
	 * Creates a new 2d texture.
	 */
	public GLTexture3D() {
		super(GLTextureTarget.TEXTURE_3D, new GLTextureAttributes());
	}

	/**
	 * Creates a new 3D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public GLTexture3D(final GLTextureAttributes theTextureAttributes) {
		super(GLTextureTarget.TEXTURE_3D, theTextureAttributes);
	}
	
	/**
	 * Creates a new 3d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theTextureData
	 */
	public GLTexture3D(final CCImage theTextureData) {
		this();
		data(theTextureData);
	}
	
	/**
	 * Creates a new 2d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theTextureData
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public GLTexture3D(final CCImage theTextureData, final GLTextureAttributes theTextureAttributes) {
		this(theTextureAttributes);
		data(theTextureData);
	}
	
	public GLTexture3D(final int theWidth, final int theHeight, final int theDepth) {
		this();
		allocateData(theWidth, theHeight, theDepth);
	}
	
	public GLTexture3D(final CCImage theImage, final int theDepth) {
		this();
		allocateData(theImage.width(), theImage.height(), theDepth);
		updateData(theImage, 0);
		_myMustFlipVertically = theImage.mustFlipVertically();
	}
	
	public GLTexture3D(GLTextureAttributes theAttributes, final int theWidth, final int theHeight, final int theDepth){
		super(GLTextureTarget.TEXTURE_3D, theAttributes, theDepth);
		allocateData(theWidth, theHeight, theDepth);
	}
	
	private void allocateData(final int theWidth, final int theHeight, final int theDepth) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myDepth = theDepth;
		
//		FloatBuffer myBuffer = FloatBuffer.allocate(4 * _myWidth * _myHeight * _myDepth);
//		for(int i = 0; i < _myWidth * _myHeight * _myDepth;i++) {
//			myBuffer.put(255f);
//			myBuffer.put(0f);
//			myBuffer.put(0f);
//			myBuffer.put(255f);
//		}
//		myBuffer.rewind();
		
		bind();
		unpackStorage(_myStorageModes);
		
		glTexImage3D(
			_myTarget.glID, 0, _myInternalFormat.glID, 
			_myWidth, _myHeight, _myDepth, 0, 
			_myFormat.glID, 
			_myPixelType.glID, 
			(ByteBuffer)null
		); 
		defaultUnpackStorage();
	}
	
	public void dataImplementation(final CCImage theImage) {
		if(theImage.isDataCompressed()) {
			glCompressedTexImage3D(
				_myTarget.glID, 0, _myInternalFormat.glID, 
				theImage.width(), theImage.height(), _myDepth, 0, 
				(ByteBuffer)theImage.buffer()
			);
		}else {
			
			if(theImage.buffer() instanceof ByteBuffer){
				glTexImage3D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), _myDepth, 0, 
					_myFormat.glID, 
					_myPixelType.glID, 
					(ByteBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof ShortBuffer){
				glTexImage3D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), _myDepth, 0, 
					_myFormat.glID, 
					_myPixelType.glID, 
					(ShortBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof IntBuffer){
				glTexImage3D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), _myDepth, 0, 
					_myFormat.glID, 
					_myPixelType.glID, 
					(IntBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof FloatBuffer){
				glTexImage3D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), _myDepth, 0, 
					_myFormat.glID, 
					_myPixelType.glID, 
					(FloatBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof DoubleBuffer){
				glTexImage3D(
					_myTarget.glID, 0, _myInternalFormat.glID, 
					theImage.width(), theImage.height(), _myDepth, 0, 
					_myFormat.glID, 
					_myPixelType.glID, 
					(DoubleBuffer)theImage.buffer()
				);
			}
		}
	}
	
	/**
	 * Replaces the content of the texture with pixels from the framebuffer. You can use this method
	 * to copy pixels from the framebuffer to a texture.
	 * @param g reference to the graphics object to copy the pixels from the framebuffer
	 * @param theDestX target x position for the copied data
	 * @param theDestY target y position for the copied data
	 * @param theSrcX source x position o the copied data
	 * @param theSrcY source y position o the copied data
	 * @param theWidth the with of the region to copy
	 * @param theHeight the height of the region to copy
	 */
	public void updateData(
		final CCGraphics g, 
		int theDestX, int theDestY, int theDestZ,
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight
	) {
		_myPixelMap = null;
		
		theSrcX = CCMath.constrain(theSrcX, 0, g.width());
		theSrcY = CCMath.constrain(theSrcY, 0, g.height());
		
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theDestY = CCMath.constrain(theDestY, 0, _myHeight);
		
		theWidth = CCMath.min(theWidth, g.width() - theSrcX, _myWidth - theDestX);
		theWidth = CCMath.min(theWidth, g.width() - theSrcX, _myWidth - theDestX);
		
		bind();
		glCopyTexSubImage3D(
			_myTarget.glID, 0, 
			theDestX, theDestY, theDestZ,
			theSrcX, theSrcY, 
			theWidth, theHeight
		);

		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture. This method makes it also possible to just copy
	 * a region of the texture data into the texture
	 * 
	 * @param theImage the source data to copy to the texture
	 * 
	 * @param theDestX the target x position for the copied data
	 * @param theDestY the target y position for the copied data
	 * 
	 * @param theSrcX the source x position of the copied data
	 * @param theSrcY the source y position of the copied data
	 *  
	 * @param theWidth the width of the copied region
	 * @param theHeight the height of the copied region
	 */
	public void updateData(
		CCImage theImage, 
		int theDestX, int theDestY, int theDestZ,
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight
	) {
		_myMustFlipVertically = theImage.mustFlipVertically();
		
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theDestY = CCMath.constrain(theDestY, 0, _myHeight);
		
		theSrcX = CCMath.constrain(theSrcX, 0, theImage.width());
		theSrcY = CCMath.constrain(theSrcY, 0, theImage.height());
		
		theWidth = CCMath.min(theWidth, theImage.width() - theSrcX, _myWidth - theDestX);
		theHeight = CCMath.min(theHeight, theImage.height() - theSrcY, _myHeight - theDestY);
		
		bind();
		
		
		unpackStorage(theImage.pixelStorageModes());

		glPixelStorei(GL_UNPACK_SKIP_PIXELS, theSrcX);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, theSrcY);
		
		GLPixelDataFormat myFormat = pixelFormat(theImage);
		GLPixelDataType myType = pixelType(theImage);
		
		
		if(theImage.buffer() instanceof ByteBuffer){
			glTexSubImage3D(
				_myTarget.glID, 0, 
				theDestX, theDestY, theDestZ,
				theImage.width(), theImage.height(), 1,
				myFormat.glID, myType.glID, (ByteBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof ShortBuffer){
			glTexSubImage3D(
				_myTarget.glID, 0, 
				theDestX, theDestY, theDestZ,
				theImage.width(), theImage.height(), 1,
				myFormat.glID, myType.glID, (ShortBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof IntBuffer){
			glTexSubImage3D(
				_myTarget.glID, 0, 
				theDestX, theDestY, theDestZ,
				theImage.width(), theImage.height(), 1,
				myFormat.glID, myType.glID, (IntBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof FloatBuffer){
			glTexSubImage3D(
				_myTarget.glID, 0, 
				theDestX, theDestY, theDestZ,
				theImage.width(), theImage.height(), 1,
				myFormat.glID, myType.glID, (FloatBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof DoubleBuffer){
			glTexSubImage3D(
				_myTarget.glID, 0, 
				theDestX, theDestY, theDestZ,
				theImage.width(), theImage.height(), 1,
				myFormat.glID, myType.glID, (DoubleBuffer)theImage.buffer()
			);
		}
		
		defaultUnpackStorage();
		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture. This method makes it also possible to just copy
	 * a region of the texture data into the texture dependent on the given
	 * width and height.
	 * 
	 * @param theImage the source data to copy to the texture
	 * 
	 * @param theDestX the target x position for the copied data
	 * @param theDestY the target y position for the copied data
	 *  
	 * @param theWidth the width of the copied region
	 * @param theHeight the height of the copied region
	 */
	public void updateData(
		CCImage theImage, 
		int theDestX, int theDestY, int theDestZ, 
		int theWidth, int theHeight
	) {
		updateData(theImage, theDestX, theDestY, theDestZ, 0, 0, theWidth, theHeight);
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture. You can
	 * also copy different images into one texture and specify their position
	 * inside the texture.
	 * @param theImage the new texture data 
	 * @param theDestX left side of the texture where to place the data
	 * @param theDestY top side of the texture where to place the data
	 */
	public void updateData(
		CCImage theImage, 
		int theDestX, int theDestY, int theDestZ
	) {
		updateData(theImage, theDestX, theDestY, theDestZ, theImage.width(), theImage.height());
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture.
	 * @param theImage the new texture data 
	 */
	public void updateData(CCImage theImage) {
		updateData(theImage,0,0,0,0,0,theImage.width(), theImage.height());
	}
	
	public void updateData(CCImage theImage, final int theZ) {
		updateData(theImage,0,0,theZ,0,0,theImage.width(), theImage.height());
	}
	
//	public CCImage data() {
//		
//		
//		if(_myIsCompressed) {
//			
//			int size = parameter(GL_TEXTURE_COMPRESSED_IMAGE_SIZE);
//			ByteBuffer res = ByteBuffer.allocate(size);
//			glGetCompressedTexImage(_myTarget.glID, 0, res);
//			
//			return new CCImage(
//				_myWidth, _myHeight, 0, 
//				_myInternalFormat, _myFormat, 
//				CCPixelType.UNSIGNED_BYTE, 
//				_myIsCompressed, _myMustFlipVertically, 
//				res, 
//				null
//			);
//		}else {
//			CCPixelFormat fetchedFormat = null;
//			int myBytesPerPixel;
//			switch (_myInternalFormat) {
//			case RGB:
//			case BGR:
//			case RGB8:
//				myBytesPerPixel = 3;
//				fetchedFormat = CCPixelFormat.RGB;
//				break;
//			case RGBA:
//			case BGRA:
//			case ABGR:
//			case RGBA8:
//				myBytesPerPixel = 4;
//				fetchedFormat = CCPixelFormat.RGBA;
//				break;
//			default:
//				throw new CCTextureException("The data method does not support the internal format of the texture:" + _myInternalFormat);
//			}
//			
//			ByteBuffer res = ByteBuffer.allocate((_myWidth + (2 * border())) * (_myHeight + (2 * border())) * myBytesPerPixel);
//			
//			_myStorageModes.packStorage();
//			glGetTexImage(_myTarget.glID, 0, fetchedFormat.glID, GL.GL_UNSIGNED_BYTE, res);
//			_myStorageModes.defaultPackStorage();
//			
//			return new CCImage(
//				_myWidth, _myHeight, 0, 
//				_myInternalFormat, _myFormat, 
//				CCPixelType.UNSIGNED_BYTE, 
//				_myIsCompressed, _myMustFlipVertically, 
//				res, 
//				null
//			);
//		}
//	}
	
	/**
	 * Sets the pixel at the given index to the given color.
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top side
	 * @param theColor the new color of the pixel
	 */
	public void setPixel(final int theX, final int theY, final int theZ, final CCColor theColor) {
		
		FloatBuffer myBuffer = FloatBuffer.allocate(4);
		myBuffer.put((float)theColor.r);
		myBuffer.put((float)theColor.g);
		myBuffer.put((float)theColor.b);
		myBuffer.put((float)theColor.a);
		myBuffer.rewind();

		
		glTexSubImage3D(
			_myTarget.glID, 0, 
			theX, theY, theZ, 1, 1, 1,
			GLPixelDataFormat.RGBA.glID, GLPixelDataType.FLOAT.glID, myBuffer
		);
	}
	
	private GLPixelMap _myPixelMap;
	
	/**
	 * Returns the color for the pixel at the given index
	 * @param theX position of the pixel from the left side
	 * @param theY position of the pixel from the top
	 * @return the color of the pixel
	 */
	public CCColor getPixel(final int theX, final int theY) {
		if(_myPixelMap == null) {
			FloatBuffer myPixelData = FloatBuffer.allocate(4 * _myWidth * _myHeight);
			
			glGetTexImage(_myTarget.glID, 0, GLPixelDataFormat.RGBA.glID, GLPixelDataType.FLOAT.glID, myPixelData);
			myPixelData.rewind();
			
			_myPixelMap = new GLPixelMap(myPixelData, _myWidth, _myHeight, _myMustFlipVertically);
		}
		
		return _myPixelMap.getPixel(theX, theY);
	}
	
	/**
	 * Returns all pixels of the texture as pixelmap
	 * @return
	 */
	public GLPixelMap getPixels() {
		FloatBuffer myBuffer = FloatBuffer.allocate(4 * _myWidth * _myHeight);
		
		glGetTexImage(_myTarget.glID, 0, GLPixelDataFormat.RGBA.glID, GLPixelDataType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new GLPixelMap(myBuffer, _myWidth, _myHeight, _myMustFlipVertically);
	}
	

}
