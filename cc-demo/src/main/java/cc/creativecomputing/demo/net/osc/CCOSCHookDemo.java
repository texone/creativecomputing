package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.CCClient;
import cc.creativecomputing.io.netty.CCUDPClient;
import cc.creativecomputing.io.netty.CCUDPServer;
import cc.creativecomputing.io.netty.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.netty.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.netty.codec.osc.CCOSCCodec;

public class CCOSCHookDemo extends CCGL2Adapter {
	
	@CCProperty(name = "CCUDPIn")
	private CCUDPServer<CCOSCPacket> _myOSCIN;
	@CCProperty(name = "CCUDPOut")
	private CCClient<CCOSCPacket> _myOSCOut;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCIN = new CCUDPServer<>(new CCOSCCodec());
		_myOSCIN.events().add(message -> {
			CCLog.info(message.message);
		});
		_myOSCOut = new CCUDPClient<>(new CCOSCCodec());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_myOSCOut.isConnected())
			_myOSCOut.write(new CCOSCMessage("/animator/time", theAnimator.time(), "Y=Y=Y0", true, theAnimator.frames()));
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCHookDemo demo = new CCOSCHookDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

