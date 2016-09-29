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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * The point o is the center of the sphere. radius1 is the outer radius of the
 * shell and radius2 is the inner radius. <br>
 * Generate returns a random point in the thick shell at a distance between
 * radius1 to radius2 from point o. If radius2 is 0, then it is the whole
 * sphere. <br>
 * Within returns true if the point lies within the thick shell at a distance
 * between radius1 to radius2 from point o.
 * 
 * @author christianr
 * 
 */
public class Sphere extends CCDomain {

	public CCVector3 center;
	public double radOut, radIn, radOutSqr, radInSqr, radDif;
	public boolean thinShell;

	public Sphere(final CCVector3 theCenter, final double theOuterRadius, final double theInnerRadius) {
		center = theCenter;

		if (theOuterRadius < theInnerRadius) {
			radOut = theInnerRadius;
			radIn = theOuterRadius;
		} else {
			radOut = theOuterRadius;
			radIn = theInnerRadius;
		}

		if (radIn < 0.0f) {
			radIn = 0.0f;
		}

		radOutSqr = CCMath.sq(radOut);
		radInSqr = CCMath.sq(radIn);

		thinShell = (radIn == radOut);
		radDif = radOut - radIn;
	}

	public Sphere(final CCVector3 theCenter, final double theRadius) {
		this(theCenter, theRadius, 0);
	}

	@Override
	public boolean isWithin(final CCVector3 i_vector) {
		final CCVector3 temp = i_vector.clone();
		temp.subtract(center);
		double distSquared = temp.lengthSquared();
		return distSquared <= radOutSqr && distSquared >= radInSqr;
	}

	@Override
	public CCVector3 generate() {
		final CCVector3 result = new CCVector3();
		result.randomize();
		result.normalize();

		// Scale unit sphere pos by [0..r] and translate
		if (thinShell) {
			result.multiplyLocal(radOut);
		} else {
			result.multiplyLocal(radIn + CCMath.random() * radDif);
		}

		result.add(center);
		return result;
	}
}
