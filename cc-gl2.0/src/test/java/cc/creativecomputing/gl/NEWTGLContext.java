package cc.creativecomputing.gl;

import org.junit.Assert;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;

public class NEWTGLContext {

    public static class WindowContext {        
        public final Window window;
        public final GLDrawable drawable;
        public final GLContext context;
        
        public WindowContext(Window w, GLDrawable d, GLContext c) {
            window = w;
            drawable = d;
            context = c;
        }
    }

    public static WindowContext createOffscreenWindow(GLCapabilities caps, int width, int height, boolean debugGL) {
        caps.setOnscreen(false);
        caps.setPBuffer(true);
        
        //
        // Create native windowing resources .. X11/Win/OSX
        // 
        Display display = NewtFactory.createDisplay(null); // local display
        Assert.assertNotNull(display);
    
        Screen screen  = NewtFactory.createScreen(display, 0); // screen 0
        Assert.assertNotNull(screen);
    
        Window window = NewtFactory.createWindow(screen, caps);
        Assert.assertNotNull(window);
        window.setSize(width, height);
        window.setVisible(true);
//        Assert.assertTrue(AWTRobotUtil.waitForVisible(window, true));
//        Assert.assertTrue(AWTRobotUtil.waitForRealized(window, true));
            
        GLDrawableFactory factory = GLDrawableFactory.getFactory(caps.getGLProfile());
        GLDrawable drawable = factory.createGLDrawable(window);
        Assert.assertNotNull(drawable);
        
        drawable.setRealized(true);
        Assert.assertTrue(drawable.isRealized());
        
        GLContext context = drawable.createContext(null);
        Assert.assertNotNull(context);
        
        context.enableGLDebugMessage(debugGL);
        
        int res = context.makeCurrent();
        Assert.assertTrue(GLContext.CONTEXT_CURRENT_NEW==res || GLContext.CONTEXT_CURRENT==res);
        
        return new WindowContext(window, drawable, context);
    }

    public static WindowContext createOnscreenWindow(GLCapabilities caps, int width, int height, boolean debugGL) {
        //
        // Create native windowing resources .. X11/Win/OSX
        // 
        Display display = NewtFactory.createDisplay(null); // local display
        Assert.assertNotNull(display);
    
        Screen screen  = NewtFactory.createScreen(display, 0); // screen 0
        Assert.assertNotNull(screen);
    
        Window window = NewtFactory.createWindow(screen, caps);
        Assert.assertNotNull(window);
        window.setSize(width, height);
        window.setVisible(true);
//        Assert.assertTrue(AWTRobotUtil.waitForVisible(window, true));
//        Assert.assertTrue(AWTRobotUtil.waitForRealized(window, true));
            
        GLDrawableFactory factory = GLDrawableFactory.getFactory(caps.getGLProfile());
        GLDrawable drawable = factory.createGLDrawable(window);
        Assert.assertNotNull(drawable);
        
        drawable.setRealized(true);
        Assert.assertTrue(drawable.isRealized());
        
        GLContext context = drawable.createContext(null);
        Assert.assertNotNull(context);
        
        context.enableGLDebugMessage(debugGL);
        
        int res = context.makeCurrent();
        Assert.assertTrue(GLContext.CONTEXT_CURRENT_NEW==res || GLContext.CONTEXT_CURRENT==res);
        
        return new WindowContext(window, drawable, context);
    }

    public static void destroyWindow(WindowContext winctx) {
        GLDrawable drawable = winctx.context.getGLDrawable();
        
        Assert.assertNotNull(winctx.context);
        winctx.context.destroy();
    
        Assert.assertNotNull(drawable);
        drawable.setRealized(false);
    
        Assert.assertNotNull(winctx.window);
        winctx.window.destroy();
    }
   
}