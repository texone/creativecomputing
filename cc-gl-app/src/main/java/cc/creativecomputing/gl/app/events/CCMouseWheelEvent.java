/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.gl.app.events;

import java.awt.event.MouseWheelEvent;

import cc.creativecomputing.app.events.CCEvent;

public class CCMouseWheelEvent extends CCEvent{

	
	public static final String MOUSE_EVENT = "MOUSE_WHEEL_EVENT";
	
	private float _myRotation;
	private boolean _myIsHorizontal;
	
	public CCMouseWheelEvent(MouseWheelEvent theEvent){
		_myIsHorizontal = theEvent.isShiftDown();
		_myRotation = (float)theEvent.getPreciseWheelRotation();
	
	}
	
	public CCMouseWheelEvent(float theRotationScale, float...theRotation){
		//_myRotation = theEvent.getWheelRotation();
		
		if(theRotation.length > 1){
			if(theRotation[0] != 0){
				_myIsHorizontal = true;
				_myRotation = theRotation[0];
			}else{
				_myIsHorizontal = false;
				_myRotation = theRotation[1];
			}
		}else{
			_myIsHorizontal = false;
			_myRotation = theRotation[0];
		}
		
		_myRotation *= theRotationScale;
		
		
	}
	
	public float rotation(){
		return _myRotation;
	}
	
	public boolean isHorizontal(){
		return _myIsHorizontal;
	}
}
