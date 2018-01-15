package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBufferUsage {
	STREAM_DRAW(GL4.GL_STREAM_DRAW),
	STREAM_READ(GL4.GL_STREAM_READ),
	STREAM_COPY(GL4.GL_STREAM_COPY),
	STATIC_DRAW(GL4.GL_STATIC_DRAW),
	STATIC_READ(GL4.GL_STATIC_READ),
	STATIC_COPY(GL4.GL_STATIC_COPY),
	DYNAMIC_DRAW(GL4.GL_DYNAMIC_DRAW),
	DYNAMIC_READ(GL4.GL_DYNAMIC_READ),
	DYNAMIC_COPY(GL4.GL_DYNAMIC_COPY);
	
	private int _myGLID;
	
	GLBufferUsage(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBufferUsage fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_STREAM_DRAW:return STREAM_DRAW;
		case GL4.GL_STREAM_READ:return STREAM_READ;
		case GL4.GL_STREAM_COPY:return STREAM_COPY;
		case GL4.GL_STATIC_DRAW:return STATIC_DRAW;
		case GL4.GL_STATIC_READ:return STATIC_READ;
		case GL4.GL_STATIC_COPY:return STATIC_COPY;
		case GL4.GL_DYNAMIC_DRAW:return DYNAMIC_DRAW;
		case GL4.GL_DYNAMIC_READ:return DYNAMIC_READ;
		case GL4.GL_DYNAMIC_COPY:return DYNAMIC_COPY;
		}
		return null;
	}
}

