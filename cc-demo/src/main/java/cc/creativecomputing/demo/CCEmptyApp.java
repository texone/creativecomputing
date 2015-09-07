package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
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
		
		
	}
	
	public static void main(String[] args) {
		
		
		CCEmptyApp demo = new CCEmptyApp();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
