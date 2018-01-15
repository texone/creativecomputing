package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTransformFeedbackPrimitiveType {
	TRIANGLES(GL4.GL_TRIANGLES),
	LINES(GL4.GL_LINES),
	POINTS(GL4.GL_POINTS);
	
	private int _myGLID;
	
	GLTransformFeedbackPrimitiveType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTransformFeedbackPrimitiveType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_TRIANGLES:return TRIANGLES;
		case GL4.GL_LINES:return LINES;
		case GL4.GL_POINTS:return POINTS;
		}
		return null;
	}
}

