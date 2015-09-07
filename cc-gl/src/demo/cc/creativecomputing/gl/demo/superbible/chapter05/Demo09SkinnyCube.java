package cc.creativecomputing.gl.demo.superbible.chapter05;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
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

public class Demo09SkinnyCube extends CCGL4Adapter{
	
	private float aspect;
	
	private GLShaderProgram _myRenderProgram;
	
	private GLVertexArray _myVAO;
	
	@Override
	public void init(GLGraphics g) {
		_myRenderProgram = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this,"Demo09SpinnyCube_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this,"Demo09SpinnyCube_frag.glsl"))
		);
		_myRenderProgram.use();
	    
	    GLBuffer myVertices = new GLBuffer(GLBufferTarget.ARRAY);
	    myVertices.bind();
	    myVertices.datavf(
	    	-0.25f,  0.25f, -0.25f,
	    	-0.25f, -0.25f, -0.25f, 
	    	 0.25f, -0.25f, -0.25f,

	    	 0.25f, -0.25f, -0.25f,
	    	 0.25f,  0.25f, -0.25f, 
	    	-0.25f,  0.25f, -0.25f,

	    	 0.25f, -0.25f, -0.25f, 
	    	 0.25f, -0.25f,  0.25f, 
	    	 0.25f,  0.25f, -0.25f,

	    	 0.25f, -0.25f,  0.25f, 
	    	 0.25f,  0.25f,  0.25f, 
	    	 0.25f,  0.25f, -0.25f,

	    	 0.25f, -0.25f,  0.25f, 
	    	-0.25f, -0.25f,  0.25f, 
	    	 0.25f,  0.25f,  0.25f,

	    	-0.25f, -0.25f,  0.25f, 
	    	-0.25f,  0.25f,  0.25f, 
	    	 0.25f,  0.25f,  0.25f,

	    	-0.25f, -0.25f,  0.25f, 
	    	-0.25f, -0.25f, -0.25f, 
	    	-0.25f,  0.25f,  0.25f,

	    	-0.25f, -0.25f, -0.25f, 
	    	-0.25f,  0.25f, -0.25f, 
	    	-0.25f,  0.25f,  0.25f,

	    	-0.25f, -0.25f,  0.25f, 
	    	 0.25f, -0.25f,  0.25f, 
	    	 0.25f, -0.25f, -0.25f,

	    	 0.25f, -0.25f, -0.25f, 
	    	-0.25f, -0.25f, -0.25f, 
	    	-0.25f, -0.25f,  0.25f,

	    	-0.25f,  0.25f, -0.25f, 
	    	 0.25f,  0.25f, -0.25f, 
	    	 0.25f,  0.25f,  0.25f,

	    	 0.25f,  0.25f,  0.25f, 
	    	-0.25f,  0.25f,  0.25f, 
	    	-0.25f,  0.25f, -0.25f
	    );
	    
	    
	    
	    int myVertexPositionLocation = _myRenderProgram.attribLocation("position");
	    
	    // Set up the vertex attributes
	    _myVAO = new GLVertexArray();
	    _myVAO.bind();
	    
	    myVertices.bind();
	    _myVAO.attributes(myVertexPositionLocation, 3);
	    _myVAO.enableVertexAttribArray(myVertexPositionLocation);
	    _myVAO.unbind();
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
		
		for (int i = 0; i < 24; i++) {
			//float f = (float) _myCurrentTime * 0.3f;
            float f = i*20 + _myTime * 0.3f;
		
			CCMatrix4x4 myProjectionMatrix = CCMatrix4x4.createFrustum(-1f,1f,-aspect,aspect,1f,500f);
			_myRenderProgram.uniformMatrix4f("projection_matrix", myProjectionMatrix);
	
			CCMatrix4x4 myModelMatrix = new CCMatrix4x4();
			myModelMatrix.applyTranslationPre(0.0f, 0.0f, -5.0f);
			myModelMatrix.applyRotationY(_myTime * CCMath.PI);
			myModelMatrix.applyRotationZ(_myTime * CCMath.TWO_PI);
			myModelMatrix.applyTranslationPost(
				CCMath.sin(CCMath.radians(2.1f) * f) * 2.0f,
				CCMath.cos(CCMath.radians(1.7f) * f) * 2.0f,
				CCMath.sin(CCMath.radians(1.3f) * f) * CCMath.cos(CCMath.radians(1.5f) * f) * 2.0f
			);
			_myRenderProgram.uniformMatrix4f("model_matrix",myModelMatrix);
			
			_myVAO.bind();
		    _myVAO.drawArrays(GLDrawMode.TRIANGLES, 0, 36);
		}
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo09SkinnyCube());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
