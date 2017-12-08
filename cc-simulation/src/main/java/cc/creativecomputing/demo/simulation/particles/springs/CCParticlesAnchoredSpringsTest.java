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
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCTileSaver;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUAttractor;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUAnchoredSprings;

public class CCParticlesAnchoredSpringsTest extends CCApp {
	
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUAnchoredSprings _mySprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private CCArcball _myArcball;
	private CCGPUAttractor _myAttractor;
	
	private boolean _myPause = false;
	
	private CCTileSaver _myTileSaver;
	
	private int _myXres = 600;
	private int _myYres = 600;
	
	private float _mySpace = 2f;

	@Override
	public void setup() {
		_myTileSaver = new CCTileSaver(g);
//		
//		
		addUpdateListener(_myTileSaver);
		addPostListener(_myTileSaver);
		
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0,-1,0)));
		_myForceField.strength(1f);
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUAnchoredSprings(g,0.1f,0f,5);
		myForces.add(_mySprings);
		
		_myAttractor = new CCGPUAttractor(new CCVector3f(-1000,0,0), -4f, 500);
//		_myAttractor.strength(0.5f);
		myForces.add(_myAttractor);
		_myParticles = new CCGPUParticles(g,myForces, myConstraints, _myXres,_myYres);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myArcball = new CCArcball(this);
		
		float myWidth = _myXres * _mySpace;
		float myHeight = _myYres * _mySpace;
		
		for(int x = 0; x < _myXres; x++){
			for(int y = 0; y < _myYres; y++){
				float myX = x * _mySpace - myWidth / 2;
				float myZ = y * _mySpace - myHeight / 2;
				
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(myX, 200, myZ),
					new CCVector3f(),
					3000, false
				);
				_mySprings.addSpring(
					myParticle,new CCVector3f(x * _mySpace - myWidth / 2, 195, y * _mySpace - myHeight / 2)
				);
			}
		}
		
		g.clearColor(255);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		if(_myPause)return;
		
		_myTime += theDeltaTime * 100;
		
		if(keyPressed)_myAttractor.strength(1);
		else _myAttractor.strength(0);
		_myAttractor.position().x = mouseX - width/2;
		_myAttractor.position().z = -mouseY + height/2;
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
//		_myForceField.noiseScale((CCMath.sin(_myTime * 0.5f)+1) * 0.0025f + 0.005f);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
//		g.scale(0.5f);
		_myArcball.draw(g);
//		g.texture(_myTexture);
		
		g.blend();
		g.noDepthTest();
		g.color(0);
		_myParticles.draw();
//		g.noTexture();
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_S:
			_myTileSaver.init("export_tile/"+frameCount+".png");
//			CCScreenCapture.capture("export/"+frameCount+".png", width, height);
			break;
		case VK_P:
			_myPause = !_myPause;
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesAnchoredSpringsTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

