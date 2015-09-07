package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLPatchParameter {
	PATCH_VERTICES(GL4.GL_PATCH_VERTICES),
	PATCH_DEFAULT_OUTER_LEVEL(GL4.GL_PATCH_DEFAULT_OUTER_LEVEL),
	PATCH_DEFAULT_INNER_LEVEL(GL4.GL_PATCH_DEFAULT_INNER_LEVEL);
	
	private int _myGLID;
	
	private GLPatchParameter(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPatchParameter fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_PATCH_VERTICES:return PATCH_VERTICES;
		case GL4.GL_PATCH_DEFAULT_OUTER_LEVEL:return PATCH_DEFAULT_OUTER_LEVEL;
		case GL4.GL_PATCH_DEFAULT_INNER_LEVEL:return PATCH_DEFAULT_INNER_LEVEL;
		}
		return null;
	}
}

