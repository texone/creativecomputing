package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLPolygonMode {
	POINT(GL4.GL_POINT),
	LINE(GL4.GL_LINE),
	FILL(GL4.GL_FILL);
	
	private int _myGLID;
	
	GLPolygonMode(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPolygonMode fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_POINT:return POINT;
		case GL4.GL_LINE:return LINE;
		case GL4.GL_FILL:return FILL;
		}
		return null;
	}
}

