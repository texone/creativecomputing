package cc.creativecomputing.gl.demo.OGL4ShadingLanguage.chapter01;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
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
import cc.creativecomputing.gl4.GLShaderProgram.GLAttributeInfo;
import cc.creativecomputing.gl4.GLShaderProgram.GLUniformInfo;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;

public class Demo04UniformBlock extends CCGL4Adapter{
	
	private GLShaderProgram _myShader;
	private GLVertexArray _myVertexArray;
	
	@GLUniform(name = "InnerColor")
	private CCColor _myInnerColor;

	@GLUniform(name = "OuterColor")
	private CCColor _myOuterColor;

	@GLUniform(name = "RadiusInner")
	private float _myRadiusInner;

	@GLUniform(name = "RadiusOuter")
	private float _myRadiusOuter;
	
	@Override
	public void init(GLGraphics g) {
		_myShader = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "shader/basic_uniformblock_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "shader/basic_uniformblock_frag.glsl"))
		);
		
		for(int i = 0; i < _myShader.activeAttributes();i++){
			GLAttributeInfo myAttrib = _myShader.activeAttribute(i);
			CCLog.info(myAttrib.name()+":" + myAttrib.location());
		}
		
		for(int i = 0; i < _myShader.activeUniforms();i++){
			GLUniformInfo myUniform = _myShader.activeUniform(i);
			CCLog.info(myUniform.name()+":" + myUniform.location());
		}
		
		GLBuffer myVertexBuffer = new GLBuffer(GLBufferTarget.ARRAY);
		myVertexBuffer.bind();
		myVertexBuffer.datavf(
			-0.8f, -0.8f, 0.0f,
			 0.8f, -0.8f, 0.0f,
			 0.8f,  0.8f, 0.0f,
			-0.8f, -0.8f, 0.0f,
			 0.8f,  0.8f, 0.0f,
			-0.8f,  0.8f, 0.0f
		);
		
		GLBuffer myTexBuffer = new GLBuffer(GLBufferTarget.ARRAY);
		myTexBuffer.bind();
		myTexBuffer.datavf(
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f
		);
		
		_myVertexArray = new GLVertexArray();
		_myVertexArray.bind();
		
		myVertexBuffer.bind();
		_myVertexArray.attributes(0, 3);
		
		myTexBuffer.bind();
		_myVertexArray.attributes(1, 2);
		
		_myVertexArray.enableVertexAttribArray(0);
		_myVertexArray.enableVertexAttribArray(1);
	}
	
	private float _myRotation = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myRotation += theAnimator.deltaTime() * CCMath.PI * 0.1f;
	}
	
	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);
		
		CCMatrix4x4 myMatrix = new CCMatrix4x4();
		myMatrix.applyRotationZ(_myRotation);

		_myShader.use();
		_myShader.uniformMatrix4f("RotationMatrix", myMatrix);
		_myVertexArray.bind();
		_myVertexArray.drawArrays(GLDrawMode.TRIANGLES, 0, 6);
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo04UniformBlock());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
