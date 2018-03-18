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

import java.util.List;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.behavior.CCNeighborHoodBehavior;

public class CCMind{
	
	private CCMindState _myCurrentState = new CCMindState();

	/**
	 * Constructor
	 */
	public CCMind() {
	}

	/**
	 * Constructor
	 * 
	 * @param theBehaviors
	 *            Array with the behaviors to be used in the mind
	 */
	public CCMind(final List<CCForce> theBehaviors) {
		_myCurrentState.setBehaviors(theBehaviors);
	}

	/**
	 * Replaces the array of behaviors with a new one
	 * 
	 * @param theBehaviors
	 *            Array with the new behaviors
	 */
	public void setBehaviors(final List<CCForce> theBehaviors) {
		_myCurrentState.setBehaviors(theBehaviors);
	}

	/**
	 * Adds a new behaviors to the list of behaviors
	 * 
	 * @param theBehavior
	 *            The new behavior
	 */
	public void addBehavior(final CCForce theBehavior) {
		_myCurrentState.addBehavior(theBehavior);
	}

	/**
	 * Toggles the state of behavior tracking. If it is turned on, the
	 * calculated forces of the behaviors are stored in the forces array for
	 * every frame.
	 * 
	 * @param theTrackBehaviors
	 *            Turns the tracking of behavior forces on or off
	 */
	public void setTrackBehaviors(final boolean theTrackBehaviors) {
		_myCurrentState.setTrackBehaviors(theTrackBehaviors);
	}

	/**
	 * Gives access to the list of behaviors
	 * 
	 * @return Iterator over the list of behaviors
	 */
	public List<CCForce> behaviors() {
		return _myCurrentState.behaviors();
	}
	
	public List<CCNeighborHoodBehavior> neighborhoodBehaviors(){
	  return _myCurrentState.neighborhoodBehaviors();
	}

	/**
	 * Calculates the forces based on internal data
	 * 
	 * @return The force for this step of animation
	 */
	public void update(final CCAgent theAgent,final double theDeltaTime) {
		_myCurrentState.update(theAgent, theDeltaTime);
	}

	public CCVector3 steering() {
		return _myCurrentState.steering();
	}
	
	public void state(final CCMindState theState){
		_myCurrentState.finish();
		_myCurrentState = theState;
		_myCurrentState.init();
	}
}
