package cc.creativecomputing.graphics.demo.oglsb;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLSampler.GLTextureMagFilter;
import cc.creativecomputing.gl4.GLSampler.GLTextureMinFilter;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.gl4.texture.GLKTXLoader;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.graphics.scene.CCCamera;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.effect.CCSimpleEffect;
import cc.creativecomputing.graphics.scene.effect.CCSingleTextureEffect;
import cc.creativecomputing.graphics.scene.shape.CCQuad;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;

public class CC_5_5_KTXView extends CCGL4Adapter {
	
	class CCKTXTextureEffect extends CCSimpleEffect{

		@GLUniform(name = "s")
		private GLTexture2D _uTexture;

		private GLUniformParameters _myParameters;
		private GLShaderProgram _myShader;
		
		public CCKTXTextureEffect(GLTexture2D theTexture){
			_uTexture = theTexture;
			
			_myShader = new GLShaderProgram(
				new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this,"ktx_effect_vertex.glsl")),
				new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this,"ktx_effect_fragment.glsl"))
			);
			_myParameters = _myShader.createUniformParameters(this);
		    shader(_myShader);
		    
		    cullState().enabled = false;
		}
		
		@Override
		protected GLUniformParameters parameters(GLShaderProgram theShader) {
			return _myParameters;
		}
		
		public GLTexture2D texture(){
			return _uTexture;
		}
	}

	private CCSpatial _myPrimitive;

	private CCCamera _myCamera;

	private CCRenderer _myRenderer;

	@Override
	public void start(CCAnimator theAnimator) {
		_myPrimitive = new CCQuad(5f,5f);
	}

	@Override
	public void init(GLGraphics g) {
		GLTexture2D myTexture = (GLTexture2D)GLKTXLoader.load(CCNIOUtil.classPath(this, "Tree.ktx"));
		myTexture.minFilter(GLTextureMinFilter.LINEAR);
		myTexture.magFilter(GLTextureMagFilter.LINEAR);

		_myPrimitive.effect(new CCSingleTextureEffect(myTexture));

		_myCamera = new CCCamera();
		_myCamera.lookAt(new CCVector3(0.0f, 0.0f, 2.0f), new CCVector3(0.0f, 0.0f, 0.0f), new CCVector3(0.0f, 1.0f, 0.0f));
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
		CCGL4Application myAppManager = new CCGL4Application(new CC_5_5_KTXView());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
