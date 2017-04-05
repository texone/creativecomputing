package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.codec.osc.CCOSCSender;

public class CCOSCSenderDemo extends CCGL2Adapter {
	
	@CCProperty(name = "osc sender")
	private CCOSCSender _myOSCSender;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCSender = new CCOSCSender();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCSenderDemo demo = new CCOSCSenderDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

