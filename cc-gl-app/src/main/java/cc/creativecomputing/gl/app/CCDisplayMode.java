package cc.creativecomputing.gl.app;


/**
 * Display modes of the application, there are 4 different display modes, ranging from
 * update only to full screen
 */
public enum CCDisplayMode{
	/**
	 * Use this mode to run an application without creating an opengl context.
	 * This is useful if you have an application that has debug visuals to setup
	 * but can than run without a visual as plain window less application
	 */
	UPDATE_ONLY,
	/**
	 * Use this mode to run an application with off screen rendering, this will
	 * allow you to create an application to export renderings or just use opengl
	 * for computation
	 */
	OFFSCREEN,
	/**
	 * This is the default mode. Here an opengl context is created on base of 
	 * window. The implementation of the window is dependent of the container. You can 
	 * also set the size and location of the window using the according methods
	 * @see CCApplicationSettings#container(CCGLContainer)
	 * @see CCApplicationSettings#size(int, int)
	 * @see CCApplicationSettings#location(int, int)
	 */
	WINDOW
}