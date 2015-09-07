package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLColorLogicOperation {
	CLEAR(GL4.GL_CLEAR),
	AND(GL4.GL_AND),
	AND_REVERSE(GL4.GL_AND_REVERSE),
	COPY(GL4.GL_COPY),
	AND_INVERTED(GL4.GL_AND_INVERTED),
	NOOP(GL4.GL_NOOP),
	XOR(GL4.GL_XOR),
	OR(GL4.GL_OR),
	NOR(GL4.GL_NOR),
	EQUIV(GL4.GL_EQUIV),
	INVERT(GL4.GL_INVERT),
	OR_REVERSE(GL4.GL_OR_REVERSE),
	COPY_INVERTED(GL4.GL_COPY_INVERTED),
	OR_INVERTED(GL4.GL_OR_INVERTED),
	NAND(GL4.GL_NAND),
	SET(GL4.GL_SET);
	
	private int _myGLID;
	
	private GLColorLogicOperation(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLColorLogicOperation fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_CLEAR:return CLEAR;
		case GL4.GL_AND:return AND;
		case GL4.GL_AND_REVERSE:return AND_REVERSE;
		case GL4.GL_COPY:return COPY;
		case GL4.GL_AND_INVERTED:return AND_INVERTED;
		case GL4.GL_NOOP:return NOOP;
		case GL4.GL_XOR:return XOR;
		case GL4.GL_OR:return OR;
		case GL4.GL_NOR:return NOR;
		case GL4.GL_EQUIV:return EQUIV;
		case GL4.GL_INVERT:return INVERT;
		case GL4.GL_OR_REVERSE:return OR_REVERSE;
		case GL4.GL_COPY_INVERTED:return COPY_INVERTED;
		case GL4.GL_OR_INVERTED:return OR_INVERTED;
		case GL4.GL_NAND:return NAND;
		case GL4.GL_SET:return SET;
		}
		return null;
	}
}

