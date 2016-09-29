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
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;
import cc.creativecomputing.util.CCFormatUtil;
/**
 * @author christianriekoff
 *
 */
public class CCMultipleColorDemo extends CCApp{

	@CCControl(name = "life time", min = 0, max = 10)
	private float _cLifeTime = 3f;

	@CCControl(name = "emit amount", min = 0, max = 1000)
	private float _cEmit = 3f;

	@CCControl(name = "init vel", min = 0, max = 1000)
	private float _cInitVel = 3f;

	@CCControl(name = "random vel", min = 0, max = 10)
	private float _cRandomVel = 3f;

	@CCControl(name = "random pos", min = 0, max = 30)
	private float _cRandomPos = 3f;

	@CCControl(name = "color pos", min = 0, max = 100)
	private float _cColorPos = 3f;

	@CCControl(name = "gx", min = -1, max = 1)
	private float _cX = 0;

	@CCControl(name = "gy", min = -1, max = 1)
	private float _cY = 0;

	@CCControl(name = "gz", min = -1, max = 1)
	private float _cZ = 0;

	@CCControl(name = "g strength", min = 0, max = 1)
	private float _cGStrength = 0;

	@CCControl(name = "attractor strength", min = -10, max = 10)
	private float _cAttractorStrength = 0;

	@CCControl(name = "attractor radius", min = 0, max = 300)
	private float _cAttractorRadius = 0;

	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 0;

	@CCControl(name = "trace", min = 0, max = 1)
	private float _cTrace = 0;

	@CCControl(name = "trace change", min = 0, max = 1)
	private float _cTraceChange = 0;
	

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;

	private CCGravity _myGravity;

	private CCForceField _myForceField;
	private CCAttractor _myAttractor;

	@CCControl(name = "n scale", min = 0, max = 1)
	private float _cNScale = 0;

	@CCControl(name = "n strength", min = 0, max = 1)
	private float _cNStrength = 0;

	@CCControl(name = "n speed", min = 0, max = 3)
	private float _cNSpeed = 0;

	private List<CCColor> _myColors = new ArrayList<CCColor>();
	
	private float _myTrace = 1;

	@Override
	public void setup() {
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myGravity = new CCGravity(new CCVector3f()));
		myForces.add(_myForceField = new CCForceField(0.01f, 1f, new CCVector3f()));
		myForces.add(_myAttractor = new CCAttractor(new CCVector3f(), 0, 100));
		// myForces.add(new CCGPUViscousDrag(0.2f));


		_myParticles = new CCParticles(g, new CCGPUIndexedParticleRenderer(),myForces, new ArrayList<CCGPUConstraint>(), 300, 300);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		addControls("app", "app", this);

		_myColors.add(new CCColor(1f,1f,0f));
		_myColors.add(new CCColor(0f,1f,1f));
		_myColors.add(new CCColor(1f,0f,1f));
	}

	float _myOffset = 0;

	@Override
	public void update(final float theDeltaTime) {
		int x = 0;
		for (CCColor myColor : _myColors) {
			float myX = _cColorPos * x - (_myColors.size() - 1) / 2 * _cColorPos;
			for (int i = 0; i < _cEmit; i++) {
				CCGPUParticle myParticle = _myEmitter.emit(
					myColor,
					new CCVector3f(myX, -300, 0).add(CCVecMath.random3f(CCMath.random(_cRandomPos))), 
					new CCVector3f(0, _cInitVel, 0).add(CCVecMath.random3f(CCMath.random(_cRandomVel))), _cLifeTime);
				if(myParticle == null)break;
			}
			x++;
		}

		if (mousePressed) {
			_myAttractor.strength(_cAttractorStrength);
			_myAttractor.position().set(mouseX - width / 2, height / 2 - mouseY, 0);
			_myAttractor.radius(_cAttractorRadius);
			
			_myTrace -= _cTraceChange * theDeltaTime;
		} else {
			_myAttractor.strength(0);
			_myTrace += _cTraceChange * theDeltaTime;
		}
		_myTrace = CCMath.constrain(_myTrace, _cTrace, 1f);

		_myOffset += theDeltaTime * _cNSpeed;

		_myGravity.direction().set(_cX, _cY, _cZ);
		_myGravity.strength(_cGStrength);

		_myForceField.noiseScale(_cNScale);
		_myForceField.strength(_cNStrength);
		_myForceField.noiseOffset(new CCVector3f(0, 0, _myOffset));

		_myParticles.update(theDeltaTime * 2);
	}

	@Override
	public void draw() {
		g.clear();
		g.noDepthTest();
		g.pointSize(0.1f);
		g.color(1f, _cAlpha);
		_myParticles.draw();
		// g.ellipse(_myAttractor.position(), _myAttractor.radius());
		g.noBlend();
		
		g.color(255);
//		g.image(_myParticles.dataBuffer().attachment(3), -width/2,-height/2);
	}

	private int i = 0;

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_R:
			_myParticles.reset();
			break;
		case VK_S:
			CCScreenCapture.capture("export/orchid/" + CCFormatUtil.nf(i++, 4) + ".png", width, height);
			break;
		case VK_C:
			for (CCColor myColor : _myColors) {
				myColor.setHSB(CCMath.random(), 1f, 1f);
			}
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMultipleColorDemo.class);
		myManager.settings().size(1400, 900);
		myManager.settings().vsync(true);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
