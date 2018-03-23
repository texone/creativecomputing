package cc.creativecomputing.demo.simulation.particles.contraints;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCSphereConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;

public class CCSphereContraintDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		
		final List<CCConstraint> myContraints = new ArrayList<>();
		myContraints.add(new CCSphereConstraint(new CCVector3(), 200, 1, 1, 1));
		
		_myParticles = new CCParticles(g,myForces, myContraints,600,600);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
		
		_myCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		for(int i = 0; i < 800; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-200,200), 300,CCMath.random(-200,200)),
				new CCVector3(0,00,0),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
		
		g.noDepthTest();
		g.clear();
		g.color(255);
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.display(g);
		
		

		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCSphereContraintDemo demo = new CCSphereContraintDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
