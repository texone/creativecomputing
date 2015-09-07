package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLSyncType {
	SYNC_FENCE(GL4.GL_SYNC_FENCE);
	
	private int _myGLID;
	
	private GLSyncType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLSyncType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_SYNC_FENCE:return SYNC_FENCE;
		}
		return null;
	}
}

