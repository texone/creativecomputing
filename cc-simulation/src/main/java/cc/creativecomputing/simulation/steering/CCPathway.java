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
package cc.creativecomputing.simulation.steering;

import cc.creativecomputing.math.CCVector3;


public abstract class CCPathway {
	
	protected boolean _myIsClosed = false;

	public abstract int mapPointToPath(CCVector3 point, CCVector3 onPath, CCVector3 tangent);

	public abstract int mapPointToPath(CCVector3 point, CCVector3 onPath, CCVector3 tangent,int start, int numberOfPoints);

	public abstract CCVector3 mapPathDistanceToPoint(final double theDistance);

	public abstract double mapPointToPathDistance(CCVector3 vector3);

	public abstract double mapPointToPathDistance(CCVector3 vector3,int start, int numberOfPoints);
	
	public abstract int numberOfPoints();
	
	public abstract double distanceToEnd(final int theIndex);
	
	public abstract CCVector3 point(int index);
	
	public abstract CCVector3[] points();
	
	public void isClosed(final boolean theIsClosed){
		_myIsClosed = theIsClosed;
	}
	
	public boolean isClosed(){
		return _myIsClosed;
	}
	
}
