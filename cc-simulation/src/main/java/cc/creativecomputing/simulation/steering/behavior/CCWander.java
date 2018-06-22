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

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
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
	@CCProperty (name = "wander strength", min = 0.f, max = 5f)
	private float _cWanderStrength = 1;
	/**
	 * The maximum random displacement added to the direction
	 */
	@CCProperty (name = "wander rate", min = 0.1f, max = 15f)
	private float _cWanderRate = 0.6f;
	
	/**
	 * Initializes a new CCWanderer using the giving strength and rate.
	 * @param theWanderStrength, int or double: The radius of the sphere projected before the agent, 
	 * 			determining the maximum wander strength
	 * @param theWanderRate, int or double: The maximum random displacement added to the direction
	 */
	public CCWander(){
	}
	
	private Map<CCParticle, CCVector3> _myDirectionMap = new HashMap<>();
	
	/**
	 * Applies the behavior to its agent
	 */
	public synchronized boolean apply(final CCParticle theAgent,final CCVector3 theForce, double theDeltaTime){
		double x = (CCMath.random() * 2 - 1) * _cWanderRate;
		double y = (CCMath.random() * 2 - 1) * _cWanderRate;
		double z = (CCMath.random() * 2 - 1) * _cWanderRate;

		if(!_myDirectionMap.containsKey(theAgent)) {
			_myDirectionMap.put(theAgent, new CCVector3());
		}
		CCVector3 _myWanderDirection = _myDirectionMap.get(theAgent);
		_myWanderDirection.addLocal(x,y,z);
		//_myWanderDirection().x(0.0F);
		_myWanderDirection.normalizeLocal();

		CCVector3 wanderGlobal = theAgent.globalizeDirection(_myWanderDirection);

		theForce.set(
			theAgent.forward.x * CCMath.SQRT2 + wanderGlobal.x * _cWanderStrength,
			theAgent.forward.y * CCMath.SQRT2 + wanderGlobal.y * _cWanderStrength,
			theAgent.forward.z * CCMath.SQRT2 + wanderGlobal.z * _cWanderStrength
		);
		return true;
	}

}
