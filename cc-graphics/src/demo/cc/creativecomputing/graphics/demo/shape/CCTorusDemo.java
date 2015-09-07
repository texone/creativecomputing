package cc.creativecomputing.graphics.demo.shape;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLGraphics.GLPolygonMode;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.graphics.scene.CCCamera;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.effect.CCSimpleEffect;
import cc.creativecomputing.graphics.scene.shape.CCTorus;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCTorusDemo extends CCGL4Adapter {

	private CCSpatial _myPrimitive;

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
	private CCVector4 _uLightPosition = new CCVector4(-5.0f, -5.0f, -2.0f, 1.0f);

	private CCRenderer _myRenderer;

	@Override
	public void start(CCAnimator theAnimator) {
		// _myPrimitive = new CCSphere(15,15, 0.7f);
		// _myPrimitive = new CCCone(15,15, 0.7f, 0.7f);
		// _myPrimitive = new CCDisk(15,15, 0.7f);
		// _myPrimitive = new CCCapsule(15,15, 15, 0.7f, 0.7f);
		// _myPrimitive = new CCDodecahedron(0.7f);
		// _myPrimitive = new CCDome(15,15, 0.7f);
		// _myPrimitive = new CCBox(0.7f);
		// _myPrimitive = new CCTube(15, 15, 0.7f, 0.5f,0.9f);
		// _myPrimitive = new CCQuad(0.7f, 0.5f);
		// _myPrimitive = new CCPyramid(0.7f, 0.5f);
		// _myPrimitive = new CCHexagon(0.7f);
		// _myPrimitive = new CCIcosahedron(0.7f);
		// _myPrimitive = new CCRoundedBox(0.7f);
		// _myPrimitive = new CCTeapot();
		// _myPrimitive = new CCCylinder(10,10,0.3f, 0.7f, true);
		// _myPrimitive = new CCAxisRods();
		_myPrimitive = new CCTorus(10, 10, 0.3f, 0.7f);

	}

	@Override
	public void init(GLGraphics g) {

		_myShader = GLShaderProgram.createShaderProgram(CCNIOUtil.classPath(this, "shader") + "/phong");
		_myShader.uniforms(_myShader.createUniformParameters(this));

		CCSimpleEffect myEffect = new CCSimpleEffect();
		myEffect.shader(_myShader);
		myEffect.wireState().polygonMode = GLPolygonMode.LINE;
		myEffect.wireState().lineWidth = 3f;
		myEffect.wireState().enabled = true;
		myEffect.cullState().enabled = false;

		
		_myPrimitive.effect(myEffect);

		_myCamera = new CCCamera();
		_myCamera.lookAt(new CCVector3(0.0f, 0.0f, 2.0f), new CCVector3(0.0f, 0.0f, 0.0f), new CCVector3(0.0f, 1.0f, 0.0f));
		_myCamera.viewMatrix().applyPost(_uLightPosition, _uLightPosition);
		_myRenderer = new CCRenderer(_myCamera, g);
	}

	float myAngle = 0;

	@Override
	public void update(CCAnimator theAnimator) {
		myAngle += theAnimator.deltaTime();

		_myPrimitive.localTransform().rotation(myAngle, 0.33f, 0.7f, 0);
		// _myPrimitive.localTransform().scale(0.4f);
		// _myPrimitive.localTransform().translation(1f, 0, 0);
		_myPrimitive.updateGeometricState(theAnimator);

	}

	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
		_myCamera.perpective(70.0f, g.aspectRatio(), 0.3f, 100.0f);
	}

	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);
		g.depthTest();

		g.pointSize(3);
		_myPrimitive.draw(_myRenderer);
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new CCTorusDemo());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
