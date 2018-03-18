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
package cc.creativecomputing.demo.simulation.particles.emit;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticleGBufferEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.render.CCIndexedParticleRenderer;

public class CCGBufferEmitDemo extends CCGL2Adapter {
	
	@CCProperty(name = "draw gbuffer")
	private boolean _cDrawGBuffer = true; 
	
	private class Box{
		private float size;
		private CCVector3 position;
		private CCColor color;
		
		private Box() {
			size = CCMath.random(15, 75);
			position = new CCVector3().randomize(300);
			color = CCColor.random();
		}
		
		void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(position);
			g.color(color);
			g.box(size);
			g.popMatrix();
		}
	}
	
	private CCGeometryBuffer _myGBuffer;
	
	private CCCameraController _myCameraController;
	
	private List<Box> _myBoxes = new ArrayList<Box>();
	
	private CCParticles _myParticles;
	private CCParticleGBufferEmitter _myEmitter;

	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_myGBuffer = new CCGeometryBuffer(g,g.width(),g.height());
//		g.camera().far(1000);
		
		for(int i = 0; i< 100;i++) {
			_myBoxes.add(new Box());
		}
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());
		
		
		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();
		
		_myParticles = new CCParticles(g, new CCIndexedParticleRenderer(), myForces, myConstraints, 800, 800);
		_myParticles.addEmitter(_myEmitter = new CCParticleGBufferEmitter(_myParticles, _myGBuffer));
		
	}
	
	private CCMatrix4x4 _myInverseView = new CCMatrix4x4();
	
	@Override
	public void update(final CCAnimator theDeltaTime) {
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void display(CCGraphics g) {
		
		_myGBuffer.beginDraw(g);
		g.clearColor(0, 255);
		g.clear();
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		_myInverseView = _myGBuffer.inverseView();
		_myGBuffer.updateMatrix();
		g.color(255);
		g.depthTest();
		for(Box myBox:_myBoxes) {
			myBox.draw(g);
		}
		g.popMatrix();
		_myGBuffer.endDraw(g);
		
		_myEmitter.inverseViewMatrix(_myInverseView);
		
		g.clear();
		

		g.clearColor(0, 255);
		g.clear();
		g.color(255);
		if(_cDrawGBuffer){
			g.noBlend();
			g.texture(0,_myGBuffer.colors());
			g.beginShape(CCDrawMode.QUADS);
			g.textureCoords2D(0f, 0f);
			g.vertex(-g.width() / 2, -g.height() / 2, 0);
			g.textureCoords2D(0f, 1f);
			g.vertex(-g.width() / 2, 0, 0);
			g.textureCoords2D(1f, 1f);
			g.vertex(0, 0, 0);
			g.textureCoords2D(1f, 0f);
			g.vertex(0, -g.height() / 2, 0);
			g.endShape();
			g.noTexture();
		}
		
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.color(255, 50);
		g.blend(CCBlendMode.ADD);
		_myParticles.display(g);
		g.popMatrix();
		g.noBlend();
		
		
		
		
//		_myRenderContext.renderTexture().bindDepthTexture();
	}

	public static void main(String[] args) {
		CCGBufferEmitDemo demo = new CCGBufferEmitDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
