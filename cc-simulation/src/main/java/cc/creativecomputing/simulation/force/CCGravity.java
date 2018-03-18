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

public class CCGravity extends CCForce{
	
	private CCVector3 _myGravity;
	
	public CCGravity(final CCVector3 theGravity){
		_myGravity = theGravity;
	}
	
	public CCGravity(final double theX, final double theY, final double theZ){
		_myGravity = new CCVector3(theX, theY, theZ);
	}
	
	public CCVector3 gravity(){
		return _myGravity;
	}
	
	public void gravity(final CCVector3 theGravity){
		_myGravity = theGravity;
	}

	public boolean apply(CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
		theForce.set(_myGravity);
		return true;
	}

}
