/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.demo.simulation.gpuparticles.rendering;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUDampedSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCGPUParticleDataDebugRenderer;
import cc.creativecomputing.simulation.particles.render.CCGPUSpringRenderer;

public class CCSpringRendererDemo extends CCApp {
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 0.01f)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 4f)
	private float _cNoiseStrength = 0;
	
	@CCControl(name = "spring strength", min = 0, max = 4f)
	private float _cSpringStrength = 0;
	
	private CCGPUSpringRenderer _myRenderer;
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUSprings _mySprings;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = true;
	
	private int _myNumberOfTrails = 1000;
	private int _myParticlesPerTrail = 7;
	private int _myRows = 10;
	
	private List<CCGPUParticle> _myLeadingParticles = new ArrayList<CCGPUParticle>();
	
	private CCGPUParticleDataDebugRenderer _myDebugRenderer;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCForce> myForces = new ArrayList<CCForce>();
//		myForces.add(new CCGPUGravity(new CCVector3f(0,0,-1f)));
		_myForceField.strength(2f);
		myForces.add(_myForceField);
		myForces.add(new CCViscousDrag(0.3f));
		
//		myForces.add(_myIDTextureForceBlend);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUDampedSprings(g,2,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUSpringRenderer(_mySprings);
		_myParticles = new CCParticles(g, _myRenderer, myForces, myConstraints, _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myArcball = new CCArcball(this);
		
		_myParticles.update(0);
		g.clearColor(255);

		addControls("app", "app", this);
		
		_myDebugRenderer = new CCGPUParticleDataDebugRenderer(_myParticles);
		addControls("debug", "debug", _myDebugRenderer);
	}
	
	private float _myTime = 0;
	
	private int _myParticleID = 0;

	@Override
	public void update(final float theDeltaTime) {
//		if(_myPause)return;
//		_myPause = true;
		
		for(int bla = 0; bla < 10;bla++){
		// setup leading particle pulled by the anchored spring
		float myX = CCMath.random(-30, 30);
		float myY = CCMath.random(-30, 30);
		
		CCGPUParticle myParticle = _myEmitter.emit(
			_myParticleID++,
			CCColor.WHITE,
			new CCVector3f(myX,myY, 0),
			new CCVector3f(),
			30, false
		);
		_myLeadingParticles.add(myParticle);
		
		// setup trail
		for(int y = 1; y < _myParticlesPerTrail; y++){
			CCGPUParticle myNewParticle = _myEmitter.emit(
				_myParticleID++,
				CCColor.WHITE,
				new CCVector3f(myX,myY, y * 10f),
				new CCVector3f(),
				30, false
			);
			_mySprings.addSpring(myNewParticle, myParticle, 10f, true);
			myParticle = myNewParticle;
		}
		}
		_myParticleID %= _myParticles.size();
		
		_myTime += theDeltaTime * 100;
		
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime * _cNoiseSpeed));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		_mySprings.strength(_cSpringStrength);
		_myParticles.update(theDeltaTime);
//		_myMesh.vertices(_myParticles.positions());
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch(theKeyEvent.keyCode()){
		case VK_P:
			_myPause = !_myPause;
			break;
		default:
		}
	}

	@Override
	public void draw() {
		g.clear();
		g.pushMatrix();
		_myArcball.draw(g);
		
//		g.noBlend();
		g.blend();
		g.noDepthTest();
		g.color(0, 50);
		_myParticles.draw();
//		_myMesh.draw(g);
		
		g.clearDepthBuffer();
		g.popMatrix();
		
		g.color(255);
		_myDebugRenderer.draw(g);
//		_myDebugRenderer.drawData(g, _mySprings.idBuffer(0).attachment(0), _myParticles.width() + _mySprings.idBuffer(0).width(), 1);
//		_myDebugRenderer.drawData(g, _mySprings.tmpidBuffer(0).attachment(0), _myParticles.width() + _mySprings.idBuffer(0).width()*2, 1);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSpringRendererDemo.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

