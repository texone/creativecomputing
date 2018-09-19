package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL33.glDeleteSamplers;
import static org.lwjgl.opengl.GL33.glGenSamplers;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;

import cc.creativecomputing.gl4.GLTextureAttributes.GLTextureWrap;
import cc.creativecomputing.gl4.GLTextureAttributes.GLTextureWrapCoord;

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
		_myID = glGenSamplers();
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
		glBindSampler(theTextureUnit, _myID);
	}

	/**
	 * Unbinds the current sampler of the given texture unit and makes the sampler state
	 * of the current bound texture active
	 * @param theTextureUnit Specifies the index of the texture unit to which the sampler is bound.
	 */
	public void unbind(int theTextureUnit) {
		glBindTexture(theTextureUnit, 0);
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
	    glSamplerParameteri(_myID, theParameterName, theParameterValue);
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
		NEAREST(GL_NEAREST),
		/**
		 * Returns the weighted average of the four texture elements that are
		 * closest to the center of the pixel being textured.
		 */
		LINEAR(GL_LINEAR),
		/**
		 * Chooses the mipmap that most closely matches the size of the pixel
		 * being textured and uses the GL_NEAREST criterion (the texture element
		 * nearest to the center of the pixel) to produce a texture value.
		 */
		NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
		/**
		 * Chooses the two mipmaps that most closely match the size of the pixel
		 * being textured and uses the NEAREST criterion (the texture element
		 * nearest to the center of the pixel) to produce a texture value from
		 * each mipmap. The final texture value is a weighted average of those
		 * two values.
		 */
		NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
		/**
		 * Chooses the mipmap that most closely matches the size of the pixel
		 * being textured and uses the LINEAR criterion (a weighted average
		 * of the four texture elements that are closest to the center of the
		 * pixel) to produce a texture value.
		 */
		LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
		/**
		 * Chooses the two mipmaps that most closely match the size of the pixel
		 * being textured and uses the LINEAR criterion (a weighted average
		 * of the four texture elements that are closest to the center of the
		 * pixel) to produce a texture value from each mipmap. The final texture
		 * value is a weighted average of those two values.
		 */
		LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);
		
		private int _myGLID;
		
		GLTextureMinFilter(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTextureMinFilter fromGLID(int theGLID){
			switch(theGLID){
			case GL_NEAREST:return NEAREST;
			case GL_LINEAR:return LINEAR;
			case GL_NEAREST_MIPMAP_NEAREST:return NEAREST_MIPMAP_NEAREST;
			case GL_NEAREST_MIPMAP_LINEAR:return NEAREST_MIPMAP_LINEAR;
			case GL_LINEAR_MIPMAP_NEAREST:return LINEAR_MIPMAP_NEAREST;
			case GL_LINEAR_MIPMAP_LINEAR:return LINEAR_MIPMAP_LINEAR;
			}
			return null;
		}
	}
	
	/**
	 * The texture minifying function is used whenever the level-of-detail
	 * function used when sampling from the texture determines that the texture
	 * should be minified. There are six defined minifying functions. Two of
	 * them use either the nearest texture elements or a weighted average of
	 * multiple texture elements to compute the texture value. The other four
	 * use mipmaps.
	 * <p>
	 * A mipmap is an ordered set of arrays representing the same image at
	 * progressively lower resolutions. If the texture has dimensions 2n x 2m,
	 * there are max(n,m)+1 mipmaps. The first mipmap is the original texture,
	 * with dimensions 2n x 2m. Each subsequent mipmap has dimensions 2k-1 x 2l-1,
	 * where 2k x 2l are the dimensions of the previous mipmap, until either k=0
	 * or l=0. At that point, subsequent mipmaps have dimension 1 x 2l-1 or 2k-1 x 1
	 * until the final mipmap, which has dimension 1 x 1. To define the mipmaps,
	 * call glTexImage1D, glTexImage2D, glTexImage3D, glCopyTexImage1D, or
	 * glCopyTexImage2D with the level argument indicating the order of the
	 * mipmaps. Level 0 is the original texture; level max(n,m) is the final 1 x 1
	 * mipmap.
	 * <p>
	 * 
	 * @param theMinFilter
	 *            function for minifying the texture
	 */
	public void minFilter(GLTextureMinFilter theMinFilter) {
		parameter(GL_TEXTURE_MIN_FILTER, theMinFilter._myGLID);
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
		NEAREST(GL_NEAREST),
		/**
		 * Returns the weighted average of the four texture elements that are
		 * closest to the center of the pixel being textured.
		 */
		LINEAR(GL_LINEAR);

		private int _myGLID;

		GLTextureMagFilter(int theGLID) {
			_myGLID = theGLID;
		}

		public int glID() {
			return _myGLID;
		}

		public static GLTextureMagFilter fromGLID(int theGLID) {
			switch (theGLID) {
			case GL_NEAREST:
				return NEAREST;
			case GL_LINEAR:
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
		parameter(GL_TEXTURE_MAG_FILTER, theMagFilter._myGLID);
	}
	
	
	
	public void wrapR(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_R.glID(), theWrap.glID);
	}
	
	public void wrapS(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_S.glID(), theWrap.glID);
	}
	
	public void wrapT(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_T.glID(), theWrap.glID);
	}
	
	public void wrap(GLTextureWrap theWrap){
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_R.glID(), theWrap.glID);
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_S.glID(), theWrap.glID);
		parameter(GLTextureWrapCoord.TEXTURE_WRAP_T.glID(), theWrap.glID);
	}

	@Override
	protected void finalize() {
		glDeleteSamplers(_myID);
	}
}
