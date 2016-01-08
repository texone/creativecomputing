package cc.creativecomputing.demo.protocol.serial.dmx;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;
import cc.creativecomputing.protocol.serial.dmx.CCDMXEnttecOpenDmxUSB;

public class CCDMXEnttecOpen extends CCGL2Adapter{
	
	@CCProperty(name = "dmx")
	private CCDMXEnttecOpenDmxUSB _myDMX;
	
	@CCProperty(name = "channel map")
	private Map<String, Double> _myChannelMap = new LinkedHashMap<>();
	
	@Override
	public void start(CCAnimator theAnimator) {
		_myDMX = new CCDMXEnttecOpenDmxUSB();
		for(int i = 0; i < 100;i++){
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
		for(int i = 0; i < 100;i++){
			_myDMX.setDMXChannel(i, _myChannelMap.get("channel " + i));
		}
		_myDMX.send();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
	}
	
	public static void main(String[] args) {
		CCDMXEnttecOpen demo = new CCDMXEnttecOpen();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
