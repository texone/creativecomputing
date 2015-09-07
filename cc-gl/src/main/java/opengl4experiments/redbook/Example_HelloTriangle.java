package opengl4experiments.redbook;

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

public class Example_HelloTriangle extends BareboneGLAbstractExperiment {

	// mixing redbook with the jogl example:
	// https://raw.github.com/xranby/jogl-demos/master/src/demos/es2/RawGL2ES2demo.java
	// TODO:
	/*
	 * private int shaderProgram; private int vertShader; private int
	 * fragShader;
	 * 
	 * switched to: http://antongerdelan.net/opengl/ !
	 */

	IntBuffer vaoHandles;
	int numVAOs = 1;
	private int vaoVertexIndex = 0;
	private int vaoColorIndex = 1;
	private int numVertices;
	
	IntBuffer triangle_vao;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();

		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
//		gl.glBindVertexArray(vaoHandles.get(vaoVertexIndex));
//		gl.glDrawArrays(GL.GL_TRIANGLES, vaoVertexIndex, numVertices);
		gl.glBindVertexArray(triangle_vao.get(0));
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);
		gl.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("cleanup, remember to release shaders");
		GL4 gl = drawable.getGL().getGL4();
		gl.glUseProgram(0);
		/*
		 * gl.glDetachShader(shaderProgram, vertShader);
		 * gl.glDeleteShader(vertShader); gl.glDetachShader(shaderProgram,
		 * fragShader); gl.glDeleteShader(fragShader);
		 * gl.glDeleteProgram(shaderProgram);
		 */
		System.exit(0);
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = arg0.getGL().getGL4();
		
		int shaderProgram = -1;

		try {
			shaderProgram = ShaderLoader.loadAndCompileShader(gl,
					"shader/passThrough.vert", "shader/passThrough.frag");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (shaderProgram != -1) {
			gl.glUseProgram(shaderProgram);
		}
		

		float[] points = { 0.0f, 0.5f, 0.0f, 0.5f, -0.5f, 0.0f, -0.5f, -0.5f,
				0.0f };

		float[] colours = { 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				1.0f };
		
		IntBuffer points_vbo = IntBuffer.allocate(1);
		gl.glGenBuffers(1, points_vbo);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, points_vbo.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, points.length * Buffers.SIZEOF_FLOAT, Buffers.newDirectFloatBuffer(points), GL.GL_STATIC_DRAW);

		IntBuffer colours_vbo = IntBuffer.allocate(1);
		gl.glGenBuffers(1, colours_vbo);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colours_vbo.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, colours.length * Buffers.SIZEOF_FLOAT, Buffers.newDirectFloatBuffer(colours), GL.GL_STATIC_DRAW);
		
		triangle_vao = IntBuffer.allocate(1);
		gl.glGenVertexArrays(1, triangle_vao);
		gl.glBindVertexArray(triangle_vao.get(0));
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, points_vbo.get(0));
		int vertex_pos = gl.glGetAttribLocation(shaderProgram, "vertex_position");
		gl.glVertexAttribPointer(vertex_pos,3,GL.GL_FLOAT, false, 0, 0);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colours_vbo.get(0));
		int colour_pos = gl.glGetAttribLocation(shaderProgram, "vertex_colour");
		gl.glVertexAttribPointer(colour_pos,3,GL.GL_FLOAT, false, 0, 0);
		
		gl.glEnableVertexAttribArray(0);
		gl.glEnableVertexAttribArray(1);
		
		/*
		
		vaoHandles = IntBuffer.allocate(numVAOs);
		gl.glGenVertexArrays(numVAOs, vaoHandles);

		gl.glGenVertexArrays(numVAOs, vaoHandles);
		gl.glBindVertexArray(vaoHandles.get(vaoVertexIndex));

		numVertices = 6;

		float[] verticesArray = new float[] { -0.90f, -0.90f, // Triangle 1
				0.85f, -0.90f, -0.90f, 0.85f, 0.90f, -0.85f, // Triangle 2
				0.90f, 0.90f, -0.85f, 0.90f };

		// FloatBuffer vertices = FloatBuffer.wrap(verticesArray);
		FloatBuffer vertices = Buffers.newDirectFloatBuffer(verticesArray);

		int numBuffers = 1;
		IntBuffer buffers = IntBuffer.allocate(numBuffers);

		gl.glGenBuffers(numBuffers, buffers);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffers.get(vaoVertexIndex));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity()
				* Buffers.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);
		*/


		
//		int vPosition = 0;
//		int vElementCount = 2; // only x and y
//		gl.glVertexAttribPointer(vPosition, vElementCount, GL.GL_FLOAT,
//				false/* normalized? */, 0 /* stride */, 0 /*
//														 * The bound VBO data
//														 * offset
//														 */);
		//gl.glEnableVertexAttribArray(vPosition);
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		System.out.println("Window resized to width=" + w + " height=" + h);

		// Get gl
		GL4 gl = drawable.getGL().getGL4();

		// Optional: Set viewport
		// Render to a square at the center of the window.
		gl.glViewport((w - h) / 2, 0, h, h);
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
