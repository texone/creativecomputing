package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLBlendFunction {
	ZERO(GL4.GL_ZERO),
	ONE(GL4.GL_ONE),
	SRC_COLOR(GL4.GL_SRC_COLOR),
	ONE_MINUS_SRC_COLOR(GL4.GL_ONE_MINUS_SRC_COLOR),
	DST_COLOR(GL4.GL_DST_COLOR),
	ONE_MINUS_DST_COLOR(GL4.GL_ONE_MINUS_DST_COLOR),
	SRC_ALPHA(GL4.GL_SRC_ALPHA),
	ONE_MINUS_SRC_ALPHA(GL4.GL_ONE_MINUS_SRC_ALPHA),
	DST_ALPHA(GL4.GL_DST_ALPHA),
	ONE_MINUS_DST_ALPHA(GL4.GL_ONE_MINUS_DST_ALPHA),
	CONSTANT_COLOR(GL4.GL_CONSTANT_COLOR),
	ONE_MINUS_CONSTANT_COLOR(GL4.GL_ONE_MINUS_CONSTANT_COLOR),
	CONSTANT_ALPHA(GL4.GL_CONSTANT_ALPHA),
	ONE_MINUS_CONSTANT_ALPHA(GL4.GL_ONE_MINUS_CONSTANT_ALPHA),
	SRC_ALPHA_SATURATE(GL4.GL_SRC_ALPHA_SATURATE),
	SRC1_COLOR(GL4.GL_SRC1_COLOR),
	ONE_MINUS_SRC1_COLOR(GL4.GL_ONE_MINUS_SRC1_COLOR),
	SRC1_ALPHA(GL4.GL_SRC1_ALPHA),
	ONE_MINUS_SRC1_ALPHA(GL4.GL_ONE_MINUS_SRC1_ALPHA);
	
	private int _myGLID;
	
	GLBlendFunction(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLBlendFunction fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_ZERO:return ZERO;
		case GL4.GL_ONE:return ONE;
		case GL4.GL_SRC_COLOR:return SRC_COLOR;
		case GL4.GL_ONE_MINUS_SRC_COLOR:return ONE_MINUS_SRC_COLOR;
		case GL4.GL_DST_COLOR:return DST_COLOR;
		case GL4.GL_ONE_MINUS_DST_COLOR:return ONE_MINUS_DST_COLOR;
		case GL4.GL_SRC_ALPHA:return SRC_ALPHA;
		case GL4.GL_ONE_MINUS_SRC_ALPHA:return ONE_MINUS_SRC_ALPHA;
		case GL4.GL_DST_ALPHA:return DST_ALPHA;
		case GL4.GL_ONE_MINUS_DST_ALPHA:return ONE_MINUS_DST_ALPHA;
		case GL4.GL_CONSTANT_COLOR:return CONSTANT_COLOR;
		case GL4.GL_ONE_MINUS_CONSTANT_COLOR:return ONE_MINUS_CONSTANT_COLOR;
		case GL4.GL_CONSTANT_ALPHA:return CONSTANT_ALPHA;
		case GL4.GL_ONE_MINUS_CONSTANT_ALPHA:return ONE_MINUS_CONSTANT_ALPHA;
		case GL4.GL_SRC_ALPHA_SATURATE:return SRC_ALPHA_SATURATE;
		case GL4.GL_SRC1_COLOR:return SRC1_COLOR;
		case GL4.GL_ONE_MINUS_SRC1_COLOR:return ONE_MINUS_SRC1_COLOR;
		case GL4.GL_SRC1_ALPHA:return SRC1_ALPHA;
		case GL4.GL_ONE_MINUS_SRC1_ALPHA:return ONE_MINUS_SRC1_ALPHA;
		}
		return null;
	}
}

