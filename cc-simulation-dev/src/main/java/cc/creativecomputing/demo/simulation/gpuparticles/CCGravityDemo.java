package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;

public class CCGravityDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<CCGPUConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		for(int i = 0; i < 800; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-100,100), 100,CCMath.random(-100,100)),
				new CCVector3(0,00,0),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}
	
	double angle = 0;

	@Override
	public void display(CCGraphics g) {
		g.noDepthTest();
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(0, 0, -1000);
		g.rotateY(angle);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.display(g);
		
		

		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCGravityDemo demo = new CCGravityDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
