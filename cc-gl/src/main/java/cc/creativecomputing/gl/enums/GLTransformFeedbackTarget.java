package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTransformFeedbackTarget {
	TRANSFORM_FEEDBACK(GL4.GL_TRANSFORM_FEEDBACK);
	
	private int _myGLID;
	
	GLTransformFeedbackTarget(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTransformFeedbackTarget fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_TRANSFORM_FEEDBACK:return TRANSFORM_FEEDBACK;
		}
		return null;
	}
}

