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

import static org.lwjgl.opengl.GL11.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.glCopyTexSubImage1D;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage1D;
import static org.lwjgl.opengl.GL11.glTexSubImage1D;
import static org.lwjgl.opengl.GL13.glCompressedTexImage1D;

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
 * This class represents a 1D texture. It contains one row of 
 * pixels. This is useful to store gradients, or data that is 
 * passed to a shader.
 * @author christian riekoff
 *
 */
public class CCTexture1D extends CCTexture{


	/**
	 * Creates a new 1D texture
	 */
	public CCTexture1D() {
		super(CCTextureTarget.TEXTURE_1D);
	}

	/**
	 * Creates a new 1D texture
	 * @param theGenerateMipmaps if <code>true</code> textures are automatically
	 * generated from passed texture data
	 */
	public CCTexture1D(final CCTextureAttributes theTextureAttributes) {
		super(CCTextureTarget.TEXTURE_1D, theTextureAttributes);
	}
	
	public CCTexture1D(CCImage theImage){
		this();
		data(theImage);
	}
	
	public void dataImplementation(final CCImage theImage) {
		if(theImage.isDataCompressed()) {
			if(theImage.buffer() instanceof ByteBuffer){
				glCompressedTexImage1D(
					_myTarget.glID, 0, _myInternalFormat.glID,
					theImage.width(), 0, (ByteBuffer)theImage.buffer()
				);
			}
		}else {
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
		}
	}
	
	
	/**
	 * Replaces the content of the texture with pixels from the framebuffer. You can use this method
	 * to copy pixels from the framebuffer to a texture.
	 * @param g
	 * @param theDestX
	 * @param theSrcX
	 * @param theSrcY
	 * @param theWidth
	 */
	public void updateData(final CCGraphics g, int theDestX, int theSrcX, int theSrcY, int theWidth) {
		theSrcX = CCMath.constrain(theSrcX, 0, g.width());
		theSrcY = CCMath.constrain(theSrcY, 0, g.height());
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theWidth = CCMath.min(theWidth, g.width() - theSrcX, _myWidth - theDestX);
		
		bind();
		glCopyTexSubImage1D(
			_myTarget.glID, 0, 
			theDestX, 
			theSrcX, theSrcY, 
			theWidth
		);
	}
	
	/**
	 * Replaces the texture content with the given data.
	 * @param theImage the new pixel values for the texture
	 * @param theDestX the x offset (in pixels) relative to the left side of this texture 
	 * 	where the update will be applied
	 * @param theSrcX the x offset (in pixels) relative to the left side of the supplied 
	 *  TextureData from which to fetch the update rectangle
	 * @param theWidth the width (in pixels) of the rectangle to be updated
	 */
	public void updateData(CCImage theImage, int theDestX, int theSrcX, int theWidth) {
		theSrcX = CCMath.constrain(theSrcX, 0, theImage.width());
		theDestX = CCMath.constrain(theDestX, 0, _myWidth);
		theWidth = CCMath.min(theWidth, theImage.width() - theSrcX, _myWidth - theDestX);
			
		bind();
		
		unpackStorage(theImage.pixelStorageModes());

		glPixelStorei(GL_UNPACK_SKIP_PIXELS, theSrcX);
		
		CCPixelFormat myFormat = pixelFormat(theImage);
		CCPixelType myType = pixelType(theImage);
		
		
		if(theImage.buffer() instanceof ByteBuffer){
			glTexSubImage1D(
				_myTarget.glID, 0, 
				theDestX, theWidth, 
				myFormat.glID, myType.glID, (ByteBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof ShortBuffer){
			glTexSubImage1D(
				_myTarget.glID, 0, 
				theDestX, theWidth, 
				myFormat.glID, myType.glID, (ShortBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof IntBuffer){
			glTexSubImage1D(
				_myTarget.glID, 0, 
				theDestX, theWidth, 
				myFormat.glID, myType.glID, (IntBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof FloatBuffer){
			glTexSubImage1D(
				_myTarget.glID, 0, 
				theDestX, theWidth, 
				myFormat.glID, myType.glID, (FloatBuffer)theImage.buffer()
			);
		}else if(theImage.buffer() instanceof DoubleBuffer){
			glTexSubImage1D(
				_myTarget.glID, 0, 
				theDestX, theWidth, 
				myFormat.glID, myType.glID, (DoubleBuffer)theImage.buffer()
			);
		}
	}

	/**
	 * Replaces the texture content with the given data.
	 * @param theImage the new pixel values for the texture
	 * @param theDestX the x offset (in pixels) relative to the left side of this texture 
	 * 	where the update will be applied
	 * @param theWidth the width (in pixels) of the rectangle to be updated
	 */
	public void updateData(CCImage theImage, final int theDestX, final int theWidth) {
		updateData(theImage, theDestX, 0, theWidth);
	}
	
	/**
	 * Replaces the texture content with the given data.
	 * @param theImage the new pixel values for the texture
	 */
	public void updateData(CCImage theImage) {
		updateData(theImage,0,0, theImage.width());
	}
	
	/**
	 * Sets the pixel at the given index o the given color.
	 * @param theX position of the pixel from the left side
	 * @param theColor the new color of the pixel
	 */
	public void setPixel(final int theX, final CCColor theColor) {
		
		FloatBuffer myBuffer = FloatBuffer.allocate(4);
		myBuffer.put((float)theColor.r);
		myBuffer.put((float)theColor.g);
		myBuffer.put((float)theColor.b);
		myBuffer.put((float)theColor.a);
		myBuffer.rewind();
		
		
		glTexSubImage1D(
			_myTarget.glID, 0, 
			theX, 1, 
			CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer
		);
	}
	
	/**
	 * Returns the color for the pixel at the given index
	 * @param theX position of the pixel from the left side
	 * @return the color of the pixel
	 */
	public CCColor getPixel(final int theX) {
		FloatBuffer myBuffer = FloatBuffer.allocate(4 * _myWidth);
		
		glGetTexImage(_myTarget.glID, 0, CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new CCColor(
			myBuffer.get(theX * 4),
			myBuffer.get(theX * 4 + 1),
			myBuffer.get(theX * 4 + 2),
			myBuffer.get(theX * 4 + 3)
		);
	}
}
