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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.CCParticleGroup;
import cc.creativecomputing.simulation.steering.behavior.CCNeighborHoodBehavior;


/** 
 * The CCNeighborhood class implements a spatial scene lookup table. 
 * Normally used as a pre-simulation simulator, it calculates the 
 * distances between the vehicles in the scene. It can be used to
 * query for all vehicles or obstacles within a defined radius of 
 * another vehicle.
 */
public class CCNeighborhood<AgentType extends CCAgent> extends CCParticleGroup<AgentType>implements CCAnimatorListener{
	
	private List<CCObstacle> _myObstacles = new ArrayList<CCObstacle>();
	
	public static CCNeighborhood<CCAgent> EMPTY = new CCNeighborhood<CCAgent>();

	/** 
	 * Distance matrix 
	 **/
	protected double[][] _myDistanceMatrix;

	/**
	 * Number of elements in the distance matrix 
	 **/
	protected int _myNumberOfElements = 0;

	/** 
	 * Constructor 
	 **/
	public CCNeighborhood(){
		super();
	}

	@Override
	/** 
	 * Adds a new vehicle to the scene description. The vehicle is
	 * automatically added to all pre- and post-Simulations as well.
	 * @param theNewVehicle The vehicle object to add to the scene
	 */
	public boolean add(final AgentType theAgent) {
		// Register this class in all behaviors that need it 
		CCMind _myMind = theAgent.mind();

		if (_myMind != null) {
			for (CCNeighborHoodBehavior myBehavior : _myMind.neighborhoodBehaviors()) {
				myBehavior.neighborhood(this);
			}
		}

		return super.add(theAgent);
	}
	
	public void add(CCObstacle theObstacle) {
		_myObstacles.add(theObstacle);
	}

	/** 
	 * Initializes the neighborhood object distance matrix
	 */
	public void init() {
		// get number of elements
		_myNumberOfElements = size();

		// create distance matrix
		_myDistanceMatrix = new double[_myNumberOfElements][_myNumberOfElements];

		// fill distance matrix with -1 	
		for (int i = 0; i < _myNumberOfElements; i++) {
			for (int j = 0; j < _myNumberOfElements; j++) {
				_myDistanceMatrix[i][j] = -1;
			}
		}
	}

	/** 
	 * Removes all objects from the scene description.
	 * The pre- and post-simulations are also cleared of all objects	 
	 */
	public void removeAll() {
		clear();

		// Reset the element count to force a re-initialization of the distance matrix
		_myNumberOfElements = 0;
	}

	/**
	 * Replaces the list of vehicles with a new list
	 *
	 * @param theAgents Array of vehicles
	 */
	public void agents(final List<AgentType> theAgents) {
		removeAll();
		addAll(theAgents);
	}

	/** 
	 * Updates the current neighborhood state and recalculates
	 * the distance informations. If the number of vehicles has
	 * changed, the distance matrix is completely regenerated.
	 */
	public void update(final CCAnimator theAnimator) {
		if (size() != _myNumberOfElements){
			init();
		}
		
		for (int i = 0; i < _myNumberOfElements; i++) {
			for (int j = i + 1; j < _myNumberOfElements; j++) {

				// quadratic distance
				double lenSqr = get(i).position.distanceSquared(get(j).position);
				_myDistanceMatrix[i][j] = lenSqr;
				_myDistanceMatrix[j][i] = lenSqr;
			}
		}
		
		super.update(theAnimator.deltaTime());
	}

	/** 
	 * Returns the number of elements in the distance matrix
	 * @return Number of elements in the distance matrix
	 */
	public int getCount() {
		return size();
	}
	
	/** 
	 * Returns an array of vehicles whose distance from the vehicle
	 * is less than the specified distance
	 * @param theAgent CCVehicle used as center for search
	 * @param theDistance maximum distance from vehicle
	 * @return Array of vehicles
	 */
	public List<CCObstacle> getNearObstacles(final CCParticle theAgent, final double theDistance){
		final List<CCObstacle> myResult = new ArrayList<CCObstacle>();
		
		final CCVector3 myMinCorner = theAgent.position.clone();
		myMinCorner.subtractLocal(theDistance, theDistance, theDistance);
		
		final CCVector3 myMaxCorner = theAgent.position.clone();
		myMaxCorner.addLocal(theDistance, theDistance, theDistance);
		for(CCObstacle myObstacle:_myObstacles){
			// Add the obstacle to the result set
			if (myObstacle.domain().intersectsBox(myMinCorner, myMaxCorner)){
				myResult.add(myObstacle);								
			}
		}				
		            
		return myResult;	
	}

	/** 
	 * Returns an array of vehicles whose distance from the vehicle
	 * is less than the specified distance
	 * @param v CCVehicle used as center for search
	 * @param theDistance maximum distance from vehicle
	 * @return Array of vehicles
	 */
	public List<AgentType> getNearAgents(final CCParticle theAgent, final double theDistance) {
		final List<AgentType> _myResult = new ArrayList<>();

		// get the index of the vehicle
		final int myVehicleIndex = indexOf(theAgent);
		
		if(myVehicleIndex == -1)return _myResult;
		// quadratic distance
		final double distSquared = theDistance * theDistance;

	
		
		// query distance matrix and put near vehicles in the result list
		for (int i = 0; i < _myNumberOfElements; i++) {
			if (i != myVehicleIndex && (_myDistanceMatrix[myVehicleIndex][i] <= distSquared)){
				_myResult.add(get(i));
			}
		}

		return _myResult;
	}

	@Override
	public void start(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}
}
