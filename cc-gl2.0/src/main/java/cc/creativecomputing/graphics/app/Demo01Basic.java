package cc.creativecomputing.graphics.app;

import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.graphics.CCGraphics;

public class Demo01Basic extends CCGL2Adapter{
	
	@Override
	public void init(CCGraphics g) {
		
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
	}
	
	public static void main(String[] args) {
		
		
		Demo01Basic demo = new Demo01Basic();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
