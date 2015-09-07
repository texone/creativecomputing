package cc.creativecomputing.gl.demo.OGL4ShadingLanguage.chapter02;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl.data.GLCombinedBuffer;
import cc.creativecomputing.gl.data.GLMesh;
import cc.creativecomputing.gl.demo.CCTorus;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class Demo07ADSFunction extends CCGL4Adapter{
	
	private GLMesh _myTorus;
	private CCMatrix4x4 _myModelMatrix;
	private CCMatrix4x4 _myViewMatrix;
	private CCMatrix4x4 _myProjectionMatrix;
	
	private GLShaderProgram _myShader;
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(GLGraphics g) {
		_myModelMatrix = new CCMatrix4x4();
	    _myViewMatrix = CCMatrix4x4.createLookAt(new CCVector3(0.0f,0.0f,2.0f), new CCVector3(0.0f,0.0f,0.0f), new CCVector3(0.0f,1.0f,0.0f));
	    _myProjectionMatrix = new CCMatrix4x4();
	    
	    _myTorus =  new GLMesh(GLDrawMode.TRIANGLES, new GLCombinedBuffer(new CCTorus(10, 10, 0.3f,0.7f).data()));
	    
	    _myShader = new GLShaderProgram(
	    	new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "shader/function_vert.glsl")),
	    	new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "shader/function_frag.glsl"))
		);
	    _myShader.use();

	    _myShader.uniform3f("Material.Kd", 0.9f, 0.5f, 0.3f);
	    _myShader.uniform3f("Light.Ld", 1.0f, 1.0f, 1.0f);
	    _myShader.uniform4f("Light.Position", _myViewMatrix.applyPost(new CCVector4(-5.0f,-5.0f,-2.0f,1.0f)));
	    _myShader.uniform3f("Material.Ka", 0.9f, 0.5f, 0.3f);
	    _myShader.uniform3f("Light.La", 0.4f, 0.4f, 0.4f);
	    _myShader.uniform3f("Material.Ks", 0.8f, 0.8f, 0.8f);
	    _myShader.uniform3f("Light.Ls", 1.0f, 1.0f, 1.0f);
	    _myShader.uniform1f("Material.Shininess", 100.0f);
	}
	
	@Override
	public void reshape(GLGraphics g) {
		_myProjectionMatrix = CCMatrix4x4.createPerspective(70.0f,g.aspectRatio(), 0.3f, 100.0f);
	}
	
	private float _myAngle = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myAngle += theAnimator.deltaTime();
		_myModelMatrix.set(CCMatrix4x4.IDENTITY);
		_myModelMatrix.applyRotationY(_myAngle);
		_myModelMatrix.applyRotationX(CCMath.radians(-35f));
		_myModelMatrix.applyRotationY(CCMath.radians( 35f));
	}
	
	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);	
		g.depthTest();
		
		CCMatrix4x4 mv = _myModelMatrix.multiply(_myViewMatrix);
		_myShader.uniformMatrix4f("ModelViewMatrix", mv);
	    _myShader.uniformMatrix3f("NormalMatrix", mv.matrix3());
	    _myShader.uniformMatrix4f("MVP", mv.multiply(_myProjectionMatrix));
	    _myTorus.draw();
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo07ADSFunction());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
