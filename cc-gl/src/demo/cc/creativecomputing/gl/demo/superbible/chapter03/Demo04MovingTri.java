package cc.creativecomputing.gl.demo.superbible.chapter03;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl.app.container.GLContainerType;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLBuffer.GLDataAccesFrequency;
import cc.creativecomputing.gl4.GLBuffer.GLDataAccesNature;
import cc.creativecomputing.gl4.GLBufferUtil;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class Demo04MovingTri extends CCGL4Adapter{
	
	private GLShaderProgram _myShader;
	
	private GLBuffer _myVertexData;
	private GLVertexArray _myVertexArray;
	
	private float _myCurrentTime = 0;
	
	@Override
	public void init(GLGraphics g) {
		_myShader = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "Demo04MovingTri_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "Demo04MovingTri_frag.glsl"))
		);
		
		// Set up the element array buffer
		_myVertexData = new GLBuffer(GLBufferTarget.ARRAY);
		_myVertexData.bind();
		_myVertexData.data(GLBufferUtil.wrapParameters(0f,0f,0f,0f,0f,0f,0f,0f,0f), GLDataAccesFrequency.STATIC, GLDataAccesNature.DRAW);
	    
		_myVertexArray = new GLVertexArray();//attrib loc, attrib comp. count, ~ type, normalized, stride, offset
		_myVertexArray.bind();
		_myVertexArray.attributes(0, 4);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myCurrentTime += theAnimator.deltaTime();
	}
	
	@Override
	public void display(GLGraphics g) {
        g.clearBufferfv(GLColorBuffer.COLOR,  0, 0.0f, 0.25f, 0.0f, 1.0f);
        g.clearDepthBuffer(1f);

        g.vertexAttrib4f(0, CCMath.sin(_myCurrentTime) * 0.5f, CCMath.cos(_myCurrentTime) * 0.6f, 0f, 0f);
        
        _myShader.use();
        _myVertexArray.bind();
        _myVertexArray.drawArrays(GLDrawMode.TRIANGLES, 0, 3);
	}

	public static void main(String[] args) {
		
		CCGL4Application myAppManager = new CCGL4Application(new Demo04MovingTri());
		
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		myAppManager.glcontext().title = "OpenGL SuperBible - Moving Triangle";
		myAppManager.glcontext().containerType = GLContainerType.NEWT;
		myAppManager.glcontext().size(800, 800);
		
		myAppManager.start();
	}
}
