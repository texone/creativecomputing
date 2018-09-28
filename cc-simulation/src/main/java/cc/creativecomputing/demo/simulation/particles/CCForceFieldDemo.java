package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCParticlePrimitiveRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticlePrimitiveRenderer.CCParticlePrimitive;

public class CCForceFieldDemo extends CCGLApp {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCForceField _myForceField;
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _cDrawAttributes = new CCDrawAttributes();
	
	@Override
	public void setup() {
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField = new CCForceField());
		
		_myParticles = new CCParticles(g, new CCParticlePrimitiveRenderer(CCParticlePrimitive.PYRAMIDE), myForces, new ArrayList<CCConstraint>(), 1000,1000);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCGLTimer theTimer) {
		_myForceField.offset().set(0,0,theTimer.time());
		for(int i = 0; i < 1600; i++){
			_myEmitter.emit(
				new CCVector3().randomize(1000),
				new CCVector3().randomize(20),
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
		g.color(255);
		g.pushMatrix();
		_cCameraController.camera().draw(g);
		_cDrawAttributes.start(g);
		_myParticles.display(g);
		_cDrawAttributes.end(g);
		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCForceFieldDemo demo = new CCForceFieldDemo();

		demo.size(1200, 600);
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		CCControlApp _myControls = new CCControlApp(myAppManager, demo);
		myAppManager.run();
	}
}
