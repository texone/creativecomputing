package cc.creativecomputing.graphics.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;

public enum CCShaderObjectType{
	
	VERTEX(GL_VERTEX_SHADER, "vertex"),
	FRAGMENT(GL_FRAGMENT_SHADER, "fragment"),
	GEOMETRY(GL_GEOMETRY_SHADER, "geometry"),
	COMPUTE(GL_COMPUTE_SHADER, "compute");
	
	int glID;
	
	String typeString;
	
	CCShaderObjectType(final int theGLID, String theTypeString) {
		glID = theGLID;
		typeString = theTypeString;
	}
}