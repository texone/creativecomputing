package cc.creativecomputing.demo.gl2.shader.imaging;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;

public class CCStripeBlenddemo extends CCGL2Adapter {
	
	
	@CCProperty(name = "Stripes")
	private CCGLProgram _cStripes;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_cStripes = new CCGLProgram(
			CCNIOUtil.classPath(this, "stripes_vertex.glsl"),
			CCNIOUtil.classPath(this, "stripes_fragment.glsl")
		);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.pushMatrix();
		g.ortho2D();
		_cStripes.start();
		g.rect(0, 0, g.width(), g.height());
		_cStripes.end();
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCStripeBlenddemo demo = new CCStripeBlenddemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
