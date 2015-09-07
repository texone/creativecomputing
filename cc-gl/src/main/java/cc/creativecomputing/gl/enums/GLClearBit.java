package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLClearBit {
	COLOR_BUFFER_BIT(GL4.GL_COLOR_BUFFER_BIT),
	DEPTH_BUFFER_BIT(GL4.GL_DEPTH_BUFFER_BIT),
	STENCIL_BUFFER_BIT(GL4.GL_STENCIL_BUFFER_BIT);
	
	private int _myGLID;
	
	private GLClearBit(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
}

