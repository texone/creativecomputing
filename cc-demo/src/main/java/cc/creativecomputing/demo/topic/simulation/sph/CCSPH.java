package cc.creativecomputing.demo.topic.simulation.sph;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCSPH extends CCGL2Adapter {
	
	private SPHSystem _mySPH;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySPH = new SPHSystem();
		_mySPH.initFluid();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_mySPH.animation();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.pushMatrix();
		g.ortho();
		g.scale(600,600);
		g.beginShape(CCDrawMode.POINTS);
		for(Particle myParticle:_mySPH.getParticles()){
			if(myParticle.pos == null)continue;
			g.vertex(myParticle.pos);
		}
		g.endShape();
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCSPH demo = new CCSPH();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(600, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

