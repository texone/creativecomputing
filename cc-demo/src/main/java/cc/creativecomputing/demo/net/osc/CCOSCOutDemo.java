package cc.creativecomputing.demo.net.osc;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.net.CCUDPOut;
import cc.creativecomputing.io.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacketCodec;

public class CCOSCOutDemo extends CCGL2Adapter {
	
	//@CCProperty(name = "CCUDPOut")
	private CCUDPOut<CCOSCPacket> _myOSCOut;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCOut = new CCUDPOut<>(new CCOSCPacketCodec());
		_myOSCOut.targetAddress().ip("127.0.0.1");
		_myOSCOut.targetAddress().port(9500);
		
		_myOSCOut.localAddress().ip("127.0.0.1");
		_myOSCOut.localAddress().port(9000);
		
		_myOSCOut.connect(true);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		CCLog.info(_myOSCOut.isConnected());
		if(_myOSCOut.isConnected()) {
			_myOSCOut.send(new CCOSCMessage("/animator/time", theAnimator.time(), "Y=Y=Y0", theAnimator.frames()));
		}else {
			if(theAnimator.frames() % 100 == 0)_myOSCOut.connect(true);
		}
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCOutDemo demo = new CCOSCOutDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

