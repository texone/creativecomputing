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
	protected float _myRepulsiveForce = 2;

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
	public void repulsiveForce(final float theRepulsiveForce) {
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
	public float repulsiveForce() {
		return _myRepulsiveForce;
	}

	/** Returns visibility status
	 * @return Visibility status
	 */
	public boolean isVisible() {
		return _myIsVisible;
	}

}
