package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBlendEquation {
	FUNC_ADD(GL4.GL_FUNC_ADD),
	FUNC_SUBTRACT(GL4.GL_FUNC_SUBTRACT),
	FUNC_REVERSE_SUBTRACT(GL4.GL_FUNC_REVERSE_SUBTRACT),
	MIN(GL4.GL_MIN),
	MAX(GL4.GL_MAX);
	
	private int _myGLID;
	
	GLBlendEquation(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBlendEquation fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FUNC_ADD:return FUNC_ADD;
		case GL4.GL_FUNC_SUBTRACT:return FUNC_SUBTRACT;
		case GL4.GL_FUNC_REVERSE_SUBTRACT:return FUNC_REVERSE_SUBTRACT;
		case GL4.GL_MIN:return MIN;
		case GL4.GL_MAX:return MAX;
		}
		return null;
	}
}

