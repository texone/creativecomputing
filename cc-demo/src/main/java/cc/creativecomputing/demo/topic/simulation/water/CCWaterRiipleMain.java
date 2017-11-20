package cc.creativecomputing.demo.topic.simulation.water;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCWaterRiipleMain extends CCGL2Adapter {
	
	private CCWater _myWater;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myWater = new CCWater();
		for (int i = 0; i < 20; i++) {
			_myWater.addDrop(g,Math.random() * 2 - 1, Math.random() * 2 - 1, 0.03, (i %2 == 1) ? 0.01 : -0.01);
		}
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255,0,0);
		g.clear();
//		_myWater.stepSimulation(g);
//		_myWater.stepSimulation(g);
//		_myWater.updateNormals(g);
		
		g.image(_myWater.textureA(),0,0);
	}

	public static void main(String[] args) {

		CCWaterRiipleMain demo = new CCWaterRiipleMain();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

