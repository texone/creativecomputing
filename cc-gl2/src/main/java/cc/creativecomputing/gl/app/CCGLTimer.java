package cc.creativecomputing.gl.app;

import cc.creativecomputing.core.CCTimer;

import static org.lwjgl.glfw.GLFW.*;

public class CCGLTimer extends CCTimer{
	
	
	/**
	 * Returns the time since the start of the animator in seconds
	 * @return
	 */
	public double time(){
		return glfwGetTime();
	}
}