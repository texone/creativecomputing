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
package cc.creativecomputing.input;

import java.util.ArrayList;
import java.util.List;


import net.java.games.input.Component;

/**
 * This class represents a button of a device. You can use the pressed() 
 * method to see if a button is pressed or add listeners to 
 * handle events.
 * @shortdesc This class represents a button of a device.
 * @see CCInputSlider
 * @see CCInputStick
 * @see CCInputDevice
 */
public class CCInputButton extends CCInput{
	
	protected boolean _myIsPressed = false;
	protected boolean _myOldIsPressed = false;
	
	private List<CCInputButtonListener> _myButtonListener = new ArrayList<CCInputButtonListener>();
		
	/**
	 * Initializes a new Slider.
	 * @param theComponent
	 */
	CCInputButton(final Component theComponent){
		super(theComponent);
	}
	
	/**
	 * This method is called before each frame to update the button state.
	 */
	void update(final float theDeltaTime){
		_myActualValue = _myComponent.getPollData()*8;
		_myIsPressed = _myActualValue > 0f;
		
		if(_myIsPressed && !_myOldIsPressed){
			for(CCInputButtonListener myListener:_myButtonListener) {
				myListener.onPress();
			}
		}else if(!_myIsPressed&& _myOldIsPressed){
			for(CCInputButtonListener myListener:_myButtonListener) {
				myListener.onRelease();
			}
		}
		
		_myOldIsPressed = _myIsPressed;
	}
	
	/**
	 * Use this method to add a listener that handles button events. A listener
	 * needs to implement the CCInputButtonListener interface and its onPress and
	 * onRelease methods.
	 * @shortdesc Adds a new listener to react on button presses and releases.
	 * @param theListener the listener to handle events
	 */
	public void addListener(final CCInputButtonListener theListener) {
		_myButtonListener.add(theListener);
	}
	
	/**
	 * @shortdesc This method returns true if the button was pressed. 
	 * @return boolean, true if the button was pressed
	 * @see CCInputButton
	 */
	public boolean pressed(){
		return _myIsPressed;
	}
}
