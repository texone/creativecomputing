package cc.creativecomputing.demo.topic.noise;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCTextureNoise extends CCGL2Adapter {

	@CCProperty(name = "texturenoise")
	private CCGLProgram _myProgram;
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	
	private CCTexture2D _myTexture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "texturenoise.glsl"));
		_cScreenCapture = new CCScreenCaptureController(this, theAnimator);
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/typo/thecrown.png")));
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.ortho();
		g.clear();
		
		g.texture(0, CCGLShaderUtil.randomTexture());
		g.texture(1, _myTexture);
		_myProgram.start();
		_myProgram.uniform2f("iResolution", g.width(), g.height());
		_myProgram.uniform1f("iTime", animator().time());
		_myProgram.uniform1i("randomTexture", 0);
		_myProgram.uniform1i("typo", 1);
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

		CCTextureNoise demo = new CCTextureNoise();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
