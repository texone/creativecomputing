package cc.creativecomputing.demo.artnet;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.artnet.CCArtNet;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCArtnetPollDemo extends CCGL2Adapter {
	
	@CCProperty(name = "artnet")
    private CCArtNet _myArtnet;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myArtnet = new CCArtNet();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCArtnetPollDemo demo = new CCArtnetPollDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

