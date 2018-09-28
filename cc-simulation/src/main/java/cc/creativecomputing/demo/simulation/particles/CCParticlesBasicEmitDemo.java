package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;

public class CCParticlesBasicEmitDemo extends CCGLApp {
	
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void setup() {
		_myParticles = new CCParticles(g,new ArrayList<>(), new ArrayList<CCConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCGLTimer theTimer) {
		for(int i = 0; i < 800; i++){
			_myEmitter.emit(
				new CCVector3(),
				new CCVector3(CCMath.random(-100,100), CCMath.random(-100,100),CCMath.random(-100,100)),
				10, false
			);
		}
		_myParticles.update(theTimer);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g); 
		
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
