package cc.creativecomputing.demo.control;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;

public class CCColorControl extends CCGL2Adapter{
	
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor();
	

	@CCProperty(name = "value", min = 1, max = 10)
	private double _myValue = 0;
	
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
		g.clearColor(_myColor);
		g.clear();	
	}
	
	public static void main(String[] args) {
		CCColorControl demo = new CCColorControl();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
