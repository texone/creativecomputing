package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLDebugOutputSeverity {
	DEBUG_SEVERITY_HIGH(GL4.GL_DEBUG_SEVERITY_HIGH),
	DEBUG_SEVERITY_MEDIUM(GL4.GL_DEBUG_SEVERITY_MEDIUM),
	DEBUG_SEVERITY_LOW(GL4.GL_DEBUG_SEVERITY_LOW),
	DEBUG_SEVERITY_NOTIFICATION(GL4.GL_DEBUG_SEVERITY_NOTIFICATION),
	DONT_CARE(GL4.GL_DONT_CARE);
	
	private int _myGLID;
	
	private GLDebugOutputSeverity(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLDebugOutputSeverity fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_DEBUG_SEVERITY_HIGH:return DEBUG_SEVERITY_HIGH;
		case GL4.GL_DEBUG_SEVERITY_MEDIUM:return DEBUG_SEVERITY_MEDIUM;
		case GL4.GL_DEBUG_SEVERITY_LOW:return DEBUG_SEVERITY_LOW;
		case GL4.GL_DEBUG_SEVERITY_NOTIFICATION:return DEBUG_SEVERITY_NOTIFICATION;
		case GL4.GL_DONT_CARE:return DONT_CARE;
		}
		return null;
	}
}

