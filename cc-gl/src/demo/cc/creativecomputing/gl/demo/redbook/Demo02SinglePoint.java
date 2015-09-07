package cc.creativecomputing.gl.demo.redbook;

import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;

import java.nio.file.Paths;

import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLBuffer.GLDataAccesFrequency;
import cc.creativecomputing.gl4.GLBuffer.GLDataAccesNature;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLVertexArray;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.glu.GLU;

public class Demo02SinglePoint extends CCGL4Adapter{
	
	private float aspect;
//	int render_prog;
//	private IntBuffer vao = IntBuffer.allocate(1);
	
	private GLShaderProgram _myRenderProgram;
	
	private GLBuffer _myEBO;
	private GLBuffer _myVBO;
	
	private GLVertexArray _myVAO;
	
	private int render_model_matrix_loc;
	private int render_projection_matrix_loc;
	
	@Override
	public void init(GLGraphics g) {
		
		_myRenderProgram = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, Paths.get("shader/primitive_restart.vs.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, Paths.get("shader/primitive_restart.fs.glsl"))
		);
//		render_prog = _myRenderProgram.id();

		_myRenderProgram.use();
		
		render_model_matrix_loc = _myRenderProgram.uniformLocation("model_matrix");
		render_projection_matrix_loc = _myRenderProgram.uniformLocation("projection_matrix");
		
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
	    short[] vertex_indices = {
	        0, 1, 2
	    };
	    
	    // Set up the element array buffer
	    _myEBO = new GLBuffer(GLBufferTarget.ELEMENT_ARRAY);
	    _myEBO.bind();
	    _myEBO.data(vertex_indices, GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);

	    
	    
	    _myVBO = new GLBuffer(GLBufferTarget.ARRAY);
	    _myVBO.bind();
	    _myVBO.allocate(sizeof(vertex_positions) + sizeof(vertex_colors));
	    _myVBO.subData(0, vertex_positions);
	    _myVBO.subData(sizeof(vertex_positions), vertex_colors);
	    
	    int vertex_position_loc = _myRenderProgram.attribLocation("position");
	    int vertex_color_loc = _myRenderProgram.attribLocation("color");
	    
	    // Set up the vertex attributes
	    _myVAO = new GLVertexArray();
	    _myVAO.bind();
	    
	    //attrib loc, attrib comp. count, ~ type, normalized, stride, offset
	    _myVAO.attributes(vertex_position_loc, 4, GLDataType.FLOAT, false, 0, 0);
	    _myVAO.attributes(vertex_color_loc, 4, GLDataType.FLOAT, false, 0, sizeof(vertex_positions));
	    GLGraphics.currentGL().glEnableVertexAttribArray(vertex_position_loc);
	    GLGraphics.currentGL().glEnableVertexAttribArray(vertex_color_loc);
	}
	
	@Override
	public void display(GLGraphics g) {
		g.pointSize(40);
	    aspect = (float)g.height() / (float)g.width();
		GL4 gl = GLU.getCurrentGL().getGL4();
		
		GLGraphics.currentGL().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f,0f,0f,0f);

		_myRenderProgram.use();
		
		float[] model_matrix;
		float[] projection_matrix = loadIdentity();
		projection_matrix = frustum(-1f,1f,-aspect,aspect,1f,500f);
		gl.glUniformMatrix4fv(render_projection_matrix_loc, 1, false, projection_matrix, 0);
		
		_myVAO.bind();
		
		
	    // Draw Arrays...
		model_matrix = loadIdentity();
		model_matrix = translate(model_matrix, -3.0f, 0.0f, -5.0f);
		gl.glUniformMatrix4fv(render_model_matrix_loc, 1, false, model_matrix, 0);
		_myVAO.drawArrays(GLDrawMode.POINTS, 0, 3);
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo02SinglePoint());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
