package cc.creativecomputing.demo.net;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.CCTCPClient;
import cc.creativecomputing.io.netty.CCTCPServer;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;

public class CCTCPDemo extends CCGL2Adapter {
	
	CCTCPServer<String> myServer;
	
	CCTCPClient<String> myClient;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		myServer = new CCTCPServer<String>(new CCNetStringCodec(), 12345);
		myServer.events().add( message -> {
			CCLog.info(message.message);
		});
		myServer.connect();
		
		myClient = new CCTCPClient<String>(new CCNetStringCodec(),"127.0.0.1", 12345);
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

		CCTCPDemo demo = new CCTCPDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
