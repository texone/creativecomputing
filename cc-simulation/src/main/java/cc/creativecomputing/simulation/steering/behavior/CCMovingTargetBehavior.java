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
package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

/**
 * This CCBehavior can be used to calculate a target vector from
 * a moving vehicle, and apply this on a target behavior.
 * On applying this behavior on the seek behavior you get the
 * pursue behavior.
 * @author christianr
 *
 */
public class CCMovingTargetBehavior extends CCForce{

	private final CCTargetBehavior targetBehavior;
	
	private CCParticle target;
	
	public CCMovingTargetBehavior(
		final CCParticle i_movingTarget, 
		final CCTargetBehavior i_targetBehavior
	){
		target = i_movingTarget;
		targetBehavior = i_targetBehavior;
	}
	
	public CCParticle target(){
		return target;
	}
	
	public CCVector3 predictedTarget(){
		return targetBehavior._myTarget;
	}
	
	public void setTarget(final CCParticle i_target){
		target = i_target;
	}

	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		double d = theAgent.position.distance(target.position);

		double normalness = 1.0F - Math.abs(theAgent.forward.dot(target.forward));
		d *= 1.1F + normalness * 0.2F;
		
		targetBehavior._myTarget.set(target.velocity().clone());
		targetBehavior._myTarget.multiplyLocal(d);
		targetBehavior._myTarget.addLocal(target.position);
		return targetBehavior.apply(theAgent,theForce, 0);
	}

}
