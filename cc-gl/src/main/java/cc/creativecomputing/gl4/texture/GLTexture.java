package cc.creativecomputing.gl4.texture;

import cc.creativecomputing.gl4.GLBufferUtil;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLSampler.GLTextureMagFilter;
import cc.creativecomputing.gl4.GLSampler.GLTextureMinFilter;
import cc.creativecomputing.gl4.GLSampler.GLTextureWrap;
import cc.creativecomputing.math.CCColor;

import com.jogamp.opengl.GL4;

public class GLTexture {
	
	protected GLTextureTarget _myTarget;
	protected int _myID;

	public GLTexture(GLTextureTarget theTarget){
		GL4 gl = GLGraphics.currentGL();
		gl.glGenTextures(1, GLBufferUtil.intBuffer());
		_myID = GLBufferUtil.intBuffer().get(0);
		_myTarget = theTarget;
		bind();
		unbind();
	}
	
	public int id(){
		return _myID;
	}
	
	public void target(GLTextureTarget theTarget){
		_myTarget = theTarget;
	}
	
	public GLTextureTarget target(){
		return _myTarget;
	}
	
	public void generateMipMaps(){
		GL4 gl = GLGraphics.currentGL();
		gl.glGenerateMipmap(_myTarget.glID());
	}
	
	public enum GLTexturePARAMETER {
		MIN_FILTER(GL4.GL_TEXTURE_MIN_FILTER),
		MAG_FILTER(GL4.GL_TEXTURE_MAG_FILTER),
		
		WRAP_R(GL4.GL_TEXTURE_WRAP_R),
		WRAP_S(GL4.GL_TEXTURE_WRAP_S),
		WRAP_T(GL4.GL_TEXTURE_WRAP_T),
		
		BASE_LEVEL(GL4.GL_TEXTURE_BASE_LEVEL),
		MAX_LEVEL(GL4.GL_TEXTURE_MAX_LEVEL),
		BORDER_COLOR(GL4.GL_TEXTURE_MAX_LEVEL);
		
		private int _myGLID;
		
		private GLTexturePARAMETER(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTexturePARAMETER fromGLID(int theGLID){
			switch(theGLID){
			case GL4.GL_TEXTURE_MIN_FILTER:return MIN_FILTER;
			case GL4.GL_TEXTURE_MAG_FILTER:return MAG_FILTER;
			
			case GL4.GL_TEXTURE_WRAP_S:return WRAP_S;
			case GL4.GL_TEXTURE_WRAP_T:return WRAP_T;
			case GL4.GL_TEXTURE_WRAP_R:return WRAP_R;
			
			case GL4.GL_TEXTURE_BASE_LEVEL:return BASE_LEVEL;
			case GL4.GL_TEXTURE_MAX_LEVEL:return MAX_LEVEL;

			case GL4.GL_TEXTURE_BORDER_COLOR:return BORDER_COLOR;
			}
			return null;
		}
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
	public void parameter(GLTexturePARAMETER theParameterName, int theParameterValue){
		bind();
		GL4 gl = GLGraphics.currentGL();
	    gl.glTexParameterf(_myTarget.glID(), theParameterName.glID(), theParameterValue);
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(GLTexturePARAMETER theParameterName, final float...theValues) {
		bind();
		GL4 gl = GLGraphics.currentGL();
		gl.glTexParameterfv(_myTarget.glID(), theParameterName.glID(), theValues,0);
	}
	
	/**
	 * Shortcut to set a texture parameter only used internally
	 * @param theType the parameter type we want to change
	 * @param theValue the value for the parameter
	 */
	protected void parameter(GLTexturePARAMETER theParameterName, final double...theValues) {
		bind();
		float[] myFloatValues = new float[theValues.length];
		for(int i = 0; i < theValues.length;i++){
			myFloatValues[i] = (float)theValues[i];
		}
		GL4 gl = GLGraphics.currentGL();
		gl.glTexParameterfv(_myTarget.glID(), theParameterName.glID(), myFloatValues,0);
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
		parameter(GLTexturePARAMETER.MIN_FILTER, theMinFilter.glID());
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
		parameter(GLTexturePARAMETER.MAG_FILTER, theMagFilter.glID());
	}
	

	public void wrapR(GLTextureWrap theWrap){
		parameter(GLTexturePARAMETER.WRAP_R, theWrap.glID());
	}
	
	public void wrapS(GLTextureWrap theWrap){
		parameter(GLTexturePARAMETER.WRAP_S, theWrap.glID());
	}
	
	public void wrapT(GLTextureWrap theWrap){
		parameter(GLTexturePARAMETER.WRAP_T, theWrap.glID());
	}
	
	public void wrap(GLTextureWrap theWrap){
		parameter(GLTexturePARAMETER.WRAP_R, theWrap.glID());
		parameter(GLTexturePARAMETER.WRAP_S, theWrap.glID());
		parameter(GLTexturePARAMETER.WRAP_T, theWrap.glID());
	}
	
	public void borderColor(final CCColor theColor) {
		parameter(GLTexturePARAMETER.BORDER_COLOR, theColor.array());
	}
	
	public void baseMipMapLevel(int theLevel){
		parameter(GLTexturePARAMETER.BASE_LEVEL, theLevel);
	}
	
	public void maxMipMapLevel(int theLevel){
		parameter(GLTexturePARAMETER.MAX_LEVEL, theLevel);
	}
	
	public void mipMapLevel(int theBaseLevel, int theMaxLevel){
		parameter(GLTexturePARAMETER.BASE_LEVEL, theBaseLevel);
		parameter(GLTexturePARAMETER.MAX_LEVEL, theMaxLevel);
	}
	
	/**
	 * Bind the texture to the given target
	 * @param theTarget
	 */
	public void bind(){
		GL4 gl = GLGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID(), _myID);
	}
	
	public void unbind(){
		GL4 gl = GLGraphics.currentGL();
		gl.glBindTexture(_myTarget.glID(), 0);
	}
	
	@Override
	protected void finalize() throws Throwable {
		GL4 gl = GLGraphics.currentGL();
		gl.glDeleteTextures(1, GLBufferUtil.wrapParameters(_myID));
	}
}
