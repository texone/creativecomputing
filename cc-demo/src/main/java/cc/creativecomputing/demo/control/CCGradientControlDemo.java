package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCGradientPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;

public class CCGradientControlDemo extends CCGL2Adapter{
	
	
	@CCProperty(name = "gradient")
	private CCGradient _myGradient = new CCGradient();
	
	@Override
	public void start(CCAnimator theAnimator) {
		_myGradient.add(0, CCColor.WHITE);
		_myGradient.add(1, CCColor.BLACK);
		for(CCGradientPoint myPoint:_myGradient){
			CCLog.info(myPoint.position() + ":" + myPoint.color());
		}
	}
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
//		CCLog.info(_myGradient.size());
//		for(CCGradientPoint myPoint:_myGradient){
//			CCLog.info(myPoint.position() + ":" + myPoint.color());
//		}
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(255);
		g.clear();
		
		g.beginShape(CCDrawMode.LINES);
		for(int x = 0; x < g.width();x++){
			g.color(_myGradient.color((double)x / g.width()));
			g.vertex(x - g.width()/2, -50);
			g.vertex(x - g.width()/2,  50);
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCGradientControlDemo demo = new CCGradientControlDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
