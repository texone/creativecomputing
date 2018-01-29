package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Key and button actions
 * @author christianr
 *
 */
public enum CCGLAction {
	/**
	 * The key or mouse button was released.
	 */
	RELEASE(GLFW_RELEASE),
	/**
	 * The key or mouse button was pressed.
	 */
	PRESS(GLFW_PRESS),
	/**
	 * The key was held down until it repeated.
	 */
	REPEAT(GLFW_REPEAT);
	

	private final int _myID;

	private CCGLAction(int theID) {
		_myID = theID;
	}

	public int id() {
		return _myID;
	}
	
	public static CCGLAction action(int theID){
		switch(theID){
		case GLFW_RELEASE:return RELEASE;
		case GLFW_PRESS:return PRESS;
		case GLFW_REPEAT:return REPEAT;
		}
		return PRESS;
	}
}
