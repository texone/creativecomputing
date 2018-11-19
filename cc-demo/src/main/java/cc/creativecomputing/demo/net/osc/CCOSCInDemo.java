package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.net.CCUDPIn;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacketCodec;

public class CCOSCInDemo extends CCGL2Adapter {
	
	//@CCProperty(name = "CCUDPIn")
	private CCUDPIn<CCOSCPacket> _myOSCIN;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCIN = new CCUDPIn<>(new CCOSCPacketCodec());
		_myOSCIN.events().add(message -> {
			CCLog.info(message.message);
		});
		
		_myOSCIN.localAddress().ip("127.0.0.1");
		_myOSCIN.localAddress().port(9500);
		

		_myOSCIN.targetAddress().ip("127.0.0.1");
		_myOSCIN.targetAddress().port(9000);
		
		_myOSCIN.connect(true);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCInDemo demo = new CCOSCInDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

