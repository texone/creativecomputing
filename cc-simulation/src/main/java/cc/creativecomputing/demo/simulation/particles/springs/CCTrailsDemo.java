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
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUDampedSprings;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

public class CCTrailsDemo extends CCApp {
	
	@CCControl(name = "noise scale", min = 0, max = 0.01f)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 0.01f)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1f)
	private float _cNoiseStrength = 0;
	
	@CCControl(name = "anchored spring strength", min = 0, max = 1f)
	private float _cAnchorStrength = 0;
	
	private CCParticlePointRenderer _myRenderer;
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	private CCGPUSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	
	private boolean _myPause = false;
	
	private int _myNumberOfTrails = 100;
	private int _myParticlesPerTrail = 170;
	private int _myRows = 10;
	
	private List<CCGPUParticle> _myLeadingParticles = new ArrayList<CCGPUParticle>();
	
	private CCShaderBuffer _myForceBlendTexture;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
//		myForces.add(new CCGPUGravity(new CCVector3f(0,0,-1f)));
		_myForceField.strength(2f);
		myForces.add(_myForceField);
		myForces.add(new CCGPUViscousDrag(0.3f));
		
		_myForceBlendTexture = new CCShaderBuffer(_myNumberOfTrails,_myParticlesPerTrail * _myRows);
//		myForces.add(_myIDTextureForceBlend);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myAnchoredSprings = new CCGPUAnchoredSprings(g,0.5f, 0.2f,1);
		_myAnchoredSprings.strength(2f);
		myForces.add(_myAnchoredSprings);
		
		_mySprings = new CCGPUDampedSprings(g,2,1f,0.1f,0.1f);	
		myForces.add(_mySprings);
		
		_myRenderer = new CCParticlePointRenderer();
		_myParticles = new CCGPUParticles(g, _myRenderer, myForces, myConstraints, _myNumberOfTrails,_myParticlesPerTrail * _myRows);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myRenderer.mesh().drawMode(CCDrawMode.LINES);
		_myArcball = new CCArcball(this);
		
		List<Integer> _myIDs = new ArrayList<Integer>();
		List<CCColor> _myColors = new ArrayList<CCColor>();
		
		_myForceBlendTexture.beginDraw();
		g.beginShape(CCDrawMode.POINTS);
		
		for(int myRow = 0; myRow < _myRows;myRow++) {
			for(int x = 0; x < _myNumberOfTrails; x++){
				// setup leading particle pulled by the anchored spring
				int myX = x % 500;
				int myY = x / 500 + myRow * 2;
				myX *= 5;
				myY *= 5;
				
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(myX,myY, 0),
					new CCVector3f(),
					3000, false
				);
				g.color(1f);
				g.vertex(myParticle.x(),myParticle.y(),0);
				_myLeadingParticles.add(myParticle);
				_myAnchoredSprings.addSpring(
					myParticle,new CCVector3f(myX,myY,0)
				);
				_myColors.add(new CCColor(0f,0.1f));
				
				// setup trail
				for(int y = 1; y < _myParticlesPerTrail; y++){
					CCGPUParticle myNewParticle = _myEmitter.emit(
						new CCVector3f(myX,myY, y * 10f),
						new CCVector3f(),
						3000, false
					);
					_mySprings.addSpring(myNewParticle, myParticle, 10f, true);
					_myIDs.add(myParticle.index());
					_myIDs.add(myNewParticle.index());
					_myColors.add(new CCColor(0f,(1f - (float)y / _myParticlesPerTrail) * 0.1f));
					myParticle = myNewParticle;
					
					g.color(CCMath.pow(1f - (float)y / _myParticlesPerTrail,1f));
					g.vertex(myParticle.x(),myParticle.y(),0);
				}
			}
		}
		
		g.endShape();
		_myForceBlendTexture.endDraw();
		
		_myRenderer.mesh().indices(_myIDs);
		_myRenderer.mesh().colors(_myColors);
		_myParticles.update(0);
		g.clearColor(255);

		addControls("app", "app", this);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myPause)return;
		
		_myTime += theDeltaTime * 100;
		
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myTime * _cNoiseSpeed));
		_myForceField.noiseScale(_cNoiseScale);
		_myForceField.strength(_cNoiseStrength);
		
		_myAnchoredSprings.strength(_cAnchorStrength);
		
		_myParticles.update(theDeltaTime);
//		_myMesh.vertices(_myParticles.positions());
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		
//		g.noBlend();
		g.blend();
		g.noDepthTest();
		_myParticles.draw();
		g.color(0,144);
//		_myMesh.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTrailsDemo.class);
		myManager.settings().size(1200, 800);
//		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

