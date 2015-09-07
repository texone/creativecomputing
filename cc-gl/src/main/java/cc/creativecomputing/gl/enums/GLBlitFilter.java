package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBlitFilter {
	NEAREST(GL4.GL_NEAREST),
	LINEAR(GL4.GL_LINEAR);
	
	private int _myGLID;
	
	private GLBlitFilter(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBlitFilter fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_NEAREST:return NEAREST;
		case GL4.GL_LINEAR:return LINEAR;
		}
		return null;
	}
}

