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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

/**
 * Wander is a useful steering force to apply a random walk to an agent.
 * A simple solution would be to calculate a random steering force for
 * each time step, resulting in an uninteresting and jittery movement 
 * instead of sustained turns.
 * <br>
 * Burt Reynold's approach is to steer toward a target that is constrained
 * to move on the perimeter of a circle projected in the front of the agent.
 * This way the direction of the agent is only slightly modified between
 * each time step, creating a jitter free smooth motion.
 * <br>
 * "The steering force takes a random walk from one direction to another. 
 * This idea can be implemented several ways, but one that has produced good 
 * results is to constrain the steering force to the surface of a sphere 
 * located slightly ahead of the character. To produce the steering force for 
 * the next frame: a random displacement is added to the previous value, 
 * and the sum is constrained again to the sphere's surface."
 * <br>
 * The sphere's radius determines the maximum wandering strength and the 
 * magnitude of the random displacement determines the wander rate.
 */
public class CCWander extends CCForce{

	/**
	 * The radius of the sphere projected before the agent, determining 
	 * the maximum wander strength
	 */
	private double _myWanderStrength;

	/**
	 * The maximum random displacement added to the direction
	 */
	private double _myWanderRate;

	/**
	 * direction of the wander that is displaced on every time step
	 */
	private final CCVector3 _myWanderDirection;
	
	/**
	 * Initializes a new CCWanderer using the giving strength and rate.
	 * @param i_wanderStrength, int or double: The radius of the sphere projected before the agent, 
	 * 			determining the maximum wander strength
	 * @param i_wanderRate, int or double: The maximum random displacement added to the direction
	 */
	public CCWander(
		final double theWanderStrength,
		final double theWanderRate
	){
		_myWanderStrength = theWanderStrength;
		_myWanderRate = theWanderRate;
		_myWanderDirection = new CCVector3();
	}
	
	/**
	 * Initializes a new CCWanderer using default values.
	 *
	 */
	public CCWander(){
		this(1f,0.6f);
	}
	
	/**
	 * Applies the behavior to its agent
	 */
	public synchronized boolean apply(final CCParticle theAgent,final CCVector3 theForce, double theDeltaTime){
		double x = (CCMath.random() * 2 - 1) * _myWanderRate;
		double y = (CCMath.random() * 2 - 1) * _myWanderRate;
		double z = (CCMath.random() * 2 - 1) * _myWanderRate;

		_myWanderDirection.addLocal(x,y,z);
		//_myWanderDirection().x(0.0F);
		_myWanderDirection.normalizeLocal();

		CCVector3 wanderGlobal = theAgent.globalizeDirection(wanderDirection());

		theForce.set(
			theAgent.forward.x * sqrt2 + wanderGlobal.x * _myWanderStrength,
			theAgent.forward.y * sqrt2 + wanderGlobal.y * _myWanderStrength,
			theAgent.forward.z * sqrt2 + wanderGlobal.z * _myWanderStrength
		);
		return true;
	}

	/**
	 * Sets the maximum strength of the wander.
	 * @param theWanderStrength The _myWanderStrength to set.
	 */
	public void wanderStrength(final double theWanderStrength){
		_myWanderStrength = theWanderStrength;
	}

	/**
	 * Returns the maximum strength of the wanderer.
	 * @return Returns the _myWanderStrength.
	 */
	public double wanderStrength(){
		return _myWanderStrength;
	}

	/**
	 * Sets the maximum displacement added each time step to 
	 * the direction of the wanderer.
	 * @param theWanderRate The _myWanderRate to set.
	 */
	public void wanderRate(final double theWanderRate){
		_myWanderRate = theWanderRate;
	}

	/**
	 * Returns the maximum displacement added each time step to 
	 * the direction of the wanderer.
	 * @return Returns the _myWanderRate.
	 */
	public double wanderRate(){
		return _myWanderRate;
	}

	/**
	 * Returns the current direction of the wanderer
	 * @return CCVector3: the current direction of the wanderer
	 */
	public CCVector3 wanderDirection(){
		return _myWanderDirection;
	}

}
