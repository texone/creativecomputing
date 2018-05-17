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
 * <p>
 * In the mathematical field of numerical analysis and in computer graphics, a 
 * Bezier spline is a spline curve where each polynomial of the spline is in Bezier form.
 * </p>
 * <p>
 * In other words, a Bezier spline is simply a series of Bezier curves joined end to end 
 * where the last point of one curve coincides with the starting point of the next curve. 
 * Usually cubic Bezier curves are used, and additional control points (called handles) 
 * are added to define the shape of each curve.
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Bezier_spline">bezier spline at wikipedia</a>
 * @author christianriekoff
 *
 */
public class CCBezierSpline extends CCSpline {
	
	
	public CCBezierSpline (boolean theIsClosed) {
		super(CCSplineType.BEZIER, theIsClosed);
		_myInterpolationIncrease = 3;
	}
	
	public CCBezierSpline (){
		this(false);
	}
	

	/**
	 * Create a spline
	 * 
	 * @param theControlPoints
	 *            An array of vector to use as control points of the spline.
	 *            The control points should be provided in the appropriate way. 
	 *            Each point 'p' describing control position in the scene should 
	 *            be surrounded by two handler points. 
	 *            
	 *            This applies to every point except for the border points of the curve, 
	 *            who should have only one handle point. The pattern should be as follows: 
	 *            P0 - H0 : H1 - P1 - H1 : ... : Hn - Pn
	 * 
	 *            n is the amount of 'P' - points.
	 * @param theIsClosed
	 *            true if the spline cycle.
	 */
	public CCBezierSpline(CCVector3[] theControlPoints, boolean theIsClosed) {
		super(CCSplineType.BEZIER, theControlPoints, theIsClosed);
		_myInterpolationIncrease = 3;
	}
	
	/**
	 * Create a spline
	 * 
	 * @param theControlPoints
	 *            An array of vector to use as control points of the spline.
	 *            The control points should be provided in the appropriate way. 
	 *            Each point 'p' describing control position in the scene should 
	 *            be surrounded by two handler points. 
	 *            
	 *            This applies to every point except for the border points of the curve, 
	 *            who should have only one handle point. The pattern should be as follows: 
	 *            P0 - H0 : H1 - P1 - H1 : ... : Hn - Pn
	 * 
	 *            n is the amount of 'P' - points.
	 * @param theIsClosed
	 *            true if the spline cycle.
	 */
	public CCBezierSpline(List<CCVector3> theControlPoints, boolean theIsClosed) {
		super(CCSplineType.BEZIER, theControlPoints, theIsClosed);
		_myInterpolationIncrease = 3;
	}
	
	@Override
	public void addPoint (CCVector3 theControlPoint) {
		if(size() == 0){
			add(theControlPoint);
			return;
		}

		CCVector3 myLastPoint = get(size() - 1);
		add(myLastPoint.blend(theControlPoint, 0.25));
		add(myLastPoint.blend(theControlPoint, 0.75));
		add(theControlPoint);
	}
	
	/**
	 * This method adds new control points to the spline. The start control point
	 * of the spline will be calculated based on the end control point of the previous 
	 * spline. The first given point will be
	 * the end control point of the spline, the second point the end point.
	 * @param theControlPoint1 second control point of the spline
	 * @param theControlPoint2 end point of the spline
	 */
	public void addControlPoints (CCVector3 theControlPoint1, CCVector3 theControlPoint2) {
		if(size() > 2){
			add(
				get(size() - 1).add(get(size() - 1).subtract (get(size() - 2)))
			);
		}else{
			add(theControlPoint2);
			return;
		}
		
		add(theControlPoint1);
		add(theControlPoint2);
	}
	
	/**
	 * Adds a new bezier segment to the spline. The first and second point are optional and can
	 * be null. When point 1 is null the last point of the spline will be used as control point, 
	 * if point 2 is null the given point 3 will be taken as end control point.
	 * @param thePoint1 start control point can be null
	 * @param thePoint2 end control point can be null
	 * @param thePoint3 end point
	 */
	public void addControlPoints(CCVector3 thePoint1, CCVector3 thePoint2, CCVector3 thePoint3){
		if(thePoint1 != null){
			add(thePoint1);
		}else{
			add(get(size() - 1));
		}
		if(thePoint2 != null){
			add(thePoint2);
		}else{
			add(thePoint3);
		}
		add(thePoint3);
	}
	
	/**
     * Compute the length on a bezier spline between control point 1 and 2
     * @param theP0 control point 0
     * @param theP1 control point 1
     * @param theP2 control point 2
     * @param theP3 control point 3
     * @return the length of the segment
     */
    public static double bezierLength(CCVector3 theP0, CCVector3 theP1, CCVector3 theP2, CCVector3 theP3) {
        double delta = 0.01f, t = 0.0f, result = 0.0f;
        CCVector3 v1 = theP0.clone(), v2 = new CCVector3();
        while(t<=1.0f) {
                v2 = CCVector3.bezierPoint(theP0, theP1, theP2, theP3, t);
                result += v1.subtract(v2).length();
                v1.set(v2);
                t += delta;
        }
        return result;
    }

	/**
	 * This method calculates the bezier curve length.
	 */
	@Override
	public void computeTotalLengthImpl() {
		if (size() > 1) {
			for (int i = 0; i < size() - 3; i += 3) {
				double l = bezierLength(
					get(i),
					get(i + 1), 
					get(i + 2), 
					get(i + 3)
				);
				_mySegmentsLength.add(l);
				_myTotalLength += l;
			}
		}
	}

	@Override
	public CCVector3 interpolate(double value, int currentControlPoint) {
		if(currentControlPoint + 3 >= size())return get(currentControlPoint);
		return CCVector3.bezierPoint(
			get(currentControlPoint), 
			get(currentControlPoint + 1), 
			get(currentControlPoint + 2), 
			get(currentControlPoint + 3), 
			value
		);
	}
}
