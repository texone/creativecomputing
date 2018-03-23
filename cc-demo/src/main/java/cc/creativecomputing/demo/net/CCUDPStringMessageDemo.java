package cc.creativecomputing.demo.net;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.CCUDPClient;
import cc.creativecomputing.io.netty.CCUDPServer;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;

public class CCUDPStringMessageDemo extends CCGL2Adapter {
	
	CCUDPServer<String> myServer;
	
	CCUDPClient<String> myClient;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		myServer = new CCUDPServer<String>(new CCNetStringCodec());
		myServer.events().add( message -> {
			CCLog.info(message.message.getClass() + ":" + message.message);
		});
		myServer.connect();
		
		myClient = new CCUDPClient<String>(new CCNetStringCodec());
		myClient.connect();
		myClient.write("texone");
		myClient.write("textwo");
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
	}

	public static void main(String[] args) {

		CCUDPStringMessageDemo demo = new CCUDPStringMessageDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
