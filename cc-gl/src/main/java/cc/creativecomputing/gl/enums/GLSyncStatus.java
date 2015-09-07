package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLSyncStatus {
	SIGNALED(GL4.GL_SIGNALED),
	UNSIGNALED(GL4.GL_UNSIGNALED);
	
	private int _myGLID;
	
	private GLSyncStatus(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLSyncStatus fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_SIGNALED:return SIGNALED;
		case GL4.GL_UNSIGNALED:return UNSIGNALED;
		}
		return null;
	}
}

