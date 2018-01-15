package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLRenderbufferTarget {
	RENDERBUFFER(GL4.GL_RENDERBUFFER);
	
	private int _myGLID;
	
	GLRenderbufferTarget(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLRenderbufferTarget fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_RENDERBUFFER:return RENDERBUFFER;
		}
		return null;
	}
}

