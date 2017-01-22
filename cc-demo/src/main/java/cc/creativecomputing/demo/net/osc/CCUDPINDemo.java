package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.net.CCUDPIn;
import cc.creativecomputing.io.net.codec.CCNetStringCodec;

public class CCUDPINDemo extends CCGL2Adapter {
	
	@CCProperty(name = "udp in")
	private CCUDPIn<String> _myUDPIn;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myUDPIn = new CCUDPIn(new CCNetStringCodec());
		_myUDPIn.events().add(message ->{
			System.out.println(message.message);
		});
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCUDPINDemo demo = new CCUDPINDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}


