package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTessGenPrimitiveSpacing {
	FRACTIONAL_EVEN(GL4.GL_FRACTIONAL_EVEN),
	FRACTIONAL_ODD(GL4.GL_FRACTIONAL_ODD),
	EQUAL(GL4.GL_EQUAL);
	
	private int _myGLID;
	
	private GLTessGenPrimitiveSpacing(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTessGenPrimitiveSpacing fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FRACTIONAL_EVEN:return FRACTIONAL_EVEN;
		case GL4.GL_FRACTIONAL_ODD:return FRACTIONAL_ODD;
		case GL4.GL_EQUAL:return EQUAL;
		}
		return null;
	}
}

