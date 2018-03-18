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
package cc.creativecomputing.simulation;

import cc.creativecomputing.math.CCVector3;



public class CCFixableParticle extends CCParticle{

	public boolean fixed = false;

	public CCFixableParticle(){
		super();
	}

	public void update(double dt){

		if (!fixed)
			super.update(dt);
	}
	
	public void addDampingForce(double factor){
		allForces.subtractLocal( _myVelocity.multiply(factor) );
	}

	public void addRepulsionForce(CCVector3 where, double radius, double scaleX, double scaleY){
	    				
		CCVector3 diff	= position.subtract(where);
		double length	= diff.length();
		
		boolean closeEnough = true;
	    if (radius > 0){
	        if (length > radius){
	            closeEnough = false;
	        }
	    }
		
		if (closeEnough == true){
			double pct = 1 - (length / radius);  // stronger on the inside
	        diff.normalize();
			allForces.x += diff.x * scaleX * pct;
			allForces.y += diff.y * scaleY * pct;
	    }
	}
}
