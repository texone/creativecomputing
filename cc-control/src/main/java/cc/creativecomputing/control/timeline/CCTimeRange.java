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

	public double start;
	public double end;
	
	public CCTimeRange(final double theStart, final double theEnd) {
		range(theStart, theEnd);
	}
	
	public CCTimeRange() {
		this(0,0);
	}
	
	public void range(final double theStart, final double theEnd) {
		start = theStart;
		end = theEnd;
		
		if(start > end) {
			double myTemp = start;
			start = end;
			end = myTemp;
		}
	}
	
	public double length(){
		return end - start;
	}
	
	public CCTimeRange clone() {
		return new CCTimeRange(start, end);
	}
}
