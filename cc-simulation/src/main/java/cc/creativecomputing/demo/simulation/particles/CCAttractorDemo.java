package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCAttractorDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	private List<CCAttractor> _myAttractors = new ArrayList<CCAttractor>();

	@CCProperty(name = "draw emitters")
	private boolean _cDrawEmitters = false;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
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
	public void update(CCAnimator theAnimator) {
		for(int i = 0; i < 1600; i++){
			_myEmitter.emit(
				new CCVector3().randomize(1000),
				new CCVector3().randomize(20),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
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
				g.sphere(myAttractor.radius()/2);
				g.popMatrix();
			}
			g.polygonMode(CCPolygonMode.FILL);
		}

		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCAttractorDemo demo = new CCAttractorDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
