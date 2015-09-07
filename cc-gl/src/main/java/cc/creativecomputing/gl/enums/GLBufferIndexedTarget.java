package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBufferIndexedTarget {
	ATOMIC_COUNTER_BUFFER(GL4.GL_ATOMIC_COUNTER_BUFFER),
	SHADER_STORAGE_BUFFER(GL4.GL_SHADER_STORAGE_BUFFER),
	TRANSFORM_FEEDBACK_BUFFER(GL4.GL_TRANSFORM_FEEDBACK_BUFFER),
	UNIFORM_BUFFER(GL4.GL_UNIFORM_BUFFER);
	
	private int _myGLID;
	
	private GLBufferIndexedTarget(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBufferIndexedTarget fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_ATOMIC_COUNTER_BUFFER:return ATOMIC_COUNTER_BUFFER;
		case GL4.GL_SHADER_STORAGE_BUFFER:return SHADER_STORAGE_BUFFER;
		case GL4.GL_TRANSFORM_FEEDBACK_BUFFER:return TRANSFORM_FEEDBACK_BUFFER;
		case GL4.GL_UNIFORM_BUFFER:return UNIFORM_BUFFER;
		}
		return null;
	}
}

