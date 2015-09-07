package opengl4experiments.redbook;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.IntBuffer;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;
import opengl4experiments.utils.ShaderLoader;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

//code ported from the red book: http://www.opengl-redbook.com/

public class Example_ch03_DrawCommands extends BareboneGLAbstractExperiment{
	
	
	private float aspect, width, height;
	int render_prog;
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vbo = IntBuffer.allocate(1);
	private IntBuffer ebo = IntBuffer.allocate(1);
	
	private int render_model_matrix_loc;
	private int render_projection_matrix_loc;
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		gl.glUseProgram(render_prog);
		
		float[] model_matrix;
		float[] projection_matrix = loadIdentity();
		projection_matrix = frustum(-1f,1f,-aspect,aspect,1f,500f);
		gl.glUniformMatrix4fv(render_projection_matrix_loc, 1, false, projection_matrix, 0);
		
		gl.glBindVertexArray(vao.get(0));
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		
		
	    // Draw Arrays...
		model_matrix = loadIdentity();
		model_matrix = translate(model_matrix, -3.0f, 0.0f, -5.0f);
		gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);
		
	    // DrawElements
		model_matrix = loadIdentity();
		model_matrix = translate(model_matrix, -1.0f, 0.0f, -5.0f);
		gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
		gl.glDrawElements(GL.GL_TRIANGLES, 3, GL.GL_UNSIGNED_SHORT, 0);
		
	    // DrawElementsBaseVertex
		model_matrix = loadIdentity();
	    model_matrix = translate(model_matrix, 1.0f, 0.0f, -5.0f);
	    gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
	    gl.glDrawElementsBaseVertex(GL.GL_TRIANGLES, 3, GL.GL_UNSIGNED_SHORT, 0, 1);
		
	    // DrawArraysInstanced
		model_matrix = loadIdentity();
	    model_matrix = translate(model_matrix, 3.0f, 0.0f, -5.0f);
	    gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
	    gl.glDrawArraysInstanced(GL.GL_TRIANGLES, 0, 3, 10);
	    
		
//		gl.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
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
		
	    // A single triangle
	    float[] vertex_positions =
	    {
	        -1.0f, -1.0f, 0.0f, 1.0f,
	         1.0f, -1.0f, 0.0f, 1.0f,
	        -1.0f,  1.0f, 0.0f, 1.0f,
	        -1.0f, -1.0f, 0.0f, 1.0f,
	    };

	    // Color for each vertex
	    float[] vertex_colors =
	    {
	        1.0f, 1.0f, 1.0f, 1.0f,
	        1.0f, 1.0f, 0.0f, 1.0f,
	        1.0f, 0.0f, 1.0f, 1.0f,
	        0.0f, 1.0f, 1.0f, 1.0f
	    };

	    // Indices for the triangle strips
	    short[] vertex_indices =
	    {
	        0, 1, 2
	    };
	    
	    // Set up the element array buffer
	    gl.glGenBuffers(1, ebo);
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
	    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(vertex_indices), asBuffer(vertex_indices), GL.GL_STATIC_DRAW);

	    // Set up the vertex attributes
	    gl.glGenVertexArrays(1, vao);
	    gl.glBindVertexArray(vao.get(0));
	    
	    gl.glGenBuffers(1, vbo);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
	    gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(vertex_positions) + sizeof(vertex_colors), null, GL.GL_STATIC_DRAW);
	    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, sizeof(vertex_positions), asBuffer(vertex_positions));
	    gl.glBufferSubData(GL.GL_ARRAY_BUFFER, sizeof(vertex_positions), sizeof(vertex_colors), asBuffer(vertex_colors));
	    
	    int vertex_position_loc = gl.glGetAttribLocation(render_prog, "position");
	    int vertex_color_loc = gl.glGetAttribLocation(render_prog, "color");
	    //attrib loc, attrib comp. count, ~ type, normalized, stride, offset
	    gl.glVertexAttribPointer(vertex_position_loc, 4, GL.GL_FLOAT, false, 0, 0);
	    gl.glVertexAttribPointer(vertex_color_loc, 4, GL.GL_FLOAT, false, 0, sizeof(vertex_positions));
	    gl.glEnableVertexAttribArray(vertex_position_loc);
	    gl.glEnableVertexAttribArray(vertex_color_loc);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
		GL4 gl = drawable.getGL().getGL4();
	    gl.glViewport(0, 0 , width, height);
	    aspect = (float)height / (float)width;
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
