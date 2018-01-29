package cc.creativecomputing.uinano;

import java.nio.IntBuffer;
import java.util.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.nanovg.NanoVG.*;

import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCOS;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLCursorShape;
import cc.creativecomputing.gl.app.CCGLFWMonitor;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.gl.app.CCGLWindow.CCGLFWRefreshListener;
import cc.creativecomputing.gl.app.CCGLWindow.CCGLFWSizeListener;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

import static org.lwjgl.glfw.GLFW.*;

/**
 * \class Screen screen.h nanogui/screen.h
 *
 * \brief Represents a display surface (i.e. a full-screen or windowed GLFW
 * window) and forms the root element of a hierarchy of nanogui widgets.
 */
public class Screen extends CCWidget {
	

//	/**
//	 * Create a new Screen instance
//	 *
//	 * \param size Size in pixels at 96 dpi (on high-DPI screens, the actual
//	 * resolution in terms of hardware pixels may be larger by an integer
//	 * factor)
//	 *
//	 * \param caption Window title (in UTF-8 encoding)
//	 *
//	 * \param resizable If creating a window, should it be resizable?
//	 *
//	 * \param fullscreen Specifies whether to create a windowed or full-screen
//	 * view
//	 *
//	 * \param colorBits Number of bits per pixel dedicated to the R/G/B color
//	 * components
//	 *
//	 * \param alphaBits Number of bits per pixel dedicated to the alpha channel
//	 *
//	 * \param depthBits Number of bits per pixel dedicated to the Z-buffer
//	 *
//	 * \param stencilBits Number of bits per pixel dedicated to the stencil
//	 * buffer (recommended to set this to 8. NanoVG can draw higher-quality
//	 * strokes using a stencil buffer)
//	 *
//	 * \param nSamples Number of MSAA samples (set to 0 to disable)
//	 *
//	 * \param glMajor The requested OpenGL Major version number. Default is 3,
//	 * if changed the value must correspond to a forward compatible core profile
//	 * (for portability reasons). For example, set this to 4 and \ref glMinor to
//	 * 1 for a forward compatible core OpenGL 4.1 profile. Requesting an invalid
//	 * profile will result in no context (and therefore no GUI) being created.
//	 *
//	 * \param glMinor The requested OpenGL Minor version number. Default is 3,
//	 * if changed the value must correspond to a forward compatible core profile
//	 * (for portability reasons). For example, set this to 1 and \ref glMajor to
//	 * 4 for a forward compatible core OpenGL 4.1 profile. Requesting an invalid
//	 * profile will result in no context (and therefore no GUI) being created.
//	 */
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits, int alphaBits,
//			int depthBits, int stencilBits, int nSamples, int glMajor) {
//		this(size, caption, resizable, fullscreen, colorBits, alphaBits, depthBits, stencilBits, nSamples, glMajor, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits, int alphaBits,
//			int depthBits, int stencilBits, int nSamples) {
//		this(size, caption, resizable, fullscreen, colorBits, alphaBits, depthBits, stencilBits, nSamples, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits, int alphaBits,
//			int depthBits, int stencilBits) {
//		this(size, caption, resizable, fullscreen, colorBits, alphaBits, depthBits, stencilBits, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits, int alphaBits,
//			int depthBits) {
//		this(size, caption, resizable, fullscreen, colorBits, alphaBits, depthBits, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits,
//			int alphaBits) {
//		this(size, caption, resizable, fullscreen, colorBits, alphaBits, 24, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits) {
//		this(size, caption, resizable, fullscreen, colorBits, 8, 24, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen) {
//		this(size, caption, resizable, fullscreen, 8, 8, 24, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable) {
//		this(size, caption, resizable, false, 8, 8, 24, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption) {
//		this(size, caption, true, false, 8, 8, 24, 8, 0, 3, 3);
//	}
//
//	public Screen(CCVector2i size, String caption, boolean resizable, boolean fullscreen, int colorBits, int alphaBits,
//			int depthBits, int stencilBits, int nSamples, int glMajor, int glMinor) {
//		super(null);
//		this.mGLFWWindow = null;
//		this.nvg = null;
//		this.mCursor = CCGLCursorShape.ARROW;
//		this.mBackground = new CCColor(0.3f, 0.3f, 0.32f, 1.0f);
//		this.mCaption = caption;
//		this.mShutdownGLFWOnDestruct = false;
//		this.mFullscreen = fullscreen;
//
//		/*
//		 * Request a forward compatible OpenGL glMajor.glMinor core profile
//		 * context. Default value is an OpenGL 3.3 core profile context.
//		 */
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, glMajor);
//		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, glMinor);
//		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
//		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//
//		glfwWindowHint(GLFW_SAMPLES, nSamples);
//		glfwWindowHint(GLFW_RED_BITS, colorBits);
//		glfwWindowHint(GLFW_GREEN_BITS, colorBits);
//		glfwWindowHint(GLFW_BLUE_BITS, colorBits);
//		glfwWindowHint(GLFW_ALPHA_BITS, alphaBits);
//		glfwWindowHint(GLFW_STENCIL_BITS, stencilBits);
//		glfwWindowHint(GLFW_DEPTH_BITS, depthBits);
//		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
//		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
//
//		if (fullscreen) {
//			long monitor = glfwGetPrimaryMonitor();
//			GLFWVidMode mode = glfwGetVideoMode(monitor);
//			mGLFWWindow = new CCGLWindow(mode.width(), mode.height(), caption, new CCGLFWMonitor(monitor), null);
//		} else {
//			mGLFWWindow = new CCGLWindow(size.x, size.y, caption, null, null);
//		}
//
//		glfwMakeContextCurrent(mGLFWWindow.id());
//
//		CCVector2i mFBSize = mGLFWWindow.framebufferSize();
//		GL11.glViewport(0, 0, mFBSize.x, mFBSize.y);
//		GL11.glClearColor((float) mBackground.r, (float) mBackground.g, (float) mBackground.b, (float) mBackground.a);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
//		glfwSwapInterval(0);
//		mGLFWWindow.swapBuffers();
//
//		glfwPollEvents();
//
//		/* Propagate GLFW events to the appropriate Screen instance */
//		mGLFWWindow.cursorPositionEvents.add((x, y) -> {
//			cursorPosCallbackEvent(x, y);
//		});
//		mGLFWWindow.mousePressEvents.add(event -> {
//			mouseButtonCallbackEvent(event);
//		});
//		mGLFWWindow.mouseReleaseEvents.add(event -> {
//			mouseButtonCallbackEvent(event);
//		});
//		mGLFWWindow.keyPressEvents.add(event -> {
//			keyCallbackEvent(event);
//		});
//		mGLFWWindow.keyReleaseEvents.add(event -> {
//			keyCallbackEvent(event);
//		});
//		mGLFWWindow.keyRepeatEvents.add(event -> {
//			keyCallbackEvent(event);
//		});
//		mGLFWWindow.keyCharEvents.add(codepoint -> {
//			charCallbackEvent(codepoint);
//		});
//		mGLFWWindow.dropEvents.add(filenames -> {
//			dropCallbackEvent(filenames);
//		});
//		mGLFWWindow.scrollEvents.add((x, y) -> {
//			scrollCallbackEvent(x, y);
//		});
//
//		/*
//		 * React to framebuffer size events -- includes window size events and
//		 * also catches things like dragging a window from a Retina-capable
//		 * screen to a normal screen on Mac OS X
//		 */
//		mGLFWWindow.frameBufferSizeEvents.add((window, width, height) -> {
//			resizeCallbackEvent(width, height);
//		});
//
//		initialize(mGLFWWindow, true);
//	}
//
//	/// Release all resources
//	public void close() {
//		if (mGLFWWindow != null && mShutdownGLFWOnDestruct) {
//			mGLFWWindow.destroy();
//		}
//	}
//
//	/// Get the window title bar caption
//	public final String caption() {
//		return mCaption;
//	}
//
//	/// Set the window title bar caption
//	public final void setCaption(String caption) {
//		if (!caption.equals(mCaption)) {
//			mGLFWWindow.title(caption);
//			mCaption = caption;
//		}
//	}
//
//	/// Return the screen's background color
//	public final CCColor background() {
//		return mBackground;
//	}
//
//	/// Set the screen's background color
//	public final void setBackground(CCColor background) {
//		mBackground = background;
//	}
//
//	/// Set the top-level window visibility (no effect on full-screen windows)
//	public final void setVisible(boolean visible) {
//		if (_myIsVisible == visible)
//			return;
//
//		_myIsVisible = visible;
//
//		if (visible) {
//			mGLFWWindow.show();
//		} else {
//			mGLFWWindow.hide();
//		}
//
//	}
//
//	/// Set window size
//	public final void setSize(CCVector2i size) {
//		super.size(size);
//
//		if (CCSystem.os == CCOS.WINDOWS || CCSystem.os == CCOS.LINUX) {
//			mGLFWWindow.windowSize((int) (size.x * mPixelRatio), (int) (size.y * mPixelRatio));
//		} else {
//			mGLFWWindow.windowSize(size.x, size.y);
//		}
//	}
//
//	/// Draw the Screen contents
//	public void drawAll() {
//		GL11.glClearColor((float) mBackground.r, (float) mBackground.g, (float) mBackground.b, (float) mBackground.a);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
//
//		drawContents();
//		drawWidgets();
//
//		mGLFWWindow.swapBuffers();
//	}
//
//	/// Draw the window contents --- put your OpenGL draw calls here
//	public void drawContents() {
//	}
//
//	/// Return the ratio between pixel and device coordinates (e.g. >= 2 on Mac
//	/// Retina displays)
//	public final float pixelRatio() {
//		return mPixelRatio;
//	}
//
//	/// Handle a file drop event
//	public boolean dropEvent(ArrayList<String> UnnamedParameter1) {
//		return false;
//	}
//
//	/// Default keyboard event handler
//	public boolean keyboardEvent(CCGLKeyEvent theEvent) {
//		if (mFocusPath.size() <= 0)
//			return false;
//
//		for (CCWidget it : mFocusPath) {
//			if (it.focused() && it.keyboardEvent(theEvent)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	/// Text input event handler: codepoint is native endian UTF-32 format
//	public boolean keyboardCharacterEvent(int codepoint) {
//		if (mFocusPath.size() > 0) {
//			for (CCWidget it : mFocusPath) {
//				if (it.focused() && it.keyboardCharacterEvent(codepoint)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/// Window resize event handler
//	public boolean resizeEvent(CCVector2i size) {
//		mResizeCallback.proxy().size(size.x, size.y);
//		return true;
//	}
//
//	/// Return the last observed mouse position value
//	public final CCVector2i mousePos() {
//		return mMousePos;
//	}
//
//	/// Return a pointer to the underlying GLFW window data structure
//	public final CCGLWindow glfwWindow() {
//		return mGLFWWindow;
//	}
//
//	/// Return a pointer to the underlying nanoVG draw context
//	public final NanoVG nvgContext() {
//		return nvg;
//	}
//
//	public final void setShutdownGLFWOnDestruct(boolean v) {
//		mShutdownGLFWOnDestruct = v;
//	}
//
//	public final boolean shutdownGLFWOnDestruct() {
//		return mShutdownGLFWOnDestruct;
//	}
//
//	/// Compute the layout of all widgets
//	public final void performLayout() {
//		super.performLayout(nvg);
//	}

	/********* API for applications which manage GLFW themselves *********/
	
	protected CCGLWindow _myWindow;
	protected CCGraphics _myGraphics;
	protected CCGLCursorShape _myCursor = CCGLCursorShape.ARROW;
	protected ArrayList<CCWidget> _myFocusPath = new ArrayList<>();
	protected CCVector2i _myFrameBufferSize = new CCVector2i();
	protected float _myPixelRatio;
	protected CCGLMouseEvent _myMouseEvent;
	protected CCVector2 _mMousePosition = new CCVector2();
	protected boolean _myDragActive;
	protected CCWidget _myDragWidget = null;
	protected double _myLastInteraction;
	protected boolean _myProcessEvents;
	protected CCColor _myBackground = new CCColor();
	protected String mCaption;
	protected boolean _myShutdownOnDestruct;
	protected boolean _myFullscreen;

	public static interface CCSizeListener {
		public void size(int theWidth, int theHeight);
	}

	protected CCListenerManager<CCSizeListener> mResizeCallback = CCListenerManager.create(CCSizeListener.class);

	/**
	 * \brief Default constructor
	 *
	 * Performs no initialization at all. Use this if the application is
	 * responsible for setting up GLFW, OpenGL, etc.
	 *
	 * In this case, override \ref Screen and call \ref initalize() with a
	 * pointer to an existing \c GLFWwindow instance
	 *
	 * You will also be responsible in this case to deliver GLFW callbacks to
	 * the appropriate callback event handlers below
	 */
	public Screen() {
		super(null);
		_myWindow = null;
		_myGraphics = null;
		_myCursor = CCGLCursorShape.ARROW;
		_myBackground = new CCColor(0.3f, 0.3f, 0.32f, 1.0f);
		_myShutdownOnDestruct = false;
		_myFullscreen = false;
	}

	/// Initialize the \ref Screen
	public final void initialize(CCGLWindow window, boolean shutdownGLFWOnDestruct) {
		_myWindow = window;
		_myShutdownOnDestruct = shutdownGLFWOnDestruct;
		_mySize = new CCVector2(_myWindow.windowSize());
		_myFrameBufferSize = _myWindow.framebufferSize();

		// mPixelRatio = nanogui.GlobalMembers.get_pixel_ratio(window);
		//
		// // C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		// /// #if _WIN32 || __linux__
		// if (mPixelRatio != 1 && !mFullscreen) {
		// glfwSetWindowSize(window, mSize.x * mPixelRatio, mSize.y *
		// mPixelRatio);
		// }
		// /// #endif

		/* Detect framebuffer properties and set up compatible NanoVG context */

		int nStencilBits = 0, nSamples = 0;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer stencilBits = stack.mallocInt(1);
			IntBuffer samples = stack.mallocInt(1);
			GL30.glGetFramebufferAttachmentParameteriv(GL30.GL_DRAW_FRAMEBUFFER, GL11.GL_STENCIL, GL30.GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE, stencilBits);
			GL11.glGetIntegerv(GL13.GL_SAMPLES, samples);

		}
		
		_myWindow.setupEvents.add((g, t) ->{
			_myGraphics = g;
			_myLastInteraction = t.time();
		});
		
		_myWindow.mouseMoveEvents.add(mousePos -> {
			if (!_myProcessEvents) {
				return;
			}
			_myLastInteraction = glfwGetTime();
			CCWidget widget = findWidget(mousePos);
			if (widget != null && widget.cursor() != _myCursor) {
				_myCursor = widget.cursor();
				_myWindow.cursor(_myCursor);
			}
			mouseMotionEvent(
				mousePos, 
				mousePos.subtract(_mMousePosition), 
				_myMouseEvent
			);
			_mMousePosition = mousePos;
		});
		
		_myWindow.mouseDragEvents.add(mousePos -> {
			if (!_myProcessEvents) {
				return;
			}
			_myLastInteraction = glfwGetTime();
			_myDragWidget.mouseDragEvent(
				mousePos.subtract(_myDragWidget.parent().absolutePosition()), 
				mousePos.subtract(_mMousePosition), 
				_myMouseEvent
			);
			_mMousePosition = mousePos;
		});
		
		_myWindow.mousePressEvents.add(this::mouseButtonCallbackEvent);
		_myWindow.mouseReleaseEvents.add(this::mouseButtonCallbackEvent);
		_myWindow.keyPressEvents.add(this::keyboardEvent);
		_myWindow.keyRepeatEvents.add(this::keyboardEvent);
		_myWindow.keyReleaseEvents.add(this::keyboardEvent);
		_myWindow.keyCharEvents.add(this::charCallbackEvent);
		_myWindow.dropEvents.add(this::dropCallbackEvent);
		_myWindow.scrollEvents.add(this::scrollCallbackEvent);
		
		setTheme(new Theme());
		
		_myWindow.drawEvents.add(this::drawWidgets);

		_myIsVisible = window.isVisible();
		_mMousePosition = new CCVector2();
		_myMouseEvent = null;
		_myDragActive = false;
		_myProcessEvents = true;
	}

	
	private boolean mouseButtonCallbackEvent(CCGLMouseEvent theEvent) {
		if (!_myProcessEvents) {
			return false;
		}
		_myMouseEvent = theEvent;
		_myLastInteraction = glfwGetTime();
		try {
			if (_myFocusPath.size() > 1) {
				Window window = (Window) ((_myFocusPath.get(_myFocusPath.size() - 2) instanceof Window) ? _myFocusPath.get(_myFocusPath.size() - 2) : null);
				if (window != null && window.modal()) {
					if (!window.contains(_mMousePosition)) {
						return false;
					}
				}
			}

			CCWidget dropWidget = findWidget(_mMousePosition);
			if (_myDragActive && theEvent.action == CCGLAction.RELEASE && dropWidget != _myDragWidget) {
				_myDragWidget.mouseButtonEvent(_mMousePosition.subtract(_myDragWidget.parent().absolutePosition()), theEvent);
			}

			if (dropWidget != null && dropWidget.cursor() != _myCursor) {
				_myCursor = dropWidget.cursor();
				_myWindow.cursor(_myCursor);
			}

			if (theEvent.action == CCGLAction.PRESS && (theEvent.button == CCGLMouseButton.BUTTON_1 || theEvent.button == CCGLMouseButton.BUTTON_2)) {
				_myDragWidget = findWidget(_mMousePosition);
				if (_myDragWidget == this) {
					_myDragWidget = null;
				}
				_myDragActive = _myDragWidget != null;
				if (!_myDragActive) {
					updateFocus(null);
				}
			} else {
				_myDragActive = false;
				_myDragWidget = null;
			}

			return mouseButtonEvent(_mMousePosition, theEvent);
		} catch (RuntimeException e) {
			CCLog.error("Caught exception in event handler: ", e);
			return false;
		}
	}

	private boolean keyCallbackEvent(CCGLKeyEvent theEvent) {
		if (!_myProcessEvents) {
			return false;
		}
		_myLastInteraction = glfwGetTime();
		try {
			return keyboardEvent(theEvent);
		} catch (RuntimeException e) {
			CCLog.error("Caught exception in event handler: ", e);
			return false;
		}
	}

	private boolean charCallbackEvent(int codepoint) {
		if (!_myProcessEvents) {
			return false;
		}
		_myLastInteraction = glfwGetTime();
		try {
			return keyboardCharacterEvent(codepoint);
		} catch (RuntimeException e) {
			CCLog.error("Caught exception in event handler: ", e);
			return false;
		}
	}

	public final boolean dropCallbackEvent(String[] filenames) {
		if (!_myProcessEvents) {
			return false;
		}
		ArrayList<String> arg = new ArrayList<>();

		for (String filename : filenames) {
			arg.add(filename);
		}
		return true;
//		return dropEvent(arg);
	}

	public final boolean scrollCallbackEvent(double x, double y) {
		if (!_myProcessEvents) {
			return false;
		}
		_myLastInteraction = glfwGetTime();
		try {
			if (_myFocusPath.size() > 1) {
				Window window = (Window) ((_myFocusPath.get(_myFocusPath.size() - 2) instanceof Window) ? _myFocusPath.get(_myFocusPath.size() - 2) : null);
				if (window != null && window.modal()) {
					if (!window.contains(_mMousePosition)) {
						return false;
					}
				}
			}
			return scrollEvent(_mMousePosition, new CCVector2(x, y));
		} catch (RuntimeException e) {
			CCLog.error("Caught exception in event handler: ", e);
			return false;
		}
	}

	public final boolean resizeCallbackEvent(int width, int height) {
		if (!_myProcessEvents) {
			return false;
		}

		CCVector2i fbSize = new CCVector2i();
		CCVector2i size = new CCVector2i();
		fbSize = _myWindow.framebufferSize();
		size = _myWindow.windowSize();

		// // C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		// /// #if _WIN32 || __linux__
		// size = (size.<Float> cast() / mPixelRatio).<Integer> cast();
		// /// #endif

		if (_myFrameBufferSize.isZero() || size.isZero()) {
			return false;
		}

		_myFrameBufferSize = fbSize;
		_mySize.set(size.x, size.y);
		_myLastInteraction = glfwGetTime();

//		try {
//			return resizeEvent(_mySize);
//		} catch (RuntimeException e) {
//			CCLog.error("Caught exception in event handler: ", e);
//			return false;
//		}
		return true;
	}

	/* Internal helper functions */
	public final void updateFocus(CCWidget widget) {
		for (CCWidget myWidget : _myFocusPath) {
			if (!myWidget.focused()) {
				continue;
			}
			myWidget.focusEvent(false);
		}
		_myFocusPath.clear();
		CCWidget window = null;
		while (widget != null) {
			_myFocusPath.add(widget);
			if ((Window) ((widget instanceof Window) ? widget : null) != null) {
				window = widget;
			}
			widget = widget.parent();
		}
		
		for (CCWidget myWidget : _myFocusPath) {
			myWidget.focusEvent(true);
		}

		if (window != null) {
			moveWindowToFront((Window) window);
		}
	}

	public final void disposeWindow(Window window) {
		if (_myFocusPath.contains(window)) {
			_myFocusPath.clear();
		}
		if (_myDragWidget == window) {
			_myDragWidget = null;
		}
		removeChild(window);
	}

	public final void centerWindow(Window window) {
		if (window.size().isZero()) {
			window.size(window.preferredSize(_myGraphics));
			window.performLayout(_myGraphics);
		}
		window.position(new CCVector2((_mySize.x - window.size().x) / 2, (_mySize.y - window.size().y) / 2));
	}

	public final void moveWindowToFront(Window window) {
		_myChildren.remove(window);
		_myChildren.add(window);
		/* Brute force topological sort (no problem for a few windows..) */
		boolean changed = false;
		do {
			int baseIndex = 0;
			for (int index = 0; index < _myChildren.size(); ++index) {
				if (_myChildren.get(index) == window) {
					baseIndex = index;
				}
			}
			changed = false;
			for (int index = 0; index < _myChildren.size(); ++index) {
				Popup pw = (Popup) ((_myChildren.get(index) instanceof Popup) ? _myChildren.get(index) : null);
				if (pw != null && pw.parentWindow() == window && index < baseIndex) {
					moveWindowToFront(pw);
					changed = true;
					break;
				}
			}
		} while (changed);
	}

	public void drawWidgets(CCGraphics g) {
		if (!_myIsVisible) {
			return;
		}

		glfwMakeContextCurrent(_myWindow.id());

		_myFrameBufferSize = _myWindow.framebufferSize();
		_mySize = new CCVector2(_myWindow.windowSize());

		// C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		/// #if _WIN32 || __linux__
		// mSize = (mSize.<Float> cast() / mPixelRatio).<Integer> cast();
		// mFBSize = (mSize.<Float> cast() * mPixelRatio).<Integer> cast();
		/// #else
		/* Recompute pixel ratio on OSX */
		// if (mSize[0]) {
		_myPixelRatio = (float) _myFrameBufferSize.x / (float) _mySize.y;
		// }
		/// #endif

//		GL11.glViewport(0, 0, _myFrameBufferSize.x, _myFrameBufferSize.y);
//		GL33.glBindSampler(0, 0);
//		_myGraphics.beginFrame(_mySize.x, _mySize.y, _myPixelRatio);

		draw(_myGraphics);

		double elapsed = glfwGetTime() - _myLastInteraction;

//		if (elapsed > 0.5f) {
//			/* Draw tooltips */
//			CCWidget widget = findWidget(_mMousePosition);
//			if (widget != null && !widget.tooltip().isEmpty()) {
//				int tooltipWidth = 150;
//
//				float[] bounds = new float[4];
//				_myGraphics.fontFace("sans");
//				_myGraphics.fontSize(15.0f);
//				_myGraphics.textAlign(NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
//				_myGraphics.textLineHeight(1.1f);
//				CCVector2 pos = widget.absolutePosition().add(new CCVector2(widget.width() / 2, widget.height() + 10));
//
//				_myGraphics.textBounds(pos.x, pos.y, widget.tooltip(), bounds);
//				int h = (int) (bounds[2] - bounds[0]) / 2;
//				if (h > tooltipWidth / 2) {
//					_myGraphics.textAlign(NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
//					bounds = _myGraphics.textBoxBounds(pos.x, pos.y, tooltipWidth, widget.tooltip());
//
//					h = (int) (bounds[2] - bounds[0]) / 2;
//				}
//				_myGraphics.globalAlpha(CCMath.min(1.0, 2 * (elapsed - 0.5f)) * 0.8);
//
//				_myGraphics.beginPath();
//				_myGraphics.color(new CCColor(0, 255));
//				_myGraphics.roundedRect(bounds[0] - 4 - h, bounds[1] - 4, (int) (bounds[2] - bounds[0]) + 8,
//						(int) (bounds[3] - bounds[1]) + 8, 3);
//
//				int px = (int) ((bounds[2] + bounds[0]) / 2) - h;
//				_myGraphics.moveTo(px, bounds[1] - 10);
//				_myGraphics.lineTo(px + 7, bounds[1] + 1);
//				_myGraphics.lineTo(px - 7, bounds[1] + 1);
//				_myGraphics.fill();
//
//				_myGraphics.color(new CCColor(255, 255));
//				_myGraphics.fontBlur(0.0f);
//				_myGraphics.textBox(pos.x - h, pos.y, tooltipWidth, widget.tooltip());
//			}
//		}
	}

}