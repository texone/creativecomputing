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

import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

import com.jogamp.opengl.GL2;

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
			CCGraphics.currentGL().glCompressedTexImage1D(
				_myTarget.glID, 0, _myInternalFormat.glID, 
				theImage.width(), 0, theImage.buffer().capacity(), theImage.buffer()
			);
		}else {
			CCGraphics.currentGL().glTexImage1D(
				_myTarget.glID, 0, _myInternalFormat.glID,
				theImage.width(), 0, 
				_myFormat.glID, _myPixelType.glID, theImage.buffer()
			);
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
		g.gl.glCopyTexSubImage1D(
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
		GL2 gl = CCGraphics.currentGL();
		unpackStorage(theImage.pixelStorageModes());

		gl.glPixelStorei(GL2.GL_UNPACK_SKIP_PIXELS, theSrcX);
		
		CCPixelFormat myFormat = pixelFormat(theImage);
		CCPixelType myType = pixelType(theImage);
		
		gl.glTexSubImage1D(
			_myTarget.glID, 0, 
			theDestX, theWidth, 
			myFormat.glID, myType.glID, theImage.buffer()
		);
			
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
		
		GL2 gl = CCGraphics.currentGL();
		gl.glTexSubImage1D(
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
		GL2 gl = CCGraphics.currentGL();
		gl.glGetTexImage(_myTarget.glID, 0, CCPixelFormat.RGBA.glID, CCPixelType.FLOAT.glID, myBuffer);
		myBuffer.rewind();
		
		return new CCColor(
			myBuffer.get(theX * 4),
			myBuffer.get(theX * 4 + 1),
			myBuffer.get(theX * 4 + 2),
			myBuffer.get(theX * 4 + 3)
		);
	}
}
