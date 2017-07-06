package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCGradientControlDemo extends CCGL2Adapter{
	
	
	@CCProperty(name = "gradient a")
	private CCGradient _myGradientA = new CCGradient();
	@CCProperty(name = "gradient b")
	private CCGradient _myGradientB = new CCGradient();
	@CCProperty(name = "gradient c")
	private CCGradient _myGradientC = new CCGradient();
	
	@CCProperty(name = "slide", min = 0, max = 1)
	private double slider = 0;
	
	@Override
	public void start(CCAnimator theAnimator) {
//		_myGradient.add(0, CCColor.WHITE);
//		_myGradient.add(1, CCColor.BLACK);
//		for(CCGradientPoint myPoint:_myGradient){
//			CCLog.info(myPoint.position() + ":" + myPoint.color());
//		}
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
	
	private void drawGradient(CCGraphics g, CCGradient theA, CCGradient theB, int theRes, int theStartY, int theEndY){
		for(int x = -g.width() / 2; x < g.width() / 2;x+=theRes){
			double xBlend = CCMath.norm(x, -g.width() / 2, g.width() / 2);
			CCColor myColorA = theA.color(xBlend);
			CCColor myColorB = theB.color(xBlend);
			for(int y = theStartY; y < theEndY;y+=theRes){
				double yBlend =	CCMath.smoothStep(theStartY, theEndY, y);
				g.color(CCColor.blend(myColorA, myColorB, yBlend));
				g.rect(x,y,theRes, theRes);
			}
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(255,0,0);
		g.clear();
		
//		g.beginShape(CCDrawMode.LINES);
//		for(int x = 0; x < g.width();x++){
//			g.color(_myGradient.color((double)x / g.width()));
//			g.vertex(x - g.width()/2, -50);
//			g.vertex(x - g.width()/2,  50);
//		}
//		g.endShape();
		drawGradient(g, _myGradientA, _myGradientB, 10, -g.height()/2, 0);
		drawGradient(g, _myGradientB, _myGradientC, 10, 0, g.height()/2);
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
