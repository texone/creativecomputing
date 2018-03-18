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

import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

public class CCAgent extends CCParticle{
	
	protected CCMind _myMind;

	public CCAgent(){
		super();
		maxSpeed = 0.3F;
		maxForce = 0.02F;
		_myMind = new CCMind();
	}
	
	public void addBehavior(final CCForce theBehavior){
		_myMind.addBehavior(theBehavior);
	}
	
	public CCMind mind(){
		return _myMind;
	}

	public void update(final double theDeltaTime){
		_myMind.update(this, theDeltaTime);
		
		super.update(theDeltaTime);
	}
}
