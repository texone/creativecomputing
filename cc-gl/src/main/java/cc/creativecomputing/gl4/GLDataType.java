package cc.creativecomputing.gl4;

import com.jogamp.opengl.GL4;

public enum GLDataType {
	BYTE(GL4.GL_BYTE),
	SHORT(GL4.GL_SHORT),
	INT(GL4.GL_INT),
	FIXED(GL4.GL_FIXED),
	FLOAT(GL4.GL_FLOAT),
	HALF_FLOAT(GL4.GL_HALF_FLOAT),
	DOUBLE(GL4.GL_DOUBLE),
	UNSIGNED_BYTE(GL4.GL_UNSIGNED_BYTE),
	UNSIGNED_SHORT(GL4.GL_UNSIGNED_SHORT),
	UNSIGNED_INT(GL4.GL_UNSIGNED_INT);
	
	private int _myGLID;
	
	private GLDataType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLDataType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_BYTE:return BYTE;
		case GL4.GL_SHORT:return SHORT;
		case GL4.GL_INT:return INT;
		case GL4.GL_FIXED:return FIXED;
		case GL4.GL_FLOAT:return FLOAT;
		case GL4.GL_HALF_FLOAT:return HALF_FLOAT;
		case GL4.GL_DOUBLE:return DOUBLE;
		case GL4.GL_UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case GL4.GL_UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case GL4.GL_UNSIGNED_INT:return UNSIGNED_INT;
		}
		return null;
	}
}

