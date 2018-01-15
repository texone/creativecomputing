package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLProgramInterface {
	UNIFORM(GL4.GL_UNIFORM),
	UNIFORM_BLOCK(GL4.GL_UNIFORM_BLOCK),
	ATOMIC_COUNTER_BUFFER(GL4.GL_ATOMIC_COUNTER_BUFFER),
	PROGRAM_INPUT(GL4.GL_PROGRAM_INPUT),
	PROGRAM_OUTPUT(GL4.GL_PROGRAM_OUTPUT),
	VERTEX_SUBROUTINE(GL4.GL_VERTEX_SUBROUTINE),
	TESS_CONTROL_SUBROUTINE(GL4.GL_TESS_CONTROL_SUBROUTINE),
	TESS_EVALUATION_SUBROUTINE(GL4.GL_TESS_EVALUATION_SUBROUTINE),
	GEOMETRY_SUBROUTINE(GL4.GL_GEOMETRY_SUBROUTINE),
	FRAGMENT_SUBROUTINE(GL4.GL_FRAGMENT_SUBROUTINE),
	COMPUTE_SUBROUTINE(GL4.GL_COMPUTE_SUBROUTINE),
	VERTEX_SUBROUTINE_UNIFORM(GL4.GL_VERTEX_SUBROUTINE_UNIFORM),
	TESS_CONTROL_SUBROUTINE_UNIFORM(GL4.GL_TESS_CONTROL_SUBROUTINE_UNIFORM),
	TESS_EVALUATION_SUBROUTINE_UNIFORM(GL4.GL_TESS_EVALUATION_SUBROUTINE_UNIFORM),
	GEOMETRY_SUBROUTINE_UNIFORM(GL4.GL_GEOMETRY_SUBROUTINE_UNIFORM),
	FRAGMENT_SUBROUTINE_UNIFORM(GL4.GL_FRAGMENT_SUBROUTINE_UNIFORM),
	COMPUTE_SUBROUTINE_UNIFORM(GL4.GL_COMPUTE_SUBROUTINE_UNIFORM),
	BUFFER_VARIABLE(GL4.GL_BUFFER_VARIABLE),
	SHADER_STORAGE_BLOCK(GL4.GL_SHADER_STORAGE_BLOCK);
	
	private int _myGLID;
	
	GLProgramInterface(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLProgramInterface fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_UNIFORM:return UNIFORM;
		case GL4.GL_UNIFORM_BLOCK:return UNIFORM_BLOCK;
		case GL4.GL_ATOMIC_COUNTER_BUFFER:return ATOMIC_COUNTER_BUFFER;
		case GL4.GL_PROGRAM_INPUT:return PROGRAM_INPUT;
		case GL4.GL_PROGRAM_OUTPUT:return PROGRAM_OUTPUT;
		case GL4.GL_VERTEX_SUBROUTINE:return VERTEX_SUBROUTINE;
		case GL4.GL_TESS_CONTROL_SUBROUTINE:return TESS_CONTROL_SUBROUTINE;
		case GL4.GL_TESS_EVALUATION_SUBROUTINE:return TESS_EVALUATION_SUBROUTINE;
		case GL4.GL_GEOMETRY_SUBROUTINE:return GEOMETRY_SUBROUTINE;
		case GL4.GL_FRAGMENT_SUBROUTINE:return FRAGMENT_SUBROUTINE;
		case GL4.GL_COMPUTE_SUBROUTINE:return COMPUTE_SUBROUTINE;
		case GL4.GL_VERTEX_SUBROUTINE_UNIFORM:return VERTEX_SUBROUTINE_UNIFORM;
		case GL4.GL_TESS_CONTROL_SUBROUTINE_UNIFORM:return TESS_CONTROL_SUBROUTINE_UNIFORM;
		case GL4.GL_TESS_EVALUATION_SUBROUTINE_UNIFORM:return TESS_EVALUATION_SUBROUTINE_UNIFORM;
		case GL4.GL_GEOMETRY_SUBROUTINE_UNIFORM:return GEOMETRY_SUBROUTINE_UNIFORM;
		case GL4.GL_FRAGMENT_SUBROUTINE_UNIFORM:return FRAGMENT_SUBROUTINE_UNIFORM;
		case GL4.GL_COMPUTE_SUBROUTINE_UNIFORM:return COMPUTE_SUBROUTINE_UNIFORM;
		case GL4.GL_BUFFER_VARIABLE:return BUFFER_VARIABLE;
		case GL4.GL_SHADER_STORAGE_BLOCK:return SHADER_STORAGE_BLOCK;
		}
		return null;
	}
}

