package cc.creativecomputing.demo.protocol.midi;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.protocol.midi.CCMidiOutDevice;

public class CCMidiOutDemo extends CCGL2Adapter{
	
	
	
	
	@CCProperty(name = "stringr")
	private String _myString = "texone";
	
	@CCProperty(name = "out device")
	private CCMidiOutDevice _myOutDevice = new CCMidiOutDevice();
	
	@Override
	public void init(CCGraphics g) {
		
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
		
		g.text(_myString,0,0);
		
//		CCLog.info(g.width + ":" + g.height);
	}
	
	public static void main(String[] args) {
		
		
		CCMidiOutDemo demo = new CCMidiOutDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
