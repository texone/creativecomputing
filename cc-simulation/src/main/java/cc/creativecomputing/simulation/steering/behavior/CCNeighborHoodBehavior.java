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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;
import cc.creativecomputing.simulation.steering.CCAgent;
import cc.creativecomputing.simulation.steering.CCNeighborhood;



public abstract class CCNeighborHoodBehavior extends CCForce{
	
	protected CCNeighborhood<?> _myNeighborhood;
	
	/**
	 * Distance in which neighbors have to be to be included into calculations
	 */
	protected double _myNearAreaRadius;
	
	/**
	 * Cosine of the angle in which neighbors have to be to be included into calculations
	 */
	protected double _myNearAngleCos;
	
	/**
	 * Vector for keeping the position of the group of related Agents
	 */
	protected CCVector3 _myGroupPosition = new CCVector3();
	
	/**
	 * List for keeping the current neighbors of this agent
	 */
	public List<CCAgent> neighbors = new ArrayList<CCAgent>();
	
	public CCNeighborHoodBehavior(final double theNearAreaRadius, final double theNearAngle){
		_myNeighborhood  = CCNeighborhood.EMPTY;
		_myNearAreaRadius = theNearAreaRadius;
		_myNearAngleCos = CCMath.cos(CCMath.radians(theNearAngle));
	}
	
	public CCNeighborHoodBehavior(){
		this(30,1);
		_myNearAngleCos = 1;
	}
	
	public CCVector3 groupPosition(){
		return _myGroupPosition;
	}
	
	public void neighborhood(final CCNeighborhood<?> theNeighborhood){
		_myNeighborhood = theNeighborhood;
	}
	
	public boolean isInAngle(final CCAgent theAgent, final CCAgent theOtherAgent){
		if(_myNearAngleCos == 1)return true;
		final CCVector3 range = theAgent.position.clone();
		range.subtractLocal(theOtherAgent.position);
		
		double dot = range.dot(theAgent.forward);
		return dot >= _myNearAngleCos;
	}
	
	public void nearAreaRadius(final double theNearAreaRadius){
		_myNearAreaRadius = theNearAreaRadius;
	}
	
	public double nearAreaRadius(){
		return _myNearAreaRadius;
	}
	
	public void nearAngle(final double theAngle){
		_myNearAngleCos = CCMath.cos(CCMath.radians(theAngle));
	}
	
	protected int _myCount = 0;
	
	
	public boolean apply(final CCParticle theAgent, final CCVector3 theForce, double theDeltaTime){
		_myCount = 0;
		theForce.set(0,0,0);
		
		neighbors.clear();
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius)){
			//if (isInAngle(theAgent,myOtherAgent)){
				_myCount++;
				theForce.addLocal(myOtherAgent.position);
				neighbors.add(myOtherAgent);
			//}
		}
		
		if (_myCount > 0){
			theForce.multiplyLocal(1.0F / _myCount);
			_myGroupPosition.set(theForce);
			return true;
		}
		return false; 
	}
}
