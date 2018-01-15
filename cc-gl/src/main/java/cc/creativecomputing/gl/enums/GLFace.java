package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFace {
	FRONT(GL4.GL_FRONT),
	BACK(GL4.GL_BACK),
	FRONT_AND_BACK(GL4.GL_FRONT_AND_BACK);
	
	private int _myGLID;
	
	GLFace(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFace fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FRONT:return FRONT;
		case GL4.GL_BACK:return BACK;
		case GL4.GL_FRONT_AND_BACK:return FRONT_AND_BACK;
		}
		return null;
	}
}

