package cc.creativecomputing.demo.net;

import java.nio.ByteBuffer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCBitUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.codec.CCNetByteCodec;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.io.netty.CCUDPClient;
import cc.creativecomputing.io.netty.CCUDPServer;

public class CCUDPByteMessageDemo extends CCGL2Adapter {
	
	CCUDPServer<ByteBuffer> myServer;
	
	CCUDPClient<ByteBuffer> myClient;
	
	@CCProperty(name = "bright", min = 0, max = 1)
	private double _cBright = 0;
	@CCProperty(name = "r", min = 0, max = 1)
	private double _cR = 0;
	@CCProperty(name = "g", min = 0, max = 1)
	private double _cG = 0;
	@CCProperty(name = "b", min = 0, max = 1)
	private double _cB = 0;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		myServer = new CCUDPServer<ByteBuffer>(new CCNetByteCodec());
		myServer.events().add( message -> {
			CCLog.info(message.message.getClass() + ":" + message.message);
			ByteBuffer myMessage = (ByteBuffer)message.message;
			while(myMessage.hasRemaining()){
				CCLog.info("get:" + myMessage.get());
			}
		});
		myServer.connect();
		
		myClient = new CCUDPClient<ByteBuffer>(new CCNetByteCodec(), "192.168.1.13", 12345);
		myClient.connect();
		ByteBuffer myBuffer = ByteBuffer.allocate(20);
		while(myBuffer.hasRemaining()){
			byte myByte = (byte)CCMath.random(255);
			CCLog.info("put:" + myByte);
			myBuffer.put(myByte);
		}
		myBuffer.rewind();
		myClient.write(myBuffer);
	}
	
	private void writeDouble(ByteBuffer theBuffer, double theValue){
		int my16BitValue = (int)(theValue * 65535);
		CCLog.info(" : " + my16BitValue);
		theBuffer.put((byte)CCBitUtil.bit(my16BitValue, 1));
		theBuffer.put((byte)CCBitUtil.bit(my16BitValue, 0));
	}

	@Override
	public void update(CCAnimator theAnimator) {
		ByteBuffer myBuffer = ByteBuffer.allocate(768);
		for(int i = 0; i < 384 / 4;i++){
//	    	int my16BitValue = (int)((CCMath.sin(theAnimator.time()) + 1) / 2 * 65535);
////			CCLog.info(i + " : " + my16BitValue);
//	    	myBuffer.put((byte)CCBitUtil.bit(my16BitValue, 1));
//	    	myBuffer.put((byte)CCBitUtil.bit(my16BitValue, 0));
			
			writeDouble(myBuffer, _cR);
			writeDouble(myBuffer, _cG);
			writeDouble(myBuffer, _cB);
			writeDouble(myBuffer, _cBright);
		}
		myBuffer.rewind();
		myClient.write(myBuffer);
//		ByteBuffer myBuffer = ByteBuffer.allocate(20);
//		while(myBuffer.hasRemaining()){
//			byte myByte = (byte)CCMath.random(255);
////			CCLog.info("put:" + myByte);
//			myBuffer.put(myByte);
//		}
//		myClient.write(myBuffer);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
	}

	public static void main(String[] args) {

		CCUDPByteMessageDemo demo = new CCUDPByteMessageDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
