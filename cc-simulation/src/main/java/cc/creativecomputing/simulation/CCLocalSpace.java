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

/**
 * Class representing a localspace
 * @author christianr
 *
 */
public class CCLocalSpace{

	/**
	 * position of the local space
	 */
	public CCVector3 position;

	/**
	 * Forward direction of the local space
	 */
	public CCVector3 forward;

	/**
	 * Side direction of the local space
	 */
	public CCVector3 side;

	/**
	 * up direction of the local space
	 */
	public CCVector3 up;

	/**
	 * used for better performance in calculations
	 */
	static CCVector3 component = new CCVector3();

	public CCLocalSpace(){
		setToIdentity();
	}

	public CCLocalSpace(final CCVector3 initialPosition){
		setToIdentity();
		position = initialPosition.clone();
	}

	/**
	 * Initializes a new local space where all values are set to zero
	 *
	 */
	public void setToIdentity(){
		position = new CCVector3(0.0F, 0.0F, 0.0F);
		forward = new CCVector3(0.0F, 0.0F, 1.0F);
		side = new CCVector3(1.0F, 0.0F, 0.0F);
		up = new CCVector3(0.0F, 1.0F, 0.0F);
	}

	
	public CCVector3 globalizePosition(final CCVector3 theLocalPosition){
		final CCVector3 myResult = globalizeDirection(theLocalPosition);
		myResult.addLocal(position);
		return myResult;
	}

	public CCVector3 globalizeDirection(final CCVector3 theLocalVector){
		double x = side.x * theLocalVector.x;
		double y = side.y * theLocalVector.x;
		double z = side.z * theLocalVector.x;
		
		x += up.x * theLocalVector.y;
		y += up.y * theLocalVector.y;
		z += up.z * theLocalVector.y;
		
		x += forward.x * theLocalVector.z;
		y += forward.y * theLocalVector.z;
		z += forward.z * theLocalVector.z;
		
		return new CCVector3(x,y,z);
	}

	public CCVector3 localizePosition(final CCVector3 theGlobalVector){
		final CCVector3 myResult = new CCVector3();
			component.set(theGlobalVector);
			component.subtractLocal(position);
			myResult.set(component.dot(side), component.dot(up), component.dot(forward));
		return myResult;
	}

}
