package cc.creativecomputing.gl.demo.redbook.chapter03;

import cc.creativecomputing.app.modules.CCAnimator;
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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;

public class Demo03InstancedRendering extends CCGL4Adapter{
	
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
	    myEBO.datavi(
	    	0, 1, 2, 3, 6, 7, 4, 5,         // First strip
	    	0xFFFF,                         // <<-- This is the restart index
	    	2, 6, 0, 4, 1, 5, 3, 7          // Second strip);
	    );
	    
	    GLBuffer myVertices = new GLBuffer(GLBufferTarget.ARRAY);
	    myVertices.bind();
	    myVertices.datavf(
	    	-1.0f, -1.0f, -1.0f, 1.0f,
	    	-1.0f, -1.0f,  1.0f, 1.0f,
	    	-1.0f,  1.0f, -1.0f, 1.0f,
	    	-1.0f,  1.0f,  1.0f, 1.0f,
	    	 1.0f, -1.0f, -1.0f, 1.0f,
	    	 1.0f, -1.0f,  1.0f, 1.0f,
	    	 1.0f,  1.0f, -1.0f, 1.0f,
	    	 1.0f,  1.0f,  1.0f, 1.0f
	    );
	    
	    GLBuffer myColors = new GLBuffer(GLBufferTarget.ARRAY);
	    myColors.bind();
	    myColors.datavf(
	    	1.0f, 1.0f, 1.0f, 1.0f,
	    	1.0f, 1.0f, 0.0f, 1.0f,
	    	1.0f, 0.0f, 1.0f, 1.0f,
	    	1.0f, 0.0f, 0.0f, 1.0f,
	    	0.0f, 1.0f, 1.0f, 1.0f,
	    	0.0f, 1.0f, 0.0f, 1.0f,
	    	0.0f, 0.0f, 1.0f, 1.0f,
	    	0.5f, 0.5f, 0.5f, 1.0f
	    );
	    
	    int myVertexPositionLocation = _myRenderProgram.attribLocation("position");
	    int myVertexColorLocation = _myRenderProgram.attribLocation("color");
	    
	    // Set up the vertex attributes
	    _myVAO = new GLVertexArray();
	    _myVAO.bind();
	    
	    myVertices.bind();
	    _myVAO.attributes(myVertexPositionLocation, 4);
	    
	    myColors.bind();
	    _myVAO.attributes(myVertexColorLocation, 4);

		myEBO.bind();
		
	    _myVAO.enableVertexAttribArray(myVertexPositionLocation);
	    _myVAO.enableVertexAttribArray(myVertexColorLocation);
	    _myVAO.unbind();
	    myEBO.unbind();
	}
	
	private float _myTime;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime() * 0.1f;
	}
	
	@Override
	public void display(GLGraphics g) {
	    aspect = (float)g.height() / (float)g.width();

		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);
		g.cullFace();
		g.depthTest();

		_myRenderProgram.use();
		
		CCMatrix4x4 myProjectionMatrix = CCMatrix4x4.createFrustum(-1f,1f,-aspect,aspect,1f,500f);
		_myRenderProgram.uniformMatrix4f("projection_matrix", myProjectionMatrix);

		CCMatrix4x4 myModelMatrix = new CCMatrix4x4();
		myModelMatrix.applyTranslationPre(0.0f, 0.0f, -5.0f);
		myModelMatrix.applyRotationY(_myTime * CCMath.PI);
		myModelMatrix.applyRotationZ(_myTime * CCMath.TWO_PI);
		_myRenderProgram.uniformMatrix4f("model_matrix",myModelMatrix);
		
		_myVAO.bind();
		
	    g.primitiveRestart(0xFFFF);
		_myVAO.drawElements(GLDrawMode.TRIANGLE_STRIP, 17, GLDataType.UNSIGNED_INT);
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo03InstancedRendering());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
