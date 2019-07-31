package cc.creativecomputing.demo.topic.simulation.yellowtail;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCGesture {
	
	private static class CCQuad{
		public double ax, ay;
		public double bx, by;
		public double cx, cy;
		public double dx, dy;
	}

	private double damp = 5.0;
	private double dampInv = 1.0 / damp;
	private double damp1 = damp - 1;

	private int w;
	private int h;

	private List<CCVector3> _myPath = new ArrayList<>();
	private List<CCVector3> _myDrawPath = new ArrayList<>();
	private List<CCQuad> _myQuads = new ArrayList<>();

	private double jumpDx, jumpDy;
	private double thickness = 30;

	public CCGesture(int mw, int mh) {
		w = mw;
		h = mh;

		jumpDx = 0;
		jumpDy = 0;
	}

	public void addPoint(double x, double y, double p) {
		_myPath.add(new CCVector3(x,y,p));

		if (_myPath.size() > 1) {
			jumpDx = _myPath.get(_myPath.size() - 1).x - _myPath.get(0).x;
			jumpDy = _myPath.get(_myPath.size() - 1).y - _myPath.get(0).y;
		}

	}
	
	public void addPoint(double x, double y) {

		addPoint(x,y,getPressureFromVelocity(distToLast(x, y)));

	}

	private double getPressureFromVelocity(double v) {
		final double scale = 18;
		final double minP = 0.02;
		final double oldP = (_myPath.size() > 0) ? _myPath.get(_myPath.size() - 1).z : 0;
		
		return ((minP + CCMath.max(0, 1.0 - v / scale)) + damp1 * oldP) * dampInv;
	}

	public double distToLast(double ix, double iy) {
		if (_myPath.size() <= 0) return 30;
		
		CCVector3 v = _myPath.get(_myPath.size() - 1);
		double dx = v.x - ix;
		double dy = v.y - iy;
		return CCMath.mag(dx, dy);
		
	}

	public void compile() {
		// compute the polygons from the path of CCVector3's
		_myQuads.clear();

		if(_myPath.size() < 3)return;
		
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
			if(dist < 120)_myQuads.add(apoly);
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
		
		_myTime += theAnimator.deltaTime()*10;
		
		// Move a Gesture one step
		int nPts1 = _myPath.size() - 1;

//		if(_myTime > 1) {
			for (int i = 0; i < nPts1; i++) {
				_myPath.get(i).x = _myPath.get(i + 1).x;
				_myPath.get(i).y = _myPath.get(i + 1).y;
			}
			_myPath.get(nPts1).x = _myPath.get(0).x + jumpDx;
			_myPath.get(nPts1).y = _myPath.get(0).y + jumpDy;
			_myTime -= 1;
//		}
		
		for (int i = 0; i < nPts1; i++) {
			_myDrawPath.add(CCVector3.blend(_myPath.get(i), _myPath.get(i + 1), _myTime));
		}
		_myDrawPath.add(new CCVector3(
			CCMath.blend(_myPath.get(nPts1).x,_myPath.get(0).x + jumpDx , _myTime), 
			CCMath.blend(_myPath.get(nPts1).y,_myPath.get(0).y + jumpDy , _myTime)
		));
		
		_myPath.forEach(v -> {
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
		
//		g.beginShape(CCDrawMode.LINE_STRIP);
//		_myPath.forEach(v -> {
//			g.vertex(v.x, v.y);
//		});
//		g.endShape();

	}
}
