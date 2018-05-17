package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_LINE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public enum GLPrimitiveType {
	POINTS(GL_POINTS),
	LINE_STRIP(GL_LINE_STRIP),
	LINE_LOOP(GL_LINE_LOOP),
	LINES(GL_LINES),
	TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL_TRIANGLE_FAN),
	TRIANGLES(GL_TRIANGLES),
	LINES_ADJACENCY(GL_LINES_ADJACENCY),
	LINE_STRIP_ADJACENCY(GL_LINE_STRIP_ADJACENCY),
	TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY),
	TRIANGLE_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY),
	PATCHES(GL_PATCHES);
	
	private int _myGLID;
	
	GLPrimitiveType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPrimitiveType fromGLID(int theGLID){
		switch(theGLID){
		case GL_POINTS:return POINTS;
		case GL_LINE_STRIP:return LINE_STRIP;
		case GL_LINE_LOOP:return LINE_LOOP;
		case GL_LINES:return LINES;
		case GL_TRIANGLE_STRIP:return TRIANGLE_STRIP;
		case GL_TRIANGLE_FAN:return TRIANGLE_FAN;
		case GL_TRIANGLES:return TRIANGLES;
		case GL_LINES_ADJACENCY:return LINES_ADJACENCY;
		case GL_LINE_STRIP_ADJACENCY:return LINE_STRIP_ADJACENCY;
		case GL_TRIANGLES_ADJACENCY:return TRIANGLES_ADJACENCY;
		case GL_TRIANGLE_STRIP_ADJACENCY:return TRIANGLE_STRIP_ADJACENCY;
		case GL_PATCHES:return PATCHES;
		}
		return null;
	}
}

