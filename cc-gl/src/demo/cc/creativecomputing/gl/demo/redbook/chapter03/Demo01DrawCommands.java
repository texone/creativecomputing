package cc.creativecomputing.gl.demo.redbook.chapter03;

import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMatrix4x4;

public class Demo01DrawCommands extends CCGL4Adapter{
	
	private float aspect;
	
	private GLShaderProgram _myRenderProgram;
	
	private GLVertexArray _myVAO;
	
	@Override
	public void init(GLGraphics g) {
		_myRenderProgram = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this,"draw_commands_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this,"draw_commands_frag.glsl"))
		);
//		render_prog = _myRenderProgram.id();

		_myRenderProgram.use();
	    
	    // Set up the element array buffer
		GLBuffer myEBO = new GLBuffer(GLBufferTarget.ELEMENT_ARRAY);
	    myEBO.bind();
	    myEBO.datavi(0,1,2);

	    GLBuffer myVertices = new GLBuffer(GLBufferTarget.ARRAY);
	    myVertices.bind();
	    myVertices.datavf(
	    	-1.0f, -1.0f, 0.0f, 1.0f,
		     1.0f, -1.0f, 0.0f, 1.0f,
		    -1.0f,  1.0f, 0.0f, 1.0f,
		    -1.0f, -1.0f, 0.0f, 1.0f
	    );
	    
	    GLBuffer myColors = new GLBuffer(GLBufferTarget.ARRAY);
	    myColors.bind();
	    myColors.datavf(
	    	1.0f, 1.0f, 1.0f, 1.0f,
	    	1.0f, 1.0f, 0.0f, 1.0f,
	    	1.0f, 0.0f, 1.0f, 1.0f,
	    	0.0f, 1.0f, 1.0f, 1.0f
	    );
	    
	    int myVertexPositionLocation = _myRenderProgram.attribLocation("position");
	    int myVertexColorLocation = _myRenderProgram.attribLocation("color");
	    
	    // Set up the vertex attributes
	    _myVAO = new GLVertexArray();
	    _myVAO.bind();
	    
	    myVertices.bind();
	    _myVAO.attributes(myVertexPositionLocation, 4);
	    _myVAO.enableVertexAttribArray(myVertexPositionLocation);
	    
	    myColors.bind();
	    _myVAO.attributes(myVertexColorLocation, 4);
	    _myVAO.enableVertexAttribArray(myVertexColorLocation);

		myEBO.bind();
		
	    _myVAO.unbind();
	    myEBO.unbind();
	}
	
	CCMatrix4x4 projection_matrix = new CCMatrix4x4();
	
	@Override
	public void reshape(GLGraphics theG) {
	    aspect = (float)theG.height() / (float)theG.width();
		projection_matrix = CCMatrix4x4.createFrustum(-1f,1f,-aspect,aspect,1f,500f);
	}
	
	@Override
	public void display(GLGraphics g) {

		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);
		g.cullFace();
		g.depthTest();

		_myRenderProgram.use();
		_myRenderProgram.uniformMatrix4f("projection_matrix", projection_matrix);
		
		_myVAO.bind();
		
	    // Draw Arrays...
		CCMatrix4x4 model_matrix = new CCMatrix4x4();
		model_matrix.applyTranslationPost(-3.0f, 0.0f, -5.0f);
		_myRenderProgram.uniformMatrix4f("model_matrix",model_matrix);
		_myVAO.drawArrays(GLDrawMode.TRIANGLES, 0, 3);
		
	    // DrawElements
		model_matrix.setIdentity();
		model_matrix.applyTranslationPost(-1.0f, 0.0f, -5.0f);
		_myRenderProgram.uniformMatrix4f("model_matrix", model_matrix);
		_myVAO.drawElements(GLDrawMode.TRIANGLES, 3, GLDataType.UNSIGNED_INT, 0);
		
	    // DrawElementsBaseVertex
		model_matrix.setIdentity();
	    model_matrix.applyTranslationPost(1.0f, 0.0f, -5.0f);
	    _myRenderProgram.uniformMatrix4f("model_matrix", model_matrix);
	    _myVAO.drawElements(GLDrawMode.TRIANGLES, 3, GLDataType.UNSIGNED_INT, 1);
		
	    // DrawArraysInstanced
		model_matrix.setIdentity();
	    model_matrix.applyTranslationPost(3.0f, 0.0f, -5.0f);
	    _myRenderProgram.uniformMatrix4f("model_matrix", model_matrix);
	    _myVAO.drawArraysInstanced(GLDrawMode.TRIANGLES, 0, 3, 10);
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo01DrawCommands());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
