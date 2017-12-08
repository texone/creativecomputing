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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

public class CCParticlesSpringsAttractorTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private CCGPUAttractor _myAttractor;
	
	private CCParticlePointRenderer _myRenderer;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		_myForceField.strength(1f);
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUSprings(g,4,2f,2.5f);
		myForces.add(_mySprings);
		
		_myAttractor = new CCGPUAttractor(new CCVector3f(-1000,2000,0), -5f, 300);
		myForces.add(_myAttractor);
		
		_myRenderer = new CCParticlePointRenderer();
		_myParticles = new CCGPUParticles(g,_myRenderer,myForces,myConstraints,600,600);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		
		CCGPUParticle[] myLeftParticles = new CCGPUParticle[600];
		
		for(int x = 0; x < 600; x++){
			CCGPUParticle myParticleAbove = null;
			for(int y = 0; y < 600; y++){
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(x * 2.5f - 750, y * 2.5f - 750,100),
					new CCVector3f(),
					3000, false
				);
				
				if(myParticleAbove != null) {
					_mySprings.addSpring(myParticleAbove, myParticle, 2.5f);
				}
				
				if(myLeftParticles[y] != null) {
					_mySprings.addSpring(myLeftParticles[y], myParticle, 2.5f);
				}

				myParticleAbove = myParticle;
				myLeftParticles[y] = myParticle;
			}
		}
		
		List<Integer> _myIndices = new ArrayList<Integer>();
		int counter = 0;
		for(int x = 0; x < 600; x++){
			for(int y = 0; y < 600; y++){
				if(y > 0)_myIndices.add(counter - 1);
				_myIndices.add(counter);
				if(y < 300 - 1)_myIndices.add(counter + 1);
				counter++;
				if(x < 599) {
					_myIndices.add(counter);
					_myIndices.add(counter + 600);
				}
				if(y < 599) {
					_myIndices.add(counter);
					_myIndices.add(counter + 1);
				}
			}
		}
		_myArcball = new CCArcball(this);
		
		_myRenderer.mesh().indices(_myIndices);
		
		g.strokeWeight(0.5f);
		g.clearColor(255);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 10;
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		
//		if(mousePressed)_myAttractor.strength(1f);
//		else _myAttractor.strength(0);
		_myAttractor.position().x = mouseX - width/2;
		_myAttractor.position().y = -mouseY + height/2;
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		g.scale(0.5f);
		_myArcball.draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0,0.25f);
		_myParticles.draw();
//		g.noTexture();
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSpringsAttractorTest.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

