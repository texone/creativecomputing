package opengl4experiments.redbook;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.rotate;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.IntBuffer;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;
import opengl4experiments.utils.ShaderLoader;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Example_ch03_PrimitiveRestart extends BareboneGLAbstractExperiment {

	int render_prog;
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vbo = IntBuffer.allocate(1);
	private IntBuffer ebo = IntBuffer.allocate(1);
	float aspect;
	
	private int render_model_matrix_loc;
	private int render_projection_matrix_loc;
	float t = 0f;
	
	private static boolean USE_PRIMITIVE_RESTART = false; 
	
	@Override
	public void display(GLAutoDrawable drawable) {
		
		t += 0.001f;
		
		GL4 gl = drawable.getGL().getGL4();
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glUseProgram(render_prog);
		
		float[] model_matrix = loadIdentity();
		model_matrix = translate(model_matrix, 0f, 0f, -5f);
		model_matrix = rotate(model_matrix, t * 3.1412f, 0, 1, 0);
		model_matrix = rotate(model_matrix, t * 3.1412f * 2f, 0, 0, 1);
		
		float[] projecton_matrix = frustum(-1.0f, 1.0f, -aspect, aspect, 1.0f, 500f);
		
		gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
		gl.glUniformMatrix4fv(render_projection_matrix_loc, 1, false, projecton_matrix, 0);
		
		gl.glBindVertexArray(vao.get(0));
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		
		if (USE_PRIMITIVE_RESTART) {
			gl.glEnable(GL4.GL_PRIMITIVE_RESTART);
			gl.glPrimitiveRestartIndex(Short.MAX_VALUE);
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, 17, GL.GL_UNSIGNED_SHORT, 0);
		} else {
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, 8, GL.GL_UNSIGNED_SHORT, 0);
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, 8, GL.GL_UNSIGNED_SHORT, 9 * Buffers.SIZEOF_SHORT);
		}
		

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		try {
			render_prog = ShaderLoader.loadAndCompileShader(gl, "shader/primitive_restart.vs.glsl", "shader/primitive_restart.fs.glsl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gl.glUseProgram(render_prog);
		
		render_model_matrix_loc = gl.glGetUniformLocation(render_prog, "model_matrix");
		render_projection_matrix_loc = gl.glGetUniformLocation(render_prog, "projection_matrix");
		
		
	    // 8 corners of a cube, side length 2, centered on the origin
	    float cube_positions[] =
	    {
	        -1.0f, -1.0f, -1.0f, 1.0f,
	        -1.0f, -1.0f,  1.0f, 1.0f,
	        -1.0f,  1.0f, -1.0f, 1.0f,
	        -1.0f,  1.0f,  1.0f, 1.0f,
	         1.0f, -1.0f, -1.0f, 1.0f,
	         1.0f, -1.0f,  1.0f, 1.0f,
	         1.0f,  1.0f, -1.0f, 1.0f,
	         1.0f,  1.0f,  1.0f, 1.0f
	    };

	    // Color for each vertex
	    float cube_colors[] =
	    {
	        1.0f, 1.0f, 1.0f, 1.0f,
	        1.0f, 1.0f, 0.0f, 1.0f,
	        1.0f, 0.0f, 1.0f, 1.0f,
	        1.0f, 0.0f, 0.0f, 1.0f,
	        0.0f, 1.0f, 1.0f, 1.0f,
	        0.0f, 1.0f, 0.0f, 1.0f,
	        0.0f, 0.0f, 1.0f, 1.0f,
	        0.5f, 0.5f, 0.5f, 1.0f
	    };

	    // Indices for the triangle strips
	    short cube_indices[] =
	    {
	        0, 1, 2, 3, 6, 7, 4, 5,         // First strip
	        Short.MAX_VALUE,                         // <<-- This is the restart index
	        2, 6, 0, 4, 1, 5, 3, 7          // Second strip
	    };
	    
	    // Set up the element array buffer
	    gl.glGenBuffers(1, ebo);
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
	    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_indices), asBuffer(cube_indices), GL.GL_STATIC_DRAW);
	    
	    // Set up the vertex attributes
	    gl.glGenVertexArrays(1, vao);
	    gl.glBindVertexArray(vao.get(0));
	    
	    gl.glGenBuffers(1,vbo);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
	    gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(cube_positions) + sizeof(cube_colors), null, GL.GL_STATIC_DRAW);
	    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, sizeof(cube_positions), asBuffer(cube_positions));
	    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, sizeof(cube_positions), sizeof(cube_colors), asBuffer(cube_colors));
	    
	    int vertex_position_loc = gl.glGetAttribLocation(render_prog, "position");
	    int vertex_color_loc = gl.glGetAttribLocation(render_prog, "color");
	    
	    gl.glVertexAttribPointer(vertex_position_loc, 4, GL.GL_FLOAT, false, 0, 0);
	    gl.glVertexAttribPointer(vertex_color_loc, 4, GL.GL_FLOAT, false, 0, sizeof(cube_positions));
	    
	    gl.glEnableVertexAttribArray(vertex_position_loc);
	    gl.glEnableVertexAttribArray(vertex_color_loc);
	    
	    gl.glClearColor(0f, 0f, 0f, 1f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w,
			int h) {
		GL4 gl = drawable.getGL().getGL4();
		gl.glViewport(0, 0, w, h);
		aspect = (float)h/w;
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
