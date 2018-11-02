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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCGravity;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

public class CCSpringsTrianglesEmitTest extends CCGL2Adapter {

	public class CCHeightMap {

		private CCVBOMesh _myMesh;
		private CCImage _myHeightMapTexture;

		// Mesh Generation Paramaters
		private double _myMeshResolution = 1;
//		private double _myMeshHeightScale = 80f;
		private int _myMeshWidth = 2000;
		private int _myMeshHeight = 1400;

		public CCHeightMap(String theHeightMapTexture) {

			_myHeightMapTexture = CCImageIO.newImage(CCNIOUtil.dataPath(theHeightMapTexture));

			_myMeshWidth = _myHeightMapTexture.width();
			_myMeshHeight = _myHeightMapTexture.height();

			// Generate Vertex Field
			final int myNumberOfVertices = (int) (_myMeshWidth * _myMeshHeight * 6 / (_myMeshResolution * _myMeshResolution));

			_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, myNumberOfVertices);

			for (int nZ = 0; nZ < _myMeshHeight; nZ += (int) _myMeshResolution) {
				for (int nX = 0; nX < _myMeshWidth; nX += (int) _myMeshResolution) {
					for (int nTri = 0; nTri < 6; nTri++) {

						double myX = (double) nX + ((nTri == 2 || nTri == 4 || nTri == 5) ? _myMeshResolution : 0.0f);
						double myZ = (double) nZ + ((nTri == 1 || nTri == 2 || nTri == 5) ? _myMeshResolution : 0.0f);

						double heightFromPixel = _myHeightMapTexture.getPixel((int) myX, (int) myZ).brightness();
						_myMesh.addVertex(
							myX - (_myMeshWidth / 2f),
							heightFromPixel,
							myZ - (_myMeshHeight / 2f)
						);
						
						_myMesh.addTextureCoords(
							myX / _myMeshWidth,
							1 - myZ / _myMeshHeight
						);
					}
				}
			}
		}

		public void draw(final CCGraphics g) {
			g.pushMatrix();
			g.translate(0, -200, 0);
			g.scale(6, 90, 6);
			_myMesh.draw(g);
			g.popMatrix();
		}

		public void meshResolution(double theResolution) {
			_myMeshResolution = theResolution;
		}

//		public void meshHeightScale(double theHeightScale) {
//			_myMeshHeightScale = theHeightScale;
//		}
	}

	
	
	private CCParticlePointRenderer _myRenderer;
	
	@CCProperty(name = "particles")
	private CCParticles _myParticles;
	
	private CCParticlesIndexParticleEmitter _myEmitter;
	private CCSpringForce _mySprings;

	private boolean _myPause = false;
	private double _myScale = 1;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		fixUpdateTime(1 / 50f);
		List<CCForce> myForces = new ArrayList<CCForce>();
		myForces.add(new CCGravity());
		myForces.add(new CCForceField());

		List<CCConstraint> myConstraints = new ArrayList<CCConstraint>();

		_mySprings = new CCSpringForce(4, 0.2f,25f);

		myForces.add(_mySprings);

		_myRenderer = new CCParticlePointRenderer();
		_myParticles = new CCParticles(g, _myRenderer, myForces, myConstraints, 600, 600);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));

		List<Integer> _myIndices = new ArrayList<Integer>();
		for (int i = 0; i < 600 * 600; i += 3) {

			_myIndices.add(i);
			_myIndices.add(i + 1);
			_myIndices.add(i + 2);

		}

		_myRenderer.mesh().drawMode(CCDrawMode.TRIANGLES);
		_myRenderer.mesh().indices(_myIndices);

		_cCameraController = new CCCameraController(this, g, 100);

		g.strokeWeight(0.5f);
		g.clearColor(255);
		
		keyPressed().add(e -> {
			switch (e.keyCode()) {
			case VK_R:
//				_myParticles.reset(g);
//				
//				double myXspace = 1500f / _myXres;
//				double myYspace = 1500f / _myYres;
//				
//				CCParticle[] myLeftParticles = new CCParticle[_myYres];
//				
////				List<Integer> _myIndices = new ArrayList<Integer>();
////				
//				for(int x = 0; x < _myXres; x++){
//					CCParticle myParticleAbove = null;
//					for(int y = 0; y < _myYres; y++){
//						CCParticle myParticle = _myEmitter.emit(
//							new CCVector3(x * myXspace - 750, 0, y * myYspace - 750),
//							new CCVector3(),
//							3000, true
//						);
//						
//						if(myParticleAbove != null) {
////							_mySprings.addSpring(myParticleAbove, myParticle, myYspace);
//						}
//						
//						if(myLeftParticles[y] != null) {
////							_mySprings.addSpring(myLeftParticles[y], myParticle, myXspace);
//						}
//
//						myParticleAbove = myParticle;
//						myLeftParticles[y] = myParticle;
//					}
//				}
//				break;
			default:
			}	
		});
	}
	
	@Override
	public void update(final CCAnimator theAnimator) {
		if (_myPause)
			return;
		
		for(int i = 0; i < 10;i++) {
			double myEdgeLength = CCMath.random(100);
			CCVector3 myPosition = new CCVector3(CCMath.random(-180, 180), 400, 0);
			// myPosition.y(400);
			CCVector3 myPosition2 = myPosition.clone().randomize(myEdgeLength);
			CCVector3 myPosition3 = myPosition.clone().randomize(myEdgeLength);

			CCParticle myParticle1 = _myEmitter.emit(myPosition, new CCVector3(), 30);
			CCParticle myParticle2 = _myEmitter.emit(myPosition2, new CCVector3(), 30);
			CCParticle myParticle3 = _myEmitter.emit(myPosition3, new CCVector3(), 30);

			_mySprings.addSpring(myParticle1, myParticle2, myEdgeLength);
			_mySprings.addSpring(myParticle2, myParticle3, myEdgeLength);
			_mySprings.addSpring(myParticle3, myParticle1, myEdgeLength);
		}

		_myParticles.update(theAnimator);
//		_myTerrainConstraint.textureScale(6, 100, 6);
	}

	@Override
	public void display(CCGraphics g) {
		_myParticles.preDisplay(g);
		g.clearColor(255,0,0);
		g.clear();
		g.scale(_myScale);
		_cCameraController.camera().draw(g);
		g.color(255);
//		_myHeightMap.draw(g);
		g.blend();
//		g.noDepthMask();
		g.color(0f);
		g.polygonMode(CCPolygonMode.LINE);
		_myParticles.display(g);
		g.polygonMode(CCPolygonMode.FILL);

//		if (frameCount % 2 == 0)
//			CCScreenCapture.capture("export/marek12/" + CCFormatUtil.nf(frameCount / 2, 4) + ".png", width, height);

		// for(CCBezierCurve myCurve:_myBezierCurves) {
		// myCurve.draw(g);
		// }
	}

//	public void reset() {
//		_myParticles.reset();
//		for (int i = 0; i < 690 * 690; i += 3) {
//			CCVector3 myPosition = new CCVector3(CCMath.random(-1800, 1800), 400, 0);
//			// myPosition.y(400);
//			CCVector3 myPosition2 = myPosition.clone().randomize(20);
//			CCVector3 myPosition3 = myPosition.clone().randomize(20)
//
//			_myEmitter.emit(myPosition, new CCVector3(), 3000, false).index();
//			_myEmitter.emit(myPosition2, new CCVector3(), 3000, false).index();
//			_myEmitter.emit(myPosition3, new CCVector3(), 3000, false).index();
//		}
//	}

//	@Override
//	public void keyPressed(final CCKeyEvent theEvent) {
//		switch (theEvent.keyCode()) {
//		case VK_P:
//			_myPause = !_myPause;
//			break;
//		case VK_UP:
//			_myScale += 0.1f;
//			break;
//		case VK_DOWN:
//			_myScale -= 0.1f;
//			break;
//		case VK_LEFT:
//			_myScale += 0.1f;
//			break;
//		case VK_RIGHT:
//			_myScale -= 0.1f;
//			break;
//		case VK_R:
//			reset();
//			break;
//		default:
//		}
//	}

	public static void main(String[] args) {
		
		CCGL2Application myAppManager = new CCGL2Application(new CCSpringsTrianglesEmitTest());
		myAppManager.glcontext().size(1200, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
