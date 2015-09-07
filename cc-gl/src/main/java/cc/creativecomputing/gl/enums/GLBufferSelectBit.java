package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBufferSelectBit {
	COLOR_BUFFER_BIT(GL4.GL_COLOR_BUFFER_BIT),
	DEPTH_BUFFER_BIT(GL4.GL_DEPTH_BUFFER_BIT),
	STENCIL_BUFFER_BIT(GL4.GL_STENCIL_BUFFER_BIT);
	
	private int _myGLID;
	
	private GLBufferSelectBit(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBufferSelectBit fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_COLOR_BUFFER_BIT:return COLOR_BUFFER_BIT;
		case GL4.GL_DEPTH_BUFFER_BIT:return DEPTH_BUFFER_BIT;
		case GL4.GL_STENCIL_BUFFER_BIT:return STENCIL_BUFFER_BIT;
		}
		return null;
	}
}

