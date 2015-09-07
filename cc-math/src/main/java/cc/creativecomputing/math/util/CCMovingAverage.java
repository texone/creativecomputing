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
package cc.creativecomputing.math.util;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;


public abstract class CCMovingAverage <Type>{
	
	protected Type _myValue;
	
	protected float _myWeight;
	
	protected float _mySkipRange = 0;
	
	public CCMovingAverage(float theWeight) {
		_myWeight = theWeight;
	}
	
	public void weight(float theWeight) {
		_myWeight = theWeight;
	}
	
	/**
	 * Defines a value to skip smoothing for big value changes. If the Change of the value
	 * is bigger than the skip range no smoothing is applied
	 * @param theSkipRange
	 */
	public void skipRange(float theSkipRange) {
		_mySkipRange = theSkipRange;
	}
	
	public abstract Type update(final Type theValue);
	
	public Type value(){
		return _myValue;
	}
	
	public static CCMovingAverage<Float> floatAverage(final float theWeight){
		return new CCFloatAverage(theWeight);
	}
	
	private static class CCFloatAverage extends CCMovingAverage<Float>{

		public CCFloatAverage(float theWeight) {
			super(theWeight);
			_myValue = 0f;
		}

		@Override
		public Float update(Float theValue) {
			if(_myValue == null) {
				_myValue = theValue;
				return _myValue;
			}
			if(CCMath.abs(_myValue - theValue) > _mySkipRange) {
				_myValue = theValue;
			}else {
				_myValue = _myValue * _myWeight + theValue * (1 - _myWeight);
			}
			return _myValue;
		}
	}
	
	public static CCMovingAverage<CCVector3> vector3fAverage(final float theWeight){
		return new CCVector3fAverage(theWeight);
	}
	
	private static class CCVector3fAverage extends CCMovingAverage<CCVector3>{

		public CCVector3fAverage(float theWeight) {
			super(theWeight);
		}

		@Override
		public CCVector3 update(CCVector3 theValue) {
			if(_myValue == null) {
				_myValue = theValue.clone();
				return _myValue;
			}
			
			_myValue.x = _myValue.x * _myWeight + theValue.x * (1 - _myWeight);
			_myValue.y = _myValue.y * _myWeight + theValue.y * (1 - _myWeight);
			_myValue.z = _myValue.z * _myWeight + theValue.z * (1 - _myWeight);
			return _myValue;
		}
		
	}
	
	public static CCMovingAverage<CCVector2> vector2fAverage(final float theWeight){
		return new CCVector2fAverage(theWeight);
	}
	
	private static class CCVector2fAverage extends CCMovingAverage<CCVector2>{

		public CCVector2fAverage(float theWeight) {
			super(theWeight);
		}

		@Override
		public CCVector2 update(CCVector2 theValue) {
			if(_myValue == null) {
				_myValue = theValue.clone();
				return _myValue;
			}
			
			_myValue.x = _myValue.x * _myWeight + theValue.x * (1 - _myWeight);
			_myValue.y = _myValue.y * _myWeight + theValue.y * (1 - _myWeight);
			return _myValue;
		}
		
	}
}
