package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.protocol.midi.CCMidiInDevice;
import cc.creativecomputing.protocol.midi.CCMidiTimeCode;

public class CCMidiInDemo extends CCGL2Adapter{
	
	
	
	
	@CCProperty(name = "stringr")
	private String _myString = "texone";
	
	@CCProperty(name = "in device")
	private CCMidiInDevice _myInDevice = new CCMidiInDevice();
	
	private CCMidiTimeCode _myTimeCode;
	
	@Override
	public void init(CCGraphics g) {
		_myInDevice.events().add(_myTimeCode = new CCMidiTimeCode());
//		CCMidiIO.getInstance().printDevices();
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
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
		
		g.text(_myTimeCode.hours() + ":" + _myTimeCode.time(),0,0);
	}
	
	public static void main(String[] args) {
		
		
		CCMidiInDemo demo = new CCMidiInDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
