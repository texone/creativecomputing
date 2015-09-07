package cc.creativecomputing.demo.control;

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

public class CCRealtimeCodingDemo extends CCGL2Adapter{
	
	public static interface CCRealtimeGraph extends CCCompileObject{
		public void draw(CCGraphics g);
	}
	
	@CCProperty(name = "real time visual")
	private CCRealtimeCompile<CCRealtimeGraph> _myRealTimeGraph;
	
	private CCControlApp _myControlApp;
	
	@Override
	public void init(CCGraphics g) {

		_myRealTimeGraph = new CCRealtimeCompile<CCRealtimeGraph>(CCRealtimeGraph.class);
		_myRealTimeGraph.createObject();
		
		_myControlApp = new CCControlApp(this, animator());
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
			CCLog.info(myGraph);
			if(myGraph == null)continue;
			myGraph.draw(g);
		}
		
	}
	
	public static void main(String[] args) {
		
		
		CCRealtimeCodingDemo demo = new CCRealtimeCodingDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
