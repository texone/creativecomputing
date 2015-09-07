package cc.creativecomputing.gl.demo;


import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGL4Context;
import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.io.net.CCUDPOut;
import cc.creativecomputing.io.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacketCodec;

public class CCGLControlDemo implements CCAnimatorListener, CCGLListener<GLGraphics>{

	
	public class DummyControl {
		@CCProperty(name="ab")
		public int ab = 0;
		
		public int c;
	}
	
	public static class DummyControlSender extends CCAnimatorAdapter {
		
		private CCUDPOut<CCOSCPacket> sender;
		
		public DummyControlSender () {
			sender = new CCUDPOut<CCOSCPacket>(new CCOSCPacketCodec(), "127.0.0.1", 1234);
			sender.connect();
		}
		
		@Override
		public void update(CCAnimator theAnimator) {
			//ch.sendData("xxx");
			CCOSCMessage msg = new CCOSCMessage("root.x");
			msg.add(5);
			sender.send(msg);
		}
	}
	
	@CCProperty(name="dummy")
	public DummyControl dummy = new DummyControl();
	
	@CCProperty(name="x")
	public int x = 0;
	
	@CCProperty(name="y")
	public float y = 2.2f;
	
	@Override
	public void start(CCAnimator theAnimator) {
	}

	@Override
	public void init(GLGraphics g) {
		g.clearColor(0, 0, 0, 0);
	}
	
	@Override
	public void reshape(GLGraphics g) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
		//System.out.println(x+" "+y+" "+dummy.ab);
	}

	@Override
	public void display(GLGraphics g) {
		g.clear();
	}

	@Override
	public void stop(CCAnimator theAnimator) {
	}

	@Override
	public void dispose(GLGraphics g) {
	}
	
	public static void main(String[] args) {
		
		CCGLControlDemo myDemo = new CCGLControlDemo();
		
		
		CCAnimator myAnimator = new CCAnimator();
		myAnimator.addListener(myDemo);
		myAnimator.framerate = 5;
		myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		CCGL4Context myGLModule = new CCGL4Context(myAnimator);
		myGLModule.addListener(myDemo);
		
		
		DummyControlSender myRemoteSender = new DummyControlSender();
		
		
		myAnimator.addListener(myRemoteSender);
		
		myGLModule.start();
		myAnimator.start();
	}
}
