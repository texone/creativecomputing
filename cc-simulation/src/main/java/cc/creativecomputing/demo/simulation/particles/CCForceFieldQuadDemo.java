package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCParticleQuadRenderer;

public class CCForceFieldQuadDemo extends CCGL2Adapter {
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCForceField _myForceField;
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _cDrawAttributes = new CCDrawAttributes();
	
	@CCProperty(name = "emitamount", min = 0, max = 10000)
	private double _cEmitAmount = 100;
	@CCProperty(name = "lifetime", min = 0, max = 60)
	private double _cLifeTime = 10;
	@CCProperty(name = "radius", min = 0, max = 2000)
	private double _cRadius = 400;
	@CCProperty(name = "gradient")
	private CCGradient _myGradient = new CCGradient();
	@CCProperty(name = "clear color")
	private CCColor _cClearColor = new CCColor();
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField = new CCForceField());
		
		_myParticles = new CCParticles(g, new CCParticleQuadRenderer(), myForces, new ArrayList<CCConstraint>(), 300,300);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		_cScreenCapture = new CCScreenCaptureController(this, theAnimator);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myForceField.offset().set(0,0,theAnimator.time());
		for(int i = 0; i < _cEmitAmount * theAnimator.deltaTime(); i++){
			_myEmitter.emit(
				_myGradient.color(theAnimator.time() / 2 % 1),
				new CCVector3().randomize().normalizeLocal().multiplyLocal(_cRadius),
				new CCVector3().randomize(20),
				_cLifeTime, false
			);
		}
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
		g.clearColor(_cClearColor);
		//g.noDepthTest();
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

		CCForceFieldQuadDemo demo = new CCForceFieldQuadDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
