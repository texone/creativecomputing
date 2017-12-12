package cc.creativecomputing.demo.realtime.particles;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCVector2;

public class CCRealtimeParticlesDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_myParticles == null)_myParticles = new CCParticles();
		_myParticles.update(theAnimator);
		
		_myParticles.addParticle(new CCVector2(), new CCVector2().randomize(40));
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myParticles.draw(g);
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
