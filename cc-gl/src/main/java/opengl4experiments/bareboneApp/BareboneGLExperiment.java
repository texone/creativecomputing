package opengl4experiments.bareboneApp;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLPipelineFactory;

public class BareboneGLExperiment extends BareboneGLAbstractExperiment {

	private int width;
	private int height;
	private float aspect;
	
	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4)theAutoDrawable.getGL();
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable theAutoDrawable) {
		GL4 gl = theAutoDrawable.getGL().getGL4();
		try {
			gl = (GL4)gl.getContext().setGL(GLPipelineFactory.create("javax.media.opengl.Debug", null, gl, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// Trace ..
			gl = (GL4)gl.getContext().setGL(GLPipelineFactory.create("javax.media.opengl.Trace", null, gl, new Object[] { System.err }));
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void reshape(GLAutoDrawable theAutoDrawable, int x, int y,
			int width, int height) {
		this.width = width;
		this.height = height;
		this.aspect = (float)height/width;		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
