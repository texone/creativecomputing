package opengl4experiments.redbook;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.rotate;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import opengl4experiments.bareboneApp.BareboneGLAbstractExperiment;
import opengl4experiments.utils.Camera;
import opengl4experiments.utils.PlyLoader;
import opengl4experiments.utils.ShaderLoader;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

public class Example_ch03_Instancing_A extends BareboneGLAbstractExperiment {
	
	private float[] model_matrix = loadIdentity();
	
	private float width, height, aspect;
	
	private int model_matrix_loc, view_matrix_loc, projection_matrix_loc;
	
	private int render_prog;
	
	private int pmouseX, pmouseY;
	private int faceCount;
	private IntBuffer ebo = IntBuffer.allocate(1);
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vbo = IntBuffer.allocate(1);
	private IntBuffer weight_vbo = IntBuffer.allocate(1);
	private IntBuffer colour_vbo = IntBuffer.allocate(1);
	
	private Camera myCam;
	
	private static final int INSTANCE_COUNT = 20;
	
	int tick = 0;
	
	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4)theAutoDrawable.getGL();
		
		tick++;
		float q = 0.0f;
				
		float[] weights = new float[INSTANCE_COUNT*4];
		
		float t = tick*0.0001f;
		
		for (int i = 0; i < weights.length; i+= 4) {
	        float a = i / 4.0f;
	        float b = i / 5.0f;
	        float c = i / 6.0f;
	        
	        weights[i+0] = 0.5f * ((float)Math.sin(t * 6.28318531f * 8.0f + a) + 1.0f);
	        weights[i+1] = 0.5f * ((float)Math.sin(t * 6.28318531f * 26.0f + b) + 1.0f);
	        weights[i+2] = 0.5f * ((float)Math.sin(t * 6.28318531f * 21.0f + c) + 1.0f);
	        weights[i+3] = 0.5f * ((float)Math.sin(t * 6.28318531f * 13.0f + a + b) + 1.0f);
		}
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, weight_vbo.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(weights), asBuffer(weights), GL.GL_DYNAMIC_DRAW);
		
		gl.glClearColor(0.3f, 0.3f, 0.3f, 1f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		model_matrix = loadIdentity();
		
		model_matrix = rotate(model_matrix, t * 360.0f * 0.4f + (0.0001f) * 29.0f, 0.0f, 1.0f, 0.0f);
		model_matrix = rotate(model_matrix, t * 360.0f * 0.2f + (0.0001f) * 35.0f, 0.0f, 0.0f, 1.0f);
		model_matrix = rotate(model_matrix, t * 360.0f * 0.3f + (0.0001f) * 67.0f, 0.0f, 1.0f, 0.0f);
		model_matrix = translate(model_matrix, 0.0f, 0.0f, -0.2f);
		//model_matrix = scale(model_matrix, 0.01f);
		
		gl.glUseProgram(render_prog);
		gl.glUniformMatrix4fv(model_matrix_loc, 1, false, model_matrix, 0);
		gl.glUniformMatrix4fv(view_matrix_loc, 1, false, myCam.getViewMatrix(), 0);
		gl.glUniformMatrix4fv(projection_matrix_loc, 1, false, myCam.getProjectionMatrix(), 0);
		
		gl.glBindVertexArray(vao.get(0));
		
        gl.glDrawElementsInstanced(GL.GL_TRIANGLES, faceCount*3, GL.GL_UNSIGNED_INT, 0, INSTANCE_COUNT);
        //gl.glDrawElements(GL.GL_TRIANGLES, faceCount*3, GL.GL_UNSIGNED_INT, 0);
        
        gl.glBindVertexArray(0);
	}

	@Override
	public void dispose(GLAutoDrawable theAutoDrawable) {
		/*
		GL4 gl = theAutoDrawable.getGL().getGL4();
	    gl.glUseProgram(0);
	    gl.glDeleteProgram(render_prog);
	    gl.glDeleteVertexArrays(1, vao);
	    gl.glDeleteBuffers(1, vbo);
	    gl.glDeleteBuffers(1, weight_vbo);
	    gl.glDeleteBuffers(1, colour_vbo);
	    gl.glDeleteBuffers(1, ebo);
	    */
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL4 gl = drawable.getGL().getGL4();
		try {
			render_prog = ShaderLoader.loadAndCompileShader(gl, "shader/Example_ch03_Instancing_A/instancing.vert", "shader/Example_ch03_Instancing_A/instancing.frag");
//			render_prog = ShaderLoader.loadAndCompileShader(gl, "shader/default.vert", "shader/default.frag");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		gl.glUseProgram(render_prog);
		
		PlyLoader plyModel = new PlyLoader("data/models/dragon_recon/dragon_vrip_res2.ply");
		//PlyLoader plyModel = new PlyLoader("data/models/bunny_recon/bun_zipper.ply");
		
		IntBuffer indexBuffer = plyModel.getIndices();
		FloatBuffer vertexBuffer = plyModel.getVertices();
		FloatBuffer normalBuffer = plyModel.getNormals();
		faceCount = indexBuffer.capacity()/3;
						
		gl.glGenBuffers(1, ebo);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(indexBuffer), indexBuffer, GL.GL_STATIC_DRAW);
		
		projection_matrix_loc = gl.glGetUniformLocation(render_prog, "projection_matrix");
		view_matrix_loc = gl.glGetUniformLocation(render_prog, "view_matrix");
		model_matrix_loc = gl.glGetUniformLocation(render_prog, "model_matrix");
	    int vertex_position_loc = gl.glGetAttribLocation(render_prog, "vertex_position");
	    int vertex_normal_loc = gl.glGetAttribLocation(render_prog, "vertex_normal");
		
	    float[] colours = new float[INSTANCE_COUNT*4];
	    
	    for (int i = 0; i < colours.length; i = i+4)
	    {
	        float a = i / 4.0f;
	        float b = i / 5.0f;
	        float c = i / 6.0f;

	        colours[i+0] = 0.5f * ((float)Math.sin(a + 1.0f) + 1.0f);
	        colours[i+1] = 0.5f * ((float)Math.sin(b + 2.0f) + 1.0f);
	        colours[i+2] = 0.5f * ((float)Math.sin(c + 3.0f) + 1.0f);
	        colours[i+3] = 1.0f;
	    }
   
	    //it is important to start with the VAO before defining and binding any attributes
		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.get(0));
		
		gl.glGenBuffers(1,vbo);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(vertexBuffer) + sizeof(normalBuffer), null, GL.GL_STATIC_DRAW);
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, sizeof(vertexBuffer), vertexBuffer);
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, sizeof(vertexBuffer), sizeof(normalBuffer), normalBuffer);
	    gl.glVertexAttribPointer(vertex_position_loc, 3, GL.GL_FLOAT, false, 0, 0);
	    gl.glVertexAttribPointer(vertex_normal_loc, 3, GL.GL_FLOAT, false, 0, sizeof(vertexBuffer));
		
	    gl.glEnableVertexAttribArray(vertex_position_loc);
	    gl.glEnableVertexAttribArray(vertex_normal_loc);
	    
	    // Create and allocate the VBO to hold the weights
	    // Notice that we use the 'colors' array as the initial data, but only because
	    // we know it's the same size.
	    gl.glGenBuffers(1, weight_vbo);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, weight_vbo.get(0));
	    gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(colours), asBuffer(colours), GL.GL_DYNAMIC_DRAW);
	    //gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(colours), null, GL.GL_DYNAMIC_DRAW);

	    //3 is the position of the attribute 'instance_weights', 4 is the component count
	    // Here is the instanced vertex attribute - set the divisor
	    int instance_weights_loc = gl.glGetAttribLocation(render_prog, "instance_weights");
	    gl.glVertexAttribDivisor(instance_weights_loc, 1);
	    // It's otherwise the same as any other vertex attribute - set the pointer and enable it
	    gl.glVertexAttribPointer(instance_weights_loc, 4, GL.GL_FLOAT, false, 0, 0);
	    gl.glEnableVertexAttribArray(instance_weights_loc);
	    
	    
	    // Same with the instance color array
	    gl.glGenBuffers(1, colour_vbo);
	    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colour_vbo.get(0));
	    gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(colours), asBuffer(colours), GL.GL_STATIC_DRAW);
	    
	    
	    int instance_colours_loc = gl.glGetAttribLocation(render_prog, "instance_colour");
	    //the first argument is the location
	    gl.glVertexAttribDivisor(instance_colours_loc, 1);
	    //4 is the position of the attribute 'instance_colour', 4 is the component count
	    gl.glVertexAttribPointer(instance_colours_loc, 4, GL.GL_FLOAT, false, 0, 0);
	    gl.glEnableVertexAttribArray(instance_colours_loc);
	    
	    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
	    
		myCam = new Camera(0f,0f,-0.5f,1f);
	}

	@Override
	public void reshape(GLAutoDrawable theAutoDrawable, int x, int y,
			int w, int h) {
		width = w;
		height = h;
		aspect = height /width;
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
