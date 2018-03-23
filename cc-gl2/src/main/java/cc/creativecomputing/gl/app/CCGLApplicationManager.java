package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2i;

public class CCGLApplicationManager {

	// The window handle
	private List<CCGLWindow> _myWindows = new ArrayList<>();
    private AtomicInteger latch = new AtomicInteger();
	protected CCGLApp _myMainWindow;
	private CCGLContext _myContext;
	
	private boolean _myIsStarted = false;
	
	private List<CCGLApp> _myAppsToStart = new ArrayList<>();
	
	public CCGLApplicationManager(CCGLApp theMainApp){
		_myMainWindow = theMainApp;
	}
	
	private void initApp(CCGLApp theApp){
		theApp.init(null, _myMainWindow);
		_myWindows.add(theApp);
		latch.incrementAndGet();
	}
	
	public void add(CCGLApp theApp){
		if(!_myIsStarted){
			_myAppsToStart.add(theApp);
			return;
		}
		
		initApp(theApp);
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
		glfwWindowHint(GLFW_SAMPLES, 8); 

		// Create the window
		_myContext = new CCGLContext();
		initApp(_myMainWindow);
		
		// Get the window size passed to glfwCreateWindow
		CCVector2i myWindowSize = _myMainWindow.framebufferSize();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center the window
//		_myMainWindow.position(-1,-1);
		
		// Make the OpenGL context current
		_myContext.makeContextCurrent(_myMainWindow);
		GL.createCapabilities();
		// Enable v-sync
		_myContext.swapInterval(1);

		// Make the window visible
		_myMainWindow.show();
		
		_myIsStarted = true;
		for(CCGLApp myApp:_myAppsToStart){
			initApp(myApp);
		}
	}
	
	public CCGLWindow window(){
		return _myMainWindow;
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

}