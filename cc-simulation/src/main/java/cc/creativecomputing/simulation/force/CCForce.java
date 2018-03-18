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
package cc.creativecomputing.simulation.force;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;


public abstract class CCForce{
	
	protected double weight = 1;
	
	protected static double sqrt2 = (double) Math.sqrt(2D);
	
	public CCForce(){
	}
	
	/**
	 * Implement this method to define a logic under which circumstances a behavior is active
	 * @return
	 */
	public boolean isActive(final CCParticle theVehicle){
		return true;
	}
	
	public double weight(){
		return weight;
	}
	
	/**
	 * Sets the weight this behavior is applied with
	 * can range from 0 to 1 exceeding values are truncated
	 * @param theWeight
	 */
	public void weight(final double theWeight){
		weight = Math.max(theWeight,0);
	}

	
	/**
	 * Applies the behavior to the given force 
	 * @param theForce CCVector3f, vector the behavior is applied to
	 * @param theDeltaTime TODO
	 * @param i_entity CCAgent, the agent the behavior is applied to
	 */
	public abstract boolean apply(final CCParticle theParticle, final CCVector3 theForce, double theDeltaTime);
}
