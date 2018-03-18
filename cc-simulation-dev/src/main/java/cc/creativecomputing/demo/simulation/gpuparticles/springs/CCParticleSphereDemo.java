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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCIcoSphere;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetForce;
import cc.creativecomputing.simulation.particles.forces.target.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticleSphereDemo extends CCApp {
	
	private CCIcoSphere _mySphere;
	private CCArcball _myArcball;
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	@CCControl(name = "spring strength", min = 0, max = 1)
	private float _cSpringStrength = 0;
	
	@CCControl(name = "spring damping", min = 0, max = 1)
	private float _cSpringDamping = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1)
	private float _cNoiseStrength = 0;
	
	@CCControl(name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "blend", min = 0, max = 1)
	private float _cBlend = 0;
	

	@Override
	public void setup() {
		_mySphere = new CCIcoSphere(new CCVector3f(), 200, 6);
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		myForces.add(new CCViscousDrag(0.2f));
		myForces.add(_myAnchoredSprings = new CCGPUAnchoredSprings(g,4f,0,10f));
		myForces.add(_myTargetForce);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCParticles(g,_myRenderer,myForces,myConstraints,300,300);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myArcball = new CCArcball(this);

		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("texone.png"),1);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		Map<Integer, List<Integer>> myIndexMap = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < _mySphere.indices().size();i+=3) {
			int i0 = _mySphere.indices().get(i);
			int i1 = _mySphere.indices().get(i + 1);
			int i2 = _mySphere.indices().get(i + 1);
			if(!myIndexMap.containsKey(i0))myIndexMap.put(i0, new ArrayList<Integer>());
			if(!myIndexMap.containsKey(i1))myIndexMap.put(i1, new ArrayList<Integer>());
			if(!myIndexMap.containsKey(i2))myIndexMap.put(i2, new ArrayList<Integer>());
			
			myIndexMap.get(i0).add(i1);
			myIndexMap.get(i0).add(i2);
			myIndexMap.get(i1).add(i0);
			myIndexMap.get(i1).add(i2);
			myIndexMap.get(i2).add(i0);
			myIndexMap.get(i2).add(i1);
		}
		
		List<CCGPUParticle> myParticles = new ArrayList<CCGPUParticle>();
		List<CCVector4f> myTextureCoords = new ArrayList<CCVector4f>();
		
		for(CCVector3f myVertex:_mySphere.vertices()) {
			CCGPUParticle myParticle = _myEmitter.emit(
				myVertex,
				new CCVector3f(),
				3000, true
			);
			myParticles.add(myParticle);
			_myAnchoredSprings.addSpring(myParticle, myVertex);
			myTextureCoords.add(new CCVector4f());
		}
		
//		for(int myKey: myIndexMap.keySet()) {
//			List<Integer> myIndices = myIndexMap.get(myKey);
//			CCGPUParticle myParticle1 = myParticles.get(myKey);
//			CCVector3f myP1 = _mySphere.vertices().get(myKey);
//			for(int myIndex:myIndices) {
//				CCGPUParticle myParticle2 = myParticles.get(myIndex);
//				CCVector3f myP2 = _mySphere.vertices().get(myIndex);
//			}
//			
//			CCGPUParticle myParticleA = myParticles.get(myIndices.get(0));
//			CCGPUParticle myParticleB = myParticles.get(myIndices.get(1));
//			
//			CCVector4f myTexCoords = myTextureCoords.get(myKey);
//			myTexCoords.x = myParticleA.x();
//			myTexCoords.y = myParticleA.y();
//			myTexCoords.z = myParticleB.x();
//			myTexCoords.w = myParticleB.y();
//		}
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", this);
		
//		_myRenderer.mesh().drawMode(CCDrawMode.TRIANGLES);
//		_myRenderer.mesh().indices(_mySphere.indices());
//		_myRenderer.mesh().textureCoords(0, myTextureCoords);
//		
//		_myGLSLShader = new CCGLSLShader(
//			CCIOUtil.classPath(this, "shader/triangles_vertex.glsl"), 
//			CCIOUtil.classPath(this, "shader/triangles_fragment.glsl")
//		);
//		_myGLSLShader.load();
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * _cNoiseSpeed;
		float myBlend = _cBlend;
		float myBlend2 = 1 - myBlend;
		_myTargetForce.strength(myBlend);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.strength(_cNoiseStrength * myBlend2);
		_myForceField.noiseScale(_cNoiseScale * 0.01f);
		_myAnchoredSprings.strength(_cSpringStrength * myBlend2);
		_myAnchoredSprings.springDamping(_cSpringDamping);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		_myArcball.draw(g);

		g.noDepthTest();
		g.color(1f, 0.1f);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
//		_myGLSLShader.start();
//		_myGLSLShader.uniform1i("positionTexture", 0);
//		_myGLSLShader.uniform("diffuse", new CCColor(red, green ,blue));
//		_myGLSLShader.uniform("ambient", new CCColor(ared, agreen ,ablue));
//		_myGLSLShader.uniform3f("lightDir", new CCVector3f(x,y,z).normalize());
//		g.texture(_myParticles.positions());
//		_myRenderer.mesh().draw(g);
//		g.noTexture();
//		_myGLSLShader.end();
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.keyCode()){
		case VK_R:
			_myParticles.reset();
			break;
		case VK_S:
			CCScreenCapture.capture("export/db03/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticleSphereDemo.class);
		myManager.settings().size(800, 600);
		myManager.start();
	}
}

