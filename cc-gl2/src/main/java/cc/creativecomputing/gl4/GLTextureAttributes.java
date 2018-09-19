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

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

/**
 * @author christianriekoff
 * 
 */
public class GLTextureAttributes {
	
	public static enum GLTextureWrapCoord {
		TEXTURE_WRAP_S(GL_TEXTURE_WRAP_S),
		TEXTURE_WRAP_T(GL_TEXTURE_WRAP_T),
		TEXTURE_WRAP_R(GL_TEXTURE_WRAP_R);
		
		private int _myGLID;
		
		GLTextureWrapCoord(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTextureWrapCoord fromGLID(int theGLID){
			switch(theGLID){
			case GL_TEXTURE_WRAP_S:return TEXTURE_WRAP_S;
			case GL_TEXTURE_WRAP_T:return TEXTURE_WRAP_T;
			case GL_TEXTURE_WRAP_R:return TEXTURE_WRAP_R;
			}
			return null;
		}
	}
	
	/**
	 * Normally, you specify texture coordinates between 0.0 and 1.0 to map out a texture. 
	 * If texture coordinates fall outside this range, OpenGL handles them according to the 
	 * current texture wrapping mode. This enum holds the possible modes.
	 * @author christian riekoff
	 *
	 */
	public enum GLTextureWrap{
		/**
		 * clamps the texture if you use values going over the image range. The needed
		 * texels are taken from the texture border.
		 */
		CLAMP(GL_CLAMP),
		/**
		 * uses only border texels whenever the texture coordinates fall outside the of the texture.
		 */
		CLAMP_TO_BORDER(GL_CLAMP_TO_BORDER), 
		/**
		 * simply ignores texel samples that go over the edge and does not include them in the average
		 */
		CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE), 
		/**
		 * works like {@link CCTextureWrap#REPEAT} but mirrors 
		 * the texture for more seamless results on repeating the texture
		 */
		MIRRORED_REPEAT(GL_MIRRORED_REPEAT),
		/**
		 * simply causes the texture to repeat in the direction in which the texture 
		 * coordinate has exceeded the image boundary. The texture repeats again for every multiple
		 * of the texture size. This mode is very useful for applying a small tiled texture to large 
		 * geometric surfaces. Well-done seamless textures can lend the appearance of a seemingly much 
		 * larger texture, but at the cost of a much smaller texture image.
		 */
		REPEAT(GL_REPEAT);
        
        public int glID;
		
		GLTextureWrap(final int theGLID) {
			glID = theGLID;
		}
		
		public static GLTextureWrap byGLID(int theGLID){
			switch(theGLID){
			case GL_CLAMP: return CLAMP;
			case GL_CLAMP_TO_BORDER: return CLAMP_TO_BORDER;
			case GL_CLAMP_TO_EDGE: return CLAMP_TO_EDGE;
			case GL_MIRRORED_REPEAT: return GLTextureWrap.MIRRORED_REPEAT;
			case GL_REPEAT: return GLTextureWrap.REPEAT;
			}
			return null;
		}
	}
	

	public enum GLTextureFilter{
		/**
		 * Returns the value of the texture element that is nearest 
		 * (in Manhattan distance) to the center of the pixel being textured.
		 */
		NEAREST(GL_NEAREST),

		/**
		 * Returns the weighted average of the four texture elements
		 * that are closest to the center of the pixel being textured.
		 * These can include border texture elements, depending on the 
		 * values of TEXTURE_WRAP, and on the exact mapping.
		 */
        LINEAR(GL_LINEAR);

        public int glID;
		
		GLTextureFilter(final int theGLID) {
			glID = theGLID;
		}
	}
	
	/**
	 * The texture mipmap filter function is used whenever the pixel being textured
	 * maps to an area greater or smaller than one texture element and mipmap data is defined. 
	 * @author christianriekoff
	 *
	 */
	public enum GLTextureMipmapFilter{
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
        LINEAR
    }

	/**
	 * wrapping behavior when a texture coordinate falls outside the 
	 * range of [0,1]. 
	 */
	public GLTextureWrap wrapS = GLTextureWrap.CLAMP_TO_EDGE;
	
	/**
	 * wrapping behavior when a texture coordinate falls outside the 
	 * range of [0,1]. 
	 */
	public GLTextureWrap wrapT = GLTextureWrap.CLAMP_TO_EDGE;

	/**
	 * Filtering behavior when a texture is displayed at a lower or higher resolution than its native resolution.
	 * Default is {@link GLTextureFilter#LINEAR} Possible values are:{@link GLTextureFilter#LINEAR}, {@link GLTextureFilter#NEAREST}
	 */
	public GLTextureFilter filter = GLTextureFilter.LINEAR;
	
	/**
	 * Filtering behavior when a texture is displayed at a higher resolution than its native resolution.
	 * Default is {@link GLTextureFilter#LINEAR} Possible values are:{@link GLTextureFilter#LINEAR}, {@link GLTextureFilter#NEAREST}
	 */
	public GLTextureMipmapFilter mipmapFilter;

	public GLPixelDataFormat format = GLPixelDataFormat.RGBA;
	
	/**
	 * The Texture's internal format. default is RGBA
	 */
	public GLPixelDataInternalFormat internalFormat = GLPixelDataInternalFormat.RGBA;
	
	public GLPixelDataType type = GLPixelDataType.FLOAT;
	
	/**
	 * Enables or disables mipmapping. Default is disabled.
	 */
	public boolean generateMipmaps = false;


	
	

	/**
	 * Sets the 
	 * @param theWrapS
	 * @param theWrapT
	 */
	public void wrap(final GLTextureWrap theWrapS, GLTextureWrap theWrapT) {
		wrapS = theWrapS;
		wrapT = theWrapT;
	}
	
	/**
	 * Sets the wrapping behavior when a texture coordinate falls outside the 
	 * range of [0,1]. 
	 * @param theWrap
	 */
	public void wrap(final GLTextureWrap theWrap) {
		wrapS = theWrap;
		wrapT = theWrap;
	}

}
