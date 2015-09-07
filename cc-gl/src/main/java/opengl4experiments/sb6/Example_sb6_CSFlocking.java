package opengl4experiments.sb6;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.multiply;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;
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
import com.jogamp.opengl.GLPipelineFactory;

public class Example_sb6_CSFlocking extends BareboneGLAbstractExperiment {

	private float[] model_matrix = loadIdentity();
	// private float[] mvp_matrix = loadIdentity();

	private int width, height;
	private float aspect;

	private int view_matrix_loc, model_matrix_loc, projection_matrix_loc;

	private int render_prog;

	private int pmouseX, pmouseY;

	// /////
	public static final int WORKGROUP_SIZE = 256;
	public static final int NUM_WORKGROUPS = 64;
	public static final int FLOCK_SIZE = (NUM_WORKGROUPS * WORKGROUP_SIZE);
	private int frame_index = 0;
	private int flock_update_program = -1;
	private int flock_render_program = -1;
	private int uniforms_update_goal;
	private int uniforms_render_mvp;
	private IntBuffer flock_buffer, geometry_buffer, flock_render_vao;
	private FloatBuffer flock_member = FloatBuffer.allocate(8);

	// private FloatBuffer geometry_buffer;
	// /////

	@Override
	public void display(GLAutoDrawable theAutoDrawable) {

		float t = frame_index * 0.01f;
		GL4 gl = theAutoDrawable.getGL().getGL4();

		gl.glUseProgram(flock_update_program);

		float[] goal = new float[] { (float) Math.sin(t * 0.34f)*35.0f,
				(float) Math.cos(t * 0.29f)*25.0f,
				(float) Math.sin(t * 0.12f) * (float) Math.cos(t * 0.5f)*60f};
		//goal = multiplyVec(goal, new float[]{35.0f, 25.0f, 60.0f});


		gl.glUniform3fv(uniforms_update_goal, 1, asBuffer(goal));

		gl.glBindBufferBase(GL4.GL_SHADER_STORAGE_BUFFER, 0,
				flock_buffer.get(frame_index % 2));
		gl.glBindBufferBase(GL4.GL_SHADER_STORAGE_BUFFER, 1,
				flock_buffer.get((frame_index + 1) % 2));

		gl.glDispatchCompute(NUM_WORKGROUPS, 1, 1);

		gl.glViewport(0, 0, width, height);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, asBuffer(new float[] { 0f, 0f, 0f,
				1f }));
		gl.glClearBufferfv(GL4.GL_DEPTH, 0, asBuffer(new float[] { 1f }));

		gl.glUseProgram(flock_render_program);

		float[] mv_matrix = loadIdentity();
		mv_matrix = translate(mv_matrix, 0f, 0f, -200f);
		float[] proj_matrix = frustum(-1,1,-aspect,aspect,1f,500f);

		float[] mvp = multiply(proj_matrix, mv_matrix);

		gl.glUniformMatrix4fv(uniforms_render_mvp, 1, false, mvp, 0);

		gl.glBindVertexArray(flock_render_vao.get(frame_index % 2));

		gl.glDrawArraysInstanced(GL.GL_TRIANGLE_STRIP, 0, 8, FLOCK_SIZE);

		frame_index++;

	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();

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

		float[] geometry = new float[] {
				// Positions
				-5.0f, 1.0f, 0.0f, -1.0f, 1.5f, 0.0f, -1.0f, 1.5f, 7.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 1.0f, 1.5f, 0.0f, 1.0f, 1.5f,
				7.0f, 5.0f, 1.0f,
				0.0f,

				// Normals
				0.0f, 0f, 0f, 0.0f, 0f, 0f, 0.107f, -0.859f, 0.00f, 0.832f,
				0.554f, 0.00f, -0.59f, -0.395f, 0.00f, -0.832f, 0.554f, 0.00f,
				0.295f, -0.196f, 0.00f, 0.124f, 0.992f, 0.00f, };

		loadShaders(gl);

		flock_buffer = IntBuffer.allocate(2);
		flock_render_vao = IntBuffer.allocate(2);
		geometry_buffer = IntBuffer.allocate(1);

		// TODO: check if "32" is correct!!!
		// int flock_members_size = FLOCK_SIZE * (6*Buffers.SIZEOF_FLOAT +
		// 2*32);
		// int flock_members_size = FLOCK_SIZE * (6*Buffers.SIZEOF_FLOAT);
		int flock_members_size = FLOCK_SIZE * (int) sizeof(flock_member);

		gl.glGenBuffers(2, flock_buffer);
		gl.glBindBuffer(GL4.GL_SHADER_STORAGE_BUFFER, flock_buffer.get(0));
		gl.glBufferData(GL4.GL_SHADER_STORAGE_BUFFER, flock_members_size, null,
				GL4.GL_DYNAMIC_COPY);
		gl.glBindBuffer(GL4.GL_SHADER_STORAGE_BUFFER, flock_buffer.get(1));
		gl.glBufferData(GL4.GL_SHADER_STORAGE_BUFFER, flock_members_size, null,
				GL4.GL_DYNAMIC_COPY);
		gl.glGenBuffers(1, geometry_buffer);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, geometry_buffer.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(geometry),
				asBuffer(geometry), GL.GL_STATIC_DRAW);

		gl.glGenVertexArrays(2, flock_render_vao);

		for (int i = 0; i < 2; i++) {
			gl.glBindVertexArray(flock_render_vao.get(i));
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, geometry_buffer.get(0));
			gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
			gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, (8 * 3 * Buffers.SIZEOF_FLOAT));
			// gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0,
			// sizeof(geometry)/2);

			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, flock_buffer.get(i));
			gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false,
					(int) sizeof(flock_member), 0);
			gl.glVertexAttribPointer(3, 3, GL.GL_FLOAT, false,
					(int) sizeof(flock_member), (4 * Buffers.SIZEOF_FLOAT));
			gl.glVertexAttribDivisor(2, 1);
			gl.glVertexAttribDivisor(3, 1);

			gl.glEnableVertexAttribArray(0);
			gl.glEnableVertexAttribArray(1);
			gl.glEnableVertexAttribArray(2);
			gl.glEnableVertexAttribArray(3);
		}

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, flock_buffer.get(0));

		// TODO!!!
		// gl.glMapBufferRange(GL.GL_ARRAY_BUFFER, 0, flock_members_size,
		// GL.GL_MAP_WRITE_BIT | GL.GL_MAP_INVALIDATE_BUFFER_BIT);
		// don't forget to call glUnmapBuffer afterwards
		float[] flock_member_data = new float[6 * FLOCK_SIZE];
		for (int i = 0; i < flock_member_data.length; i += 6) {
			flock_member_data[i + 0] = (float) Math.random() - 150f;
			flock_member_data[i + 1] = (float) Math.random() - 150f;
			flock_member_data[i + 2] = (float) Math.random() - 150f;
			flock_member_data[i + 3] = (float) Math.random() - 0.5f;
			flock_member_data[i + 4] = (float) Math.random() - 0.5f;
			flock_member_data[i + 5] = (float) Math.random() - 0.5f;
		}

		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(flock_member_data),
				asBuffer(flock_member_data), GL4.GL_DYNAMIC_COPY);

		 gl.glEnable(GL.GL_DEPTH_TEST);
		 gl.glDepthFunc(GL.GL_LEQUAL);
	}

	private void loadShaders(GL4 gl) {
		if (flock_update_program != -1)
			gl.glDeleteProgram(flock_update_program);

		if (flock_render_program != -1)
			gl.glDeleteProgram(flock_render_program);

		try {
			flock_update_program = loadAndCompileShader(gl,
					"shader/Example_sb6_CSFlocking/flocking.cs.glsl");
			flock_render_program = loadAndCompileShader(gl,
					"shader/Example_sb6_CSFlocking/render.vs.glsl",
					"shader/Example_sb6_CSFlocking/render.fs.glsl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		uniforms_update_goal = gl.glGetUniformLocation(flock_update_program,
				"goal");
		uniforms_render_mvp = gl.glGetUniformLocation(flock_render_program,
				"mvp");
	}

	@Override
	public void reshape(GLAutoDrawable theAutoDrawable, int x, int y, int w,
			int h) {
		width = w;
		height = h;
		aspect = (float) height / width;
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
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
