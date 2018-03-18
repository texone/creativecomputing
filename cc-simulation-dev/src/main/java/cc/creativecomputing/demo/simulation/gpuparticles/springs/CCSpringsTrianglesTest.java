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
package cc.creativecomputing.demo.simulation.particles.springs;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.constraints.CCGPUConstraint;
import cc.creativecomputing.simulation.particles.constraints.CCGPUTerrainConstraint;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;
import cc.creativecomputing.simulation.particles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.particles.forces.CCGPUGravity;
import cc.creativecomputing.simulation.particles.forces.springs.CCDampedSprings;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

public class CCSpringsTrianglesTest extends CCApp {

	public class CCHeightMap {

		private CCVBOMesh _myMesh;
		private CCTextureData _myHeightMapTexture;

		// Mesh Generation Paramaters
		private float _myMeshResolution = 1;
		private int _myMeshWidth = 2000;
		private int _myMeshHeight = 1400;

		public CCHeightMap(String theHeightMapTexture) {

			_myHeightMapTexture = CCTextureIO.newTextureData(theHeightMapTexture);

			_myMeshWidth = _myHeightMapTexture.width();
			_myMeshHeight = _myHeightMapTexture.height();

			// Generate Vertex Field
			final int myNumberOfVertices = (int) (_myMeshWidth * _myMeshHeight * 6 / (_myMeshResolution * _myMeshResolution));
			FloatBuffer myVertices = FloatBuffer.allocate(myNumberOfVertices * 3);
			FloatBuffer myTexCoords = FloatBuffer.allocate(myNumberOfVertices * 2);

			_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, myNumberOfVertices);

			for (int nZ = 0; nZ < _myMeshHeight; nZ += (int) _myMeshResolution) {
				for (int nX = 0; nX < _myMeshWidth; nX += (int) _myMeshResolution) {
					for (int nTri = 0; nTri < 6; nTri++) {

						float myX = (float) nX + ((nTri == 2 || nTri == 4 || nTri == 5) ? _myMeshResolution : 0.0f);
						float myZ = (float) nZ + ((nTri == 1 || nTri == 2 || nTri == 5) ? _myMeshResolution : 0.0f);

						myVertices.put(myX - (_myMeshWidth / 2f));
						float heightFromPixel = _myHeightMapTexture.getPixel((int) myX, (int) myZ).brightness();
						myVertices.put(heightFromPixel);
						myVertices.put(myZ - (_myMeshHeight / 2f));

						myTexCoords.put(myX / _myMeshWidth);
						myTexCoords.put(1 - myZ / _myMeshHeight);
					}
				}
			}
			_myMesh.vertices(myVertices);
			_myMesh.textureCoords(myTexCoords);
		}

		public void draw(final CCGraphics g) {
			g.pushMatrix();
			g.translate(0, -200, 0);
			g.scale(6, 90, 6);
			_myMesh.draw(g);
			g.popMatrix();
		}

		public void meshResolution(float theResolution) {
			_myMeshResolution = theResolution;
		}
	}
	
	private CCParticlePointRenderer _myRenderer;
	private CCGPUParticles _myParticles;
	private CCGPUIndexParticleEmitter _myEmitter;
	private CCDampedSprings _mySprings;

	private boolean _myPause = false;
	private float _myScale = 1;

	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f, 1, new CCVector3f(100, 20, 30));

	private CCArcball _myArcball;

	private CCTexture2D _myTexture;
	private CCGPUTerrainConstraint _myTerrainConstraint;

	@Override
	public void setup() {
//		fixUpdateTime(1 / 50f);
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(new CCGPUGravity(new CCVector3f(0, -2.7f, 0)));
		myForces.add(_myForceField);
		_myForceField.strength(5f);

		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("texone.png"), CCTextureTarget.TEXTURE_RECT);
		_myTerrainConstraint = new CCGPUTerrainConstraint(_myTexture, new CCVector3f(6f, 400, 6f), new CCVector3f(400, -200, 225), 0.8f, 0.1f, 0.01f);
		myConstraints.add(_myTerrainConstraint);

		_mySprings = new CCDampedSprings(g, 4, 0.2f, 0.01f, 25f);

		myForces.add(_mySprings);

		_myRenderer = new CCParticlePointRenderer();
		_myParticles = new CCGPUParticles(g, _myRenderer, myForces, myConstraints, 500, 500);
		_myParticles.addEmitter(_myEmitter = new CCGPUIndexParticleEmitter(_myParticles));

		List<Integer> _myIndices = new ArrayList<Integer>();
		for (int i = 0; i < 490 * 490; i += 3) {

			_myIndices.add(i);
			_myIndices.add(i + 1);
			_myIndices.add(i + 2);
			CCVector3f myPosition = new CCVector3f(CCMath.random(-1800, 1800), 400, 0);
			// myPosition.y(400);
			CCVector3f myPosition2 = myPosition.clone().add(CCVecMath.random3f(20));
			CCVector3f myPosition3 = myPosition.clone().add(CCVecMath.random3f(20));

			CCGPUParticle myParticle1 = _myEmitter.emit(myPosition, new CCVector3f(), 3000, false);
			CCGPUParticle myParticle2 = _myEmitter.emit(myPosition2, new CCVector3f(), 3000, false);
			CCGPUParticle myParticle3 = _myEmitter.emit(myPosition3, new CCVector3f(), 3000, false);

			_mySprings.addSpring(myParticle1, myParticle2);
			_mySprings.addSpring(myParticle2, myParticle3);
			_mySprings.addSpring(myParticle3, myParticle1);

		}

		_myRenderer.mesh().drawMode(CCDrawMode.TRIANGLES);
		_myRenderer.mesh().indices(_myIndices);

		_myArcball = new CCArcball(this);

		g.strokeWeight(0.5f);
		g.clearColor(255);
	}

	private float _myTime = 0;
	
	@Override
	public void update(final float theDeltaTime) {
		if (_myPause)
			return;
		
		_myTime += theDeltaTime;

		_myForceField.noiseOffset(new CCVector3f(_myTime * 0.5f, 0, 0));

		_myParticles.update(theDeltaTime);
		_myTerrainConstraint.textureScale(6, 100, 6);
	}

	@Override
	public void draw() {
		g.clear();
		g.scale(_myScale);
		_myArcball.draw(g);
		g.color(255);
//		_myHeightMap.draw(g);
		g.blend();
		g.noDepthMask();
		g.color(0f,0.5f);
		_myRenderer.mesh().draw(g);
//		if (frameCount % 2 == 0)
//			CCScreenCapture.capture("export/marek12/" + CCFormatUtil.nf(frameCount / 2, 4) + ".png", width, height);

		// for(CCBezierCurve myCurve:_myBezierCurves) {
		// myCurve.draw(g);
		// }
	}

	public void reset() {
		_myParticles.reset(null);
		for (int i = 0; i < 490 * 490; i += 3) {
			CCVector3f myPosition = new CCVector3f(CCMath.random(-1800, 1800), 400, 0);
			// myPosition.y(400);
			CCVector3f myPosition2 = myPosition.clone().add(CCVecMath.random3f(20));
			CCVector3f myPosition3 = myPosition.clone().add(CCVecMath.random3f(20));

			_myEmitter.emit(myPosition, new CCVector3f(), 3000, false).index();
			_myEmitter.emit(myPosition2, new CCVector3f(), 3000, false).index();
			_myEmitter.emit(myPosition3, new CCVector3f(), 3000, false).index();

		}
	}

	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_P:
			_myPause = !_myPause;
			break;
		case VK_UP:
			_myScale += 0.1f;
			break;
		case VK_DOWN:
			_myScale -= 0.1f;
			break;
		case VK_LEFT:
			_myScale += 0.1f;
			break;
		case VK_RIGHT:
			_myScale -= 0.1f;
			break;
		case VK_R:
			reset();
			break;
		default:
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSpringsTrianglesTest.class);
		myManager.settings().size(1280, 720);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
