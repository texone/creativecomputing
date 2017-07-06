package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

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
		
		g.rect(0,0,110,100);
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
