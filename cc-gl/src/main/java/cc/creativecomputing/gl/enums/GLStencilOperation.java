package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLStencilOperation {
	KEEP(GL4.GL_KEEP),
	ZERO(GL4.GL_ZERO),
	REPLACE(GL4.GL_REPLACE),
	INCR(GL4.GL_INCR),
	DECR(GL4.GL_DECR),
	INVERT(GL4.GL_INVERT),
	INCR_WRAP(GL4.GL_INCR_WRAP),
	DECR_WRAP(GL4.GL_DECR_WRAP);
	
	private int _myGLID;
	
	private GLStencilOperation(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLStencilOperation fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_KEEP:return KEEP;
		case GL4.GL_ZERO:return ZERO;
		case GL4.GL_REPLACE:return REPLACE;
		case GL4.GL_INCR:return INCR;
		case GL4.GL_DECR:return DECR;
		case GL4.GL_INVERT:return INVERT;
		case GL4.GL_INCR_WRAP:return INCR_WRAP;
		case GL4.GL_DECR_WRAP:return DECR_WRAP;
		}
		return null;
	}
}

