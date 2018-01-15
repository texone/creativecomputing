package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFunctionality {
	CLIP_DISTANCE0(GL4.GL_CLIP_DISTANCE0);
	
	private int _myGLID;
	
	GLFunctionality(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFunctionality fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_CLIP_DISTANCE0:return CLIP_DISTANCE0;
		}
		return null;
	}
}

