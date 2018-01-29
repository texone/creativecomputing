package cc.creativecomputing.gl.app;


import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

/**
 * A monitor object represents a currently connected monitor and is represented
 * as a pointer to the opaque type GLFWmonitor. Monitor objects cannot be
 * created or destroyed by the application and retain their addresses until the
 * monitors they represent are disconnected or until the library is terminated.
 * <P>
 * Each monitor has a current video mode, a list of supported video modes, a
 * virtual position, a human-readable name, an estimated physical size and a
 * gamma ramp. One of the monitors is the primary monitor.
 * <p>
 * The virtual position of a monitor is in screen coordinates and, together with
 * the current video mode, describes the viewports that the connected monitors
 * provide into the virtual desktop that spans them.
 * 
 * @author christianr
 *
 */
public class CCGLFWMonitor {

	private long _myID;

	public CCGLFWMonitor(long theID) {
		_myID = theID;
		
//		glfwGetMonitorPhysicalSize(monitor, widthMM, heightMM);
	}
	
	public long id(){
		return _myID;
	}

	public void getVideoModes() {
		Buffer myData = glfwGetVideoModes(_myID);
		
//		GLFWVidMode myMode = new GLFWVidMode(container)
//				myMode.
	}
}
