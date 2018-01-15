package cc.creativecomputing.gl4;

import cc.creativecomputing.gl4.texture.GLTexture;
import cc.creativecomputing.gl4.texture.GLTextureTarget;

import com.jogamp.opengl.GL4;

/**
 * When a sampler object is bound to a texture unit, its state supersedes that
 * of the texture object bound to that texture unit. If the sampler name zero is
 * bound to a texture unit, the currently bound texture's sampler state becomes
 * active. A single sampler object may be bound to multiple texture units
 * simultaneously.
 * 
 * @author christianr
 * 
 */
public class GLSampler {
	private int _myID;

	public GLSampler() {
		GL4 gl = GLGraphics.currentGL();
		gl.glGenSamplers(1, GLBufferUtil.intBuffer());
		_myID = GLBufferUtil.intBuffer().get(0);
	}

	public int id() {
		return _myID;
	}

	/**
	 * When a sampler object is bound to a texture unit, its state supersedes
	 * that of the texture object bound to that texture unit. A single sampler
	 * object may be bound to multiple texture units simultaneously.
	 * 
	 * @param theTextureUnit Specifies the index of the texture unit to which the sampler is bound.
	 */
	public void bind(int theTextureUnit) {
		GL4 gl = GLGraphics.currentGL();
		gl.glBindSampler(theTextureUnit, _myID);
	}

	/**
	 * Unbinds the current sampler of the given texture unit and makes the sampler state
	 * of the current bound texture active
	 * @param theTextureUnit Specifies the index of the texture unit to which the sampler is bound.
	 */
	public void unbind(int theTextureUnit) {
		GL4 gl = GLGraphics.currentGL();
		gl.glBindTexture(theTextureUnit, 0);
	}

	/**
	 * Texture mapping is a technique that applies an image onto an object's
	 * surface as if the image were a decal or cellophane shrink-wrap. The image
	 * is created in texture space, with an (s, t) coordinate system. A texture
	 * is a two-dimensional or cube-mapped image and a set of parameters that
	 * determine how samples are derived from the image.
	 * 
	 * glTexParameter assigns the value or values in params to the texture
	 * parameter specified as pname. target defines the target texture of the
	 * active texture unit, either {@linkplain GLTextureTarget#TEXTURE_2D} or {@linkplain GLTextureTarget#TEXTURE_CUBE_MAP}.
	 * 
	 * @param theParameterName Specifies the symbolic name of a texture parameter.
	 * @param theParameterValue value of pname
	 */
	public void parameter(int theParameterName, int theParameterValue){
		GL4 gl = GLGraphics.currentGL();
	    gl.glSamplerParameteri(_myID, theParameterName, theParameterValue);
	}
	
	/**
	 * @see GLTexture#minFilter(GLTextureMinFilter)
	 * @author christianr
	 *
	 */
	public enum GLTextureMinFilter {
		/**
		 * Returns the value of the texture element that is nearest (in
		 * Manhattan distance) to the center of the pixel being textured.
		 */
		NEAREST(GL4.GL_NEAREST),
		/**
		 * Returns the weighted average of the four texture elements that are
		 * closest to the center of the pixel being textured.
		 */
		LINEAR(GL4.GL_LINEAR),
		/**
		 * Chooses the mipmap that most closely matches the size of the pixel
		 * being textured and uses the GL_NEAREST criterion (the texture element
		 * nearest to the center of the pixel) to produce a texture value.
		 */
		NEAREST_MIPMAP_NEAREST(GL4.GL_NEAREST_MIPMAP_NEAREST),
		/**
		 * Chooses the two mipmaps that most closely match the size of the pixel
		 * being textured and uses the NEAREST criterion (the texture element
		 * nearest to the center of the pixel) to produce a texture value from
		 * each mipmap. The final texture value is a weighted average of those
		 * two values.
		 */
		NEAREST_MIPMAP_LINEAR(GL4.GL_NEAREST_MIPMAP_LINEAR),
		/**
		 * Chooses the mipmap that most closely matches the size of the pixel
		 * being textured and uses the LINEAR criterion (a weighted average
		 * of the four texture elements that are closest to the center of the
		 * pixel) to produce a texture value.
		 */
		LINEAR_MIPMAP_NEAREST(GL4.GL_LINEAR_MIPMAP_NEAREST),
		/**
		 * Chooses the two mipmaps that most closely match the size of the pixel
		 * being textured and uses the LINEAR criterion (a weighted average
		 * of the four texture elements that are closest to the center of the
		 * pixel) to produce a texture value from each mipmap. The final texture
		 * value is a weighted average of those two values.
		 */
		LINEAR_MIPMAP_LINEAR(GL4.GL_LINEAR_MIPMAP_LINEAR);
		
		private int _myGLID;
		
		GLTextureMinFilter(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTextureMinFilter fromGLID(int theGLID){
			switch(theGLID){
			case GL4.GL_NEAREST:return NEAREST;
			case GL4.GL_LINEAR:return LINEAR;
			case GL4.GL_NEAREST_MIPMAP_NEAREST:return NEAREST_MIPMAP_NEAREST;
			case GL4.GL_NEAREST_MIPMAP_LINEAR:return NEAREST_MIPMAP_LINEAR;
			case GL4.GL_LINEAR_MIPMAP_NEAREST:return LINEAR_MIPMAP_NEAREST;
			case GL4.GL_LINEAR_MIPMAP_LINEAR:return LINEAR_MIPMAP_LINEAR;
			}
			return null;
		}
	}
	
	/**
	 * The texture minifying function is used whenever the pixel being textured
	 * maps to an area greater than one texture element. There are six defined
	 * minifying functions. Two of them use the nearest one or nearest four
	 * texture elements to compute the texture value. The other four use
	 * mipmaps.
	 * <p>
	 * A mipmap is an ordered set of arrays representing the same image at
	 * progressively lower resolutions. If the texture has dimensions w * h ,
	 * there are floor ⁡ log 2 ⁡ max ⁡ w h + 1 mipmap levels. The first mipmap
	 * level is the original texture, with dimensions w * h . Each subsequent
	 * mipmap level has dimensions max ⁡ 1 floor ⁡ w 2 i * max ⁡ 1 floor ⁡ h 2 i
	 * , where i is the mipmap level, until the final mipmap is reached, which
	 * has dimension 1 * 1 .
	 * 
	 * @param theMinFilter function for minifying the texture
	 */
	public void minFilter(GLTextureMinFilter theMinFilter){
		parameter(GL4.GL_TEXTURE_MIN_FILTER, theMinFilter._myGLID);
	}
	
	/**
	 * @see GLTexture#magFilter(GLTextureMagFilter)
	 * @author christianr
	 *
	 */
	public enum GLTextureMagFilter {
		/**
		 * Returns the value of the texture element that is nearest (in
		 * Manhattan distance) to the center of the pixel being textured.
		 */
		NEAREST(GL4.GL_NEAREST),
		/**
		 * Returns the weighted average of the four texture elements that are
		 * closest to the center of the pixel being textured.
		 */
		LINEAR(GL4.GL_LINEAR);

		private int _myGLID;

		GLTextureMagFilter(int theGLID) {
			_myGLID = theGLID;
		}

		public int glID() {
			return _myGLID;
		}

		public static GLTextureMagFilter fromGLID(int theGLID) {
			switch (theGLID) {
			case GL4.GL_NEAREST:
				return NEAREST;
			case GL4.GL_LINEAR:
				return LINEAR;
			}
			return null;
		}
	}
	
	/**
	 * The texture magnification function is used when the pixel being textured
	 * maps to an area less than or equal to one texture element. It sets the
	 * texture magnification function to either NEAREST or LINEAR (see
	 * below). NEAREST is generally faster than GL_LINEAR, but it can produce
	 * textured images with sharper edges because the transition between texture
	 * elements is not as smooth.
	 * 
	 * @param theMagFilter
	 */
	public void magFilter(GLTextureMagFilter theMagFilter){
		parameter(GL4.GL_TEXTURE_MAG_FILTER, theMagFilter._myGLID);
	}
	
	public enum GLTextureWrap {
		CLAMP_TO_EDGE(GL4.GL_CLAMP_TO_EDGE),
		REPEAT(GL4.GL_REPEAT),
		CLAMP_TO_BORDER(GL4.GL_CLAMP_TO_BORDER),
		MIRRORED_REPEAT(GL4.GL_MIRRORED_REPEAT);
		
		private int _myGLID;
		
		GLTextureWrap(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTextureWrap fromGLID(int theGLID){
			switch(theGLID){
			case GL4.GL_CLAMP_TO_EDGE:return CLAMP_TO_EDGE;
			case GL4.GL_REPEAT:return REPEAT;
			case GL4.GL_CLAMP_TO_BORDER:return CLAMP_TO_BORDER;
			case GL4.GL_MIRRORED_REPEAT:return MIRRORED_REPEAT;
			}
			return null;
		}
	}
	
	public enum GLTextureWrapCoord {
		TEXTURE_WRAP_S(GL4.GL_TEXTURE_WRAP_S),
		TEXTURE_WRAP_T(GL4.GL_TEXTURE_WRAP_T),
		TEXTURE_WRAP_R(GL4.GL_TEXTURE_WRAP_R);
		
		private int _myGLID;
		
		GLTextureWrapCoord(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTextureWrapCoord fromGLID(int theGLID){
			switch(theGLID){
			case GL4.GL_TEXTURE_WRAP_S:return TEXTURE_WRAP_S;
			case GL4.GL_TEXTURE_WRAP_T:return TEXTURE_WRAP_T;
			case GL4.GL_TEXTURE_WRAP_R:return TEXTURE_WRAP_R;
			}
			return null;
		}
	}
	
	public void wrapR(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_R.glID(), theWrap.glID());
	}
	
	public void wrapS(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_S.glID(), theWrap.glID());
	}
	
	public void wrapT(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_T.glID(), theWrap.glID());
	}
	
	public void wrap(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_R.glID(), theWrap.glID());
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_S.glID(), theWrap.glID());
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_T.glID(), theWrap.glID());
	}

	@Override
	protected void finalize() {
		GL4 gl = GLGraphics.currentGL();
		gl.glDeleteSamplers(1, GLBufferUtil.wrapParameters(_myID));
	}
}
