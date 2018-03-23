package cc.creativecomputing.demo;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCEmptyApp extends CCGL2Adapter{
	
	
	
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	
	
	@Override
	public void display(CCGraphics g) {
		
		g.clear();
		for(int i = 0; i < 200; i++){

			g.color(CCColor.random());
			g.rect(CCMath.random(200),CCMath.random(200),110,100);
		}
	}
	
	public static void main(String[] args) {
		
		
		CCEmptyApp demo = new CCEmptyApp();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
