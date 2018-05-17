package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGravity;

public class CCGravityDemo extends CCGLApp {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;

	@Override
	public void setup() {
		final List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<CCConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
	}

	@Override
	public void update(CCGLTimer theTimer) {
		for(int i = 0; i < 8; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-400,400), CCMath.random(-400,400), 100),
				new CCVector3(0,00,0),
				10, false
			);
		}
		_myParticles.update(theTimer);
		angle += theTimer.deltaTime() * 10;
	}
	
	double angle = 0;

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
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

		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		CCControlApp _myControls = new CCControlApp(myAppManager, demo);
		myAppManager.run();
	}
}
