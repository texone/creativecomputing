/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control.timeline;

import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.math.CCColor;

public abstract class CCAbstractTrack {

	protected final CCPropertyHandle<?> _myProperty;
	
	private CCColor _myColor;
	
	private boolean _myMuteFlag;
	
	protected boolean _myDirtyFlag;
	
	protected double _myMin = 0;
	protected double _myMax = 1;

	public CCAbstractTrack(CCPropertyHandle<?> theProperty) {
		_myMuteFlag = false;
		_myProperty = theProperty;
		if(_myProperty instanceof CCNumberPropertyHandle<?>) {
			CCNumberPropertyHandle<?> myNumberHandle = (CCNumberPropertyHandle<?>)_myProperty;
			if(!myNumberHandle.isNumberBox()) {
				_myMin = myNumberHandle.min().doubleValue();
				_myMax = myNumberHandle.max().doubleValue();
			}else {
				_myMin = myNumberHandle.value().doubleValue() - 10;
				_myMax = myNumberHandle.value().doubleValue() + 10;
			}
		}
		_myColor = CCColor.LIGHT_GRAY;
	}
	
	public CCPropertyHandle<?> property() {
		return _myProperty;
	}
	
	public void mute( boolean theFlag ) {
		_myProperty.mute(theFlag);
		_myMuteFlag = theFlag;
	}
	
	public boolean mute() {
		return _myMuteFlag;
	}
	
	public double min(){
		return _myMin;
	}
	
	public void min(double theMin){
		_myMin = theMin;
	}
	
	public double max(){
		return _myMax;
	}
	
	public void max(double theMax){
		_myMax = theMax;
	}
	
	public CCColor color() {
		return _myColor;
	}
	
	public void color(CCColor theColor) {
		_myColor = theColor;
	}
}
