package cc.creativecomputing.gl4;

import com.jogamp.opengl.GL4;

public enum GLAccesMode {
	
	READ_ONLY(GL4.GL_READ_ONLY),
	WRITE_ONLY(GL4.GL_WRITE_ONLY),
	READ_WRITE(GL4.GL_READ_WRITE);

	private int glID;
	
	private GLAccesMode(int theGLID){
		glID = theGLID;
	}
	
	public int glID(){
		return glID;
	}
}
