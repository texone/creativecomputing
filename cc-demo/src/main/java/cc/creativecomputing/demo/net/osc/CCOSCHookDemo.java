package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCUDPIn;
import cc.creativecomputing.io.net.CCUDPOut;
import cc.creativecomputing.io.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacketCodec;

public class CCOSCHookDemo extends CCGL2Adapter {
	
	@CCProperty(name = "CCUDPIn")
	private CCUDPIn<CCOSCPacket> _myOSCIN;
	@CCProperty(name = "CCUDPOut")
	private CCUDPOut<CCOSCPacket> _myOSCOut;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCIN = new CCUDPIn<>(new CCOSCPacketCodec());
		_myOSCIN.events().add(message -> {
			CCLog.info(message.message);
		});
		
		_myOSCOut = new CCUDPOut<>(new CCOSCPacketCodec());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myOSCOut.send(new CCOSCMessage("/animator/time", theAnimator.time()));
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

