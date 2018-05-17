package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL41.GL_FIXED;

public enum GLDataType {
	BYTE(GL_BYTE),
	SHORT(GL_SHORT),
	INT(GL_INT),
	FIXED(GL_FIXED),
	FLOAT(GL_FLOAT),
	HALF_FLOAT(GL_HALF_FLOAT),
	DOUBLE(GL_DOUBLE),
	UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
	UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
	UNSIGNED_INT(GL_UNSIGNED_INT);
	
	private int _myGLID;
	
	GLDataType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLDataType fromGLID(int theGLID){
		switch(theGLID){
		case GL_BYTE:return BYTE;
		case GL_SHORT:return SHORT;
		case GL_INT:return INT;
		case GL_FIXED:return FIXED;
		case GL_FLOAT:return FLOAT;
		case GL_HALF_FLOAT:return HALF_FLOAT;
		case GL_DOUBLE:return DOUBLE;
		case GL_UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case GL_UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case GL_UNSIGNED_INT:return UNSIGNED_INT;
		}
		return null;
	}
}

