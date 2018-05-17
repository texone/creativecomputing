package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_WRITE;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;

public enum GLAccesMode {
	
	READ_ONLY(GL_READ_ONLY),
	WRITE_ONLY(GL_WRITE_ONLY),
	READ_WRITE(GL_READ_WRITE);

	private int glID;
	
	GLAccesMode(int theGLID){
		glID = theGLID;
	}
	
	public int glID(){
		return glID;
	}
}
