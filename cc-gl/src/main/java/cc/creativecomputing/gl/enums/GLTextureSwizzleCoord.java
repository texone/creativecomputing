package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTextureSwizzleCoord {
	TEXTURE_SWIZZLE_R(GL4.GL_TEXTURE_SWIZZLE_R),
	TEXTURE_SWIZZLE_G(GL4.GL_TEXTURE_SWIZZLE_G),
	TEXTURE_SWIZZLE_B(GL4.GL_TEXTURE_SWIZZLE_B),
	TEXTURE_SWIZZLE_A(GL4.GL_TEXTURE_SWIZZLE_A),
	TEXTURE_SWIZZLE_RGBA(GL4.GL_TEXTURE_SWIZZLE_RGBA);
	
	private int _myGLID;
	
	private GLTextureSwizzleCoord(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTextureSwizzleCoord fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_TEXTURE_SWIZZLE_R:return TEXTURE_SWIZZLE_R;
		case GL4.GL_TEXTURE_SWIZZLE_G:return TEXTURE_SWIZZLE_G;
		case GL4.GL_TEXTURE_SWIZZLE_B:return TEXTURE_SWIZZLE_B;
		case GL4.GL_TEXTURE_SWIZZLE_A:return TEXTURE_SWIZZLE_A;
		case GL4.GL_TEXTURE_SWIZZLE_RGBA:return TEXTURE_SWIZZLE_RGBA;
		}
		return null;
	}
}

