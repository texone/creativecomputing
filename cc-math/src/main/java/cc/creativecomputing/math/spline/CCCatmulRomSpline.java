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
package cc.creativecomputing.math.spline;

import java.util.List;

import cc.creativecomputing.math.CCVector3;

/**
 * In computer graphics, Catmull-Rom splines are frequently used to get smooth interpolated 
 * motion between key frames. For example, most camera path animations generated from discrete 
 * key-frames are handled using Catmull-Rom splines. They are popular mainly for being relatively 
 * easy to compute, guaranteeing that each key frame position will be hit exactly, and also 
 * guaranteeing that the tangents of the generated curve are continuous over multiple segments.
 * @author christianriekoff
 *
 */
public class CCCatmulRomSpline extends CCSpline {
	private double _myCurveTension = 0.5f;
	
	public CCCatmulRomSpline(double theCurveTension, boolean theIsClosed){
		super(CCSplineType.CATMULL_ROM, theIsClosed);
		_myCurveTension = theCurveTension;
	}
	public CCCatmulRomSpline(){
		this(0.5,false);
	}

	public CCCatmulRomSpline(CCVector3[] theControlPoints, double theCurveTension, boolean theIsClosed) {
		super(CCSplineType.CATMULL_ROM, theControlPoints, theIsClosed);
		_myCurveTension = theCurveTension;
	}

	public CCCatmulRomSpline(List<CCVector3> theControlPoints, double theCurveTension, boolean theIsClosed) {
		super(CCSplineType.CATMULL_ROM, theControlPoints, theIsClosed);
	}
	
	@Override
	public void beginEditSpline() {
		if(_myIsModified)return;

		_myIsModified = true;
		
		if(size() < 2)return;
		
		remove(0);
		remove(size() - 1);
		
		if (_myIsClosed) {
			remove(size() - 1);
		}
	}
	
	@Override
	public void endEditSpline() {
		if(!_myIsModified)return;
		
		_myIsModified = false;
		
		if(size() < 2)return;
		if (_myIsClosed) {
			add(0,get(size() - 1));
			add(get(1));
			add(get(2));
		}else{
			add(0,get(0));
			add(get(size() - 1));
		}
		computeTotalLentgh();
	}
	
	/**
     * Compute the length on a catmull rom spline between control point 1 and 2
     * @param theP0 control point 0
     * @param theP1 control point 1
     * @param theP2 control point 2
     * @param theP3 control point 3
     * @param theStartRange the starting range on the segment (use 0)
     * @param theEndRange the end range on the segment (use 1)
     * @param theCurveTension the curve tension
     * @return the length of the segment
     */
    private double catmullRomLength(
    	CCVector3 theP0, CCVector3 theP1, CCVector3 theP2, CCVector3 theP3, 
    	double theStartRange, double theEndRange, 
    	double theCurveTension
    ) {

        double epsilon = 0.001f;
        double middleValue = (theStartRange + theEndRange) * 0.5f;
        CCVector3 start = theP1.clone();
        if (theStartRange != 0) {
        	start = CCVector3.catmulRomPoint(theP0, theP1, theP2, theP3, theStartRange, theCurveTension);
        }
        CCVector3 end = theP2.clone();
        if (theEndRange != 1) {
        	end = CCVector3.catmulRomPoint(theP0, theP1, theP2, theP3, theEndRange, theCurveTension);
        }
        CCVector3 middle = CCVector3.catmulRomPoint(theP0, theP1, theP2, theP3, middleValue, theCurveTension);
        double l = end.subtract(start).length();
        double l1 = middle.subtract(start).length();
        double l2 = end.subtract(middle).length();
        double len = l1 + l2;
        if (l + epsilon < len) {
            l1 = catmullRomLength(theP0, theP1, theP2, theP3, theStartRange, middleValue, theCurveTension);
            l2 = catmullRomLength(theP0, theP1, theP2, theP3, middleValue, theEndRange, theCurveTension);
        }
        l = l1 + l2;
        return l;
    }
    
    /**
     * Compute the length on a catmull rom spline between control point 1 and 2
     * @param theP0 control point 0
     * @param theP1 control point 1
     * @param theP3 control point 2
     * @param theP4 control point 3
     * @param theCurveTension the curve tension
     * @return the length of the segment
     */
    private double catmullRomLength(CCVector3 theP0, CCVector3 theP1, CCVector3 theP3, CCVector3 theP4, double theCurveTension) {
    	return catmullRomLength(theP0, theP1, theP3, theP4, 0, 1, theCurveTension);
    }

	@Override
	/**
	 * This method computes the Catmull Rom curve length.
	 */
	protected void computeTotalLengthImpl() {
		if (size() > 3) {
			for (int i = 0; i < size() - 3; i++) {
				double l = catmullRomLength(
					get(i),
					get(i + 1), 
					get(i + 2),
					get(i + 3), 
					_myCurveTension
				);
				_mySegmentsLength.add(l);
				_myTotalLength += l;
			}
		}
	}

	@Override
	public CCVector3 interpolate(double value, int currentControlPoint) {
		endEditSpline();
		if(currentControlPoint + 3 >= size())return get(currentControlPoint);
		return CCVector3.catmulRomPoint(
			get(currentControlPoint), 
			get(currentControlPoint + 1), 
			get(currentControlPoint + 2), 
			get(currentControlPoint + 3), 
			value, _myCurveTension
		);
		
		
	}
//	@Override
//	public CCVector3 interpolate(double value, int currentControlPoint) {
//		endEditSpline();
//		return cubicInterpolate(
//			get(currentControlPoint), 
//			get(currentControlPoint + 1), 
//			get(currentControlPoint + 2), 
//			get(currentControlPoint + 3), 
//			value, _myCurveTension
//		);
//		
//		
//	}
//	 private CCVector3 cubicInterpolate( CCVector3 p0, CCVector3 p1, CCVector3 p2, CCVector3 p3, double t , double theTension)
//     {
//		 
//           return new CCVector3(
//        		   p1.x + 0.5f * t * (p2.x - p0.x + t * (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x + t * (3 * (p1.x - p2.x) + p3.x - p0.x))),
//        		   p1.y + 0.5f * t * (p2.y - p0.y + t * (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y + t * (3 * (p1.y - p2.y) + p3.y - p0.y))),
//        		   p1.z + 0.5f * t * (p2.z - p0.z + t * (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z + t * (3 * (p1.z - p2.z) + p3.z - p0.z)))
//        	);
//     }
	
	public CCVector3 closestPoint(CCVector3 theClosestPoint, int theStart, int theEnd){
		if (size() < 4) return null;
		
		if(theStart > theEnd){
			int myTemp = theStart;
			theStart = theEnd;
			theEnd = myTemp;
		}
		
		CCVector3 myNearestPoint = null;
		double myMinDistance = Float.MAX_VALUE;
			
		for (int i = theStart; i < theEnd; i++) {
			for(int j = 0; j < 60; j++){
				CCVector3 myTest = interpolate(j / 60f, i);
				double myDistance = myTest.distanceSquared(theClosestPoint);
				
				if(myDistance < myMinDistance){
					myMinDistance = myDistance;
					myNearestPoint = myTest;
				}
			}
		}
		
		return myNearestPoint;
	}
	
	@Override
	public CCVector3 closestPoint(CCVector3 theClosestPoint){
		return closestPoint(theClosestPoint, 0, size() - 3);
	}
	
	/**
	 * returns the curve tension
	 * 
	 * @return
	 */
	public double curveTension() {
		return _myCurveTension;
	}

	/**
	 * sets the curve tension
	 * 
	 * @param _myCurveTension
	 *            the tension
	 */
	public void curveTension(double theCurveTension) {
		_myCurveTension = theCurveTension;
		computeTotalLentgh();
	}
	
	
	
	public List<CCVector3> points() {
		if(size() < 2)return this;
		return subList(1, size() - 1);
	}
	
	/**
	 * For the catmulrom spline two extra vertices are inserted at the end 
	 * and the beginning of the curve to create a nice curve. To get all points
	 * used for drawing and interpolating the curve call this method instead of
	 * {@linkplain #points()}
	 * @return all vertices used to draw the curve
	 */
	public List<CCVector3> curvePoints(){
		return this;
	}
}
