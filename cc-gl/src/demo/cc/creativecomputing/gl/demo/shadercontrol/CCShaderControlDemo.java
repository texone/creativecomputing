package cc.creativecomputing.gl.demo.shadercontrol;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
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
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCShaderControlDemo extends CCGL4Adapter{
	
	private GLMesh _myTorus;
	private CCMatrix4x4 _myModelMatrix;
	private CCMatrix4x4 _myViewMatrix;
	private CCMatrix4x4 _myProjectionMatrix;
	
	@CCProperty(name="intensity")
	public float _cIntensity = 1f;
	
	@CCProperty(name="fog depth")
	public float _cFog = 0f;
	
	@CCProperty(name="fog", desc = "fog shader")
	public GLShaderProgram _myShader;
	
	//@CCProperty(name = "Kd")
	public CCColor _myColor = new CCColor();
	
	@Override
	public void start(CCAnimator theAnimator) {
  
	}

	@Override
	public void init(GLGraphics theG) {
		_myTorus = new GLMesh(GLDrawMode.TRIANGLES, new GLCombinedBuffer(new CCTorus(10, 10, 2.0f, 2.6f).data()));
	
		_myShader = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "shader/fog_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "shader/fog_frag.glsl"))
		);
		_myShader.use();

		

	    _myModelMatrix = new CCMatrix4x4();
	    _myViewMatrix = CCMatrix4x4.createLookAt(new CCVector3(0.0f,4.0f,6.0f), new CCVector3(0.0f,2.0f,0.0f), new CCVector3(0.0f,1.0f,0.0f));
	    _myProjectionMatrix = new CCMatrix4x4();
	}
	
	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
		_myProjectionMatrix = CCMatrix4x4.createPerspective(70.0f,g.aspectRatio(), 0.3f, 100.0f);
	}
	float _myAngle = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myAngle += theAnimator.deltaTime();
	}
	
	private void setMatrices() {
		 CCMatrix4x4 mv = _myModelMatrix.multiply(_myViewMatrix);
		 _myShader.uniformMatrix4f("ModelViewMatrix", mv);
		 _myShader.uniformMatrix3f("NormalMatrix", mv.matrix3());
		 _myShader.uniformMatrix4f("MVP", mv.multiply(_myProjectionMatrix));
	}
	
	@Override
	public void display(GLGraphics g) {
		
		_myShader.updateSources();
		_myShader.uniform3f("Light.intensity", 0.9f, 0.9f, 0.9f);
		_myShader.uniform1f("Fog.minDist", 1.0f);
		_myShader.uniform1f("Fog.maxDist", 30.0f);
		_myShader.uniform3f("Fog.color", 0.5f, 0.5f, 0.5f);
		
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0.5f, 0.5f, 0.5f, 1f);
		g.clearDepthBuffer(1f);
		g.depthTest();

		_myShader.uniform4f("Light.position", _myViewMatrix.applyPost(new CCVector4(10.0f * CCMath.cos(_myAngle), 10.0f, 10.0f * CCMath.sin(_myAngle), 1.0f)));
		
		_myShader.uniform3f("Kd", 0.9f, 0.5f, 0.3f);
		_myShader.uniform3f("Ks", 0.0f, 0.0f, 0.0f);
		_myShader.uniform3f("Ka", 0.9f * 0.3f, 0.5f * 0.3f, 0.3f * 0.3f);
		_myShader.uniform1f("Shininess", 100.0f);
		
		float dist = 0.0f;
	    for( int i = 0 ; i < 4; i++ ) {
			_myModelMatrix.set(CCMatrix4x4.IDENTITY);
			_myModelMatrix.applyTranslationPost(dist * 0.6f - 1.0f,0.0f, -dist);
			_myModelMatrix.applyRotationY(CCMath.radians(-90.0f));
			_myModelMatrix.applyRotationY(_myAngle);
	        setMatrices();
			_myTorus.draw();
	        dist += 7.0f;
	    }

		_myShader.uniform3f("Kd", 0.7f, 0.7f, 0.7f);
		_myShader.uniform3f("Ks", 0.9f, 0.9f, 0.9f);
		_myShader.uniform3f("Ka", 0.1f, 0.1f, 0.1f);
		_myShader.uniform1f("Shininess", 180.0f);

		_myModelMatrix.set(CCMatrix4x4.IDENTITY);
		_myModelMatrix.applyTranslationPost(0.0f, -0.75f, 0.0f);
		setMatrices();
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new CCShaderControlDemo());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
	
		myAppManager.glcontext().size(800, 800);
		
		
		myAppManager.start();
	}
}