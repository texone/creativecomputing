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
package cc.creativecomputing.simulation.force;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;


public class CCAttractor extends CCForce {

	private CCVector3 _myPosition;

	private double _myStrength;

	private double _myRadius;

    private boolean _myActive;

    public CCAttractor() {
        _myPosition = new CCVector3();
        _myRadius = 100;
        _myStrength = 1;
        _myActive = true;
    }


    public CCVector3 position() {
        return _myPosition;
    }


    public void setPositionRef(CCVector3 thePosition) {
        _myPosition = thePosition;
    }


    public double strength() {
        return _myStrength;
    }


    public void strength(double theStrength) {
        _myStrength = theStrength;
    }


    public double radius() {
        return _myRadius;
    }


    public void radius(double theRadius) {
        _myRadius = theRadius;
    }

    public boolean apply(CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
    	if (_myStrength != 0) {
    		theForce = _myPosition.subtract(theParticle.position);
            final double myDistance = fastInverseSqrt(1 / theForce.lengthSquared());
            if (myDistance < _myRadius) {
                double myFallOff = 1f - myDistance / _myRadius;
                final double myForce = myFallOff * myFallOff * _myStrength;
                theForce.multiplyLocal(myForce / myDistance);
            }
    	}
		return true;
	}


    private static double fastInverseSqrt(double x) {
        /** this is shamelessly stolen from traer ( http://www.cs.princeton.edu/~traer/physics/ ) */
        final double half = 0.5F * x;
        int i = Float.floatToIntBits((float)x);
        i = 0x5f375a86 - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5F - half * x * x);
    }


    public boolean dead() {
        return false;
    }


    public boolean active() {
        return _myActive;
    }


    public void active(boolean theActiveState) {
        _myActive = theActiveState;
    }
}
