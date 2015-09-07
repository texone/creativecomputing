package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLPrecisionType {
	LOW_FLOAT(GL4.GL_LOW_FLOAT),
	MEDIUM_FLOAT(GL4.GL_MEDIUM_FLOAT),
	HIGH_FLOAT(GL4.GL_HIGH_FLOAT),
	LOW_INT(GL4.GL_LOW_INT),
	MEDIUM_INT(GL4.GL_MEDIUM_INT),
	HIGH_INT(GL4.GL_HIGH_INT);
	
	private int _myGLID;
	
	private GLPrecisionType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPrecisionType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_LOW_FLOAT:return LOW_FLOAT;
		case GL4.GL_MEDIUM_FLOAT:return MEDIUM_FLOAT;
		case GL4.GL_HIGH_FLOAT:return HIGH_FLOAT;
		case GL4.GL_LOW_INT:return LOW_INT;
		case GL4.GL_MEDIUM_INT:return MEDIUM_INT;
		case GL4.GL_HIGH_INT:return HIGH_INT;
		}
		return null;
	}
}

