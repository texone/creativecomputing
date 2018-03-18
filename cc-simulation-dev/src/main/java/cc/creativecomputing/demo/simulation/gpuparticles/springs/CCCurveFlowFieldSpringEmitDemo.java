/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.demo.simulation.particles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCNoiseCurveField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCDampedSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringRenderer;

public class CCCurveFlowFieldSpringEmitDemo extends CCGL2Adapter {
	
	@CCProperty(name = "spring strength", min = 0, max = 4f)
	private float _cSpringStrength = 0;
	
	@CCProperty(name = "noise strength", min = 0, max = 10)
	private float _cFieldStrength = 0;
	
	@CCProperty(name = "attractor strength", min = -10, max = 10)
	private float _cAttractorStrength = 0;
	
	@CCProperty(name = "attractor radius", min = 0, max = 300)
	private float _cAttractorRadius = 0;
	
	@CCProperty(name = "gravity strength", min = 0, max = 1)
	private float _cGravityStrength = 0;
	
	@CCProperty(name = "curve strength", min = 0, max = 10)
	private float _cCurveStrength = 0;
	
	@CCProperty(name = "noise speed", min = 0, max = 1)
	private float _cCurveSpeed = 0;

	@CCProperty(name = "prediction", min = 0, max = 1)
	private float _cPrediction = 0;
	
	@CCProperty(name = "curveNoiseScale", min = 0, max = 1)
	private float _cCurveNoiseScale = 0;
	
	@CCProperty(name = "curveOutputScale", min = 0, max = 200)
	private float _cCurveOuputScale = 0;
	
	@CCProperty(name = "curveRadius", min = 0, max = 400)
	private float _cCurveRadius = 0;
	
	@CCProperty(name = "emit radius", min = 0, max = 400)
	private float _cEmitRadius = 0;

	private CCParticles _myParticles;
	private CCIndexParticleEmitter _myEmitter;
	private CCArcball _myArcball;
	
	private CCNoiseCurveField _myCurveField = new CCNoiseCurveField();
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3(100,20,30));
	private CCGravity _myGravity = new CCGravity(new CCVector3(10,0,0));
	private CCAttractor _myAttractor = new CCAttractor(new CCVector3(), 0, 0);
	
	private CCSpringRenderer _myRenderer;
	private CCParticlePointRenderer _myPointRenderer;
	private CCSprings _mySprings;
	
	private int _myNumberOfTrails = 1000;
	private int _myParticlesPerTrail = 20;
	private int _myRows = 10;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.3f));
		myForces.add(_myCurveField);
		myForces.add(_myGravity);
		myForces.add(_myForceField);
		myForces.add(_myAttractor);
		_mySprings = new CCDampedSprings(g,4,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCSpringRenderer(_mySprings);
		_myPointRenderer = new CCParticlePointRenderer();
		_myParticles = new CCParticles(g, _myRenderer, myForces, new ArrayList<CCConstraint>(), _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myParticles.addEmitter(_myEmitter = new CCIndexParticleEmitter(_myParticles));
		
		_myPointRenderer.setup(_myParticles);
		
		addControls("app", "app", this);
		addControls("app", "point",1, _myPointRenderer);
	}
	
	private float _myTime = 0;

	private int _myParticleID = 0;
	
	public void update(final float theDeltaTime){
		_myTime += 1/30f * 0.5f;
		
		for(int bla = 0; bla < 1;bla++){
			// setup leading particle pulled by the anchored spring
			float myX = -width/2;//CCMath.random(-width/2, width/2);
			float myY = 0;
			float myZ = 0;
			
			CCParticle myParticle = null;
			CCParticle myFirstParticle = null;
			
			// setup trail
			for(int i = 0; i < _myParticlesPerTrail; i++){
				float myAngle = CCMath.map(i, 0, _myParticlesPerTrail - 1, 0, CCMath.TWO_PI);
				float myAngleY = CCMath.sin(myAngle) * _cEmitRadius;
				float myAngleZ = CCMath.cos(myAngle) * _cEmitRadius;
				CCParticle myNewParticle = _myEmitter.emit(
					_myParticleID++,
					CCColor.WHITE,
					new CCVector3(myX,myY+myAngleY, myZ + myAngleZ),
					new CCVector3(),
					30, false
				);
				if(myParticle != null){
					float myDistance = myNewParticle.position().distance(myParticle.position());
					_mySprings.addSpring(myNewParticle, myParticle, myDistance, true);
				}
				else myFirstParticle = myNewParticle;
				myParticle = myNewParticle;
			}
			float myDistance = myFirstParticle.position().distance(myParticle.position());
			_mySprings.addSpring(myFirstParticle, myParticle, myDistance, true);
			}
			_myParticleID %= _myParticles.size();
		_myParticles.update(theDeltaTime);
		
		_myGravity.strength(_cGravityStrength);
		
		_myForceField.strength(_cFieldStrength);
		_myForceField.noiseOffset(new CCVector3(0,0,_myTime));
		_myForceField.noiseScale(0.0025f);
		
		_myAttractor.strength(_cAttractorStrength);
		_myAttractor.radius(_cAttractorRadius);
		_myAttractor.position().x = mouseX - width/2;
		_myAttractor.position().y = height/2 - mouseY;
		
		_myCurveField.strength(_cCurveStrength);
		_myCurveField.outputScale(_cCurveOuputScale);
		_myCurveField.speed(_cCurveSpeed);
		_myCurveField.scale(_cCurveNoiseScale / 100);
		_myCurveField.radius(_cCurveRadius);
		
		_myCurveField.prediction(_cPrediction);
		
		_mySprings.strength(_cSpringStrength);
		
		_myPointRenderer.pointSize(7);
		_myPointRenderer.update(theDeltaTime);
	}

	public void display(CCGraphics g) {
		g.clear();
		g.noDepthTest();
		g.pushMatrix();
		_myArcball.draw(g);
		g.color(255,50);
		g.blend();
//		g.pointSprite(_mySpriteTexture);
//		g.smooth();
		g.blend();
		_myParticles.draw();
//		_myPointRenderer.draw(g);
//		g.noSmooth();
//		g.noPointSprite();
		g.popMatrix();
		g.color(255);
		g.text(frameRate + ":" + _myEmitter.particlesInUse(),-width/2+20,-height/2+20);
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset(null);
			break;
		case VK_S:
			CCScreenCapture.capture("export/db04/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCurveFlowFieldSpringEmitDemo.class);
		myManager.settings().size(1900, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
