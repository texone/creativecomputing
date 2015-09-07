package opengl4experiments.sb6;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.cos;
import static cc.creativecomputing.gl4.GLBufferUtil.sin;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static opengl4experiments.utils.ShaderLoader.loadAndCompileShader;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Example_sb6_Springmass extends BareboneGLAbstractExperiment {
	
	private final static int POINTS_X = 50; 
	private final static int POINTS_Y = 50; 
	private final static int POINTS_TOTAL = POINTS_X * POINTS_Y;
	private final static int CONNECTIONS_TOTAL = (POINTS_X-1)*POINTS_Y + (POINTS_Y - 1)*POINTS_X;

	private int m_update_program = -1, m_render_program = -1;
	private IntBuffer m_vao = IntBuffer.allocate(2);
	private IntBuffer m_vbo = IntBuffer.allocate(5);
	private IntBuffer m_pos_tbo = IntBuffer.allocate(2);
	private IntBuffer m_index_buffer = IntBuffer.allocate(1);
	
	private int iterations_per_frame = 16;
	private int m_iteration_index = 1;
	
	private int width;
	private int height;
	private float aspect;
	
	private static final int POSITION_A = 0, POSITION_B = 1, VELOCITY_A = 2, VELOCITY_B = 3, CONNECTION =4;

	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4)theAutoDrawable.getGL();
		
		gl.glUseProgram(m_update_program);
		gl.glEnable(GL4.GL_RASTERIZER_DISCARD);
		for (int i = iterations_per_frame; i != 0; --i) {
			gl.glBindVertexArray(m_vao.get(m_iteration_index % 2));
			gl.glBindTexture(GL4.GL_TEXTURE_BUFFER, m_pos_tbo.get(m_iteration_index%2));
			m_iteration_index = (m_iteration_index+1)%2;
			gl.glBindBufferBase(GL4.GL_TRANSFORM_FEEDBACK_BUFFER, 0, m_vbo.get(POSITION_A + m_iteration_index % 2));
			gl.glBindBufferBase(GL4.GL_TRANSFORM_FEEDBACK_BUFFER, 0, m_vbo.get(VELOCITY_A + m_iteration_index % 2));
			gl.glBeginTransformFeedback(GL.GL_POINTS);
			gl.glDrawArrays(GL.GL_POINTS, 0, POINTS_TOTAL);
			gl.glEndTransformFeedback();
		}
		gl.glDisable(GL4.GL_RASTERIZER_DISCARD);

		gl.glViewport(0,0,width,height);
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, FloatBuffer.wrap(new float[]{0f, 0f, 0f, 1f}));
		
		gl.glUseProgram(m_render_program);
		
		gl.glPointSize(4.0f);
		gl.glDrawArrays(GL.GL_POINTS, 0, POINTS_TOTAL);
		
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, m_index_buffer.get(0));
		gl.glDrawElements(GL.GL_LINES, CONNECTIONS_TOTAL * 2, GL.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		GL4 gl = theAutoDrawable.getGL().getGL4();
		gl.glDeleteProgram(m_update_program);
		gl.glDeleteProgram(m_render_program);
		gl.glDeleteBuffers(5, m_vbo);
		gl.glDeleteBuffers(2, m_vao);
	}

	@Override
	public void init(GLAutoDrawable theAutoDrawable) {
		GL4 gl = theAutoDrawable.getGL().getGL4();
		/*
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
		*/		
		try {
			m_update_program = loadAndCompileShader(gl, "shader/Example_sb6_Springmass/update.vs.glsl", "shader/Example_sb6_Springmass/update.fs.glsl");
			m_render_program = loadAndCompileShader(gl, "shader/Example_sb6_Springmass/render.vs.glsl", "shader/Example_sb6_Springmass/render.fs.glsl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float[] initial_positions = new float[POINTS_TOTAL*4];
		float[] initial_velocities = new float[POINTS_TOTAL*3];
		int[] connection_vectors = new int[POINTS_TOTAL*4];
		
		int n = 0;
		for (int j= 0; j < POINTS_Y; j++) {
			float fj = (float) j / POINTS_Y;
			for ( int i = 0; i < POINTS_X; i++) {
				float fi = (float)i/POINTS_X;
				float r = (float)Math.random();
				initial_positions[n*4 + 0] = (fi - 0.5f) * POINTS_X + r*2; 
				initial_positions[n*4 + 1] = (fj - 0.5f) * POINTS_Y + r*3; 
				initial_positions[n*4 + 2] = 0.6f * sin(fi) * cos(fi) - r*4; 
				initial_positions[n*4 + 3] = 1.0f;
				initial_velocities[n*3 + 0] = 0f;
				initial_velocities[n*3 + 1] = 0f;
				initial_velocities[n*3 + 2] = 0f;
//				initial_velocities[n*3 + 2] = r - 50f;
               	connection_vectors[n*4 + 0] = - 1;
               	connection_vectors[n*4 + 1] = - 1;
               	connection_vectors[n*4 + 2] = - 1;
               	connection_vectors[n*4 + 3] = - 1;
                if (j != (POINTS_Y - 1))
                {
                    if (i != 0) {
                        connection_vectors[n*4 + 0] = n - 1;
                    } 
                    if (j != 0) {
                        connection_vectors[n*4 + 1] = n - POINTS_X;
                    } 
                    if (i != (POINTS_X - 1)) {
                        connection_vectors[n*4 + 2] = n + 1;
                    } 
                    if (j != (POINTS_Y - 1)) {
                        connection_vectors[n*4 + 3] = n + POINTS_X;
                    } 
                }
				n++;
			}
		}
		
		gl.glGenVertexArrays(2, m_vao);
		gl.glGenBuffers(5, m_vbo);
		
		for (int i = 0; i < 2; i++) {
			gl.glBindVertexArray(m_vao.get(i));
			
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, m_vbo.get(POSITION_A + i));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, POINTS_TOTAL * Buffers.SIZEOF_FLOAT * 4, asBuffer(initial_positions), GL4.GL_DYNAMIC_COPY);
			gl.glVertexAttribPointer(0, 4, GL.GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, m_vbo.get(VELOCITY_A + i));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, POINTS_TOTAL * Buffers.SIZEOF_FLOAT * 3, asBuffer(initial_velocities), GL4.GL_DYNAMIC_COPY);
			gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
			
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, m_vbo.get(CONNECTION));
			gl.glBufferData(GL.GL_ARRAY_BUFFER, POINTS_TOTAL * Buffers.SIZEOF_INT*4, asBuffer(connection_vectors), GL.GL_STATIC_DRAW);
			gl.glVertexAttribPointer(2,4,GL4.GL_INT, false, 0, 0);
			gl.glEnableVertexAttribArray(2);
		}
		
		gl.glGenTextures(2, m_pos_tbo);
		gl.glBindTexture(GL4.GL_TEXTURE_BUFFER, m_pos_tbo.get(0));
		gl.glTexBuffer(GL4.GL_TEXTURE_BUFFER, GL.GL_RGBA32F, m_vbo.get(POSITION_A));
		gl.glBindTexture(GL4.GL_TEXTURE_BUFFER, m_pos_tbo.get(1));
		gl.glTexBuffer(GL4.GL_TEXTURE_BUFFER, GL.GL_RGBA32F, m_vbo.get(POSITION_B));
		
		
		//TODO: implement the following with MapBuffer.
		int[] indices = new int[CONNECTIONS_TOTAL*2];
		
		int index = 0;
		for (int j = 0; j < POINTS_Y; j++) {
			for (int i = 0; i < POINTS_X -1; i++) {
				indices[index++] = i + j*POINTS_X;
				indices[index++] = 1 + i + j*POINTS_X;
			}
		}
		
		for (int i = 0; i < POINTS_X; i++) {
			for (int j = 0; j < POINTS_Y-1; j++) {
				indices[index++] = i + j*POINTS_X;
				indices[index++] = POINTS_X + i + j*POINTS_X;
			}
		}
		
		gl.glGenBuffers(1, m_index_buffer);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, m_index_buffer.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), asBuffer(indices), GL.GL_STATIC_DRAW);
		
		
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
