package opengl4experiments.redbook;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;
import opengl4experiments.jogamp.Bunny;
import cc.creativecomputing.gl4.GLException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Example_misc_Bunny extends BareboneGLAbstractExperiment {

	private Bunny myBunny;
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		myBunny.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glClearColor(0f,0f,0f,1f);
		
		myBunny = new Bunny();
		try {
			myBunny.load(gl);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w,
			int h) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glViewport(0,0,w,h);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
