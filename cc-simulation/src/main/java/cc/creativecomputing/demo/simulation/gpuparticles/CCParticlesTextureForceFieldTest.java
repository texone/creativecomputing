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
package cc.creativecomputing.demo.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUTextureForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCParticlesTextureForceFieldTest extends CCApp {

	@CCControl(name = "scale x", min = 0.1f, max = 10)
	private float scaleX = 0;

	@CCControl(name = "scale y", min = 0.1f, max = 10)
	private float scaleY = 0;

	@CCControl(name = "offset x", min = -1000, max = 1000)
	private float offsetX = 0;

	@CCControl(name = "offset y", min = -1000, max = 1000)
	private float offsetY = 0;

	@CCControl(name = "force scale", min = 0, max = 20)
	private float forceScale = 0;

	@CCControl(name = "alpha", min = 0f, max = 1f)
	private float alpha = 0;

	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUTextureForceField _myForceField;
	private CCTexture2D _myForceFieldTexture;

	@Override
	public void setup() {
		_myForceFieldTexture = new CCTexture2D(CCTextureIO.newTextureData("world_stream_map.png"), CCTextureTarget.TEXTURE_RECT);
		_myForceField = new CCGPUTextureForceField(_myForceFieldTexture, new CCVector2f(1, -1), new CCVector2f(450, 225));
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		myForces.add(new CCViscousDrag(0.3f));
		_myParticles = new CCParticles(g, myForces, new ArrayList<CCGPUConstraint>(), 700, 700);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		addControls("app", "app", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		for (int i = 0; i < 1000; i++) {
			_myEmitter.emit(new CCVector3f(CCMath.random(-width / 2, width / 2), CCMath.random(-height / 2, height / 2), 0), new CCVector3f(), 5);
		}
		// _myForceField.textureScale().set(ForceFieldSettings.scaleX,
		// ForceFieldSettings.scaleY);
		// _myForceField.textureOffset().set(ForceFieldSettings.offsetX,
		// ForceFieldSettings.offsetY);
		_myForceField.strength(forceScale);

		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.blend();
		g.color(0);
		g.rect(-width / 2, -height / 2, width, height);
		g.clearDepthBuffer();
		g.color(1, 1f);
		// g.rect(-width/2, -height/2, width, height);
		// g.image(_myForceFieldTexture, -width/2, -height/2);
		g.color(1f, alpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
		g.blend();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesTextureForceFieldTest.class);
		myManager.settings().size(900, 450);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
