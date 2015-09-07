package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLHintOption {
	FASTEST(GL4.GL_FASTEST),
	NICEST(GL4.GL_NICEST),
	DONT_CARE(GL4.GL_DONT_CARE);
	
	private int _myGLID;
	
	private GLHintOption(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLHintOption fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FASTEST:return FASTEST;
		case GL4.GL_NICEST:return NICEST;
		case GL4.GL_DONT_CARE:return DONT_CARE;
		}
		return null;
	}
}

