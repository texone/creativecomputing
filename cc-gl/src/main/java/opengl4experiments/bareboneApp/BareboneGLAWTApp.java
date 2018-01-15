package opengl4experiments.bareboneApp;

/* This app is derived from the jogamp example by Wade Walker found here:
 * http://jogamp.org/wiki/index.php/Using_JOGL_in_AWT_SWT_and_Swing
 * It takes a qualified class name as argument
 * int the form:
 * <pre>-DemoClass=com.foo.bar.MyClass</pre>
 *  
 */

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import opengl4experiments.redbook.Example_ch03_DrawCommands;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class BareboneGLAWTApp {

	public static void main(String[] args) {

		Class myDemoClass = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-DemoClass")) {
				try {
					String qualifiedName = arg.split("=")[1];
					myDemoClass = Class.forName(qualifiedName);
				} catch (ClassNotFoundException e) {

				}
			}
		}

		if (true ) {

/*		if (myDemoClass == null) {
			System.err.println("No suitable demo class found!");
		} else {
	*/		
			
//			String className = "de.artcom.opengl4experiments.sb6.Example_sb6_Springmass";
//			String className = "de.artcom.opengl4experiments.sb6.Example_sb6_CSFlocking";
//			String className = "de.artcom.opengl4experiments.sb6.Example_sb6_BasicFBO";
			//String className = "de.artcom.opengl4experiments.redbook.Example_ch03_Instancing_A";
			//String className = "de.artcom.opengl4experiments.redbook.Example_misc_PlyModel";
			//String className = "de.artcom.opengl4experiments.redbook.Example_HelloTriangle";
			String className = Example_ch03_DrawCommands.class.getName();
			//String className = "de.artcom.opengl4experiments.redbook.Example_misc_Bunny";
			final BareboneGLAbstractExperiment myDemo = instantiate(className, BareboneGLAbstractExperiment.class);
			System.out.println("RUNNING: " + className);

			GLProfile glprofile = GLProfile.getMaximum(true);
			GLCapabilities glcapabilities = new GLCapabilities(glprofile);

			final GLCanvas glcanvas = new GLCanvas(glcapabilities);

			glcanvas.addGLEventListener(new GLEventListener() {

				@Override
				public void reshape(GLAutoDrawable glautodrawable, int x,
						int y, int width, int height) {
					myDemo.reshape(glautodrawable, x, y, width, height);
				}

				@Override
				public void init(GLAutoDrawable glautodrawable) {
					myDemo.init(glautodrawable);
				}

				@Override
				public void dispose(GLAutoDrawable glautodrawable) {
					myDemo.dispose(glautodrawable);
				}

				@Override
				public void display(GLAutoDrawable glautodrawable) {
					myDemo.display(glautodrawable);
				}
			});
			
			glcanvas.addMouseWheelListener(new MouseWheelListener() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					myDemo.mouseWheelMoved(e);
				}
			});
			glcanvas.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					myDemo.mouseReleased(arg0);
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					myDemo.mousePressed(arg0);
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					myDemo.mouseExited(arg0);					
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					myDemo.mouseEntered(arg0);
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					myDemo.mouseClicked(arg0);
				}
			});
			glcanvas.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					myDemo.mouseMoved(e);
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					myDemo.mouseDragged(e);
				}
			});

			final Frame frame = new Frame("barebone OGL AWT App");
			frame.add(glcanvas);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent windowevent) {
					frame.remove(glcanvas);
					frame.dispose();
					System.exit(0);
				}
			});

			frame.setSize(1600, 900);
			frame.setVisible(true);
	        Animator animator = new Animator();
	        animator.add(glcanvas);
	        //animator.setUpdateFPSFrames(60, null);
	        //animator.setRunAsFastAsPossible(true);
	        animator.start();
		}
	}
	
	public static <T> T instantiate(final String className, final Class<T> type){
	    try{
	        return type.cast(Class.forName(className).newInstance());
	    } catch(final InstantiationException e){
	        throw new IllegalStateException(e);
	    } catch(final IllegalAccessException e){
	        throw new IllegalStateException(e);
	    } catch(final ClassNotFoundException e){
	        throw new IllegalStateException(e);
	    }
	}
}