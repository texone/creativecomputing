package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCDensity;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCDensityDemo extends CCGL2Adapter {
	
	private static enum CCDensityShow{
		PARTICLES, DENSITY, FORCE
	}
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@CCProperty(name = "show")
	private CCDensityShow _cDensityShow = CCDensityShow.PARTICLES;
	
	private CCForceField _myForceField;
	
	private CCDensity _myDensity;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField = new CCForceField());
		myForces.add(_myDensity = new CCDensity(g.width(), g.height()));
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCConstraint>(), 500,500);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myForceField.offset().set(0,0,theAnimator.time());
		for(int i = 0; i < 160; i++){
			_myEmitter.emit(
				new CCVector3().randomize(300).multiplyLocal(1, 1, 0),
				new CCVector3().randomize(20),
				10, false
			);
		}
		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.animate(g);
		
		g.clearColor(0);
		g.clear();
		
		switch(_cDensityShow){
		case PARTICLES:
			g.noDepthTest();
			g.pushMatrix();
			g.blend(CCBlendMode.ADD);
			g.color(255);
			g.scale(1,1,0);
			_myParticles.display(g);
			g.popMatrix();
			break;
		case DENSITY:
			g.color(255);
			g.image(_myDensity.density(), -g.width() / 2, -g.height() / 2);
			break;
		case FORCE:
			g.color(255);
			g.image(_myDensity.force(), -g.width() / 2, -g.height() / 2);
			break;
		}
		
	}

	public static void main(String[] args) {

		CCDensityDemo demo = new CCDensityDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
