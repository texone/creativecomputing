package cc.creativecomputing.topic.simulation.drops;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;

public class CCRainDropDemo extends CCGL2Adapter {
	
	@CCProperty(name = "drops")
	private CCRainDropSimulation _cRainDrops;
	
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCapture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cRainDrops = new CCRainDropSimulation(g.width(), g.height());
		_cScreenCapture = new CCScreenCaptureController(this);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_cRainDrops.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cRainDrops.draw(g);
	}

	public static void main(String[] args) {

		CCRainDropDemo demo = new CCRainDropDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1080, 1920);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
