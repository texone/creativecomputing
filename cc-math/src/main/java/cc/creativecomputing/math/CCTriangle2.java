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

/**
 * @author info
 *
 */
public class CCTriangle2 {

	protected CCVector2[] _myPoints;
	
	protected CCVector2 _myCenter;
	
	public CCTriangle2(final CCVector2 thePointA, final CCVector2 thePointB, final CCVector2 thePointC) {
		_myPoints = new CCVector2[3];
		_myPoints[0] = thePointA;
		_myPoints[1] = thePointB;
		_myPoints[2] = thePointC;
		
		_myCenter = new CCVector2();
		_myCenter.addLocal(_myPoints[0]);
		_myCenter.addLocal(_myPoints[1]);
		_myCenter.addLocal(_myPoints[2]);
		_myCenter.multiplyLocal(1f/3);
	}
	
	public CCTriangle2(double theAX, double theAY, double theBX, double theBY, double theCX, double theCY){
		this(new CCVector2(theAX, theAY), new CCVector2(theBX, theBY), new CCVector2(theCX, theCY));
	}
	
	public CCTriangle2(){
		this(new CCVector2(),new CCVector2(),new CCVector2());
	}
	
	public CCVector2 a() {
		return _myPoints[0];
	}
	
	public CCVector2 b() {
		return _myPoints[1];
	}
	
	public CCVector2 c() {
		return _myPoints[2];
	}
	
	public CCVector2 center() {
		return _myCenter;
	}
	
	public CCVector2[] points(){
		return _myPoints;
	}
	
	/**
	 * In geometry, the barycentric coordinate system is a coordinate system in 
	 * which the location of a point is specified as the center of mass, or barycenter, 
	 * of masses placed at the vertices of a simplex (a triangle, tetrahedron, etc).
	 * In the context of a triangle, barycentric coordinates are also known as 
	 * areal coordinates, because the coordinates of P with respect to triangle 
	 * ABC are proportional to the (signed) areas of PBC, PCA and PAB
	 * @param thePoint the point to convert to barycentric coordinates
	 * @return the given point as barycentric coordinates
	 */
	public CCVector2 toBarycentricCoordinates(final CCVector2 thePoint){
		// Compute vectors        
		CCVector2 v0 = c().subtract(a());
		CCVector2 v1 = b().subtract(a());
		CCVector2 v2 = thePoint.subtract(a());

		// Compute dot products
		double dot00 = v0.dot(v0);
		double dot01 = v0.dot(v1);
		double dot02 = v0.dot(v2);
		double dot11 = v1.dot(v1);
		double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
		
		return new CCVector2(u,v);
	}
	
	public CCVector2 toTriangleCoordinates(final CCVector2 thePoint){
		// Compute vectors        
		CCVector2 v0 = c().subtract(a());
		CCVector2 v1 = b().subtract(a());
		
		CCVector2 myResult = a().clone();
		myResult.add(v0.multiplyLocal(thePoint.x));
		myResult.add(v1.multiplyLocal(thePoint.y));
		return myResult;
	}

	/**
	 * Returns true if the given point lies inside the triangle
	 * @param thePoint point to test
	 * @param theIsBaryCentric if true the given point is not converted to barycentric coordinates for testing
	 * @return true if the point is inside otherwise false
	 */
	public boolean isInside(CCVector2 thePoint, boolean theIsBaryCentric) {
		if(!theIsBaryCentric)thePoint = toBarycentricCoordinates(thePoint);

		// Check if point is in triangle
		return (thePoint.x > 0) && (thePoint.y > 0) && (thePoint.x + thePoint.y < 1);
	}
	
	/**
	 * Returns true if the given point lies inside the triangle
	 * @param thePoint
	 * @return true if the point is inside otherwise false
	 */
	public boolean isInside(CCVector2 thePoint){
		return isInside(thePoint, false);
	}
	
	/**
	 * Returns true if the given point lies inside the triangle
	 * @param theX x coord of the point to check
	 * @param theY y coord of the point to check
	 * @return
	 */
	public boolean isInside(final double theX, final double theY) {
		return isInside(new CCVector2(theX, theY));
	}
	
	@Override
	public String toString(){
		return "CCTriangle2d["+a()+","+b()+","+c()+","+"]";
	}
}
