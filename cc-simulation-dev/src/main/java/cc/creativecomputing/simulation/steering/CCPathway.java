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
