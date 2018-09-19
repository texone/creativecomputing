package cc.creativecomputing.demo.protocol.serial.firmata;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.protocol.serial.firmata.CCArduino;

public class CCFirmataDemo extends CCGL2Adapter {
	@CCProperty(name = "arduino")
	private CCArduino _cArduino;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cArduino = new CCArduino();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_cArduino.isConnected())CCLog.info(_cArduino.analogRead(0));
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCFirmataDemo demo = new CCFirmataDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
