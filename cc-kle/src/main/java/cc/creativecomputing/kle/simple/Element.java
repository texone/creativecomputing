package cc.creativecomputing.kle.simple;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class Element {
	
	public CCVector3 ceil0  = new CCVector3();
	public CCVector3 ceil1  = new CCVector3();
	public CCVector3 center = new CCVector3();
	
	public CCVector3 pos = new CCVector3();
	public CCVector3 pos1 = new CCVector3();
	public CCVector3 pos2 = new CCVector3();

	public CCVector3 posi0 = new CCVector3();
	public CCVector3 posi1 = new CCVector3();
	public CCVector3 posi2 = new CCVector3();
	
	
	
	public Rope rope0 = new Rope();
	public Rope rope1 = new Rope();
	double cutoff = 0.1f;
	public double R = 30;
	
	public List<CCVector3> polygon = new ArrayList<CCVector3>();
	public List<CCVector2> bounds  = new ArrayList<CCVector2>();
	
	int cnt = 0;
	
	void filter (double theCutoff) {
		
		pos = new CCVector3(posi0);
		if (theCutoff!=1 && cnt>2) {
			
			/*
		    double ff = theCutoff*0.5f;
		    double ita = 1.0f / CCMath.tan (CCMath.PI*ff);
		    double q = CCMath.sqrt(2f);
		    double b0 = 1f / (1f + q*ita + ita*ita);
		    double b1 = 2*b0;
		    double b2 = b0;
		    double a1 = 2f * (ita*ita - 1f) * b0;
		    double a2 = -(1f - q*ita + ita*ita) * b0;    
	
		    pos.x = posi0.x * b0 + posi1.x * b1 + posi2.x * b2 + pos.x * a1 + pos1.x * a2;
		    pos.y = posi0.y * b0 + posi1.y * b1 + posi2.y * b2 + pos.y * a1 + pos1.y * a2;
		    */

			pos.x = (1-theCutoff)*pos1.x + theCutoff*posi0.x;
			pos.y = (1-theCutoff)*pos1.y + theCutoff*posi0.y;
			
//		    theRope.lf1 = theRope.l0 * b0 + theRope.l1 * b1 + theRope.l2 * b2 + theRope.lf1 * a1 + theRope.lf2 * a2;
		}
		cnt += 1;
	}
	
	public CCVector3 getPosition() {
		return pos;
	}
	
	public class Rope {
		public double l0, l1, l2, v0, v1, a0;
		public void update (double newLen, double dT) {
			v1 = v0;
			v0 = (l0-newLen)/dT;
			a0 = (v0-v1)/dT;
			
			l2 = l1;
			l1 = l0;
			l0 = newLen;
		}
	}
	
	void rope2pos() {
		
		double a = rope0.l0*100;
		double b = rope1.l0*100;
		double c = ceil0.distance(ceil1);
		
		double beta = CCMath.acos ((a*a + c*c - b*b)/(2*a*c));
		double x = a*CCMath.cos(beta);
		double h = a*CCMath.sin(beta);
				
		if (Double.isNaN(x)) System.out.println(a+" "+b+" "+c+" "+(a*a+c*c-b*b)/(2*a*c));
		pos.x = x-c/2f;
		pos.y = -h;
	}
	
	void pos2rope (double dT) {
		double w = ceil0.distance(ceil1);
		
		double x0 = w/2 + pos.x;
		double x1 = w - x0;
		double y = pos.y;
		
		double l0 = CCMath.sqrt (x0*x0 + y*y) / 100;
		double l1 = CCMath.sqrt (x1*x1 + y*y) / 100;
		
		rope0.update(l0, dT);
		rope1.update(l1, dT);
	}
	
	void setRopes (double r0, double r1, double dT) {
		rope0.update(r0, dT);
		rope1.update(r1, dT);
		rope2pos();
	}
	
	public Element (CCVector3 theCeil0, CCVector3 theCeil1, CCVector3 theCenter, double theR) {
		R = theR;
		center = theCenter;
		ceil0 = theCeil0;
		ceil1 = theCeil1;
	
		double MAX = 12;
		for (int i=0; i<MAX; i++) {
			double phi = CCMath.TWO_PI * i / MAX;
			polygon.add(new CCVector3(R*CCMath.cos(phi), R*CCMath.sin(phi), 0));
		}
	}
	
	public void translateNoScale (double x, double y, double dT) {
		pos.x = x;
		pos.y = y;
		pos.z = center.z;
		
		pos2rope(dT);
	}
	
	public void translate (double x, double y, List<CCVector2>movingPolygon, double dT, double theCutoff) {
		
		double t = 1 - (y*0.5f+0.5f);
		y = y*0.5f-0.5f;
		double w0 = CCMath.abs (movingPolygon.get(0).x - movingPolygon.get(3).x);
		double w1 = CCMath.abs (movingPolygon.get(1).x - movingPolygon.get(2).x);
		double h0 = CCMath.abs (movingPolygon.get(0).y - movingPolygon.get(2).y);
		
		double sw = CCMath.blend(w0, w1, t);
		x = x * sw/2;
		y = y*h0 + bounds.get(0).y;
	
		posi2 = new CCVector3(posi1);
		posi1 = new CCVector3(posi0);
		pos2 = new CCVector3(pos1);
		pos1 = new CCVector3(pos);
		
		posi0.x = center.x + x; //mW1/2 * x;
		posi0.y = center.y + y; //mH/2 * y - mH/2 + top;
		posi0.z = center.z;

		
		filter(theCutoff);
		
	
		pos2rope(dT);
	}
	
	public boolean outOfBounds () {
		for (int i=0; i<bounds.size(); i++) {

			CCVector3 v0 = new CCVector3 (bounds.get(i).x, bounds.get(i).y, 0);
			CCVector3 v1 = new CCVector3 (bounds.get((i+1)%bounds.size()).x, bounds.get((i+1)%bounds.size()).y, 0);
			
			CCVector3 p0 = new CCVector3 (pos.x, pos.y,0);
			CCLine3 line = new CCLine3 (v0, v1);
			
			CCVector3 p = line.closestPoint(p0);
			double dist = p.distance(pos);
			dist = dist*1;
		}
		return false;
	}
	
	public boolean violate () {
		return CCMath.abs (rope0.v0)>1.5f || CCMath.abs(rope1.v0)>1.5f || CCMath.abs(rope0.a0)>1.5f || CCMath.abs(rope1.a0)>1.5f;
	}
}