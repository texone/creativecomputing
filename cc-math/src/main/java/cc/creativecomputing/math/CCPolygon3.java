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


public class CCPolygon3 implements Iterable<CCVector3>{
	
	private List<CCVector3> _myVertices = new ArrayList<CCVector3>();
	
	public CCPolygon3(CCVector3...theVertices){
		for(CCVector3 myVertex:theVertices){
			_myVertices.add(myVertex);
		}
	}
	
	public void addVertex(final double theX, final double theY){
		_myVertices.add(new CCVector3(theX,theY));
	}
	
	public void addVertex(final CCVector3 theVertex){
		_myVertices.add(theVertex);
	}
	
	public CCVector3 vertex(int theID) {
		return _myVertices.get(theID);
	}
	
	public boolean isInShape(final CCVector3 theVertex){
		return isInShape(theVertex.x, theVertex.y, theVertex.z);
	}
	
	private CCVector3 closestPointLine(final double theX, final double theY, final double theZ, final CCVector3 theStart, final CCVector3 theEnd){
		double myBlend =  CCMath.saturate(( 
		    (theX - theStart.x) * ( theEnd.x - theStart.x) +
		    (theY - theStart.y) * ( theEnd.y - theStart.y) +
		    (theZ - theStart.z) * ( theEnd.z - theStart.z)
		) / theStart.distanceSquared(theEnd));
		return CCVector3.blend(theStart, theEnd, myBlend);
	}
	
	public CCVector3 closestPoint(final double theX, final double theY, final double theZ){
		double myMinDistance = Float.MAX_VALUE;
		CCVector3 myResult = null;
		for (int i = 0; i < _myVertices.size(); i++){
			CCVector3 p1 = _myVertices.get(i);
			CCVector3 p2 = _myVertices.get((i + 1) % _myVertices.size());
			CCVector3 cP = closestPointLine(theX, theY, theZ, p1, p2);
			double dist = cP.distance(theX, theY, theZ);
			if(dist < myMinDistance){
				myResult = cP;
				myMinDistance = dist;
			}
		}
		return myResult;
	}
	
	public CCVector3 closestPoint(final CCVector3 thePoint){
		return closestPoint(thePoint.x, thePoint.y, thePoint.z);
	}
	
	private static final double EPSILON =  0.0001f;
	
	private static final double MODULUS(CCVector3 p) {
		return CCMath.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
	}

    public boolean isInShape(final double theX, final double theY, final double theZ) {

		double anglesum = 0, costheta;

		for (int i = 0; i < _myVertices.size(); i++) {
			CCVector3 p1 = new CCVector3(
				_myVertices.get(i).x - theX, 
				_myVertices.get(i).y - theY, 
				_myVertices.get(i).z - theZ
			);

			CCVector3 p2 = new CCVector3(
				_myVertices.get((i + 1) % _myVertices.size()).x - theX,
				_myVertices.get((i + 1) % _myVertices.size()).y - theY, 
				_myVertices.get((i + 1) % _myVertices.size()).z - theZ
			);

			double m1 = MODULUS(p1);
			double m2 = MODULUS(p2);
			if (m1 * m2 <= EPSILON)
				return true;
			else
				costheta = (p1.x * p2.x + p1.y * p2.y + p1.z * p2.z) / (m1 * m2);

			anglesum += CCMath.acos(costheta);
		}
		return CCMath.abs(anglesum - CCMath.TWO_PI) <= EPSILON;
	}

	public Iterator<CCVector3> iterator() {
		return _myVertices.iterator();
	}
	
	public List<CCVector3> vertices(){
		return _myVertices;
	}
}
