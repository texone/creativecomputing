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
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCSpring extends CCForce{
	private double _mySpringConstant;

	private double _myDamping;

	private double _myRestLength;

	private CCAgent _myTarget;

	boolean on;

	public CCSpring(
		final CCAgent theTarget, 
		final double theSpringConstant, 
		final double theDamping, 
		final double theRestLength
	){
		_mySpringConstant = theSpringConstant;
		_myDamping = theDamping;
		_myRestLength = theRestLength;
		_myTarget = theTarget;
		on = true;
	}

	public final CCAgent target(){
		return _myTarget;
	}

	public final double currentLength(final CCAgent theAgent){
		return theAgent.position.distance(_myTarget.position);
	}

	public final double restLength(){
		return _myRestLength;
	}

	public final double strength(){
		return _mySpringConstant;
	}

	public final void setStrength(double ks){
		_mySpringConstant = ks;
	}

	public final double damping(){
		return _myDamping;
	}

	public final void setDamping(double d){
		_myDamping = d;
	}

	public final void restLength(
		final double i_restLength
	){
		_myRestLength = i_restLength;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime) {
		if (on){
			theForce.set(theAgent.position);
			theForce.subtractLocal(_myTarget.position);

			double a2bDistance = theForce.length();
			double springForce = -(a2bDistance - _myRestLength) * _mySpringConstant;
			
			theForce.normalizeLocal();
			
			CCVector3 velocityA2B = theAgent.velocity().clone();
			velocityA2B.subtractLocal(_myTarget.velocity());
			
			double dampingForce = -_myDamping * (theForce.dot(velocityA2B));
			double r = springForce + dampingForce;
			
			theForce.multiplyLocal(r);
		}
		return true;
	}
}
