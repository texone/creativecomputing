package opengl4experiments.bareboneApp;

import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import opengl4experiments.utils.Camera;
import opengl4experiments.utils.ShaderLoader;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class BareboneGLExperimentWithCamera extends BareboneGLAbstractExperiment {
	
	private float[] model_matrix = loadIdentity();
	private Camera myCam;
//	private float[] mvp_matrix = loadIdentity(); 
	
	private float width, height, aspect;
	
	private int view_matrix_loc, model_matrix_loc, projection_matrix_loc;
	
	private int render_prog;
	
	private int pmouseX, pmouseY;
	
	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4)theAutoDrawable.getGL();
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glUniformMatrix4fv(model_matrix_loc, 1, false, model_matrix, 0);
		gl.glUniformMatrix4fv(view_matrix_loc, 1, false, myCam.getViewMatrix(), 0);
		gl.glUniformMatrix4fv(projection_matrix_loc, 1, false, myCam.getProjectionMatrix(), 0);

	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		try {
			render_prog = ShaderLoader.loadAndCompileShader(gl, "shader/default.vert", "shader/default.frag");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gl.glUseProgram(render_prog);
		
		model_matrix_loc = gl.glGetUniformLocation(render_prog, "model_matrix");
		projection_matrix_loc = gl.glGetUniformLocation(render_prog, "projection_matrix");
		view_matrix_loc = gl.glGetUniformLocation(render_prog, "view_matrix");
		
		myCam = new Camera(0f,0f,-0.5f,1f);
	}

	@Override
	public void reshape(GLAutoDrawable theAutoDrawable, int x, int y,
			int w, int h) {
		width = w;
		height = h;
		aspect = (float)height/width;
		myCam.updateProjectionMatrix(aspect);
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
		pmouseX = e.getX();
		pmouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - pmouseX;
		int dy = e.getY() - pmouseY;
		pmouseX = e.getX();
		pmouseY = e.getY();
		if (e.isShiftDown()) {			
			myCam.translate((float)dx/width, -(float)dy/height, 0);
		} else {
			myCam.yaw(dx/360f);
			myCam.pitch(dy/360f);
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		float delta = e.getWheelRotation();
		myCam.translate(0, 0, delta*0.01f);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
