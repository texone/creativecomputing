package cc.creativecomputing.demo.realtime.particles;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

public class CCRealtimeParticlesDemo extends CCGL2Adapter {
	
	@CCProperty(name = "rectColor")
	private CCColor _crectColor = new CCColor();
	
	@CCProperty(name = "particles")
	private CCParticles _cParticles = new CCParticles();
	
	@CCProperty(name = "test", min = 0, max = 1)
	private double _ctest = 1; 

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(_crectColor);
		g.rect(0,0,100,10);
	}

	public static void main(String[] args) {

		CCRealtimeParticlesDemo demo = new CCRealtimeParticlesDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
