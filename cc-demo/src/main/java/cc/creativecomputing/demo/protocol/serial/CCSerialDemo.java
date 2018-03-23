package cc.creativecomputing.demo.protocol.serial;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.protocol.serial.CCSerialModule;

public class CCSerialDemo extends CCGL2Adapter{
	
	@CCProperty(name = "serial")
    private CCSerialModule _mySerial;
	
	private StringBuffer _myReadBuffer = new StringBuffer();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySerial = new CCSerialModule("serial", 9600);
		_mySerial.listener().add(input -> {
			String myInput = input.readString();
			if(myInput.equals("\n")){
				CCLog.info(_myReadBuffer);
				_myReadBuffer = new StringBuffer();
			}else{
				_myReadBuffer.append(myInput);
			}
		});
	}
	
	@Override
	public void init(CCGraphics g) {

		
	}
	
	@CCProperty(name = "trigger")
	public void triggerDemo(){
		CCLog.info("EVENT");
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
	}
	
	public static void main(String[] args) {
		CCSerialDemo demo = new CCSerialDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
