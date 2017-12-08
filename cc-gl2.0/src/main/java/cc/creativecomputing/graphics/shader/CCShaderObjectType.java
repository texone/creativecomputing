package cc.creativecomputing.graphics.shader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;

public enum CCShaderObjectType{
	
	VERTEX(GL2.GL_VERTEX_SHADER, "vertex"),
	FRAGMENT(GL2.GL_FRAGMENT_SHADER, "fragment"),
	GEOMETRY(GL3.GL_GEOMETRY_SHADER, "geometry"),
	COMPUTE(GL4.GL_COMPUTE_SHADER, "compute");
	
	int glID;
	
	String typeString;
	
	private CCShaderObjectType(final int theGLID, String theTypeString) {
		glID = theGLID;
		typeString = theTypeString;
	}
}