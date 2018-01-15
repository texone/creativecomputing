package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTextureCompareMode {
	NONE(GL4.GL_NONE),
	COMPARE_REF_TO_TEXTURE(GL4.GL_COMPARE_REF_TO_TEXTURE);
	
	private int _myGLID;
	
	GLTextureCompareMode(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTextureCompareMode fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_NONE:return NONE;
		case GL4.GL_COMPARE_REF_TO_TEXTURE:return COMPARE_REF_TO_TEXTURE;
		}
		return null;
	}
}

