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

import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCopyTexSubImage2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL13.glCompressedTexImage2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.graphics.CCBufferObject;
import cc.creativecomputing.graphics.CCBufferObject.CCBufferTarget;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageFrequency;
import cc.creativecomputing.graphics.CCBufferObject.CCUsageTYPE;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;


/**
 * This class represents a 2d texture. This is probably the
 * most common kind of texture used. You can create textures
 * by loading all kind of images as texture data and than pass the content
 * to a texture object. 
 * @author christian riekoff
 *
 */
public class GLTexture2D extends GLTexture{

	/**
	 * Creates a new 2d texture.
	 */
	public GLTexture2D() {
		super(GLTextureTarget.TEXTURE_2D, new GLTextureAttributes());
	}
	
	public GLTexture2D(GLTextureTarget theTarget) {
		super(theTarget, new GLTextureAttributes());
	}

	/**
	 * Creates a new 2D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public GLTexture2D(final GLTextureAttributes theTextureAttributes) {
		super(GLTextureTarget.TEXTURE_2D, theTextureAttributes);
	}

	/**
	 * Creates a new 2D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public GLTexture2D(final GLTextureAttributes theTextureAttributes, int theWidth, int theHeight) {
		super(GLTextureTarget.TEXTURE_2D, theTextureAttributes);
		allocateData(theWidth, theHeight);
	}
	
	public GLTexture2D(GLTextureTarget theTarget, final GLTextureAttributes theTextureAttributes) {
		super(theTarget, theTextureAttributes);
	}
	
	/**
	 * This is used internally for texture sequences and multitexturing
	 * @param theTarget
	 * @param theGenerateMipmaps
	 */
	protected GLTexture2D(GLTextureTarget theTarget, final GLTextureAttributes theTextureAttributes, final int theNumberOfTextures) {
		super(theTarget, theTextureAttributes, theNumberOfTextures);
	}
	
	/**
	 * Creates a new 2d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theImage
	 */
	public GLTexture2D(final CCImage theImage) {
		this();
		data(theImage);
	}
	
	public GLTexture2D(final CCImage theImage, GLTextureTarget theTarget) {
		this(theTarget);
		data(theImage);
	}
	
	/**
	 * Creates a new 2d texture based on the given texture data. The width
	 * and height of the texture will match the size of the texture data.
	 * @param theImage
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public GLTexture2D(final CCImage theImage, final GLTextureAttributes theTextureAttributes) {
		this(theTextureAttributes);
		data(theImage);
	}
	
	public GLTexture2D(final int theWidth, final int theHeight) {
		this();
		allocateData(theWidth, theHeight);
	}
	
	public GLTexture2D(final int theWidth, final int theHeight, final GLTextureAttributes theTextureAttributes) {
		this(theTextureAttributes);
		allocateData(theWidth, theHeight);
	}
	
	public GLTexture2D(final int theWidth, final int theHeight, GLTextureTarget theTarget) {
		this(theTarget);
		allocateData(theWidth, theHeight);
	}
	
	public GLTexture2D(final GLTextureTarget theTarget, GLTextureAttributes theAttributes, int theNumberOfTextures, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theNumberOfTextures);
		allocateData(theWidth, theHeight);
	}
	
	public void allocateData(final int theWidth, final int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		for(int i = 0; i < _myTextureIDs.length;i++) {
		
		bind(i);
		unpackStorage(_myStorageModes);
		
		glTexImage2D(
			_myTarget.glID, 0, 
			_myInternalFormat.glID, 
			_myWidth, _myHeight, 0, 
			_myFormat.glID, 
			_myPixelType.glID, 
			(ByteBuffer)null
		); 
		defaultUnpackStorage();
		}
	}
	
	public void dataImplementation(final CCImage theImage) {
		if(theImage.isDataCompressed()) {
			glCompressedTexImage2D(
				_myTarget.glID, 0, _myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				(ByteBuffer)theImage.buffer()
			);
		}else {
			try {
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
			}catch(Exception e) {
//				CCLog.info(_myTarget);
//				CCLog.info(theImage.internalFormat());
//				CCLog.info(theImage.pixelFormat());
//				CCLog.info(theImage.pixelType());
//				CCLog.info(theImage.width());
//				CCLog.info(theImage.height());
			}
		}
	}
	
	/**
	 * Replaces the content of the texture with pixels from the frame buffer. You can use this method
	 * to copy pixels from the frame buffer to a texture.
	 * @param g reference to the graphics object to copy the pixels from the frame buffer
	 * @param theDestX target x position for the copied data
	 * @param theDestY target y position for the copied data
	 * @param theSrcX source x position o the copied data
	 * @param theSrcY source y position o the copied data
	 * @param theWidth the with of the region to copy
	 * @param theHeight the height of the region to copy
	 */
	public void updateData(
		final CCGraphics g, 
		int theDestX, int theDestY,
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
		glCopyTexSubImage2D(
			_myTarget.glID, 0, 
			theDestX, theDestY,
			theSrcX, theSrcY, 
			theWidth, theHeight
		);

		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);
	}
	
	private boolean _myUsePBO = false;
	
	/**
	 * If this is set true calls to update data will use a pixel buffer object
	 * to get direct access to the pixel buffer. This should give better performance
	 * and allow asynchronous texture updates. But needs far more testing and development. 
	 * @param theUsePBO
	 */
	public void usePBO(boolean theUsePBO) {
		_myUsePBO = theUsePBO;
	}
	
	public boolean usePBO() {
		return _myUsePBO;
	}
	
	private CCBufferObject _myBufferObject;
	
//	public ByteBuffer buffer() {
//		int mySize = _myPixelType.bytesPerChannel * _myFormat.numberOfChannels * _myWidth * _myHeight;
//		if(_myBufferObject == null) {
//			_myBufferObject = new CCBufferObject();
//			_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
//	        _myBufferObject.bufferData(mySize, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
//		}
//		_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
//		return _myBufferObject.mapBuffer();
//	}
	
	private void updateData(
		CCImage theImage, 
		int theDestX, int theDestY, 
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight,
		boolean theDoResize
	) {
		if(theDoResize) {
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theDestY = CCMath.constrain(theDestY, 0, _myHeight);
		
		theSrcX = CCMath.constrain(theSrcX, 0, theImage.width());
		theSrcY = CCMath.constrain(theSrcY, 0, theImage.height());
		
		theWidth = CCMath.min(theWidth, theImage.width() - theSrcX, _myWidth - theDestX);
		theHeight = CCMath.min(theHeight, theImage.height() - theSrcY, _myHeight - theDestY);

		unpackStorage(theImage.pixelStorageModes());

		
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, theSrcX);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, theSrcY);

        // update the texture image:
        glEnable(_myTarget.glID);
		glBindTexture(_myTarget.glID, _myTextureIDs[_myTextureID]);
		
		
//		CCPixelFormat myFormat = pixelFormat(theImage);
		GLPixelDataType myType = pixelType(theImage);
		
		if(_myUsePBO) {
//			int mySize = _myPixelType.bytesPerChannel * _myFormat.numberOfChannels * _myWidth * _myHeight;
//			if(_myBufferObject == null) {
//				_myBufferObject = new CCBufferObject();
//				_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
//		        _myBufferObject.bufferData(mySize, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
//			}
//			_myBufferObject.bind(CCBufferTarget.PIXEL_UNPACK);
//
//	        // map the buffer object into client's memory
//	        // Note that glMapBufferARB() causes sync issue.
//	        // If GPU is working with this buffer, glMapBufferARB() will wait(stall)
//	        // for GPU to finish its job. To avoid waiting (stall), you can call
//	        // first glBufferDataARB() with NULL pointer before glMapBufferARB().
//	        // If you do that, the previous data in PBO will be discarded and
//	        // glMapBufferARB() returns a new allocated pointer immediately
//	        // even if GPU is still working with the previous data.
//	        _myBufferObject.bufferData(mySize, CCUsageFrequency.STREAM, CCUsageTYPE.DRAW);
//	        
//	        ByteBuffer ptr = _myBufferObject.mapBuffer();
//	        if(ptr != null)
//	        {
//	            // update data directly on the mapped buffer
//	            if(theImage.buffer() instanceof FloatBuffer){
//	            	FloatBuffer myFloatBuffer = (FloatBuffer)theImage.buffer();
//	            	myFloatBuffer.rewind();
//	            	ptr.asFloatBuffer().put(myFloatBuffer);
//	            	ptr.rewind();
//	            }
//	            if(theImage.buffer() instanceof ByteBuffer){
////	            	ByteBuffer myByteBuffer = (ByteBuffer)theImage.buffer();
////	            	myByteBuffer.rewind();
////	            	ptr.put(myByteBuffer);
////	            	ptr.rewind();
//	            	while(ptr.hasRemaining()) {
//	            		ptr.put((byte)CCMath.random(255));
//	            	}
//	            }
//	            _myBufferObject.unmapBuffer();
//	        } 
//	        glTexSubImage2D(
//		    	_myTarget.glID, 0, 
//		    	theDestX, theDestY, 
//		    	theWidth, theHeight, 
//		    	_myFormat.glID, myType.glID, 0
//		    );
//
//	        _myBufferObject.unbind();
		}else {
			
			int myWidth = CCMath.min(theWidth, _myWidth - theDestX);
			int myHeight = CCMath.min(theHeight, _myHeight - theDestY);
			
			if(theImage.buffer() instanceof ByteBuffer){
				glTexSubImage2D(
					_myTarget.glID, 0, 
					theDestX, theDestY, 
					myWidth, myHeight, 
					_myFormat.glID, myType.glID, (ByteBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof ShortBuffer){
				glTexSubImage2D(
					_myTarget.glID, 0, 
					theDestX, theDestY, 
					myWidth, myHeight, 
					_myFormat.glID, myType.glID, (ShortBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof IntBuffer){
				glTexSubImage2D(
					_myTarget.glID, 0, 
					theDestX, theDestY, 
					myWidth, myHeight, 
					_myFormat.glID, myType.glID, (IntBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof FloatBuffer){
				glTexSubImage2D(
					_myTarget.glID, 0, 
					theDestX, theDestY, 
					myWidth, myHeight, 
					_myFormat.glID, myType.glID, (FloatBuffer)theImage.buffer()
				);
			}else if(theImage.buffer() instanceof DoubleBuffer){
				glTexSubImage2D(
					_myTarget.glID, 0, 
					theDestX, theDestY, 
					myWidth, myHeight, 
					_myFormat.glID, myType.glID, (DoubleBuffer)theImage.buffer()
				);
			}
		}
		
		defaultUnpackStorage();
		if(_myGenerateMipmaps)glGenerateMipmap(_myTarget.glID);

		glBindTexture(_myTarget.glID, 0);
        glDisable(_myTarget.glID);
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
		int theDestX, int theDestY, 
		int theSrcX, int theSrcY, 
		int theWidth, int theHeight
	) {
		updateData(theImage, theDestX, theDestY, theSrcX, theSrcY, theWidth, theHeight, false);
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
		int theDestX, int theDestY, 
		int theWidth, int theHeight
	) {
		updateData(theImage, theDestX, theDestY, 0, 0, theWidth, theHeight);
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
		int theDestX, int theDestY
	) {
		updateData(theImage, theDestX, theDestY, theImage.width(), theImage.height());
	}
	
	/**
	 * Updates the pixels of the texture with the content from the given
	 * texture data. Be aware to replace all pixels of the texture, the size
	 * of the given texture data must match the size of the texture.
	 * @param theImage the new texture data 
	 */
	public void updateData(CCImage theImage) {
		if(
			theImage.width() != _myWidth || 
			theImage.height() != _myHeight) {
			data(theImage);
		}else {
			updateData(theImage,0,0,0,0,theImage.width(), theImage.height(), true);
		}
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
//				cc.creativecomputing.image.CCPixelType.UNSIGNED_BYTE, 
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
//			case DEPTH_COMPONENT24:
//				fetchedFormat = CCPixelFormat.DEPTH_COMPONENT;
//				myBytesPerPixel = 3;
//				break;
//			default:
//				throw new CCTextureException("The data method does not support the internal format of the texture:" + _myInternalFormat);
//			}
//			
//			ByteBuffer res = ByteBuffer.allocate((_myWidth + (2 * border())) * (_myHeight + (2 * border())) * myBytesPerPixel);
//			
//			packStorage(_myStorageModes);
//			glGetTexImage(_myTarget.glID, 0, fetchedFormat.glID, GL_UNSIGNED_BYTE, res);
//			defaultPackStorage();
//			
//			return new CCImage(
//				_myWidth, _myHeight, 0, 
//				_myInternalFormat, _myFormat, 
//				cc.creativecomputing.image.CCPixelType.UNSIGNED_BYTE, 
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
	public void setPixel(final int theX, final int theY, final CCColor theColor) {
		
		
		FloatBuffer myBuffer = FloatBuffer.allocate(4);
		myBuffer.put((float)theColor.r);
		myBuffer.put((float)theColor.g);
		myBuffer.put((float)theColor.b);
		myBuffer.put((float)theColor.a);
		myBuffer.rewind();
		
		glTexSubImage2D(
			_myTarget.glID, 0, 
			theX, theY, 1, 1,
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
			
			bind();
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
		
		bind();
		glGetTexImage(_myTarget.glID, 0, GLPixelDataFormat.RGBA.glID, GLPixelDataType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new GLPixelMap(myBuffer, _myWidth, _myHeight, _myMustFlipVertically);
	}
	
	protected String checkError(final String theString){
		switch(glGetError()){
		case GL_NO_ERROR:
//			if(_myReportNoError)CCLog.error(theString + " # NO ERROR REPORTED");
			return null;
		case GL_INVALID_ENUM:
			return theString + " # INVALID ENUMERATION REPORTED. check for errors in OPENGL calls with constants.";
		case GL_INVALID_VALUE:
			return theString + "# INVALID VALUE REPORTED. check for errors with passed values that are out of a defined range.";
		case GL_INVALID_OPERATION:
			return theString + "# INVALID OPERATION REPORTED. check for function calls that are invalid in the current graphics state.";
		case GL_STACK_OVERFLOW:
			return theString + "# STACK OVERFLOW REPORTED. check for errors in matrix operations";
		case GL_STACK_UNDERFLOW:
			return theString + "# STACK UNDERFLOW REPORTED. check for errors  in matrix operations";
		case GL_OUT_OF_MEMORY:
			return theString + "# OUT OF MEMORY. not enough memory to execute the commands";
		}
		return null;
	}
	
	protected String checkError(){
		return checkError("");
	}
}
