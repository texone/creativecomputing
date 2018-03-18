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
