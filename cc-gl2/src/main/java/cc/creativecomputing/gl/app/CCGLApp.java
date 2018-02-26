package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2i;

public class CCGLApp {

	// The window handle
	private List<CCGLWindow> _myWindows = new ArrayList<>();
    private AtomicInteger latch = new AtomicInteger();
	protected CCGLWindow _myMainWindow;
	private CCGLContext _myContext;
	
	
	@CCProperty(desc = "window width")
	public int width = 800;
	@CCProperty(desc = "window height")
	public int height = 800;
	@CCProperty(desc = "window title")
	public String title = "cc app";
	@CCProperty(desc = "number of samples used for antialiasing of the application")
	public int antialiasing = 8;
	@CCProperty(desc = "flag to make a window undecorated default false")
	public boolean undecorated = false;
	@CCProperty(desc = "flag to make a window fullscreen default false")
	public boolean fullscreen = false;
	@CCProperty(desc = "flag to make a window resizable default true")
	public boolean resizable = true;
	@CCProperty(desc = "flag to let the app run in vsync")
	public boolean inVsync = false;
	@CCProperty(desc = "flag to define if the window should be shown")
	public boolean visible = true;
	@CCProperty(desc = "flag to define if the window should be shown on top")
	public boolean alwaysOnTop = false;
	
	public CCGLWindow createWindow(int theWidth, int theHeight, String theTitle){
		CCGLWindow myWindow = new CCGLWindow(theWidth, theHeight, theTitle, null, _myMainWindow);
		_myWindows.add(myWindow);
		latch.incrementAndGet();
		return myWindow;
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		// optional, the current window hints are already the default
		glfwDefaultWindowHints(); 
		// the window will stay hidden after creation
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		// the window will be resizable										
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); 

		// Create the window
		_myContext = new CCGLContext();
		_myMainWindow = createWindow(width, height, title);
		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		_myMainWindow.keyReleaseEvents.add(event ->{
			if(event.key == CCGLKey.KEY_ESCAPE){
				_myMainWindow.shouldClose();
			}
		});
		
		_myMainWindow.setupEvents.add(this::setup);
		
		_myMainWindow.updateEvents.add(this::update);
		
		_myMainWindow.drawEvents.add(this::display);
		
		// Get the window size passed to glfwCreateWindow
		CCVector2i myWindowSize = _myMainWindow.windowSize();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center the window
		_myMainWindow.position((vidmode.width() - myWindowSize.x) / 2, (vidmode.height() - myWindowSize.y) / 2);
		
		// Make the OpenGL context current
		_myContext.makeContextCurrent(_myMainWindow);
		GL.createCapabilities();
		// Enable v-sync
		_myContext.swapInterval(1);

		// Make the window visible
		_myMainWindow.show();
		
	}
	
	public CCGLWindow window(){
		return _myMainWindow;
	}
	
	public void setup(CCGraphics g, CCGLTimer theTimer){
		
	}
	
	public void update(CCGLTimer theTimer){
		
	}
	
	public void display(CCGraphics g) {
		
	}
	
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (latch.get() != 0) {

			for (CCGLWindow myWindow:new ArrayList<>(_myWindows)) {
                _myContext.makeContextCurrent(myWindow);
				if(!myWindow.isPrepared()){
					myWindow.prepareForDraw();
				}

                myWindow.draw();
                glfwPollEvents();

                if (myWindow.shouldClose()) {
                	myWindow.freeCallbacks();
                    myWindow.destroy();
                    
                    // make sure to close if windows are active but not visible
                    int myVisibleWindows = 0;
                    for(CCGLWindow myWindow2:new ArrayList<>(_myWindows)){
                    	myVisibleWindows += myWindow2.isVisible() ? 1 : 0;
                    }
                    if(myVisibleWindows == 0){
                    	for(CCGLWindow myWindow2:new ArrayList<>(_myWindows)){
                    		myWindow2.shouldClose(true);
                        }
                    }
                    myWindow.isVisible();
                    _myWindows.remove(myWindow);
                    latch.decrementAndGet();
                }
            }
		}
	}

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void main(String[] args) {
		new CCGLApp().run();
	}

}