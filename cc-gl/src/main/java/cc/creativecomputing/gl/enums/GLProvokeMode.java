package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLProvokeMode {
	FIRST_VERTEX_CONVENTION(GL4.GL_FIRST_VERTEX_CONVENTION),
	LAST_VERTEX_CONVENTION(GL4.GL_LAST_VERTEX_CONVENTION);
	
	private int _myGLID;
	
	GLProvokeMode(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLProvokeMode fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FIRST_VERTEX_CONVENTION:return FIRST_VERTEX_CONVENTION;
		case GL4.GL_LAST_VERTEX_CONVENTION:return LAST_VERTEX_CONVENTION;
		}
		return null;
	}
}

