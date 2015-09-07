package opengl4experiments.sb6;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.cos;
import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.rotate;
import static cc.creativecomputing.gl4.GLBufferUtil.sin;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;
import opengl4experiments.utils.ShaderLoader;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLPipelineFactory;

public class Example_sb6_BasicFBO extends BareboneGLAbstractExperiment {
	
	private double currentTime = 0;
    private int program1, program2, mv_location, proj_location, mv_location2, proj_location2;
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer position_buffer = IntBuffer.allocate(1);
	private IntBuffer index_buffer = IntBuffer.allocate(1);
	private IntBuffer fbo = IntBuffer.allocate(1);
	private IntBuffer color_texture = IntBuffer.allocate(1);
	private IntBuffer depth_texture = IntBuffer.allocate(1);
	private int width, height;
	private float aspect;
			
    
	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		currentTime+=0.000005f;
		GL4 gl = theAutoDrawable.getGL().getGL4();
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		//float[] proj_matrix = frustum(-0.5f,0.5f,-0.5f,0.5f,0.1f,100f);
		float[] proj_matrix = frustum(-1f,1f,-aspect,aspect,1f,500f);
		//float[] proj_matrix = loadIdentity();
		float f = (float)(currentTime * 0.3f);
		float[] mv_matrix = loadIdentity();
		mv_matrix = translate(mv_matrix, 0.0f, 0.0f, -1f);
		mv_matrix = translate(mv_matrix, sin(2.1f*f)*0.5f, sin(1.7f*f)*0.5f, sin(1.3f * f) * cos(1.5f * f) * 2.0f);
		mv_matrix = rotate(mv_matrix, (float)currentTime * 45.0f, 0.0f, 1.0f, 0.0f);
		mv_matrix = rotate(mv_matrix, (float)currentTime * 81.0f, 1.0f, 0.0f, 0.0f);
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo.get(0));
		
		gl.glViewport(0,0,512,512);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, FloatBuffer.wrap(new float[]{0f,1f,0f}));
		gl.glClearBufferfv(GL4.GL_DEPTH, 0, FloatBuffer.wrap(new float[]{1f}));
		
		gl.glUseProgram(program1);
		
		gl.glUniformMatrix4fv(proj_location, 1, false, asBuffer(proj_matrix));
		gl.glUniformMatrix4fv(mv_location, 1, false, asBuffer(mv_matrix));
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
		
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		gl.glViewport(0,0,width,height);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, FloatBuffer.wrap(new float[]{0f,0f,1f}));
		gl.glClearBufferfv(GL4.GL_DEPTH, 0, FloatBuffer.wrap(new float[]{1f}));
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, color_texture.get(0));
		
		gl.glUseProgram(program2);
		
		gl.glUniformMatrix4fv(proj_location2, 1, false, asBuffer(proj_matrix));
		gl.glUniformMatrix4fv(mv_location2, 1, false, asBuffer(mv_matrix));
		
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);		
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
		
		
		
		try {
			program1 = ShaderLoader.loadAndCompileShader(gl, "shader/Example_sb6_basicFBO/render.vs.glsl", "shader/Example_sb6_basicFBO/render.fs.glsl");
			program2 = ShaderLoader.loadAndCompileShader(gl, "shader/Example_sb6_basicFBO/render.vs.glsl", "shader/Example_sb6_basicFBO/render2.fs.glsl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mv_location = gl.glGetUniformLocation(program1, "mv_matrix");
		proj_location = gl.glGetUniformLocation(program1, "proj_matrix");
		mv_location2 = gl.glGetUniformLocation(program2, "mv_matrix");
		proj_location2 = gl.glGetUniformLocation(program2, "proj_matrix");
		
		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.get(0));
		
	    int[] vertex_indices = new int[]
	           {
	               0, 1, 2,
	               2, 1, 3,
	               2, 3, 4,
	               4, 3, 5,
	               4, 5, 6,
	               6, 5, 7,
	               6, 7, 0,
	               0, 7, 1,
	               6, 0, 2,
	               2, 4, 6,
	               7, 5, 3,
	               7, 3, 1
	           };

	    float[] vertex_data =
	           {
	                // Position                 Tex Coord
	               -0.25f, -0.25f,  0.25f,      0.0f, 1.0f,
	               -0.25f, -0.25f, -0.25f,      0.0f, 0.0f,
	                0.25f, -0.25f, -0.25f,      1.0f, 0.0f,

	                0.25f, -0.25f, -0.25f,      1.0f, 0.0f,
	                0.25f, -0.25f,  0.25f,      1.0f, 1.0f,
	               -0.25f, -0.25f,  0.25f,      0.0f, 1.0f,

	                0.25f, -0.25f, -0.25f,      0.0f, 0.0f,
	                0.25f,  0.25f, -0.25f,      1.0f, 0.0f,
	                0.25f, -0.25f,  0.25f,      0.0f, 1.0f,

	                0.25f,  0.25f, -0.25f,      1.0f, 0.0f,
	                0.25f,  0.25f,  0.25f,      1.0f, 1.0f,
	                0.25f, -0.25f,  0.25f,      0.0f, 1.0f,

	                0.25f,  0.25f, -0.25f,      1.0f, 0.0f,
	               -0.25f,  0.25f, -0.25f,      0.0f, 0.0f,
	                0.25f,  0.25f,  0.25f,      1.0f, 1.0f,

	               -0.25f,  0.25f, -0.25f,      0.0f, 0.0f,
	               -0.25f,  0.25f,  0.25f,      0.0f, 1.0f,
	                0.25f,  0.25f,  0.25f,      1.0f, 1.0f,

	               -0.25f,  0.25f, -0.25f,      1.0f, 0.0f,
	               -0.25f, -0.25f, -0.25f,      0.0f, 0.0f,
	               -0.25f,  0.25f,  0.25f,      1.0f, 1.0f,

	               -0.25f, -0.25f, -0.25f,      0.0f, 0.0f,
	               -0.25f, -0.25f,  0.25f,      0.0f, 1.0f,
	               -0.25f,  0.25f,  0.25f,      1.0f, 1.0f,

	               -0.25f,  0.25f, -0.25f,      0.0f, 1.0f,
	                0.25f,  0.25f, -0.25f,      1.0f, 1.0f,
	                0.25f, -0.25f, -0.25f,      1.0f, 0.0f,

	                0.25f, -0.25f, -0.25f,      1.0f, 0.0f,
	               -0.25f, -0.25f, -0.25f,      0.0f, 0.0f,
	               -0.25f,  0.25f, -0.25f,      0.0f, 1.0f,

	               -0.25f, -0.25f,  0.25f,      0.0f, 0.0f,
	                0.25f, -0.25f,  0.25f,      1.0f, 0.0f,
	                0.25f,  0.25f,  0.25f,      1.0f, 1.0f,

	                0.25f,  0.25f,  0.25f,      1.0f, 1.0f,
	               -0.25f,  0.25f,  0.25f,      0.0f, 1.0f,
	               -0.25f, -0.25f,  0.25f,      0.0f, 0.0f,
	           };
		
			
	    gl.glGenBuffers(1, position_buffer);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, position_buffer.get(0));
	    gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(vertex_data), asBuffer(vertex_data), GL.GL_STATIC_DRAW);
	    //'0' is the layout location for the vertex_data position 
	    gl.glVertexAttribPointer(0,3,GL.GL_FLOAT, false, 5 * Buffers.SIZEOF_FLOAT, 0);
	    gl.glEnableVertexAttribArray(0);
	    //'1' -- the same for tex coords
	    gl.glVertexAttribPointer(1,2,GL.GL_FLOAT, false, 5*Buffers.SIZEOF_FLOAT, 3*Buffers.SIZEOF_FLOAT);
	    gl.glEnableVertexAttribArray(1);
	    
	    gl.glGenBuffers(1, index_buffer);
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, index_buffer.get(0));
	    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(vertex_indices), asBuffer(vertex_indices), GL.GL_STATIC_DRAW);
	    
	    gl.glEnable(GL.GL_CULL_FACE);
	    
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL.GL_LEQUAL);
	    
	    gl.glGenFramebuffers(1, fbo);
	    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo.get(0));
	    
	    gl.glGenTextures(1, color_texture);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, color_texture.get(0));
	    gl.glTexStorage2D(GL.GL_TEXTURE_2D, 9, GL.GL_RGBA8, 512, 512);
	    
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	    
	    gl.glGenTextures(1, depth_texture);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, depth_texture.get(0));
	    gl.glTexStorage2D(GL.GL_TEXTURE_2D, 9, GL4.GL_DEPTH_COMPONENT32F, 512, 512);
	    
	    gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, color_texture.get(0), 0);
	    gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, depth_texture.get(0), 0);
	    
	    gl.glDrawBuffers(1, IntBuffer.wrap(new int[]{GL.GL_COLOR_ATTACHMENT0}));
	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		GL4 gl = theAutoDrawable.getGL().getGL4();
        gl.glDeleteVertexArrays(1, vao);
        gl.glDeleteProgram(program1);
        gl.glDeleteProgram(program2);
        gl.glDeleteBuffers(1, position_buffer);
        gl.glDeleteFramebuffers(1, fbo);
        gl.glDeleteTextures(1, color_texture);
	}

	@Override
	public void reshape(GLAutoDrawable theAutoDrawable, int x, int y,
			int width, int height) {
		this.width = width;
		this.height = height;
		aspect = (float)height/width;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
