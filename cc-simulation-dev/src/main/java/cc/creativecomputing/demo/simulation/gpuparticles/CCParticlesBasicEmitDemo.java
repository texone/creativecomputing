package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;

public class CCParticlesBasicEmitDemo extends CCGL2Adapter {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myParticles = new CCParticles(g,new ArrayList<>(), new ArrayList<CCGPUConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		for(int i = 0; i < 800; i++){
			_myEmitter.emit(
				new CCVector3(),
				new CCVector3(CCMath.random(-100,100), CCMath.random(-100,100),CCMath.random(-100,100)),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.noDepthTest();
		g.clear();
		
		g.pushMatrix();
		_cCameraController.camera().draw(g);
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.display(g);
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCParticlesBasicEmitDemo demo = new CCParticlesBasicEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
