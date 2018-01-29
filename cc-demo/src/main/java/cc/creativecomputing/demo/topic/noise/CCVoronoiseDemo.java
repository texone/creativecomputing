package cc.creativecomputing.demo.topic.noise;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVoronoiseDemo extends CCGL2Adapter {

	@CCProperty(name = "voronoise")
	private CCGLProgram _myProgram;
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	@CCProperty(name = "shaper")
	private CCEnvelope _cShaper = new CCEnvelope();

	private CCGLWriteDataShader _myWriteDataShader;
	private CCShaderBuffer _myEvelopeData;
	
	private CCTexture2D _myTexture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "voronoise.glsl"));
		_cScreenCapture = new CCScreenCaptureController(this, theAnimator);
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/typo/thecrown.png")));
		
		_myWriteDataShader = new CCGLWriteDataShader();
		_myEvelopeData = new CCShaderBuffer(100,1, CCTextureTarget.TEXTURE_2D);
		_myEvelopeData.attachment(0).textureFilter(CCTextureFilter.LINEAR);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		_myEvelopeData.beginDraw(g);
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < 100; i++){
			double myVal = _cShaper.value(i / 100d);
			g.textureCoords4D(0, myVal, myVal, myVal, 1d);
			g.vertex(i + 0.5, 0.5);
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		_myEvelopeData.endDraw(g);
		
		g.ortho();
		g.clear();
		
		g.texture(0, _myEvelopeData.attachment(0));
		g.texture(1, _myTexture);
		_myProgram.start();
		_myProgram.uniform2f("iResolution", g.width(), g.height());
		_myProgram.uniform1f("time", animator().time());
		_myProgram.uniform1i("randomTexture", 0);
		_myProgram.uniform1i("typo", 1);
		_myProgram.uniform1i("shaper", 0);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex( g.width(),  g.height());
		g.vertex(0,  g.height());
		g.vertex(0, 0);
		g.vertex( g.width(), 0);
		g.endShape();
		_myProgram.end();
		g.noTexture();

		
		
	}

	public static void main(String[] args) {

		CCVoronoiseDemo demo = new CCVoronoiseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
