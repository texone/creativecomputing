package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetDropCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

public class CCGLWindow {
	
	private long _myID;
	private GLCapabilities _myCapabilities;
	public CCGraphics g;
	public final CCGLTimer t = new CCGLTimer();
	
	/**
	 * This function creates a window and its associated OpenGL or OpenGL ES
	 * context. Most of the options controlling how the window and its context
	 * should be created are specified with window hints.
	 * <p>
	 * Successful creation does not change which context is current. Before you
	 * can use the newly created context, you need to make it current. For
	 * information about the share parameter, see Context object sharing.
	 * <p>
	 * The created window, framebuffer and context may differ from what you
	 * requested, as not all parameters and hints are hard constraints. This
	 * includes the size of the window, especially for full screen windows. To
	 * query the actual attributes of the created window, framebuffer and
	 * context, see glfwGetWindowAttrib, glfwGetWindowSize and
	 * glfwGetFramebufferSize.
	 * <p>
	 * To create a full screen window, you need to specify the monitor the
	 * window will cover. If no monitor is specified, the window will be
	 * windowed mode. Unless you have a way for the user to choose a specific
	 * monitor, it is recommended that you pick the primary monitor. For more
	 * information on how to query connected monitors, see Retrieving monitors.
	 * <p>
	 * For full screen windows, the specified size becomes the resolution of the
	 * window's desired video mode. As long as a full screen window is not
	 * iconified, the supported video mode most closely matching the desired
	 * video mode is set for the specified monitor. For more information about
	 * full screen windows, including the creation of so called windowed full
	 * screen or borderless full screen windows, see "Windowed full screen"
	 * windows.
	 * <p>
	 * Once you have created the window, you can switch it between windowed and
	 * full screen mode with glfwSetWindowMonitor. If the window has an OpenGL
	 * or OpenGL ES context, it will be unaffected.
	 * <p>
	 * By default, newly created windows use the placement recommended by the
	 * window system. To create the window at a specific position, make it
	 * initially invisible using the GLFW_VISIBLE window hint, set its position
	 * and then show it.
	 * <p>
	 * As long as at least one full screen window is not iconified, the
	 * screensaver is prohibited from starting.
	 * <p>
	 * Window systems put limits on window sizes. Very large or very small
	 * window dimensions may be overridden by the window system on creation.
	 * Check the actual size after creation.
	 * <p>
	 * The swap interval is not set during window creation and the initial value
	 * may vary depending on driver settings and defaults.
	 * 
	 * @param theWidth The desired width, in screen coordinates, of the window. This must be greater than zero.
	 * @param theHeight The desired height, in screen coordinates, of the window. This must be greater than zero.
	 * @param theTitle The initial, UTF-8 encoded window title.
	 * @param theMonitor The monitor to use for full screen mode, or NULL for windowed mode.
	 * @param theWindow The window whose context to share resources with, or NULL to not share resources.
	 */
	public void init(int theWidth, int theHeight, String theTitle, CCGLFWMonitor theMonitor, CCGLWindow theWindow){
		_myID = glfwCreateWindow(
			theWidth, 
			theHeight, 
			theTitle, 
			theMonitor == null ? NULL : theMonitor.id(), 
			theWindow == null ? NULL : theWindow.id()
		);

		if (_myID == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		setWindowCallbacks();
		setMouseCallbacks();
		setKeyCallbacks();
		
	}
	
	protected CCGLWindow(){
	}
	
	public long id(){
		return _myID;
	}

	public final CCEventManager<?> setupEvents = new CCEventManager<>();
	public final CCEventManager<CCGLTimer> updateEvents = new CCEventManager<>();
	public final CCEventManager<CCGraphics> drawEvents = new CCEventManager<>();
	
	public boolean isPrepared(){
		return _myCapabilities != null;
	}
	
	public void prepareForDraw() {
		_myCapabilities = GL.createCapabilities();

		CCVector2i myFrameBufferSize = framebufferSize();
		
		g = new CCGraphics(myFrameBufferSize.x, myFrameBufferSize.y);
		frameBufferSizeEvents.add(size ->{
			g.resize(size.x, size.y);
			draw();
		});
		
		setupEvents.event(null);
	}
	
	public void draw(){
		if(_myCapabilities == null)return;
		GL.setCapabilities(_myCapabilities);
		t.calculateDeltaTime();
		updateEvents.event(t);
		g.beginDraw();
		drawEvents.event(g);
		g.endDraw();
		
        swapBuffers();
	}
	
	
	public final CCEventManager<CCVector2i> windowSizeEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2i> windowPosEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2i> frameBufferSizeEvents = new CCEventManager<>();
	public final CCEventManager<?> closeEvents = new CCEventManager<>();
	public final CCEventManager<Boolean> focusEvents = new CCEventManager<>();
	public final CCEventManager<Boolean> iconifyEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2i> positionEvents = new CCEventManager<>();
	public final CCEventManager<?> refreshEvents = new CCEventManager<>();
	public final CCEventManager<String[]> dropEvents = new CCEventManager<>();
	
	private void setWindowCallbacks(){
		glfwSetWindowSizeCallback(_myID, (window, width, height)->{
			windowSizeEvents.event(new CCVector2i(width, height));
		});
		glfwSetWindowPosCallback(_myID, (window, x, y)->{
			windowPosEvents.event(new CCVector2i(x, y));
		});
		glfwSetFramebufferSizeCallback(_myID, (window, width, height)->{
			frameBufferSizeEvents.event(new CCVector2i(width, height));
		});
		glfwSetWindowCloseCallback(_myID, (window)->{
			closeEvents.event();
		});
		glfwSetWindowFocusCallback(_myID, (window, focus)->{
			focusEvents.event(focus);
		});
		glfwSetWindowIconifyCallback(_myID, (window, iconify)->{
			iconifyEvents.event(iconify);
		});
		glfwSetWindowPosCallback(_myID, (window, x, y)->{
			positionEvents.event(new CCVector2i( x, y));
		});
		glfwSetWindowRefreshCallback(_myID, (window)->{
			refreshEvents.event();
		});
		glfwSetDropCallback(_myID, (long arg0, int count, long names)-> {
			String[] filenames = new String[count];
			for ( int i = 0; i < count; i++ ) {
				filenames[i] = GLFWDropCallback.getName(names, i);
		    }

			dropEvents.event(filenames);
		});
	}
	
	public final CCEventManager<CCGLKeyEvent> keyReleaseEvents = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyPressEvents = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyRepeatEvents = new CCEventManager<>();
	public final CCEventManager<Character> keyCharEvents = new CCEventManager<>();
	
	
	private void setKeyCallbacks(){
		glfwSetCharCallback(_myID, (window,theChar)->{
			keyCharEvents.event((char)theChar);
		});
		
	}
	
	/**
	 * This function sets the cursor boundary crossing callback of the specified
	 * window, which is called when the cursor enters or leaves the client area
	 * of the window.
	 */
	public final CCEventManager<Boolean> cursorEnterEvents = new CCEventManager<>();;
	
	/**
	 * This function sets the cursor position callback of the specified window,
	 * which is called when the cursor is moved. The callback is provided with
	 * the position, in screen coordinates, relative to the upper-left corner of
	 * the client area of the window.
	 */
	public final CCEventManager<CCVector2> cursorPositionEvents = new CCEventManager<>();;
	
	public final CCEventManager<CCGLMouseEvent> mouseReleaseEvents = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mousePressEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseEnterEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseExitEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseMoveEvents = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseDragEvents = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mouseClickEvents = new CCEventManager<>();

	public final CCEventManager<CCVector2> scrollEvents = new CCEventManager<>();

	private double _myMouseX;
	private double _myMouseY;
	private boolean _myMousePressed;
	
	private long _myPressMillis = 0;

	private long _myLastDif = 0;
	private int _myClicks = 1;
	
	private void setMouseCallbacks(){
		glfwSetCursorEnterCallback(_myID, (window, enter) -> {
			CCVector2 myCursorPosition = cursorPosition().multiplyLocal(
				framebufferSize().x / (double)windowSize().x, 
				framebufferSize().y / (double)windowSize().y
			);
			if(enter){
				mouseEnterEvents.event(myCursorPosition);
			}else{
				mouseExitEvents.event(myCursorPosition);
			}
			cursorEnterEvents.event(enter);
		});
		glfwSetCursorPosCallback(_myID, (window, x, y) -> {
			CCVector2 myCursorPosition = new CCVector2(
				x * framebufferSize().x / (double)windowSize().x, 
				y * framebufferSize().y / (double)windowSize().y
			);
			_myMouseX = myCursorPosition.x;
			_myMouseY = myCursorPosition.y;
			cursorPositionEvents.event(myCursorPosition);
			
			if(_myMousePressed){
				mouseDragEvents.event(myCursorPosition);
			}else{
				mouseMoveEvents.event(myCursorPosition);
			}
		});

		
		glfwSetMouseButtonCallback(_myID, (window, button, action, mods) -> {
			CCGLMouseEvent myMouseEvent = new CCGLMouseEvent(button, action, mods,_myMouseX, _myMouseY, position().x + (int)_myMouseX, position().y + (int)_myMouseY);
			switch(action){
			case GLFW_RELEASE:
				long myDif = System.currentTimeMillis() - _myPressMillis;

				if(_myLastDif < 200){
					_myClicks++;
					myMouseEvent.clickCount = _myClicks;
				}else{
					_myClicks = 1;
					myMouseEvent.clickCount = _myClicks;
				}
				mouseReleaseEvents.event(myMouseEvent);
				if(myDif < 200){
					mouseClickEvents.event(myMouseEvent);
				}

				_myMousePressed = false;
				break;
			case GLFW_PRESS:
				myDif = System.currentTimeMillis() - _myPressMillis;
				_myLastDif = myDif;
				_myPressMillis = System.currentTimeMillis();
				mousePressEvents.event(myMouseEvent);
				_myMousePressed = true;
				break;
			}
		});
		glfwSetScrollCallback(_myID, (w, x, y) -> {
			scrollEvents.event(new CCVector2(x, y));
		});
		glfwSetKeyCallback(_myID, (window, key, scancode, action, mods) -> {
			CCGLKeyEvent myKeyEvent = new CCGLKeyEvent(key, scancode, action, mods);
			switch(action){
			case GLFW_RELEASE:
				keyReleaseEvents.event(myKeyEvent);
				break;
			case GLFW_PRESS:
				keyPressEvents.event(myKeyEvent);
				break;
			case GLFW_REPEAT:
				keyRepeatEvents.event(myKeyEvent);
				break;
			}
		});
	}
	
	/**
	 * This function resets all window hints to their default values.
	 */
	public static void defaultWindowHints(){
		glfwDefaultWindowHints();
	}
	
	public void freeCallbacks(){
		glfwFreeCallbacks(_myID);
		
	}
	
	/**
	 * This function destroys the specified window and its context. On calling
	 * this function, no further callbacks will be called for that window.
	 * <p>
	 * If the context of the specified window is current on the main thread, it
	 * is detached before being destroyed.
	 */
	public void destroy() {
		glfwDestroyWindow(_myID);
	}

	/**
	 * This function brings the specified window to front and sets input focus.
	 * The window should already be visible and not iconified.
	 * <p>
	 * By default, both windowed and full screen mode windows are focused when
	 * initially created. Set the GLFW_FOCUSED to disable this behavior.
	 * <p>
	 * Do not use this function to steal focus from other applications unless
	 * you are certain that is what the user wants. Focus stealing can be
	 * extremely disruptive.
	 */
	public void focus() {
		glfwFocusWindow(_myID);
		
		
	}
	
	/**
	 * This function retrieves the size, in pixels, of the framebuffer of the
	 * specified window. If you wish to retrieve the size of the window in
	 * screen coordinates, see {@linkplain #windowSize()}.
	 * 
	 * @return
	 */
	public CCVector2i framebufferSize(){
		try (MemoryStack stack = stackPush()) {
			IntBuffer width = stack.mallocInt(1); // int*
			IntBuffer height = stack.mallocInt(1); // int*

			glfwGetFramebufferSize(_myID, width, height);
			return new CCVector2i(width.get(0), height.get(0));
		}
	}
	
	/**
	 * This function retrieves the size, in screen coordinates, of the client
	 * area of the specified window. If you wish to retrieve the size of the
	 * framebuffer of the window in pixels, {@linkplain #framebufferSize()}.
	 * 
	 * @return window size as 2i vector
	 */
	public CCVector2i windowSize() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer width = stack.mallocInt(1); // int*
			IntBuffer height = stack.mallocInt(1); // int*

			glfwGetWindowSize(_myID, width, height);
			return new CCVector2i(width.get(0), height.get(0));
		}
	}

	/**
	 * This function sets the size, in screen coordinates, of the client area of
	 * the specified window.
	 * <p>
	 * For full screen windows, this function updates the resolution of its
	 * desired video mode and switches to the video mode closest to it, without
	 * affecting the window's context. As the context is unaffected, the bit
	 * depths of the framebuffer remain unchanged.
	 * <p>
	 * If you wish to update the refresh rate of the desired video mode in
	 * addition to its resolution, see glfwSetWindowMonitor.
	 * <p>
	 * The window manager may put limits on what sizes are allowed. GLFW cannot
	 * and should not override these limits.
	 * 
	 * @param theWidth The desired width, in screen coordinates, of the window
	 *            client area.
	 * @param theHeight The desired height, in screen coordinates, of the window
	 *            client area.
	 */
	public void windowSize(int theWidth, int theHeight) {
		glfwSetWindowSize(_myID, theWidth, theHeight);
	}
	
	/**
	 * This function sets the position, in screen coordinates, of the upper-left
	 * corner of the client area of the specified windowed mode window. If the
	 * window is a full screen window, this function does nothing.
	 * <p>
	 * Do not use this function to move an already visible window unless you
	 * have very good reasons for doing so, as it will confuse and annoy the
	 * user.
	 * <p>
	 * The window manager may put limits on what positions are allowed. GLFW
	 * cannot and should not override these limits.
	 * 
	 * @param theX The x-coordinate of the upper-left corner of the client area.
	 * @param theY The y-coordinate of the upper-left corner of the client area.
	 */
	public void position(int theX, int theY){
		glfwSetWindowPos(_myID, theX, theY);
	}
	
	/**
	 * This function retrieves the position, in screen coordinates, of the upper-left corner of the client area of the specified window.
	 * 
	 * @return vector with x y position of the window
	 */
	public CCVector2i position(){
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1); // int*
			IntBuffer y = stack.mallocInt(1); // int*

			glfwGetWindowPos(_myID, x, y);
			return new CCVector2i(x.get(0), y.get(0));
		}
	} 

	/**
	 * This function hides the specified window if it was previously visible. If
	 * the window is already hidden or is in full screen mode, this function
	 * does nothing.
	 */
	public void hide() {
		glfwHideWindow(_myID);
	}

	/**
	 * This function makes the specified window visible if it was previously
	 * hidden. If the window is already visible or is in full screen mode, this
	 * function does nothing.
	 */
	public void show() {
		glfwShowWindow(_myID);
	}

	/**
	 * indicates whether the specified window is visible.
	 * @return
	 */
	public boolean isVisible(){
		return glfwGetWindowAttrib(_myID, GLFW_VISIBLE) == GLFW_TRUE;
	}

	/**
	 * This function iconifies (minimizes) the specified window if it was
	 * previously restored. If the window is already iconified, this function
	 * does nothing.
	 * <p>
	 * If the specified window is a full screen window, the original monitor
	 * resolution is restored until the window is restored.
	 */
	public void iconify() {
		glfwIconifyWindow(_myID);
	}
	
	/**
	 * indicates whether the specified window is iconified.
	 * @return
	 */
	public boolean isIconified(){
		return glfwGetWindowAttrib(_myID, GLFW_ICONIFIED) == GLFW_TRUE;
	}

	/**
	 * This function maximizes the specified window if it was previously not
	 * maximized. If the window is already maximized, this function does
	 * nothing.
	 * <p>
	 * If the specified window is a full screen window, this function does
	 * nothing.
	 */
	public void maximize() {
		glfwMaximizeWindow(_myID);
	}

	/**
	 * indicates whether the specified window is maximized
	 * @return
	 */
	public boolean isMaximized(){
		return glfwGetWindowAttrib(_myID, GLFW_MAXIMIZED) == GLFW_TRUE;
	}
	
	/**
	 * This function returns the value of the close flag of the specified window.
	 * @return The value of the close flag.
	 */
	public boolean shouldClose(){
		return glfwWindowShouldClose(_myID);
	}
	
	public void shouldClose(boolean theShouldClose){
		glfwSetWindowShouldClose(_myID,theShouldClose);
	}
	
	/**
	 * The {@linkplain CCGLCursorMode} input mode provides several cursor modes
	 * for special forms of mouse motion input. By default, the cursor mode is
	 * {@linkplain CCGLCursorMode#NORMAL}, meaning the regular arrow cursor (or another cursor
	 * set with glfwSetCursor) is used and cursor motion is not limited.
	 * <p>
	 * If you wish to implement mouse motion based camera controls or other
	 * input schemes that require unlimited mouse movement, set the cursor mode
	 * to {@linkplain CCGLCursorMode#DISABLED}.
	 * <p>
	 * {@linkplain CCGLCursorMode#DISABLED} This will
	 * hide the cursor and lock it to the specified window. GLFW will then take
	 * care of all the details of cursor re-centering and offset calculation and
	 * providing the application with a virtual cursor position. This virtual
	 * position is provided normally via both the cursor position callback and
	 * through polling.
	 * <p>
	 * Note You should not implement your own version of this functionality
	 * using other features of GLFW. It is not supported and will not work as
	 * robustly as {@linkplain CCGLCursorMode#DISABLED}. If you just wish the cursor to become
	 * hidden when it is over a window, set the cursor mode to
	 * {@linkplain CCGLCursorMode#HIDDEN}.This mode puts
	 * no limit on the motion of the cursor.
	 * <p>
	 * To exit out of either of these special modes, restore the
	 * {@linkplain CCGLCursorMode#NORMAL} cursor mode.
	 * 
	 * @param theMode
	 */
	public void cursorMode(CCGLCursorMode theMode) {
		glfwSetInputMode(_myID, GLFW_CURSOR, theMode.id());
	}
	
	/**
	 * This function sets the cursor image to be used when the cursor is over
	 * the client area of the specified window. The set cursor will only be
	 * visible when the cursor mode of the window is {@linkplain CCGLCursorMode#NORMAL}.
	 * <p>
	 * On some platforms, the set cursor may not be visible unless the window
	 * also has input focus.
	 * 
	 * @param theCursor The cursor to set, or NULL to switch back to the default
	 *            arrow cursor.
	 */
	public void cursor(CCGLCursorShape theCursor) {
		long myCursor = theCursor == null ? NULL : glfwCreateStandardCursor(theCursor.id());
		glfwSetCursor(_myID, myCursor);
	}
	
	public void cursor(){
		GLFWImage myCursorImage = GLFWImage.create();
	}

	/**
	 * This function sets the position, in screen coordinates, of the cursor
	 * relative to the upper-left corner of the client area of the specified
	 * window. The window must have input focus. If the window does not have
	 * input focus when this function is called, it fails silently.
	 * <p>
	 * Do not use this function to implement things like camera controls. GLFW
	 * already provides the {@linkplain CCGLCursorMode#DISABLED} cursor mode that hides the
	 * cursor, transparently re-centers it and provides unconstrained cursor
	 * motion. See glfwSetInputMode for more information.
	 * <p>
	 * If the cursor mode is {@linkplain CCGLCursorMode#DISABLED} then the cursor position is
	 * unconstrained and limited only by the minimum and maximum values of a
	 * double.
	 * 
	 * @param theX The desired x-coordinate, relative to the left edge of the client area.
	 * @param theY The desired y-coordinate, relative to the top edge of the client area.
	 */
	public void cursorPosition(double theX, double theY) {
		glfwSetCursorPos(_myID, theX, theY);
	}

	/**
	 * This function returns the position of the cursor, in screen coordinates,
	 * relative to the upper-left corner of the client area of the specified
	 * window.
	 * <P>
	 * If the cursor is disabled (with {@linkplain CCGLCursorMode#DISABLED}) then the cursor
	 * position is unbounded and limited only by the minimum and maximum values
	 * of a double.
	 * <P>
	 * The coordinate can be converted to their integer equivalents with the
	 * floor function. Casting directly to an integer type works for positive
	 * coordinates, but fails for negative ones.
	 * 
	 * @return
	 */
	public CCVector2 cursorPosition() {
		try (MemoryStack stack = stackPush()) {
			DoubleBuffer myX = stack.mallocDouble(1);
			DoubleBuffer myY = stack.mallocDouble(1);
			glfwGetCursorPos(_myID, myX, myY);
			return new CCVector2(myX.get(0), myY.get(0));
		}
	}
	
	/**
	 * This function swaps the front and back buffers of the specified window
	 * when rendering with OpenGL or OpenGL ES. If the swap interval is greater
	 * than zero, the GPU driver waits the specified number of screen updates
	 * before swapping the buffers.
	 * <p>
	 * The specified window must have an OpenGL or OpenGL ES context. Specifying
	 * a window without a context will generate a GLFW_NO_WINDOW_CONTEXT error.
	 */
	public void swapBuffers() {
		glfwSwapBuffers(_myID);
	}
	
	/**
	 * This function returns the contents of the system clipboard, if it contains or is convertible to a UTF-8 encoded string.
	 * @return The contents of the clipboard as a UTF-8 encoded string, or NULL if an error occurred.
	 */
	public String clipboardString(){
		return glfwGetClipboardString(_myID);
	}
	
	/**
	 * This function sets the system clipboard to the specified, UTF-8 encoded string. The string is copied before returning, so you don't have to retain it afterwards.
	 * @param theString A UTF-8 encoded string.
	 */
	public void clipboardString(String theString){
		glfwSetClipboardString(_myID, theString);
	}

	/**
	 * This function sets the window title, encoded as UTF-8, of the specified window.
	 * @param caption The UTF-8 encoded window title.
	 */
	public void title(String caption) {
		glfwSetWindowTitle(_myID, caption);
	}

	/**
	 * indicates whether the specified window has input focus.
	 * @return
	 */
	public boolean isFocused(){
		return glfwGetWindowAttrib(_myID, GLFW_FOCUSED) == GLFW_TRUE;
	}



	/**
	 * indicates whether the specified window is resizable by the user. 
	 * @return
	 */
	public boolean isResizable(){
		return glfwGetWindowAttrib(_myID, GLFW_RESIZABLE) == GLFW_TRUE;
	}

	/**
	 * indicates whether the specified window has decorations such as a border, a close widget, etc.
	 * @return
	 */
	public boolean isDecorated(){
		return glfwGetWindowAttrib(_myID, GLFW_DECORATED) == GLFW_TRUE;
	}

	/**
	 * indicates whether the specified window is floating, also called topmost or always-on-top.
	 * @return
	 */
	public boolean isFloating(){
		return glfwGetWindowAttrib(_myID, GLFW_FLOATING) == GLFW_TRUE;
	}
	
	
}
