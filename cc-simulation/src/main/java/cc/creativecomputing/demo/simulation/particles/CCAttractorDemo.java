package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCAttractorDemo extends CCGLApp {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	private List<CCAttractor> _myAttractors = new ArrayList<CCAttractor>();

	@CCProperty(name = "draw emitters")
	private boolean _cDrawEmitters = false;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@Override
	public void setup() {
		final List<CCForce> myForces = new ArrayList<CCForce>();
//		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		myForces.add(new CCViscousDrag(0.3f));
		
		for(int i = 0; i < 9;i++){
			CCAttractor myAttractor = new CCAttractor(
				new CCVector3().randomize(1000),
				1000
			);
			myForces.add(myAttractor);
			_myAttractors.add(myAttractor);
		}
		
		_myParticles = new CCParticles(g,myForces, new ArrayList<CCConstraint>(),600,600);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		_myParticles.reset(g);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCGLTimer theTimer) {
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
		g.blend(CCBlendMode.ADD);
		g.color(255,50);
		_myParticles.display(g);
		
		if(_cDrawEmitters){
			g.color(255,10);
			g.polygonMode(CCPolygonMode.LINE);
			for(CCAttractor myAttractor:_myAttractors){
				g.pushMatrix();
				g.translate(myAttractor.position());
				g.box(myAttractor.radius()/2);
				g.popMatrix();
			}
			g.polygonMode(CCPolygonMode.FILL);
		}

		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCAttractorDemo demo = new CCAttractorDemo();
		demo.size(1200, 600);
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		CCControlApp _myControls = new CCControlApp(myAppManager, demo);
		myAppManager.run();
	}
}
