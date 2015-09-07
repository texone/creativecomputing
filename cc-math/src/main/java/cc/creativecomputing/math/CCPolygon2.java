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
package cc.creativecomputing.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CCPolygon2 implements Iterable<CCVector2>{
	
	private List<CCVector2> _myVertices = new ArrayList<CCVector2>();
	
	public CCPolygon2(CCVector2...theVertices){
		for(CCVector2 myVertex:theVertices){
			_myVertices.add(myVertex);
		}
	}
	
	public void addVertex(final double theX, final double theY){
		_myVertices.add(new CCVector2(theX,theY));
	}
	
	public void addVertex(final CCVector2 theVertex){
		_myVertices.add(theVertex);
	}
	
	public CCVector2 vertex(int theID) {
		return _myVertices.get(theID);
	}
	
	private double angle2D(final double theX1, final double theY1, final double theX2, final double theY2){
		double theta1 = (double)Math.atan2(theY1, theX1);
		double theta2 = (double)Math.atan2(theY2, theX2);
		double dtheta = theta2 - theta1;

		while (dtheta > CCMath.PI)
			dtheta -= CCMath.TWO_PI;
		while (dtheta < -CCMath.PI)
			dtheta += CCMath.TWO_PI;

		return dtheta;
	}
	
	public boolean isInShape(final CCVector2 theVertex){
		return isInShape(theVertex.x, theVertex.y);
	}
	
	private CCVector2 closestPointLine(final double theX, final double theY, final CCVector2 theStart, final CCVector2 theEnd){
		double myBlend =  CCMath.saturate(( 
		    (theX - theStart.x) * ( theEnd.x - theStart.x) +
		    (theY - theStart.y) * ( theEnd.y - theStart.y)
		) / theStart.distanceSquared(theEnd));
		return CCVector2.lerp(theStart, theEnd, myBlend);
	}
	
	public CCVector2 closestPoint(final double theX, final double theY){
		double myMinDistance = Float.MAX_VALUE;
		CCVector2 myResult = null;
		for (int i = 0; i < _myVertices.size(); i++){
			CCVector2 p1 = _myVertices.get(i);
			CCVector2 p2 = _myVertices.get((i + 1) % _myVertices.size());
			CCVector2 cP = closestPointLine(theX, theY, p1, p2);
			double dist = cP.distance(theX, theY);
			if(dist < myMinDistance){
				myResult = cP;
				myMinDistance = dist;
			}
		}
		return myResult;
	}
	
	public CCVector2 closestPoint(final CCVector2 thePoint){
		return closestPoint(thePoint.x, thePoint.y);
	}
	
	public boolean isInShape(final double theX, final double theY){
		double R = 0;

		for (int i = 0; i < _myVertices.size(); i++){
			double p1x = _myVertices.get(i).x - theX;
			double p1y = _myVertices.get(i).y - theY;
			double p2x = _myVertices.get((i + 1) % _myVertices.size()).x - theX;
			double p2y = _myVertices.get((i + 1) % _myVertices.size()).y - theY;

			R += angle2D(p1x, p1y, p2x, p2y);
		}

		if (CCMath.abs(R) < CCMath.PI)
			return false;
		else
			return true;
	}

	public double signedArea() {
		double area = 0;

		for (int i = 0; i < _myVertices.size(); i++) {
			int j = (i + 1) % _myVertices.size();
			CCVector2 myA = _myVertices.get(i);
			CCVector2 myB = _myVertices.get(j);
			area += myA.x * myB.y;
			area -= myA.y * myB.x;
		}
		area /= 2.0;

		return area;
	}
	
	public double area() {
		return CCMath.abs(signedArea());
	}

	public CCVector2 centroid() {
		double cx = 0, cy = 0;

		double factor = 0;
		
		for (int i = 0; i < _myVertices.size(); i++) {
			int j = (i + 1) % _myVertices.size();
			CCVector2 myA = _myVertices.get(i);
			CCVector2 myB = _myVertices.get(j);
			factor = (myA.x * myB.y - myB.x * myA.y);
			cx += (myA.x + myB.x) * factor;
			cy += (myA.y + myB.y) * factor;
		}
		factor = 1 / (signedArea() * 6);
		cx *= factor;
		cy *= factor;
		
		return new CCVector2(cx, cy);
	}

	public Iterator<CCVector2> iterator() {
		return _myVertices.iterator();
	}
	
	public List<CCVector2> vertices(){
		return _myVertices;
	}
}
