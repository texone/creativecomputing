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

public class CCFlowFieldFollow extends CCForce{
	
	private CCFlowField _myFlowField;
	private double _myPrediction = 7;
	
	public CCFlowFieldFollow(final CCFlowField theFlowField){
		_myFlowField = theFlowField;
	}
	
	public double prediction(){
		return _myPrediction;
	}
	
	public void prediction(final double thePrediction){
		_myPrediction = thePrediction;
	}
	
	public void flowField(final CCFlowField theFlowField){
		_myFlowField = theFlowField;
	}
	
	public CCFlowField flowField(){
		return _myFlowField;
	}
	
	public boolean apply(final CCParticle theParticle, CCVector3 theForce, double theDeltaTime) {
		double x = theParticle.position.x;
		double y = theParticle.position.y;
		double z = theParticle.position.z;
		
		if(_myPrediction > 0){
			x += theParticle.velocity().x * _myPrediction;
			y += theParticle.velocity().y * _myPrediction;
			z += theParticle.velocity().z * _myPrediction;
		}
		
		CCVector3 myFlow = _myFlowField.flowAtPoint(x,y,z);
	    
		double length = (1.1F * theParticle.velocity().length()) / myFlow.length();

		theForce.set(
			myFlow.x * length - theParticle.velocity().x,
			myFlow.y * length - theParticle.velocity().y,
			myFlow.z * length - theParticle.velocity().z
		);
	    
	    
		return true;
	}

}
