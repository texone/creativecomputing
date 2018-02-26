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

/**
 * @author christianriekoff
 *
 */
public class CCTimeRange {

	private double _myStart;
	private double _myEnd;
	
	public CCTimeRange(final double theStart, final double theEnd) {
		
		if (theStart < theEnd) {
			_myStart = theStart;
			_myEnd = theEnd;
		} else {
			_myStart = theEnd;
			_myEnd = theStart;
		}
	}
	
	public CCTimeRange() {
		this(0,0);
	}
	
	public double start() {
		return _myStart;
	}
	
	public void start(final double theStart) {
		_myStart = theStart;
	}
	
	public double end() {
		return _myEnd;
	}
	
	public void end(double theEnd) {
		_myEnd = theEnd;
	}
	
	public void range(final double theStart, final double theEnd) {
		_myStart = theStart;
		_myEnd = theEnd;
		
		if(_myStart > _myEnd) {
			double myTemp = _myStart;
			_myStart = _myEnd;
			_myEnd = myTemp;
		}
	}
	
	public double length(){
		return _myEnd - _myStart;
	}
	
	public CCTimeRange clone() {
		return new CCTimeRange(_myStart, _myEnd);
	}
}
