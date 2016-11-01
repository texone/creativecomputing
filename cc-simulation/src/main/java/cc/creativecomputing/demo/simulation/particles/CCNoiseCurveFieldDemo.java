package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
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
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCNoiseCurveField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCNoiseCurveFieldDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCForceField _myForceField = new CCForceField();
	private CCAttractor _myAttractor;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<CCForce>();
		
		
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add( new CCNoiseCurveField());
		myForces.add(new CCGravity(new CCVector3(10,0,0)));
		myForces.add(_myForceField = new CCForceField());
		myForces.add(_myAttractor = new CCAttractor());
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCConstraint>(), 1000,1000);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		mouseListener().add(
			new CCMouseAdapter() {
				public void mousePressed(CCMouseEvent theEvent) {};
				
				public void mouseReleased(CCMouseEvent theEvent) {};
			}
		);
		mouseMotionListener().add(new CCMouseAdapter() {
			@Override
			public void mouseDragged(CCMouseEvent theMouseEvent) {
				_myAttractor.position().x = theMouseEvent.x() - g.width()/2;
				_myAttractor.position().y = g.height()/2 - theMouseEvent.y();
			}
		});
	}
	

	@Override
	public void update(CCAnimator theAnimator) {
		for(int i = 0; i < 4000; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(-1000,1000),CCMath.random(-_cEmitRadius, _cEmitRadius),CCMath.random(-50, 50)),
				new CCVector3().randomize(10),
				10, false
			);
		}
		_myParticles.update(theAnimator);
		
		
		_myForceField.offset().set(0,0,theAnimator.time() / 3);
		
		
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

		g.popMatrix();
		
		g.blend();
	}

	public static void main(String[] args) {

		CCNoiseCurveFieldDemo demo = new CCNoiseCurveFieldDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
