package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLTessGenPrimitiveType {
	QUADS(GL4.GL_QUADS),
	TRIANGLES(GL4.GL_TRIANGLES),
	ISOLINES(GL4.GL_ISOLINES);
	
	private int _myGLID;
	
	private GLTessGenPrimitiveType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLTessGenPrimitiveType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_QUADS:return QUADS;
		case GL4.GL_TRIANGLES:return TRIANGLES;
		case GL4.GL_ISOLINES:return ISOLINES;
		}
		return null;
	}
}

