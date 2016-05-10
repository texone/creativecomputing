package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;

public class CCEnumDemo extends CCGL2Adapter{
	
	
	@CCProperty(name = "draw mode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		
		CCMath.randomSeed(0);
		g.color(255);
		g.beginShape(_myDrawMode);
		for(int i = 0; i < 1000;i++){
			g.vertex(
				CCMath.random(-g.width()/2, g.width()/2),
				CCMath.random(-g.height()/2, g.height()/2)
			);
		}
		g.endShape();
		
	
	}
	
	public static void main(String[] args) {
		
		
		CCEnumDemo demo = new CCEnumDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
