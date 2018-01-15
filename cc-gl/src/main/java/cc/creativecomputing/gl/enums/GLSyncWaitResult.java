package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLSyncWaitResult {
	CONDITION_SATISFIED(GL4.GL_CONDITION_SATISFIED),
	ALREADY_SIGNALED(GL4.GL_ALREADY_SIGNALED),
	TIMEOUT_EXPIRED(GL4.GL_TIMEOUT_EXPIRED),
	WAIT_FAILED(GL4.GL_WAIT_FAILED);
	
	private int _myGLID;
	
	GLSyncWaitResult(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLSyncWaitResult fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_CONDITION_SATISFIED:return CONDITION_SATISFIED;
		case GL4.GL_ALREADY_SIGNALED:return ALREADY_SIGNALED;
		case GL4.GL_TIMEOUT_EXPIRED:return TIMEOUT_EXPIRED;
		case GL4.GL_WAIT_FAILED:return WAIT_FAILED;
		}
		return null;
	}
}

