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

import cc.creativecomputing.simulation.domain.CCDomain;

/**
 * class CCObstacle
 *
 * Implements the base class for obstacle objects
 */
public class CCObstacle {
	/** 
	 * Visibility status 
	 **/
	protected boolean _myIsVisible = true;

	/** 
	 * Collision detection status 
	 **/
	protected boolean _myCheckForCollision = true;

	/** 
	 * repulsive force 
	 **/
	protected double _myRepulsiveForce = 2;

	protected CCDomain _myDomain;

	/** 
	 * Constructor 
	 **/
	public CCObstacle(final CCDomain theDomain) {
		_myDomain = theDomain;
	}

	/** 
	 * Sets the visibility of the object
	 * @param theIsVisible True / False
	 */
	public void visible(final boolean theIsVisible) {
		_myIsVisible = theIsVisible;
	}

	/** 
	 * Turns collision detection on / off
	 * @param theCheckForCollision True / False
	 */
	public void checkForCollision(final boolean theCheckForCollision) {
		_myCheckForCollision = theCheckForCollision;
	}

	/** 
	 * Sets the repulsive force of the object 
	 *
	 * @param    theRepulsiveForce      repulsive force
	 */
	public void repulsiveForce(final double theRepulsiveForce) {
		_myRepulsiveForce = theRepulsiveForce;
	}
	
	public CCDomain domain(){
		return _myDomain;
	}

	/** 
	 * Returns collision detection status
	 * @return Collision status
	 */
	public boolean checkForCollision() {
		return _myCheckForCollision;
	}

	/** 
	 * Returns the repulsive force
	 * @return The repulsive force
	 */
	public double repulsiveForce() {
		return _myRepulsiveForce;
	}

	/** Returns visibility status
	 * @return Visibility status
	 */
	public boolean isVisible() {
		return _myIsVisible;
	}

}
