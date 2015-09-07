package cc.creativecomputing.demo.protocol.serial.dmx;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;

public class CCDMXDemo extends CCGL2Adapter{
	
	private CCControlApp _myControlApp;
	
	@CCProperty(name = "dmx")
	private CCDMX _myDMX;
	
	@Override
	public void init(CCGraphics g) {

		_myDMX = new CCDMX();
		
		_myControlApp = new CCControlApp(this, animator());
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
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
