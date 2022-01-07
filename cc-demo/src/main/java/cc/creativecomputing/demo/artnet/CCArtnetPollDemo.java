package cc.creativecomputing.demo.artnet;

import artnet4j.packets.ArtNetPacket;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.artnet.CCArtNet;
import cc.creativecomputing.artnet.packets.ArtDmxPacket;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCBitUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCArtnetPollDemo extends CCGL2Adapter {
	
	//@CCProperty(name = "artnet")
    private CCArtNet _myArtnet;
	
	private long _myStartMillis = 0;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myArtnet = new CCArtNet();
		_myArtnet.ip("127.0.0.2");
		_myArtnet.connect();
		
		_myStartMillis = System.currentTimeMillis();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		ArtDmxPacket dmx = new ArtDmxPacket();
        dmx.setUniverse(0,0);
        dmx.setSequenceID(0);
        byte[] buffer = new byte[512];//CCBitUtil.split(myMillis);
        //CCLog.info(myMillis, buffer[0], buffer[1], buffer[2], buffer[3]);

        
        
      for (int i = 0; i < buffer.length; i++) {
	      buffer[i] = (byte) (Math.sin(theAnimator.time()  + i * 0.8) * 127 + 128);
	      
      }dmx.setDMX(buffer, buffer.length);
      _myArtnet.unicastPacket(dmx, "127.0.0.1");
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
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

