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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * These are the minima and maxima of an axis-aligned box. It doesn't 
 * matter which of each coordinate is min and which is max.
 * <br>
 * Generate returns a random point in this box. 
 * Within returns true if the point is in the box.
 * @author christianr
 *
 */
public class CCBox extends CCDomain{


	/**
	 * the top left front vector of the box
	 */
	private final CCVector3 _myMinCorner;
	
	/**
	 * the bottom right back vector of the box
	 */
	private final CCVector3 _myMaxCorner;
	
	/**
	 * Vector between min and max point
	 */
	private final CCVector3 _myDifference;
	
	/**
	 * planes building the bounding box for calculating the intersection point
	 */
	private final CCPlaneDomain[] _myBoundingPlanes;
	
	/**
	 * The center of the box
	 */
	protected final CCVector3 _myCenter;
	
	/**
	 * Creates a new box using the given vectors as corner points.
	 * @param theMinCorner
	 * @param theMaxCorner
	 */
	public CCBox(final CCVector3 theMinCorner, final CCVector3 theMaxCorner){
		_myMinCorner = theMinCorner.clone();
		_myMaxCorner = theMaxCorner.clone();
			
		if(theMaxCorner.x < theMinCorner.x){
			_myMinCorner.x = theMaxCorner.x; 
			_myMaxCorner.x = theMaxCorner.x; 
		}
		if(theMaxCorner.y < theMinCorner.y){
			_myMinCorner.y = theMaxCorner.y; 
			_myMaxCorner.y = theMaxCorner.y; 
		}
		if(theMaxCorner.z < theMinCorner.z){
			_myMinCorner.z = theMaxCorner.z; 
			_myMaxCorner.z = theMaxCorner.z;
		}
		
		_myCenter = new CCVector3();
		_myCenter.addLocal(_myMinCorner);
		_myCenter.addLocal(_myMaxCorner);
		_myCenter.multiplyLocal(0.5f);

		_myDifference = _myMaxCorner.clone();
		_myDifference.subtract(_myMinCorner);
		
		_myBoundingPlanes = new CCPlaneDomain[]{
			new CCPlaneDomain(_myMinCorner, new CCVector3(-1,0,0)),
			new CCPlaneDomain(_myMinCorner, new CCVector3(0,-1,0)),
			new CCPlaneDomain(_myMinCorner, new CCVector3(0,0,-1)),
			new CCPlaneDomain(_myMaxCorner, new CCVector3(1,0,0)),
			new CCPlaneDomain(_myMaxCorner, new CCVector3(0,1,0)),
			new CCPlaneDomain(_myMaxCorner, new CCVector3(0,0,1))
		};
	}

	@Override
	public boolean isWithin(final CCVector3 theVector){
		return !(
			(theVector.x < _myMinCorner.x) || (theVector.x > _myMaxCorner.x) ||
			(theVector.y < _myMinCorner.y) || (theVector.y > _myMaxCorner.y) ||
			(theVector.z < _myMinCorner.z) || (theVector.z > _myMaxCorner.z)
		);
	}

	@Override
	public CCVector3 generate(){
		final CCVector3 result = new CCVector3(
			CCMath.random() * _myDifference.x,
			CCMath.random() * _myDifference.y,
			CCMath.random() * _myDifference.z
		);
		
		result.add(_myMinCorner);
		return result;
	}

	@Override
	public boolean intersectsLine(final CCVector3 theVectorA, final CCVector3 theVectorB) {
		return isWithin(theVectorA) != isWithin(theVectorB);
	}
	
	@Override
	public boolean intersectsLine(
		final CCVector3 theVectorA, final CCVector3 theVectorB,
		final CCVector3 thePointOnSurface, final CCVector3 theNormal
	) {
		
		if(intersectsLine(theVectorA, theVectorB)){
			for(CCPlaneDomain myPlane:_myBoundingPlanes){
				if(myPlane.intersectsLine(theVectorA, theVectorB, thePointOnSurface, theNormal)){
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean intersectsBox(final CCVector3 theMinCorner, final CCVector3 theMaxCorner) {
		if (_myMaxCorner.x <= theMinCorner.x && _myMinCorner.x <= theMinCorner.x)return false;
		if (_myMaxCorner.x >= theMaxCorner.x && _myMinCorner.x >= theMaxCorner.x)return false;
		if (_myMaxCorner.y <= theMinCorner.y && _myMinCorner.y <= theMinCorner.y)return false;
		if (_myMaxCorner.y >= theMaxCorner.y && _myMinCorner.y >= theMaxCorner.y)return false;
		
		return true;
	}

	public CCVector3 min(){
		return _myMinCorner;
	}
	
	public CCVector3 max(){
		return _myMaxCorner;
	}
}
