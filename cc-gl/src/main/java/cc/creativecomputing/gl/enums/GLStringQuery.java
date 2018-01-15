package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLStringQuery {
	RENDERER(GL4.GL_RENDERER),
	VENDOR(GL4.GL_VENDOR),
	VERSION(GL4.GL_VERSION),
	SHADING_LANGUAGE_VERSION(GL4.GL_SHADING_LANGUAGE_VERSION);
	
	private int _myGLID;
	
	GLStringQuery(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLStringQuery fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_RENDERER:return RENDERER;
		case GL4.GL_VENDOR:return VENDOR;
		case GL4.GL_VERSION:return VERSION;
		case GL4.GL_SHADING_LANGUAGE_VERSION:return SHADING_LANGUAGE_VERSION;
		}
		return null;
	}
}

