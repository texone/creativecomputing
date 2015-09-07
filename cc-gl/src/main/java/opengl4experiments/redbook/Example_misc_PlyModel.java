package opengl4experiments.redbook;

import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;

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

public class Example_misc_PlyModel extends BareboneGLAbstractExperiment {
	
	private float[] model_matrix = loadIdentity();
	
	private float width, height, aspect;
	
	private int view_matrix_loc, model_matrix_loc, projection_matrix_loc;
	
	private int render_prog;
	
	private int pmouseX, pmouseY;
	private int faceCount;
	private IntBuffer ebo = IntBuffer.allocate(1);
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vbo = IntBuffer.allocate(1);
	
	private Camera myCam;	
	
	@Override
	public void display(GLAutoDrawable theAutoDrawable) {
		// TODO Auto-generated method stub
		GL4 gl = (GL4)theAutoDrawable.getGL();
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		gl.glBindVertexArray(vao.get(0));
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		
		gl.glUniformMatrix4fv(model_matrix_loc, 1, false, model_matrix, 0);
		gl.glUniformMatrix4fv(view_matrix_loc, 1, false, myCam.getViewMatrix(), 0);
		gl.glUniformMatrix4fv(projection_matrix_loc, 1, false, myCam.getProjectionMatrix(), 0);
		
		gl.glDrawElements(GL.GL_TRIANGLES, faceCount*3, GL.GL_UNSIGNED_INT, 0);

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
		
		PlyLoader plyModel = new PlyLoader("data/models/dragon_recon/dragon_vrip_res2.ply");
		//PlyLoader plyModel = new PlyLoader("data/models/bunny_recon/bun_zipper.ply");
		
		IntBuffer indexBuffer = plyModel.getIndices();
		FloatBuffer vertexBuffer = plyModel.getVertices();
		FloatBuffer normalBuffer = plyModel.getNormals();
		faceCount = indexBuffer.capacity()/3;
						
		gl.glGenBuffers(1, ebo);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(indexBuffer), indexBuffer, GL.GL_STATIC_DRAW);
		
		model_matrix_loc = gl.glGetUniformLocation(render_prog, "model_matrix");
		projection_matrix_loc = gl.glGetUniformLocation(render_prog, "projection_matrix");
		view_matrix_loc = gl.glGetUniformLocation(render_prog, "view_matrix");
	    int vertex_position_loc = gl.glGetAttribLocation(render_prog, "vertex_position");
	    int vertex_normal_loc = gl.glGetAttribLocation(render_prog, "vertex_normal");
		
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
