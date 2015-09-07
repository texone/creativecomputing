package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBufferStorageBit {
	MAP_READ_BIT(GL4.GL_MAP_READ_BIT),
	MAP_WRITE_BIT(GL4.GL_MAP_WRITE_BIT);
	
	private int _myGLID;
	
	private GLBufferStorageBit(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBufferStorageBit fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_MAP_READ_BIT:return MAP_READ_BIT;
		case GL4.GL_MAP_WRITE_BIT:return MAP_WRITE_BIT;
		}
		return null;
	}
}

