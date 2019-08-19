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
package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCTextureForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;

public class CCTextureForceFieldDemo extends CCGL2Adapter {

	@CCProperty(name = "alpha", min = 0f, max = 1f)
	private float alpha = 0;

	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	private CCParticlesIndexParticleEmitter _myEmitter;
	private CCTextureForceField _myForceField;
	private CCTexture2D _myForceFieldTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myForceFieldTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("world_stream_map.png")), CCTextureTarget.TEXTURE_RECT);
		_myForceField = new CCTextureForceField(_myForceFieldTexture, new CCVector2(1, -1), new CCVector2(450, 225));
		final List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		myForces.add(new CCViscousDrag(0.3f));
		
		_myParticles = new CCParticles(g, myForces, new ArrayList<>(), new ArrayList<CCConstraint>(), 1000, 1000);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));
	}

	@Override
	public void update(final CCAnimator theAnimator) {
		for (int i = 0; i < 1000; i++) {
			_myEmitter.emit(new CCVector3(CCMath.random(-450, 450), CCMath.random(-200, 200), 0), new CCVector3(), 15);
		}
		// _myForceField.textureScale().set(ForceFieldSettings.scaleX,
		// ForceFieldSettings.scaleY);
		// _myForceField.textureOffset().set(ForceFieldSettings.offsetX,
		// ForceFieldSettings.offsetY);
		

		_myParticles.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		
		g.blend();
		g.color(0);
		g.rect(-g.width() / 2, -g.height() / 2, g.width(), g.height());
		g.clearDepthBuffer();
		g.color(1, 1f);
		// g.rect(-width/2, -height/2, width, height);
		// g.image(_myForceFieldTexture, -width/2, -height/2);
		g.color(1f, alpha);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		g.blend();
	}

	public static void main(String[] args) {
		CCTextureForceFieldDemo demo = new CCTextureForceFieldDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
