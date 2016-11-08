package cc.creativecomputing.demo.simulation.particles.contraints;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.signal.CCMixSignal;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.constraints.CCBoxConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCShapeConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCDensity;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCShapeConstraintDemo extends CCGL2Adapter {
	
	 
	
	@CCProperty(name = "noise")
	private CCMixSignal _myNoise = new CCMixSignal();
	
	@CCProperty(name = "curves", min = 3, max = 20)
	private int _myCurves = 3;
	
	
	
	private static enum CCDensityShow{
		PARTICLES, DENSITY, FORCE
	}
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	
	@CCProperty(name = "show")
	private CCDensityShow _cDensityShow = CCDensityShow.PARTICLES;
	
	private CCForceField _myForceField;
	
	private CCDensity _myDensity;
	
	private CCShapeConstraint _myShapeConstraint;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		

		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myForceField = new CCForceField());
		myForces.add(new CCGravity(new CCVector3(0,-1,0)));
		myForces.add(_myDensity = new CCDensity(g.width(), g.height()));
		_myDensity.textureOffset().set(0,0);

		final List<CCConstraint> myConstraints = new ArrayList<>();
		myConstraints.add(new CCBoxConstraint(new CCVector3(0, 0, -200), new CCVector3(g.width(), g.height(), 200), 1, 1, 1));
		myConstraints.add(_myShapeConstraint = new CCShapeConstraint(new CCVector3(1, 1, 1), new CCVector3(0,0,0), g.width(), g.height()));
		
		_myParticles = new CCParticles(g, myForces, myConstraints, 500,500);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
	}

	@Override
	public void update(CCAnimator theAnimator) {

		_myForceField.offset().set(0,0,theAnimator.time());
		
		_myParticles.update(theAnimator);
	}
	
	@CCProperty(name = "partile attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();

	@Override
	public void display(CCGraphics g) {
		_myShapeConstraint.paths().clear();
		List<CCVector2> myStartCurve = new ArrayList<>();
		for(int x = 0; x < g.width();x ++){
			myStartCurve.add(new CCVector2(x, 0));
		}
		_myShapeConstraint.paths().add(myStartCurve);
		for(int i = 0; i < _myCurves;i++){
			List<CCVector2> myCurve = new ArrayList<>();
			float mySpace = g.height() / (_myCurves + 1);
			float myBaseY = mySpace * (i + 1);
			for(int x = 0; x < g.width();x ++){
				myCurve.add(new CCVector2(x, myBaseY + (_myNoise.value(x, myBaseY, animator().time() * 1) -0.5) * mySpace));
			}
			_myShapeConstraint.paths().add(myCurve);
		}
		List<CCVector2> myEndCurve = new ArrayList<>();
		for(int x = 0; x <= g.width();x ++){
			myEndCurve.add(new CCVector2(x, g.height()));
		}
		_myShapeConstraint.paths().add(myEndCurve);
		
		
		
		for(int i = 0; i < 160; i++){
			_myEmitter.emit(
				new CCVector3(CCMath.random(g.width()), CCMath.random(g.height())),
				new CCVector3().randomize(20),
				60, false
			);
		}
		
		_myParticles.animate(g);
		
		g.ortho();
		g.clearColor(0);
		g.clear();
		g.color(1d);
		g.image(_myShapeConstraint.lookUpBuffer().attachment(0), 0, 0);
		g.image(_myShapeConstraint.curveBuffer().attachment(0), 0, 0);
		
		g.color(1d);

		for(List<CCVector2> myPath:_myShapeConstraint.paths()){
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCVector2 myPoint:myPath){
				g.vertex(myPoint);
			}
			g.endShape();
		}
		
		switch(_cDensityShow){
		case PARTICLES:
			
			g.pushMatrix();
			_cAttributes.start(g);
			g.scale(1,1,0);
			g.pointSize(3);
			_myParticles.display(g);
			_cAttributes.end(g);
			g.popMatrix();
			break;
		case DENSITY:
			g.color(255);
			g.image(_myDensity.density(), 0, 0);
			break;
		case FORCE:
			g.color(255);
			g.image(_myDensity.force(), 0, 0);
			break;
		}
	}

	public static void main(String[] args) {

		CCShapeConstraintDemo demo = new CCShapeConstraintDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

