package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.CCUDPServer;
import cc.creativecomputing.io.netty.codec.osc.CCOSCCodec;
import cc.creativecomputing.io.netty.codec.osc.CCOSCPacket;

public class CCOSCMonitor extends CCGL2Adapter {
	
//	@CCProperty(name = "CCUDPIn")
	private CCUDPServer<CCOSCPacket> _myOSCIN;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCIN = new CCUDPServer<>(new CCOSCCodec(), "192.168.43.212",12345);
		_myOSCIN.events().add(message -> {
			CCLog.info(message.message);
		});
		_myOSCIN.connect();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCMonitor demo = new CCOSCMonitor();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

