package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLFramebufferAttachment {
	COLOR_ATTACHMENT0(GL4.GL_COLOR_ATTACHMENT0),
	COLOR_ATTACHMENT1(GL4.GL_COLOR_ATTACHMENT1),
	COLOR_ATTACHMENT2(GL4.GL_COLOR_ATTACHMENT2),
	COLOR_ATTACHMENT3(GL4.GL_COLOR_ATTACHMENT3),
	COLOR_ATTACHMENT4(GL4.GL_COLOR_ATTACHMENT4),
	COLOR_ATTACHMENT5(GL4.GL_COLOR_ATTACHMENT5),
	COLOR_ATTACHMENT6(GL4.GL_COLOR_ATTACHMENT6),
	COLOR_ATTACHMENT7(GL4.GL_COLOR_ATTACHMENT7),
	COLOR_ATTACHMENT8(GL4.GL_COLOR_ATTACHMENT8),
	COLOR_ATTACHMENT9(GL4.GL_COLOR_ATTACHMENT9),
	COLOR_ATTACHMENT10(GL4.GL_COLOR_ATTACHMENT10),
	COLOR_ATTACHMENT11(GL4.GL_COLOR_ATTACHMENT11),
	COLOR_ATTACHMENT12(GL4.GL_COLOR_ATTACHMENT12),
	COLOR_ATTACHMENT13(GL4.GL_COLOR_ATTACHMENT13),
	COLOR_ATTACHMENT14(GL4.GL_COLOR_ATTACHMENT14),
	COLOR_ATTACHMENT15(GL4.GL_COLOR_ATTACHMENT15),
	DEPTH_ATTACHMENT(GL4.GL_DEPTH_ATTACHMENT),
	STENCIL_ATTACHMENT(GL4.GL_STENCIL_ATTACHMENT),
	DEPTH_STENCIL_ATTACHMENT(GL4.GL_DEPTH_STENCIL_ATTACHMENT);
	
	private int _myGLID;
	
	GLFramebufferAttachment(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLFramebufferAttachment fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_COLOR_ATTACHMENT0:return COLOR_ATTACHMENT0;
		case GL4.GL_COLOR_ATTACHMENT1:return COLOR_ATTACHMENT1;
		case GL4.GL_COLOR_ATTACHMENT2:return COLOR_ATTACHMENT2;
		case GL4.GL_COLOR_ATTACHMENT3:return COLOR_ATTACHMENT3;
		case GL4.GL_COLOR_ATTACHMENT4:return COLOR_ATTACHMENT4;
		case GL4.GL_COLOR_ATTACHMENT5:return COLOR_ATTACHMENT5;
		case GL4.GL_COLOR_ATTACHMENT6:return COLOR_ATTACHMENT6;
		case GL4.GL_COLOR_ATTACHMENT7:return COLOR_ATTACHMENT7;
		case GL4.GL_COLOR_ATTACHMENT8:return COLOR_ATTACHMENT8;
		case GL4.GL_COLOR_ATTACHMENT9:return COLOR_ATTACHMENT9;
		case GL4.GL_COLOR_ATTACHMENT10:return COLOR_ATTACHMENT10;
		case GL4.GL_COLOR_ATTACHMENT11:return COLOR_ATTACHMENT11;
		case GL4.GL_COLOR_ATTACHMENT12:return COLOR_ATTACHMENT12;
		case GL4.GL_COLOR_ATTACHMENT13:return COLOR_ATTACHMENT13;
		case GL4.GL_COLOR_ATTACHMENT14:return COLOR_ATTACHMENT14;
		case GL4.GL_COLOR_ATTACHMENT15:return COLOR_ATTACHMENT15;
		case GL4.GL_DEPTH_ATTACHMENT:return DEPTH_ATTACHMENT;
		case GL4.GL_STENCIL_ATTACHMENT:return STENCIL_ATTACHMENT;
		case GL4.GL_DEPTH_STENCIL_ATTACHMENT:return DEPTH_STENCIL_ATTACHMENT;
		}
		return null;
	}
}

