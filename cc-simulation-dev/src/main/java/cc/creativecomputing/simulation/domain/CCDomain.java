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

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;

/**
 * A Domain is a representation of a region of space. For example, the pSource action uses a 
 * domain to describe the volume in which a particle will be created. A random point within 
 * the domain is chosen as the initial position of the particle. The pAvoid, pSink and pBounce 
 * actions use domains to describe a volume in space for particles to steer around, die when 
 * they enter, or bounce off, respectively.
 * <br>
 * Domains are also used to describe velocities. Picture the velocity vector as having its tail 
 * at the origin and its tip being in the domain.
 * <br>
 * Finally, domains can be used to describe colors. For drawing with OpenGL, the full color 
 * space is 0.0 -> 1.0 in the red, green, and blue axes. For example, the domain PDLine, 
 * 1, 0, 0, 1, 1, 0 will choose points on a line between red and yellow. Points outside the 0.0 -> 1.0 
 * range will not be clamped, but eventually will be clamped deep within the OpenGL pipeline.
 * <br>
 * Since data from particle systems can be used by more than just the OpenGL renderer, the 
 * doubleing point triple used for color can mean different things to different consumers of 
 * the data. For example, if a software renderer used colors on the range 0 -> 255, the domain 
 * used to choose the colors can be on that range. The color space does not even need to be 
 * thought of as RGB, but will be for use in OpenGL.
 * <br>
 * Several types of domains can be specified. The two basic abstract operations on a domain 
 * are Generate, which returns a random point in the domain, and Within, which tells whether 
 * a given point is within the domain. Functions such as pSource that take a domain as an 
 * argument take it in the form of a PDomainEnum, followed by nine doubles. The PDomainEnum 
 * is one of the symbolic finalants listed below, such as PDPoint. The nine doubles mean 
 * different things for each type of domain, as described below. Not all domains require all 
 * nine doubles. You only need to specify the first few values that are relevant to that domain 
 * type. The rest default to 0.0 and will be ignored.
 * @author christianr
 *
 */
public abstract class CCDomain implements Cloneable{
	
	static protected double SQRT2PI = 2.506628274631000502415765284811045253006f;
	static protected double ONEOVERSQRT2PI = 1.f / SQRT2PI;
	
	/**
	 * Checks if the given vector lies in the domain
	 * @param theVector
	 * @return
	 */
	public boolean isWithin(final CCVector3 theVector){
		return false;
	}
	
	/**
	 * Overwrite this method to define an intersection test with the given line
	 * @param theVectorA
	 * @param theVectorB
	 * @return
	 */
	public boolean intersectsLine(final CCVector3 theVectorA, final CCVector3 theVectorB){
		return false;
	}
	
	public boolean intersectsLine(final CCVector3 theVectorA, final CCVector3 theVectorB, final CCVector3 thePointOnSurface, final CCVector3 theNormal){
		return false;
	}
	
	public boolean intersectsBox(final CCVector3 theMinCorner, final CCVector3 theMaxVector){
		return false;
	}
	
	/**
	 * Generates a vector that lies inside the Domain
	 * @return
	 */
	public abstract CCVector3 generate();
	
	public void bounce(
		final CCParticle theParticle, 
		final CCVector3 theVector, 
		final double theDeltaTime, 
		final double theOneMinusFriction,
		final double theResilence,
		final double theSquaredCutoff
	){}
	
	public void avoidance(final CCParticle theParticle, final CCVector3 theVector, final double theLookAhead, final double theEpsilon){}
	
};
