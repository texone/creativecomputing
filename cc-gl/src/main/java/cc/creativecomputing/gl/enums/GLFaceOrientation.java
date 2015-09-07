package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFaceOrientation {
	CW(GL4.GL_CW),
	CCW(GL4.GL_CCW);
	
	private int _myGLID;
	
	private GLFaceOrientation(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFaceOrientation fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_CW:return CW;
		case GL4.GL_CCW:return CCW;
		}
		return null;
	}
}

