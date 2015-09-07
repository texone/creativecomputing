package cc.creativecomputing.graphics.demo.shape;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.graphics.scene.CCCamera;
import cc.creativecomputing.graphics.scene.CCNode;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.controllers.CCController;
import cc.creativecomputing.graphics.scene.effect.CCSimpleEffect;
import cc.creativecomputing.graphics.scene.shape.CCQuad;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCQuadsDemo extends CCGL4Adapter{
	
	private class CCAnimatedController extends CCController<CCSpatial>{
		
		private CCVector3 _myTranslation;
		private CCVector3 _myRotationAxis;
		private float _mySpeed;
		
		public CCAnimatedController(){
			_myTranslation = new CCVector3(CCMath.random(-1,1), CCMath.random(-1,1), CCMath.random(-1,1));
			_myRotationAxis = new CCVector3(CCMath.random(-1,1), CCMath.random(-1,1), CCMath.random(-1,1));
			_mySpeed = CCMath.random(-1,1);
		}

		@Override
		public boolean update(CCAnimator theAnimator) {
			if(!super.update(theAnimator)){
				return false;
			}
			object().localTransform().rotation(myAngle * _mySpeed,_myRotationAxis);
			object().localTransform().scale(0.4f);
			object().localTransform().translation(_myTranslation);
			return true;
		}
		
	}
	
	private CCCamera _myCamera;
	
	private GLShaderProgram _myShader;
	
	@GLUniform(name = "Material.Kd")
	private CCColor _uMaterialKd = new CCColor(0.9f, 0.5f, 0.3f);
	@GLUniform(name = "Material.Ka")
	private CCColor _uMaterialKa = new CCColor(0.9f, 0.5f, 0.3f);
	@GLUniform(name = "Material.Ks")
	private CCColor _uMaterialKs = new CCColor(0.8f, 0.8f, 0.8f);
	@GLUniform(name = "Material.Shininess")
	private float _uMaterialShininess = 100f;
	
	@GLUniform(name = "Light.Ld")
	private CCColor _uLightLd = new CCColor(1.0f, 1.0f, 1.0f);
	@GLUniform(name = "Light.La")
	private CCColor _uLightLa = new CCColor(0.4f, 0.4f, 0.4f);
	@GLUniform(name = "Light.Ls")
	private CCColor _uLightLs = new CCColor(1.0f, 1.0f, 1.0f); 
	@GLUniform(name = "Light.Position")
	private CCVector4 _uLightPosition = new CCVector4(-5.0f,-5.0f,-2.0f,1.0f); 
	
	private CCRenderer _myRenderer;
	
	private CCNode _myNode;
	
	@Override
	public void start(CCAnimator theAnimator) {
		_myNode = new CCNode();
	}
	
	@Override
	public void init(GLGraphics g) {
//		_myNode.attachController(new CCAnimatedController());
		for(int i = 0; i < 50;i++){
			CCQuad myQuad = new CCQuad();
			myQuad.attachController(new CCAnimatedController());
			_myNode.attachChild(myQuad);
		}
	    
	    _myCamera = new CCCamera();

//	    _myCamera.frame(new CCVector3(0.0f,0.0f,-2.0f), CCVector3.NEG_UNIT_Z, CCVector3.UNIT_Y, CCVector3.UNIT_X);
	    _myCamera.lookAt(new CCVector3(0.0f,0.0f,2.0f), new CCVector3(0.0f,0.0f,0.0f), new CCVector3(0.0f,1.0f,0.0f));
	    _myCamera.viewMatrix().applyPost(_uLightPosition, _uLightPosition);
		
	    _myShader = GLShaderProgram.createShaderProgram(CCNIOUtil.classPath(this, "shader") + "/phong");
	    _myShader.uniforms(this);
		
		CCSimpleEffect myEffect = new CCSimpleEffect();
	    myEffect.shader(_myShader);
	    myEffect.cullState().enabled = false;
	    _myNode.effect(myEffect);
	    _myRenderer = new CCRenderer(_myCamera, g);
	}
	
	float myAngle = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		myAngle += theAnimator.deltaTime();
		
		_myNode.updateGeometricState(theAnimator);

		CCLog.info(theAnimator.frameRate());
	}
	
	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
	    _myCamera.perpective(70.0f,g.aspectRatio(), 0.3f, 100.0f);
	}
	
	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);	
		g.depthTest();
	    
	    g.pointSize(3);
	    _myNode.draw(_myRenderer);
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new CCQuadsDemo());
		myAppManager.animator().framerate = 120;
		myAppManager.start();
	}
}
