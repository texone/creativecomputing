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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.simulation.particles.realsense.CCRealSenseForceField;
import cc.creativecomputing.demo.simulation.particles.realsense.CCRealSenseForceFieldDemo;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.shader.CCShaderBufferDebugger;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.emit.CCParticlesIndexParticleEmitter;
import cc.creativecomputing.simulation.particles.forces.CCAttractor;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.forces.CCForceField;
import cc.creativecomputing.simulation.particles.forces.CCTargetForce;
import cc.creativecomputing.simulation.particles.forces.CCTextureForceField2D;
import cc.creativecomputing.simulation.particles.forces.CCViscousDrag;
import cc.creativecomputing.simulation.particles.forces.springs.CCSpringForce;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleTriangleRenderer;
import cc.creativecomputing.simulation.particles.render.CCQuadRenderer;
import cc.creativecomputing.simulation.particles.render.CCSpringVolumentricLineRenderer;

public class CCParticlesSpringsDrawDemo extends CCGL2Adapter {
	private static class CCQuad {
		public double ax, ay;
		public double bx, by;
		public double cx, cy;
		public double dx, dy;
	}

	public class CCGesture implements Iterable<CCVector3> {

		private double damp = 5.0;
		private double dampInv = 1.0 / damp;
		private double damp1 = damp - 1;

		private int w;
		private int h;

		private List<CCVector3> _myPath = new ArrayList<>();
		private List<CCVector3> _myDrawPath = new ArrayList<>();
		private List<CCQuad> _myQuads = new ArrayList<>();

		private double jumpDx, jumpDy;
		private double thickness = 5;

		public CCGesture(int mw, int mh) {
			w = mw;
			h = mh;

			jumpDx = 0;
			jumpDy = 0;
		}

		public void addPoint(double x, double y, double p) {
			_myPath.add(new CCVector3(x, y, p));

			if (_myPath.size() > 1) {
				jumpDx = _myPath.get(_myPath.size() - 1).x - _myPath.get(0).x;
				jumpDy = _myPath.get(_myPath.size() - 1).y - _myPath.get(0).y;
			}

		}

		public void addPoint(double x, double y) {

			addPoint(x, y, getPressureFromVelocity(distToLast(x, y)));

		}

		private double getPressureFromVelocity(double v) {
			final double scale = 18;
			final double minP = 0.02;
			final double oldP = (_myPath.size() > 0) ? _myPath.get(_myPath.size() - 1).z : 0;

			return ((minP + CCMath.max(0, 1.0 - v / scale)) + damp1 * oldP) * dampInv;
		}

		public double distToLast(double ix, double iy) {
			if (_myPath.size() <= 0)
				return 30;

			CCVector3 v = _myPath.get(_myPath.size() - 1);
			double dx = v.x - ix;
			double dy = v.y - iy;
			return CCMath.mag(dx, dy);

		}

		public void compile() {
			// compute the polygons from the path of CCVector3's
			_myQuads.clear();

			if (_myPath.size() < 3)
				return;

			for (int i = 1; i < _myPath.size() - 1; i++) {

				CCVector3 p0 = _myPath.get(i - 1);
				CCVector3 p1 = _myPath.get(i);
				CCVector3 p2 = _myPath.get(i + 1);

				double radius0 = p0.z * thickness;
				double radius1 = p1.z * thickness;

				// assumes all segments are roughly the same length...
				double dx01 = p1.x - p0.x;
				double dy01 = p1.y - p0.y;

				double dx02 = p2.x - p1.x;
				double dy02 = p2.y - p1.y;

				double hp01 = CCMath.sqrt(dx01 * dx01 + dy01 * dy01);
				double hp02 = CCMath.sqrt(dx02 * dx02 + dy02 * dy02);
				double dist = CCMath.max(hp02, hp01);

				if (hp02 != 0) {
					hp02 = radius1 / hp02;
				}

				double co01 = radius0 * dx01 / hp01;
				double si01 = radius0 * dy01 / hp01;

				double co02 = dx02 * hp02;
				double si02 = dy02 * hp02;

				double ax = p0.x - si01;
				double ay = p0.y + co01;
				double bx = p0.x + si01;
				double by = p0.y - co01;

				double cx = p1.x + si02;
				double cy = p1.y - co02;
				double dx = p1.x - si02;
				double dy = p1.y + co02;

				// set the vertices of the polygon
				CCQuad apoly = new CCQuad();
				apoly.ax = ax;
				apoly.bx = bx;
				apoly.cx = cx;
				apoly.dx = dx;

				apoly.ay = ay;
				apoly.by = by;
				apoly.cy = cy;
				apoly.dy = dy;
				if (dist < 120)
					_myQuads.add(apoly);
				// swap data for next time
				ax = dx;
				ay = dy;
				bx = cx;
				by = cy;
			}
		}

		public void smooth() {
			// average neighboring points

			final double weight = 18;
			final double scale = 1.0 / (weight + 2);

			for (int i = 1; i < _myPath.size() - 2; i++) {
				CCVector3 lower = _myPath.get(i - 1);
				CCVector3 center = _myPath.get(i);
				CCVector3 upper = _myPath.get(i + 1);

				center.x = (lower.x + weight * center.x + upper.x) * scale;
				center.y = (lower.y + weight * center.y + upper.y) * scale;
			}
		}

		private double _myTime = 0;

		public void update(CCAnimator theAnimator) {
			if (_myPath.size() <= 0)
				return;

			_myDrawPath.clear();

			_myTime += theAnimator.deltaTime() * 10;

			// Move a Gesture one step
			int nPts1 = _myPath.size() - 1;

			if (_myTime > 1) {
				for (int i = 0; i < nPts1; i++) {
					_myPath.get(i).x = _myPath.get(i + 1).x;
					_myPath.get(i).y = _myPath.get(i + 1).y;
				}
				_myPath.get(nPts1).x = _myPath.get(0).x + jumpDx;
				_myPath.get(nPts1).y = _myPath.get(0).y + jumpDy;
				_myTime -= 1;
			}

			for (int i = 0; i < nPts1; i++) {
				_myDrawPath.add(CCVector3.blend(_myPath.get(i), _myPath.get(i + 1), _myTime));
			}
			_myDrawPath.add(new CCVector3(CCMath.blend(_myPath.get(nPts1).x, _myPath.get(0).x + jumpDx, _myTime),
					CCMath.blend(_myPath.get(nPts1).y, _myPath.get(0).y + jumpDy, _myTime)));

			_myDrawPath.forEach(v -> {
				v.x = (v.x < 0) ? (w - ((-v.x) % w)) : v.x % w;
				v.y = (v.y < 0) ? (h - ((-v.y) % h)) : v.y % h;
			});
		}

		public void display(CCGraphics g) {
			compile();
			g.beginShape(CCDrawMode.QUADS);
			_myQuads.forEach(quad -> {
				g.vertex(quad.ax, quad.ay);
				g.vertex(quad.bx, quad.by);
				g.vertex(quad.cx, quad.cy);
				g.vertex(quad.dx, quad.dy);
			});
			g.endShape();

//			g.beginShape(CCDrawMode.LINE_STRIP);
//			_myPath.forEach(v -> {
//				g.vertex(v.x, v.y);
//			});
//			g.endShape();

		}

		@Override
		public Iterator<CCVector3> iterator() {
			return _myPath.iterator();
		}
	}

	private List<CCGesture> _myGestures = new ArrayList<>();

	private void loadStrokes(int w, int h) {
		CCDataElement myXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("strokes.xml"));
		for (CCDataElement myStrokeXML : myXML) {
			CCGesture myStroke = new CCGesture(w, h);
			for (CCDataElement myPointXML : myStrokeXML) {
				// if(myPointXML.getFloat("x") == 0 && myPointXML.getFloat("y") == 0)continue;
				myStroke.addPoint(
					myPointXML.doubleAttribute("x"), 
					myPointXML.doubleAttribute("y"),
					myPointXML.doubleAttribute("p")
				);
			}
			_myGestures.add(myStroke);
		}
	}

	@CCProperty(name = "particles")
	private CCParticles _myParticles;

	private CCParticlesIndexParticleEmitter _myEmitter;

	private double _myX = 0;

	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	private int _myXres = 500;
	private int _myYres = 500;

	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;

	@CCProperty(name = "debugger")
	private CCShaderBufferDebugger _myDebugger;
	@CCProperty(name = "particle debugger")
	private CCShaderBufferDebugger _myParticleDebugger;
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCaptureController;

	private CCSpringForce _mySprings;
	private CCTargetForce _myTargetForce;
	

	private CCTextureForceField2D _myForceField;
	
	@CCProperty(name = "real sense")
	private CCRealSenseForceField _RealSenseForceField;
	
	private List<CCParticle> _myNewTargets = new ArrayList<>();
	private CCParticleTriangleRenderer myTriangleRenderer;

	double width;
	double height;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCaptureController = new CCScreenCaptureController(this);
		_RealSenseForceField = new CCRealSenseForceField(CCNIOUtil.dataPath("realsense02.byt"),1280,720);

		
//		frameRate(30);
		List<CCForce> myForces = new ArrayList<>();
		myForces.add(new CCForceField());
		myForces.add(new CCViscousDrag());
		myForces.add(new CCAttractor());
		myForces.add(_mySprings = new CCSpringForce(4, 4f));
		myForces.add(_myTargetForce = new CCTargetForce());
		myForces.add(_myForceField = new CCTextureForceField2D(_RealSenseForceField.forceField(), new CCVector2(1920d, -1080d), new CCVector2(0.5, 0.5)));

		List<CCParticleRenderer> myRenderer = new ArrayList<>();

		myTriangleRenderer = new CCParticleTriangleRenderer(3);
		myRenderer.add(new CCSpringVolumentricLineRenderer(_mySprings, false));
		myRenderer.add(new CCQuadRenderer());

		_myParticles = new CCParticles(g, myRenderer, myForces, new ArrayList<>(), _myXres, _myYres);
		_myParticles.addEmitter(_myEmitter = new CCParticlesIndexParticleEmitter(_myParticles));

		_cCameraController = new CCCameraController(this, g, 100);

		g.strokeWeight(0.5f);

		_myDebugger = new CCShaderBufferDebugger(_mySprings.idBuffer());
		_myParticleDebugger = new CCShaderBufferDebugger(_myParticles.infoData());

		g.textFont(CCFontIO.createTextureMapFont("arial", 12));

		loadStrokes(g.width(), g.height());

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

		width = g.width();
		height = g.height();
	}

	int myIndex = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_RealSenseForceField.update(theAnimator);

		CCGesture myGesture = _myGestures.get(myIndex);
		CCParticle myLast = null;
		for (CCVector3 myPoint : myGesture) {
			CCVector3 myPosition = new CCVector3(myPoint.x - width / 2, myPoint.y - height / 2);
			CCParticle myParticle = _myEmitter.emit(myPosition, new CCVector3(), 30);
			myParticle.target().set(myPosition.x, myPosition.y, myPosition.z, 1);
			if (myLast != null) {
				_mySprings.addSpring(myParticle, myLast);
			}

			myLast = myParticle;
			_myNewTargets.add(myParticle);
		}

		myIndex++;
		myIndex %= _myGestures.size();

		_myParticles.update(theAnimator);
	}

	@CCProperty(name = "debug")
	private boolean _cDebug = true;

	@Override
	public void display(CCGraphics g) {
		_RealSenseForceField.preDisplay(g);
		_myTargetForce.beginSetTargets(g);
		for(CCParticle myParticle:_myNewTargets){
			_myTargetForce.addTarget(myParticle);
		}
		_myTargetForce.endSetTargets(g);
		_myNewTargets.clear();
		
		_myParticles.preDisplay(g);
		g.clear();
		g.pushMatrix();
		_cCameraController.camera().draw(g);
		
		g.pushMatrix();
		g.scale(1,-1);
		g.image(_RealSenseForceField.forceField(),-g.width()/2, -g.height()/2, g.width(), g.height());
		g.popMatrix();

		g.blend();
		g.noDepthTest();
		g.color(0f, _cAlpha);
		_myParticles.display(g);

		g.blend();
		g.popMatrix();

		g.color(1d);
//		CCLog.info(g.vendor());
		_myDebugger.display(g);
		_myParticleDebugger.display(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCParticlesSpringsDrawDemo());
		myAppManager.glcontext().size(1800, 1368);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
