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
 * A cooliehat is a special button, that can be found on joypads
 * for example. It is not only on or of but does also have a 
 * direction.
 * @see CCInputSlider
 * @see CCInputStick
 * @see CCInputDevice
 */
public class CCInputCoolieHat extends CCInputButton{
	
	/**
    * Standard value for center HAT position
    */
    private static final int OFF = 0;
   /**
    * Standard value for up-left HAT position
    */
    private static final int UP_LEFT = 1;
   /**
    * Standard value for up HAT position
    */
    private static final int UP = 2;
   /**
    * Standard value for up-right HAT position
    */
    private static final int UP_RIGHT = 3;
    /**
    * Standard value for right HAT position
    */
    private static final int RIGHT = 4;
   /**
    * Standard value for down-right HAT position
    */
    private static final int DOWN_RIGHT = 5;
    /**
    * Standard value for down HAT position
    */
    private static final int DOWN = 6;
   /**
    * Standard value for down-left HAT position
    */
    private static final int DOWN_LEFT = 7;
    /**
    * Standard value for left HAT position
    */
    private static final int LEFT = 8;
	
	private float _myX = 0;
	private float _myY = 0;
	
	private static float DIAGONAL_FACTOR = (float)Math.sin(Math.PI/4);
	private float _myChange = 1;
	private float _myDiagonalChange = DIAGONAL_FACTOR;
	
	private List<CCInputCoolieHatListener> _myCoolieHatListener = new ArrayList<CCInputCoolieHatListener>();
		
	/**
	 * Initializes a new ControllCrossButton.
	 * @param theComponent
	 */
	CCInputCoolieHat(final Component theComponent){
		super(theComponent);
	}
	
	/**
	 * Add a listener to react on press and releases of the cooliehat
	 * @param theListener
	 */
	public void addListener(final CCInputCoolieHatListener theListener) {
		_myCoolieHatListener.add(theListener);
	}
	
	/**
	 * This method is called before each frame to update the button state.
	 */
	void update(){
		_myActualValue = _myComponent.getPollData()*8;
		_myIsPressed = _myActualValue > 0f;
		
		if(_myIsPressed && !_myOldIsPressed){
			for(CCInputCoolieHatListener myListener:_myCoolieHatListener) {
				myListener.onPress(_myX,_myY);
			}
		}else if(!_myIsPressed&& _myOldIsPressed){
			for(CCInputCoolieHatListener myListener:_myCoolieHatListener) {
				myListener.onRelease(_myX,_myY);
			}
		}
		
		_myOldIsPressed = _myIsPressed;
		
		switch((int)_myActualValue){
			case DOWN:
				_myY = _myChange;
				break;
			case DOWN_LEFT:
				_myX = -_myDiagonalChange;
				_myY = _myDiagonalChange;
				break;
			case LEFT:
				_myX = -_myChange;
				break;
			case UP_LEFT:
				_myX = -_myDiagonalChange;
				_myY = -_myDiagonalChange;
				break;
			case UP:
				_myY = -_myChange;
				break;
			case UP_RIGHT:
				_myX = +_myDiagonalChange;
				_myY = -_myDiagonalChange;
				break;
			case RIGHT:
				_myX = _myChange;
				break;
			case DOWN_RIGHT:
				_myX = _myDiagonalChange;
				_myY = _myDiagonalChange;
				break;
			case OFF:
				_myX = 0;
				_myY = 0;
		}
	}
	
	/**
	 * Returns the name of the cooliehat .
	 * @return String, the name of the input element
	 */
	public String name(){
		return "cooliehat: " + super.name();
	}
	
	/**
	 * The current x value of the cooliehat.
	 * @return float, the x value of the cooliehat
	 */
	public float x(){
		return _myX;
	}
	
	/**
	 * The current y value of the cooliehat.
	 * @return float, the y value of the cooliehat
	 */
	public float y(){
		return _myY;
	}
	
	private float _myMultiplier = 1;
	
	/**
	 * Pressing a cooliehat causes a change by 1 in the according direction.
	 * With the multiplier you can increase and decrease this value. Use this 
	 * method to get the actual multiplier. By default this value is 1.0.
	 * @return float, the actual multiplier for the cooliehat
	 */
	public float multiplier(){
		return _myMultiplier;
	}
	
	/**
	 * Pressing a cooliehat causes a change by 1 in the according direction.
	 * With the multiplier you can increase and decrease this range. Use this 
	 * method to set the actual multiplier. By default this value is 1.0.
	 * @param theMultiplier float, the new multiplier for a CrossButton
	 */
	public void multiplier(final float theMultiplier){
		_myMultiplier = theMultiplier;
		_myChange = _myMultiplier;
		_myDiagonalChange = DIAGONAL_FACTOR * theMultiplier;
	}
}
