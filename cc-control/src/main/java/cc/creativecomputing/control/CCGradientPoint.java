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
package cc.creativecomputing.control;

import cc.creativecomputing.math.CCColor;

public class CCGradientPoint implements Comparable<CCGradientPoint>{
	private double _myPosition;
	private final CCColor _myColor;

	public CCGradientPoint(double thePosition, CCColor theColor) {
		_myPosition = thePosition;
		_myColor = theColor;
	}
	
	public double position(){
		return _myPosition;
	}
	
	public void position(double thePosition){
		_myPosition = thePosition;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public CCGradientPoint clone(){
		return new CCGradientPoint(_myPosition, _myColor.clone());
	}

	@Override
	public int compareTo(CCGradientPoint o) {
		return new Double(this.position()).compareTo(o.position());
	}
	
	@Override
	public String toString() {
		return _myPosition + " : " + _myColor;
	}
}
