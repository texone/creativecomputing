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

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13.glCompressedTexImage2D;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.image.CCImage;


/**
 * @author christianriekoff
 *
 */
public class CCTextureCubeMap extends CCTexture{
	
	public enum CCCubeMapSide{
		POSITIVE_X(GL_TEXTURE_CUBE_MAP_POSITIVE_X),
		POSITIVE_Y(GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
		POSITIVE_Z(GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
		NEGATIVE_X(GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
		NEGATIVE_Y(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
		NEGATIVE_Z(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);
		
		public int glID;
		
		CCCubeMapSide(final int theGLID) {
			glID = theGLID;
		}
	}
	
	private static final CCCubeMapSide cube[] = new CCCubeMapSide[]{
		CCCubeMapSide.POSITIVE_X, 
		CCCubeMapSide.NEGATIVE_X, 
		CCCubeMapSide.POSITIVE_Y, 
		CCCubeMapSide.NEGATIVE_Y, 
		CCCubeMapSide.POSITIVE_Z, 
		CCCubeMapSide.NEGATIVE_Z
	};

	/**
	 * @param theTarget
	 * @param theGenerateMipmaps
	 */
	public CCTextureCubeMap(CCImage theImage) {
		super(CCTextureTarget.TEXTURE_CUBE_MAP);
		data(theImage);
	}
	
	private void compressedData(final CCImage theImage, final int theID) {
		CCPixelInternalFormat myInternalFormat = internalFormat(theImage);
		glCompressedTexImage2D(
			cube[theID].glID, 0, myInternalFormat.glID, 
			theImage.width(), theImage.height(), 0, 
			(ByteBuffer)theImage.buffer(theID)
		);
	}
	
	private void data(final CCImage theImage, final int theID) {
		CCPixelInternalFormat myInternalFormat = internalFormat(theImage);
		CCPixelFormat myFormat = pixelFormat(theImage);
		CCPixelType myType = pixelType(theImage);
		
		if(theImage.buffer() instanceof ByteBuffer){
			glTexImage2D(
				cube[theID].glID, 0, myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				myFormat.glID, 
				myType.glID, 
				(ByteBuffer)theImage.buffer(theID)
			);
		}else if(theImage.buffer() instanceof ShortBuffer){
			glTexImage2D(
				cube[theID].glID, 0, myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				myFormat.glID, 
				myType.glID, 
				(ShortBuffer)theImage.buffer(theID)
			);
		}else if(theImage.buffer() instanceof IntBuffer){
			glTexImage2D(
				cube[theID].glID, 0, myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				myFormat.glID, 
				myType.glID, 
				(IntBuffer)theImage.buffer(theID)
			);
		}else if(theImage.buffer() instanceof FloatBuffer){
			glTexImage2D(
				cube[theID].glID, 0, myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				myFormat.glID, 
				myType.glID, 
				(FloatBuffer)theImage.buffer(theID)
			);
		}else if(theImage.buffer() instanceof DoubleBuffer){
			glTexImage2D(
				cube[theID].glID, 0, myInternalFormat.glID, 
				theImage.width(), theImage.height(), 0, 
				myFormat.glID, 
				myType.glID, 
				(DoubleBuffer)theImage.buffer(theID)
			);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.CCTextureNew#dataImplementation(cc.creativecomputing.texture_new.CCTextureDataNew)
	 */
	@Override
	public void dataImplementation(CCImage theImage) {
		if(theImage.mipmapData().length < 6) {
			throw new CCTextureException("Cubemap requires 6 images to be generated. Make sure you pass a data object that contains 6 images.");
		}
		// Load Cube Map images 
		if(theImage.isDataCompressed()) {
			for(int i = 0; i < 6; i++){ 
				compressedData(theImage, i);
			}
		}else {
			for(int i = 0; i < 6; i++){ 
				data(theImage, i);
			}
		}
	}

	@Override
	public void updateData(CCImage theImage) {
	}

}
