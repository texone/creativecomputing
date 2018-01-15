package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTextureSwizzle {
	RED(GL4.GL_RED),
	GREEN(GL4.GL_GREEN),
	BLUE(GL4.GL_BLUE),
	ALPHA(GL4.GL_ALPHA),
	ZERO(GL4.GL_ZERO),
	ONE(GL4.GL_ONE);
	
	private int _myGLID;
	
	GLTextureSwizzle(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTextureSwizzle fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_RED:return RED;
		case GL4.GL_GREEN:return GREEN;
		case GL4.GL_BLUE:return BLUE;
		case GL4.GL_ALPHA:return ALPHA;
		case GL4.GL_ZERO:return ZERO;
		case GL4.GL_ONE:return ONE;
		}
		return null;
	}
}

