package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFramebufferStatus {
	FRAMEBUFFER_COMPLETE(GL4.GL_FRAMEBUFFER_COMPLETE),
	FRAMEBUFFER_UNDEFINED(GL4.GL_FRAMEBUFFER_UNDEFINED),
	FRAMEBUFFER_INCOMPLETE_ATTACHMENT(GL4.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT),
	FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT(GL4.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT),
	FRAMEBUFFER_UNSUPPORTED(GL4.GL_FRAMEBUFFER_UNSUPPORTED),
	FRAMEBUFFER_INCOMPLETE_MULTISAMPLE(GL4.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE),
	FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS(GL4.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS);
	
	private int _myGLID;
	
	GLFramebufferStatus(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFramebufferStatus fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_FRAMEBUFFER_COMPLETE:return FRAMEBUFFER_COMPLETE;
		case GL4.GL_FRAMEBUFFER_UNDEFINED:return FRAMEBUFFER_UNDEFINED;
		case GL4.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:return FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
		case GL4.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:return FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
		case GL4.GL_FRAMEBUFFER_UNSUPPORTED:return FRAMEBUFFER_UNSUPPORTED;
		case GL4.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:return FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
		case GL4.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:return FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
		}
		return null;
	}
}

