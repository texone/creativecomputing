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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.behavior.CCNeighborHoodBehavior;

public class CCMindState {
	/**
	 * List of behaviors used on this object
	 */
	protected List<CCForce> _myBehaviors = null;
	
	protected List<CCNeighborHoodBehavior> _myNeighborhoodBehaviors = null;

	protected CCVector3 _mySteering = new CCVector3();

	/**
	 * Use this to turn on the tracking of behavior forces
	 */
	protected boolean _myTrackBehaviors = false;
	
	/**
	 * Constructor
	 */
	public CCMindState() {
		_myBehaviors = new ArrayList<CCForce>();
		_myNeighborhoodBehaviors = new ArrayList<CCNeighborHoodBehavior>();
	}

	/**
	 * Constructor
	 * 
	 * @param theBehaviors
	 *            Array with the behaviors to be used in the mind
	 */
	public CCMindState(final List<CCForce> theBehaviors) {
		setBehaviors(theBehaviors);
	}

	/**
	 * Replaces the array of behaviors with a new one
	 * 
	 * @param theBehaviors
	 *            Array with the new behaviors
	 */
	public void setBehaviors(final List<CCForce> theBehaviors) {
		_myBehaviors = theBehaviors;
	}

	/**
	 * Adds a new behaviors to the list of behaviors
	 * 
	 * @param theBehavior
	 *            The new behavior
	 */
	public void addBehavior(final CCForce theBehavior) {
		_myBehaviors.add(theBehavior);
		if(theBehavior instanceof CCNeighborHoodBehavior){
		    _myNeighborhoodBehaviors.add((CCNeighborHoodBehavior)theBehavior);
		}
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
		_myTrackBehaviors = theTrackBehaviors;
	}

	/**
	 * Gives access to the list of behaviors
	 * 
	 * @return Iterator over the list of behaviors
	 */
	public List<CCForce> behaviors() {
		return _myBehaviors;
	}
	
	public List<CCNeighborHoodBehavior> neighborhoodBehaviors(){
		return _myNeighborhoodBehaviors;
	}

	/**
	 * Calculates the forces based on internal data
	 * 
	 * @return The force for this step of animation
	 */
	public void update(final CCAgent theAgent,final float theDeltaTime) {
		update(theDeltaTime);
		for (CCForce myBehavior : _myBehaviors) {
			if (!myBehavior.isActive(theAgent)){
				continue;
			}
			
			_mySteering.set(0,0,0);
			myBehavior.apply(theAgent, _mySteering, theDeltaTime);
			_mySteering.normalizeLocal();
			_mySteering.multiplyLocal(myBehavior.weight());
			
			_mySteering.multiplyLocal(theDeltaTime * CCSimulation.TARGET_FRAMERATE);
			theAgent.applyForce(_mySteering);
		}
	}

	public CCVector3 steering() {
		return _mySteering;
	}
	
	public void init(){}
	
	public void update(final float theDeltaTime){}
	
	public void finish(){}
}
