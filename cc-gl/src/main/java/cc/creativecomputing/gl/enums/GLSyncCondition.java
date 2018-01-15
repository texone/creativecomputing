package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLSyncCondition {
	SYNC_GPU_COMMANDS_COMPLETE(GL4.GL_SYNC_GPU_COMMANDS_COMPLETE);
	
	private int _myGLID;
	
	GLSyncCondition(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLSyncCondition fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_SYNC_GPU_COMMANDS_COMPLETE:return SYNC_GPU_COMMANDS_COMPLETE;
		}
		return null;
	}
}

