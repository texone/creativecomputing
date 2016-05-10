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
import cc.creativecomputing.math.signal.CCMixSignal;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;

public class CCDMXSignalDemo extends CCGL2Adapter{
	
	@CCProperty(name = "dmx")
	private CCDMX _myDMX;
	@CCProperty(name = "signal")
	private CCMixSignal _mySigal = new CCMixSignal();
	
	@CCProperty(name = "speed", min = 0, max = 40)
	private double _cSpeed = 0;
	@CCProperty(name = "channel offset", min = 0, max = 40)
	private double _cChannelOffset = 0;
	
	private double _myPhase = 0;
	
	@Override
	public void start(CCAnimator theAnimator) {
		_myDMX = new CCDMX();
		
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
		_myPhase += _cSpeed * theAnimator.deltaTime();
		for(int i = 0; i < _myDMX.universeSize();i++){
			_myDMX.setDMXChannel(i, _mySigal.value(i * _cChannelOffset + _myPhase));
		}
		_myDMX.send();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
	}
	
	public static void main(String[] args) {
		CCDMXSignalDemo demo = new CCDMXSignalDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
