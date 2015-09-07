package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLObjectType {
	BUFFER(GL4.GL_BUFFER),
	FRAMEBUFFER(GL4.GL_FRAMEBUFFER),
	PROGRAM_PIPELINE(GL4.GL_PROGRAM_PIPELINE),
	PROGRAM(GL4.GL_PROGRAM),
	QUERY(GL4.GL_QUERY),
	RENDERBUFFER(GL4.GL_RENDERBUFFER),
	SAMPLER(GL4.GL_SAMPLER),
	SHADER(GL4.GL_SHADER),
	TEXTURE(GL4.GL_TEXTURE),
	TRANSFORM_FEEDBACK(GL4.GL_TRANSFORM_FEEDBACK),
	NONE(GL4.GL_NONE);
	
	private int _myGLID;
	
	private GLObjectType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLObjectType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_BUFFER:return BUFFER;
		case GL4.GL_FRAMEBUFFER:return FRAMEBUFFER;
		case GL4.GL_PROGRAM_PIPELINE:return PROGRAM_PIPELINE;
		case GL4.GL_PROGRAM:return PROGRAM;
		case GL4.GL_QUERY:return QUERY;
		case GL4.GL_RENDERBUFFER:return RENDERBUFFER;
		case GL4.GL_SAMPLER:return SAMPLER;
		case GL4.GL_SHADER:return SHADER;
		case GL4.GL_TEXTURE:return TEXTURE;
		case GL4.GL_TRANSFORM_FEEDBACK:return TRANSFORM_FEEDBACK;
		case GL4.GL_NONE:return NONE;
		}
		return null;
	}
}

