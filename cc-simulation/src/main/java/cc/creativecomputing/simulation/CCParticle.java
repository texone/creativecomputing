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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;



/**
 * A vehicle with a local space and needed movement fields
 * @author christianr
 *
 */
public class CCParticle extends CCLocalSpace{

	protected double _myMass;

	@CCProperty(name = "max speed")
	public double maxSpeed;

	public double maxForce;
	
	protected double _myAge;
	
	protected boolean _myIsKilled = false;

	protected CCVector3 _myVelocity;

	protected CCVector3 allForces;

	protected CCVector3 _myAcceleration;
	
	public double radius = 0.5f;

	static protected CCVector3 accelUp = new CCVector3();

	static protected final CCVector3 globalUp = new CCVector3(0.0F, 0.1F, 0.0F);

	static protected CCVector3 bankUp = new CCVector3();

	static protected CCVector3 newAccel = new CCVector3();

	static protected double accelDamping = 0.7F;
	
	static protected CCVector3 steering = new CCVector3();

	public CCParticle(){
		_myAcceleration = new CCVector3();
		_myMass = 1.0F;
		_myAge = 0;
		maxSpeed = 1.0F;
		maxForce = 0.04F;
		velocity(new CCVector3());
		allForces = new CCVector3();
	}
	
	/**
	 * Returns the future position of the particle based on its velocity
	 * and the given look ahead time.
	 * @param theLookAhead time to look in the future
	 * @return
	 */
	public CCVector3 futurePosition(final double theLookAhead){
		return new CCVector3(
	        position.x + _myVelocity.x * theLookAhead,
	        position.y + _myVelocity.y * theLookAhead,
	        position.z + _myVelocity.z * theLookAhead
	    );
	}

	
	public void applyForce(CCVector3 force){
		allForces.addLocal(force);
	}

	public void update(double theDeltaTime){
		_myAge += theDeltaTime;
		theDeltaTime *= CCSimulation.TARGET_FRAMERATE;
		
		if(allForces.length() > maxForce * theDeltaTime){
			allForces.normalizeLocal().multiplyLocal(maxForce * theDeltaTime);
		}
		
		newAccel.set(allForces);
		
		if (_myMass != 1.0F){
			newAccel.multiplyLocal(1.0F / _myMass);
		}
		_myAcceleration.blendLocal(newAccel, accelDamping);

		allForces.set(0,0,0);
		
		_myVelocity.addLocal(_myAcceleration);
		if(_myVelocity.length() > maxSpeed * theDeltaTime){
			_myVelocity.normalizeLocal().multiplyLocal(maxSpeed * theDeltaTime);
		}
		
		position.addLocal(_myVelocity);
		
		accelUp.set(_myAcceleration);
		accelUp.multiplyLocal(0.5F);
		
		bankUp.set(up);
		bankUp.addLocal(accelUp);
		bankUp.addLocal(globalUp);
		bankUp.normalizeLocal();
		
		double speed = _myVelocity.length();
		
		if (speed > 0.0F){
			forward.set(_myVelocity);
			forward.multiplyLocal(1.0F / speed);
			side.set(forward.cross(bankUp));
			up.set(side.cross(forward));
		}
	}
	
	/**
	 * Returns the speed relative to maximum speed of the vehicle
	 * @return
	 */
	public double relativeSpeed() {
		return _myVelocity.length() / maxSpeed;
	}

	public void velocity(CCVector3 velocity) {
		_myVelocity = velocity;
	}

	public CCVector3 velocity() {
		return _myVelocity;
	}

	public void acceleration(CCVector3 acceleration) {
		_myAcceleration = acceleration;
	}

	public CCVector3 acceleration() {
		return _myAcceleration;
	}
	
	public double age(){
		return _myAge;
	}
	
	public void age(final double theAge){
		_myAge = theAge;
	}
	
	public boolean isKilled(){
		return _myIsKilled;
	}
	
	public void isKilled(final boolean theIsKilled){
		_myIsKilled = theIsKilled;
	}
}
