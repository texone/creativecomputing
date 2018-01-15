package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFramebufferTarget {
	DRAW_FRAMEBUFFER(GL4.GL_DRAW_FRAMEBUFFER),
	READ_FRAMEBUFFER(GL4.GL_READ_FRAMEBUFFER);
	
	private int _myGLID;
	
	GLFramebufferTarget(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFramebufferTarget fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_DRAW_FRAMEBUFFER:return DRAW_FRAMEBUFFER;
		case GL4.GL_READ_FRAMEBUFFER:return READ_FRAMEBUFFER;
		}
		return null;
	}
}

