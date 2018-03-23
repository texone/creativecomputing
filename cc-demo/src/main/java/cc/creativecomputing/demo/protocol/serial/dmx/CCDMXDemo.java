package cc.creativecomputing.demo.protocol.serial.dmx;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;

public class CCDMXDemo extends CCGL2Adapter{
	
	@CCProperty(name = "dmx")
	private CCDMX _myDMX;
	
	@CCProperty(name = "channel map", min = 0, max = 1)
	private Map<String, Double> _myChannelMap = new LinkedHashMap<>();
	
	
	@CCProperty(name = "send 16 bit")
	private boolean _cSend16Bit = false;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myDMX = new CCDMX();
		for(int i = 0; i < 512;i++){
			_myChannelMap.put("channel " + i, 0d);
		}
	}
	
	@Override
	public void init(CCGraphics g) {

		
//		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
//			CCLog.info(myGraph);
//			if(myGraph == null)return;
//			myGraph.draw(g);
//		}
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	@CCProperty(name = "trigger")
	public void triggerDemo(){
		CCLog.info("EVENT");
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(_cSend16Bit){
			for(int i = 0; i < 512;i+=2){
				_myDMX.setDMXChannel16bit(i, _myChannelMap.get("channel " + i / 2));
			}
		}else{
			for(int i = 0; i < 512;i++){
				_myDMX.setDMXChannel(i, _myChannelMap.get("channel " + i));
			}
		}
		_myDMX.send();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
	}
	
	public static void main(String[] args) {
		CCDMXDemo demo = new CCDMXDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
