package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLColorBuffer {
	NONE(GL4.GL_NONE),
	COLOR(GL4.GL_COLOR),
	FRONT_LEFT(GL4.GL_FRONT_LEFT),
	FRONT_RIGHT(GL4.GL_FRONT_RIGHT),
	BACK_LEFT(GL4.GL_BACK_LEFT),
	BACK_RIGHT(GL4.GL_BACK_RIGHT),
	FRONT(GL4.GL_FRONT),
	BACK(GL4.GL_BACK),
	LEFT(GL4.GL_LEFT),
	RIGHT(GL4.GL_RIGHT),
	FRONT_AND_BACK(GL4.GL_FRONT_AND_BACK);
	
	private int _myGLID;
	
	GLColorBuffer(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLColorBuffer fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_NONE:return NONE;
		case GL4.GL_COLOR:return COLOR;
		case GL4.GL_FRONT_LEFT:return FRONT_LEFT;
		case GL4.GL_FRONT_RIGHT:return FRONT_RIGHT;
		case GL4.GL_BACK_LEFT:return BACK_LEFT;
		case GL4.GL_BACK_RIGHT:return BACK_RIGHT;
		case GL4.GL_FRONT:return FRONT;
		case GL4.GL_BACK:return BACK;
		case GL4.GL_LEFT:return LEFT;
		case GL4.GL_RIGHT:return RIGHT;
		case GL4.GL_FRONT_AND_BACK:return FRONT_AND_BACK;
		}
		return null;
	}
}

