package cc.creativecomputing.gl.app;

import com.jogamp.opengl.GL;


public abstract class CCGLGraphics <GLType extends GL>{
	/**
	 * width of the parent application
	 */
	protected int _myWidth;

	/**
	 * height of the parent application
	 */
	protected int _myHeight;
	
	public GLType gl;
	
	public CCGLGraphics(GLType theGL, int theWidth, int theHeight){
		gl = theGL;
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	
	public void reshape(int theX, int theY, int theWidth, int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public abstract void clear();

	/**
	 * Returns the height of the gl context.
	 * @return height of the gl context
	 */
	public int height(){
		return _myHeight;
	}
	
	/**
	 * Returns the width of the gl context.
	 * @return width of the gl context
	 */
	public int width(){
		return _myWidth;
	}
	
	/**
	 * Returns the aspect ratio of the gl context
	 * @return aspect ratio of the gl context
	 */
	public float aspectRatio(){
		return (float)_myWidth / (float)_myHeight;
	}
	
	public void viewport(final int theX, final int theY, final int theWidth, final int theHeight) {
		gl.glViewport(theX, theY, theWidth, theHeight);
	}
	
}
