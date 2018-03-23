package cc.creativecomputing.demo.control;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCCircularControlDemo extends CCGL2Adapter {
	
	@CCProperty(name = "circle control")
	private CCCircularControl _myCircularControl = new CCCircularControl();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCircularControl.init(this, g, theAnimator);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myCircularControl.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myCircularControl.display(g);
	}

	public static void main(String[] args) {

		CCCircularControlDemo demo = new CCCircularControlDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

