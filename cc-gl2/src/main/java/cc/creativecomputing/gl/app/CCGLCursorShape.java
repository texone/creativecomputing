package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.GLFW.*;

public enum CCGLCursorShape {
	/**
	 * The regular arrow cursor shape.
	 */
	ARROW(GLFW_ARROW_CURSOR),
	/**
	 * The text input I-beam cursor shape
	 */
	IBEAM(GLFW_IBEAM_CURSOR),
	/**
	 * The crosshair shape.
	 */
	CROSSHAIR(GLFW_CROSSHAIR_CURSOR),
	/**
	 * The hand shape.
	 */
	HAND(GLFW_HAND_CURSOR),
	/**
	 * The horizontal resize arrow shape.
	 */
	HRESIZE(GLFW_HRESIZE_CURSOR),
	/**
	 * The vertical resize arrow shape.
	 */
	VRESIZE(GLFW_VRESIZE_CURSOR);

	private final int _myID;

	private CCGLCursorShape(int theID) {
		_myID = theID;
	}

	public int id() {
		return _myID;
	}
}
