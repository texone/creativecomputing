package cc.creativecomputing.gl.app;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.HashMap;
import java.util.Map;

public class CCGLContext {
	
	private Map<Long, CCGLWindow> _myWindowMap = new HashMap<>();

	/**
	 * This function returns whether the specified API extension is supported by
	 * the current OpenGL or OpenGL ES context. It searches both for client API
	 * extension and context creation API extensions.
	 * <p>
	 * A context must be current on the calling thread. Calling this function
	 * without a current context will cause a GLFW_NO_CURRENT_CONTEXT error.
	 * <p>
	 * As this functions retrieves and searches one or more extension strings
	 * each call, it is recommended that you cache its results if it is going to
	 * be used frequently. The extension strings will not change during the
	 * lifetime of a context, so there is no danger in doing this.
	 * 
	 * @param theExtension The ASCII encoded name of the extension.
	 * @return <code>true</code> if the extension is available, or
	 *         <code>false</code> otherwise.
	 */
	public boolean extensionSupported(String theExtension) {
		return glfwExtensionSupported(theExtension);
	}
	
	/**
	 * This function returns the window whose OpenGL or OpenGL ES context is current on the calling thread.
	 * @return The window whose context is current, or NULL if no window's context is current.
	 */
	public CCGLWindow currentContext(){
		long myWindowID = glfwGetCurrentContext();
		if(myWindowID == NULL)return null;
		
		return _myWindowMap.get(myWindowID);
	}
	
	/**
	 * This function makes the OpenGL or OpenGL ES context of the specified
	 * window current on the calling thread. A context can only be made current
	 * on a single thread at a time and each thread can have only a single
	 * current context at a time.
	 * <p>
	 * By default, making a context non-current implicitly forces a pipeline
	 * flush. On machines that support GL_KHR_context_flush_control, you can
	 * control whether a context performs this flush by setting the
	 * GLFW_CONTEXT_RELEASE_BEHAVIOR window hint.
	 * <p>
	 * The specified window must have an OpenGL or OpenGL ES context. Specifying
	 * a window without a context will generate a GLFW_NO_WINDOW_CONTEXT error.
	 * 
	 * @param theWindow
	 */
	public void makeContextCurrent(CCGLWindow theWindow){
		glfwMakeContextCurrent(theWindow.id());
	}

	/**
	 * This function sets the swap interval for the current OpenGL or OpenGL ES
	 * context, i.e. the number of screen updates to wait from the time
	 * glfwSwapBuffers was called before swapping the buffers and returning.
	 * This is sometimes called vertical synchronization, vertical retrace
	 * synchronization or just vsync.
	 * <p>
	 * Contexts that support either of the WGL_EXT_swap_control_tear and
	 * GLX_EXT_swap_control_tear extensions also accept negative swap intervals,
	 * which allow the driver to swap even if a frame arrives a little bit late.
	 * You can check for the presence of these extensions using
	 * glfwExtensionSupported. For more information about swap tearing, see the
	 * extension specifications.
	 * <p>
	 * A context must be current on the calling thread. Calling this function
	 * without a current context will cause a GLFW_NO_CURRENT_CONTEXT error.
	 * <p>
	 * This function does not apply to Vulkan. If you are rendering with Vulkan,
	 * see the present mode of your swapchain instead.
	 * 
	 * @param theInterval The minimum number of screen updates to wait for until
	 *            the buffers are swapped by glfwSwapBuffers.
	 * 
	 */
	public void swapInterval(int theInterval) {
		glfwSwapInterval(theInterval);
	}
}
