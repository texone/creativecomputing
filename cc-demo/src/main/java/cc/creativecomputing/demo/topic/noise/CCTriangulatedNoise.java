package cc.creativecomputing.demo.topic.noise;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;

public class CCTriangulatedNoise extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _cProgram;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cProgram = CCGLProgram.createEmptyFrament();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho2D();
		_cProgram.start();
		_cProgram.uniform2f("iResolution", g.width(), g.height());
		_cProgram.uniform1f("iTime", animator().time());
		g.rect(0, 0, g.width(), g.height());
		_cProgram.end();
	}

	public static void main(String[] args) {

		CCTriangulatedNoise demo = new CCTriangulatedNoise();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

