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


public abstract class CCTargetBehavior extends CCForce{

	protected final CCVector3 _myTarget;
	protected double _myMaxActiveDistance = Float.MAX_VALUE;
	protected double _myMaxActiveDistanceSq = Float.MAX_VALUE;
	protected double _myMinActiveDistance = 0;
	protected double _myMinActiveDistanceSq = 0;
	
	public CCTargetBehavior(final CCVector3 theTarget){
		_myTarget = theTarget;
	}
	
	/**
	 * Use this method to get and set the position the agent is steered
	 * away from. To set the target you have to use the set method of the returned
	 * Vector3f.
	 * @return Vector3f, the position the agent is directed to
	 */
	public CCVector3 target(){
		return _myTarget;
	}
	
	public void target(final CCVector3 theTarget){
		_myTarget.set(theTarget);
	}
	
	/**
	 * Returns the active distance of the behavior. This distance will determine
	 * how far an object has to near the target to start the behavior.
	 * @return the distance under which the behavior is active
	 */
	public double maxActiveDistance(){
		return _myMaxActiveDistance;
	}
	
	/**
	 * Sets the active distance of the behavior.
	 * @param theActiveDistance
	 */
	public void maxActiveDistance(final double theActiveDistance){
		_myMaxActiveDistance = theActiveDistance;
		_myMaxActiveDistanceSq = theActiveDistance * theActiveDistance;
	}
	
	/**
	 * Returns the minimal active distance of the behavior. This distance will determine
	 * how far an object has to near the target to start the behavior.
	 * @return the distance under which the behavior is active
	 */
	public double minActiveDistance(){
		return _myMinActiveDistance;
	}
	
	/**
	 * Sets the minimal active distance of the behavior.
	 * @param theActiveDistance
	 */
	public void minActiveDistance(final double theActiveDistance){
		_myMinActiveDistance = theActiveDistance;
		_myMinActiveDistanceSq = theActiveDistance * theActiveDistance;
	}
	
	@Override
	public boolean isActive(final CCParticle theVehicle){
		double mySquaredDistance = theVehicle.position.distanceSquared(_myTarget);
		return mySquaredDistance < _myMaxActiveDistanceSq && mySquaredDistance > _myMinActiveDistanceSq;
	}

}
