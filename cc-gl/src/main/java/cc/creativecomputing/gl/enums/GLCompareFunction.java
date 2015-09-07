package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLCompareFunction {
	LEQUAL(GL4.GL_LEQUAL),
	GEQUAL(GL4.GL_GEQUAL),
	LESS(GL4.GL_LESS),
	GREATER(GL4.GL_GREATER),
	EQUAL(GL4.GL_EQUAL),
	NOTEQUAL(GL4.GL_NOTEQUAL),
	ALWAYS(GL4.GL_ALWAYS),
	NEVER(GL4.GL_NEVER);
	
	private int _myGLID;
	
	private GLCompareFunction(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLCompareFunction fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_LEQUAL:return LEQUAL;
		case GL4.GL_GEQUAL:return GEQUAL;
		case GL4.GL_LESS:return LESS;
		case GL4.GL_GREATER:return GREATER;
		case GL4.GL_EQUAL:return EQUAL;
		case GL4.GL_NOTEQUAL:return NOTEQUAL;
		case GL4.GL_ALWAYS:return ALWAYS;
		case GL4.GL_NEVER:return NEVER;
		}
		return null;
	}
}

