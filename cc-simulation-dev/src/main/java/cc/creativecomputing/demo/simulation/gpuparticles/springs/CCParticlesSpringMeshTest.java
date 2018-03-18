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
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.math.util.CCBezierCurve;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.springs.CCGPUSprings;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;

public class CCParticlesSpringMeshTest extends CCApp {
	
	private CCParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUSprings _mySprings;
	
	private CCForceField _myForceField = new CCForceField(0.005f,1,new CCVector3f(100,20,30));
	
	private float _myX = 0;
	private CCArcball _myArcball;
	
	private List<CCBezierCurve> _myBezierCurves = new ArrayList<CCBezierCurve>();
	CCBezierCurve _myCurve;
	
	private int _myXres = 400;
	private int _myYres = 400;
	
	private CCGLSLShader _myGLSLShader;

	@Override
	public void setup() {
//		frameRate(30);
		
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(_myForceField);
		_myForceField.strength(1f);
		
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_mySprings = new CCGPUSprings(g,4,4f,5f);
		
		myForces.add(_mySprings);
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCParticles(g,_myRenderer,myForces,myConstraints,_myXres,_myYres);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));
		_myArcball = new CCArcball(this);
		
		float myXspace = 1500f / _myXres;
		float myYspace = 1500f / _myYres;
		
		CCGPUParticle[] myLeftParticles = new CCGPUParticle[_myYres];
		
//		List<Integer> _myIndices = new ArrayList<Integer>();
//		
		for(int x = 0; x < _myXres; x++){
			CCGPUParticle myParticleAbove = null;
			for(int y = 0; y < _myYres; y++){
				CCGPUParticle myParticle = _myEmitter.emit(
					new CCVector3f(x * myXspace - 750, 200, y * myYspace - 750),
					new CCVector3f(),
					3000, true
				);
				
				if(myParticleAbove != null) {
					_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
				}
				
				if(myLeftParticles[y] != null) {
					_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
				}

				myParticleAbove = myParticle;
				myLeftParticles[y] = myParticle;
			}
		}
//		
//		int counter = 0;
//		for(int x = 0; x < _myXres; x++){
//			for(int y = 0; y < _myYres; y++){
//				if(x < _myXres - 1 && y < _myYres - 1) {
//					_myIndices.add(counter);
//					_myIndices.add(counter + _myXres);
//					_myIndices.add(counter + _myXres + 1);
//					_myIndices.add(counter + 1);
//				}
//				counter++;
////				_mySprings.addSpring(
////					new CCVector2i(x,y),
////					x < 299	? new CCVector2i(x + 1, y) : new CCVector2i(-1, -1),
////					x > 0 	? new CCVector2i(x - 1, y) : new CCVector2i(-1, -1),
////					y < 299 ? new CCVector2i(x, y + 1) : new CCVector2i(-1, -1),
////					y > 0 	? new CCVector2i(x, y - 1) : new CCVector2i(-1, -1)
////				);
//			}
//		}
//		
//		_myRenderer.mesh().drawMode(CCDrawMode.QUADS);
//		_myRenderer.mesh().indices(_myIndices);
		
		g.strokeWeight(0.5f);
		g.clearColor(255);
		

		_myGLSLShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "shader/triangles_vertex.glsl"), 
			CCIOUtil.classPath(this, "shader/triangles_fragment.glsl")
		);
		_myGLSLShader.load();

		createCurves();
		_myCurve = _myBezierCurves.get(0);
		
		addControls("light","light", this);
	}
	

	private void createCurves() {
		CCVector3f _myStartVector = CCVecMath.random3f(CCMath.random(200,400));
		if(_myStartVector.y < 0)_myStartVector.scale(1, -1, 1);
		

		
		CCVector3f myAnchor1 = CCVecMath.random3f(CCMath.random(500,1000));
		if(myAnchor1.y < 0)myAnchor1.scale(1, -1, 1);
		myAnchor1.add(_myStartVector);
		
		_myBezierCurves.clear();
		for(int i = 0; i < 10;i++) {
			
			CCBezierCurve myCurve = new CCBezierCurve(_myStartVector);
			

			CCVector3f _myEndVector = CCVecMath.random3f(CCMath.random(200,400));
			if(_myEndVector.y < 0)_myEndVector.scale(1, -1, 1);
			
			_myEndVector.scale(1,1f,1);
			
			CCVector3f myAnchor2 = CCVecMath.random3f(CCMath.random(1000,2000));
			if(myAnchor2.y < 0)myAnchor2.scale(1, -1, 1);
			myAnchor2.add(_myEndVector);
			

			
			myCurve.addSegmentToEnd(myAnchor1, myAnchor2, _myEndVector);
			_myBezierCurves.add(myCurve);
			
			_myStartVector = _myEndVector.clone();
			myAnchor1 = myAnchor2.clone();
		}
	}
	
	private float _myTime = 0;

	private float _myCameraTime = 0;
	private int _myCameraIndex = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 100;
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		
		_myParticles.update(theDeltaTime);
		
		_myCameraTime += theDeltaTime;
		
		if(_myCameraTime> 10) {
			_myCameraTime = 0;
			_myCameraIndex++;
			if(_myCameraIndex >= _myBezierCurves.size()) {
//				reset();
				_myCameraIndex = 0;
			}
			createCurves();
			_myCurve = _myBezierCurves.get(_myCameraIndex);
		}
	}

	@Override
	public void draw() {
		g.clear();
//		g.scale(0.5f);
		g.translate(_myX, 0);
		_myArcball.draw(g);
//		g.texture(_myTexture);
		
		g.blend();
//		g.noDepthTest();
		g.color(0);
//		g.smooth();
//		_myGLSLShader.start();
//		_myGLSLShader.uniform1i("positionTexture", 0);
//		_myGLSLShader.uniform("diffuse", new CCColor(red, green ,blue));
//		_myGLSLShader.uniform("ambient", new CCColor(ared, agreen ,ablue));
//		_myGLSLShader.uniform("specular", new CCColor(sred, sgreen, sblue));
//		_myGLSLShader.uniform1f("shininess", shininess);
//		_myGLSLShader.uniform3f("lightDir", new CCVector3f(x,y,z).normalize());
//		_myGLSLShader.uniform("focalDistance", focalDistance);
//		_myGLSLShader.uniform("focalRange", focalRange);
//		g.texture(_myParticles.positions());
//		_myRenderer.mesh().draw(g);
//		g.noTexture();
//		_myGLSLShader.end();
//		g.noSmooth();
//		g.noTexture();
		_myParticles.draw();
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_R:
			_myParticles.reset();
			
			float myXspace = 1500f / _myXres;
			float myYspace = 1500f / _myYres;
			
			CCGPUParticle[] myLeftParticles = new CCGPUParticle[_myYres];
			
//			List<Integer> _myIndices = new ArrayList<Integer>();
//			
			for(int x = 0; x < _myXres; x++){
				CCGPUParticle myParticleAbove = null;
				for(int y = 0; y < _myYres; y++){
					CCGPUParticle myParticle = _myEmitter.emit(
						new CCVector3f(x * myXspace - 750, 200, y * myYspace - 750),
						new CCVector3f(),
						3000, true
					);
					
					if(myParticleAbove != null) {
						_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
					}
					
					if(myLeftParticles[y] != null) {
						_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
					}

					myParticleAbove = myParticle;
					myLeftParticles[y] = myParticle;
				}
			}
			break;
		case VK_S:
			CCScreenCapture.capture("export/springs/"+frameCount+".png", width, height);
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticlesSpringMeshTest.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

