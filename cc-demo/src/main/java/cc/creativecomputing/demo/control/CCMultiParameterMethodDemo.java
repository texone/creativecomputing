package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;

public class CCMultiParameterMethodDemo extends CCGL2Adapter{
	
	public static interface CCRealtimeGraph extends CCCompileObject{
		public void draw(CCGraphics g);
	}
	
	private double _myX = 0;
	private double _myY = 0;
	
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
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.rect(_myX, _myY,200,200);
		
//		CCLog.info(g.width + ":" + g.height);
	}
	
	public static void main(String[] args) {
		
		
		CCMultiParameterMethodDemo demo = new CCMultiParameterMethodDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
//		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
