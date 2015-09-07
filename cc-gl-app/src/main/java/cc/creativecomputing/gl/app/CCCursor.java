package cc.creativecomputing.gl.app;

import java.awt.Cursor;

/**
 * Simply maps the java awt cursor types for better convenience
 */
public enum CCCursor {
	/**
	 * The default cursor type (gets set if no cursor is defined).
	 */
	DEFAULT_CURSOR(Cursor.DEFAULT_CURSOR),

	/**
	 * The crosshair cursor type.
	 */
	CROSSHAIR_CURSOR(Cursor.CROSSHAIR_CURSOR),

	/**
	 * The text cursor type.
	 */
	TEXT_CURSOR(Cursor.TEXT_CURSOR),

	/**
	 * The wait cursor type.
	 */
	WAIT_CURSOR(Cursor.WAIT_CURSOR),

	/**
	 * The south-west-resize cursor type.
	 */
	SW_RESIZE_CURSOR(Cursor.SW_RESIZE_CURSOR),

	/**
	 * The south-east-resize cursor type.
	 */
	SE_RESIZE_CURSOR(Cursor.SE_RESIZE_CURSOR),

	/**
	 * The north-west-resize cursor type.
	 */
	NW_RESIZE_CURSOR(Cursor.NW_RESIZE_CURSOR),

	/**
	 * The north-east-resize cursor type.
	 */
	NE_RESIZE_CURSOR(Cursor.NE_RESIZE_CURSOR),

	/**
	 * The north-resize cursor type.
	 */
	N_RESIZE_CURSOR(Cursor.N_RESIZE_CURSOR),

	/**
	 * The south-resize cursor type.
	 */
	S_RESIZE_CURSOR(Cursor.S_RESIZE_CURSOR),

	/**
	 * The west-resize cursor type.
	 */
	W_RESIZE_CURSOR(Cursor.W_RESIZE_CURSOR),

	/**
	 * The east-resize cursor type.
	 */
	E_RESIZE_CURSOR(Cursor.E_RESIZE_CURSOR),

	/**
	 * The hand cursor type.
	 */
	HAND_CURSOR(Cursor.HAND_CURSOR),

	/**
	 * The move cursor type.
	 */
	MOVE_CURSOR(Cursor.DEFAULT_CURSOR);

	private Cursor _myJavaCursor;

	private CCCursor(final int theJavaID) {
		_myJavaCursor = new Cursor(theJavaID);
	}

	public Cursor javaCursor() {
		return _myJavaCursor;
	}
}