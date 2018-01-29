package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The cursor mode provides several cursor modes for special forms of mouse motion input. 
 * @author christianr
 *
 */
public enum CCGLCursorMode {
	/**
	 * makes the cursor visible and behaving normally.
	 */
	NORMAL(GLFW_CURSOR_NORMAL),
	/**
	 * makes the cursor invisible when it is over the client area of the window but does not restrict the cursor from leaving.
	 */
	HIDDEN(GLFW_CURSOR_HIDDEN),
	/**
	 * hides and grabs the cursor, providing virtual and unlimited cursor movement. This is useful for implementing for example 3D camera controls..
	 */
	DISABLED(GLFW_CURSOR_DISABLED);
	

	private final int _myID;

	private CCGLCursorMode(int theID) {
		_myID = theID;
	}

	public int id() {
		return _myID;
	}
}
