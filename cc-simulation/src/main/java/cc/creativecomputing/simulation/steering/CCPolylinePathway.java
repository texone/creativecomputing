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
package cc.creativecomputing.simulation.steering;

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;


public class CCPolylinePathway extends CCPathway {

	public double radius;
	
	private CCVector3 _myPoints[];
	private double _myLengths[];
	private CCVector3 _myNormals[];
	
	private double segmentLength;
	private double segmentProjection;
	
	// vectors for internal calculations
	private CCVector3 local;
	private CCVector3 chosen;
	private CCVector3 segmentNormal;

	public CCPolylinePathway() {
		local = new CCVector3();
		chosen = new CCVector3();
		segmentNormal = new CCVector3();
		_myPoints = new CCVector3[0];
		radius = 100;
	}
	
	public void points(final List<CCVector3> thePoints){
		_myPoints = new CCVector3[thePoints.size()];
		int counter = 0;
		for(CCVector3 myPoint:thePoints){
			_myPoints[counter++] = myPoint;
		}
		cachePathStats();
	}
	
	public CCVector3[] points(){
		return _myPoints;
	}
	
	@Override
	public int numberOfPoints(){
		return _myPoints.length;
	}

	protected void cachePathStats() {
		_myLengths = new double[_myPoints.length];
		_myNormals = new CCVector3[_myPoints.length];
		
		for (int i = 0; i < _myPoints.length; i++) {
			int index = (i + 1) % _myPoints.length;
			_myNormals[index] = _myPoints[i].clone();
			_myNormals[index].subtractLocal(_myPoints[index]);
			_myLengths[index] = _myNormals[index].length();
			_myNormals[index].multiplyLocal(1.0F / _myLengths[index]);
		}

	}

	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param thePoint
	 * @param theEndPoint1
	 * @param theEndPoint2
	 * @return
	 */
	private double pointToSegmentDistance(
		final CCVector3 thePoint, 
		final CCVector3 theEndPoint1, 
		final CCVector3 theEndPoint2
	){
		local.set(thePoint);
		local.subtractLocal(theEndPoint1);
		
		segmentProjection = segmentNormal.dot(local);
		
		if (segmentProjection < 0.0F) {
			chosen.set(theEndPoint1);
			segmentProjection = 0.0F;
			return thePoint.distance(theEndPoint1);
		}
		if (segmentProjection > segmentLength) {
			chosen.set(theEndPoint2);
			segmentProjection = segmentLength;
			return thePoint.distance(theEndPoint2);
		}
		
		chosen.set(segmentNormal);
		chosen.multiplyLocal(segmentProjection);
		chosen.addLocal(theEndPoint1);
		return thePoint.distance(chosen);
	}
	
	@Override
	public int mapPointToPath(
		final CCVector3 point, 
		final CCVector3 onPath, 
		final CCVector3 tangent
	) {
		return mapPointToPath(point,onPath,tangent,0,_myPoints.length);
	}

	@Override
	public int mapPointToPath(
		final CCVector3 point, 
		final CCVector3 onPath, 
		final CCVector3 tangent,
		final int start,
		final int numberOfPoints
	) {
		double minDistance = Float.MAX_VALUE;
		int result = -1;
		
		if(_myIsClosed){
			for (int i = start; i < start + numberOfPoints; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				double d = pointToSegmentDistance(
						point, 
						_myPoints[i % _myPoints.length],
						_myPoints[index]);
				
				if (d < minDistance) {
					result = i;
					minDistance = d;
					onPath.set(chosen);
					tangent.set(segmentNormal);
				}
			}
		}else{
			for (int i = CCMath.max(start,1); i < _myPoints.length && i < start + numberOfPoints; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				double d = pointToSegmentDistance(point, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					result = i;
					minDistance = d;
					onPath.set(chosen);
					tangent.set(segmentNormal);
				}
			}
		}
		return result;
	}

	@Override
	public CCVector3 mapPathDistanceToPoint(final double thePathDistance) {
		double remainingDistance = thePathDistance;
		
		final CCVector3 myResult = new CCVector3();
		
		if(_myIsClosed){
			for (int i = 0; i < _myPoints.length; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				if (segmentLength < remainingDistance) {
					remainingDistance -= segmentLength;
				} else {
					double ratio = remainingDistance / segmentLength;
					myResult.set(_myPoints[i]);
					myResult.blendLocal(_myPoints[index], ratio);
					return myResult;
				}
			}
		}else{
			for (int i = 1; i < _myPoints.length; i++) {
				segmentLength = _myLengths[i];
				if (segmentLength < remainingDistance) {
					remainingDistance -= segmentLength;
				} else {
					double ratio = remainingDistance / segmentLength;
					myResult.set(_myPoints[i - 1]);
					myResult.blendLocal(_myPoints[i], ratio);
					return myResult;
				}
			}
		}
		
		return myResult;
	}

	@Override
	public double mapPointToPathDistance(final CCVector3 thePoint,final int start, final int numberOfPoints) {
		double minDistance = 3.402823E+038F;
		double segmentLengthTotal = 0.0F;
		double pathDistance = 0.0F;
		
		if(_myIsClosed){
			for (int i = start; i < numberOfPoints; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				
				double d = pointToSegmentDistance(thePoint, _myPoints[i], _myPoints[index]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}else{
			for (int i = CCMath.max(start,1); i < _myPoints.length && i < start + numberOfPoints; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				double d = pointToSegmentDistance(thePoint, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}

		return pathDistance;
	}
	public double mapPointToPathDistance(final CCVector3 thePoint) {
		double minDistance = 3.402823E+038F;
		double segmentLengthTotal = 0.0F;
		double pathDistance = 0.0F;
		
		if(_myIsClosed){
			for (int i = 0; i < _myPoints.length; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				
				double d = pointToSegmentDistance(thePoint, _myPoints[i], _myPoints[index]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}else{
			for (int i = 1; i < _myPoints.length; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				double d = pointToSegmentDistance(thePoint, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}

		return pathDistance;
	}
	
	
	
	@Override
	public CCVector3 point(int theIndex) {
		return _myPoints[theIndex];
	}

	public double distanceToEnd(final int theIndex){
		double myResult = 0;
		for (int i = theIndex; i < _myPoints.length; i++) {
			myResult += _myLengths[i];
		}
		return myResult;
	}
}
