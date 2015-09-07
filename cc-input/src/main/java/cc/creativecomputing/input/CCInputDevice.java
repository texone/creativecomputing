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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;

/**
 * <p>
 * The CCInputDevice class handles the communication with your input device. 
 * A device consists of buttons and sliders, sliders can be grouped to sticks.
 * </p>
 * <p>
 * To react on button events you can add listeners, that are called when a 
 * button is pressed or released.
 * </p>
 * 
 * @see CCInputIO
 * @see CCInputSlider
 * @see CCInputButton
 * @see CCInputStick
 */
public class CCInputDevice {

	/**
	 * The JInput controller instance for this device
	 */
	private final Controller _myController;

	/**
	 * list containing the _mySticks of the device
	 */
	private final List<CCInputStick> _mySticks = new ArrayList<CCInputStick>();

	/**
	 * list containing the _mySliders of the device
	 */
	private final List<CCInputSlider> _mySliders = new ArrayList<CCInputSlider>();

	/**
	 * list containing the _myButtons of the device
	 */
	private final List<CCInputButton> _myButtons = new ArrayList<CCInputButton>();

	/**
	 * list containing the rumblers of the device
	 */
	private Rumbler[] _myRumblers = new Rumbler[0];

	/**
	 * to map the input names and Controller inputs
	 */
	private final Map<String, CCInput> _myInputMap = new HashMap<String, CCInput>();

	/**
	 * A List with the _myButtons and Sliders available by the device
	 */
	private final List<CCInput> _myInputs = new ArrayList<CCInput>();

	/**
	 * true if the device has been opened. Only opened devices are updated before a frame.
	 */
	private boolean _myIsOpen = false;

	/**
	 * The name of the device.
	 */
	private final String _myName;

	/**
	 * Initializes a new device by the given Controller
	 * 
	 * @param theController
	 */
	CCInputDevice(final Controller theController) {
		_myController = theController;
		_myName = theController.getName();
		setupDevice();
	}

	/**
	 * Loads the available Sliders, Sticks and Buttons for a device
	 */
	private void setupDevice() {
		final Component[] components = _myController.getComponents();
		
		for (int i = 0; i < components.length; i++) {
			if (components[i].isAnalog()) {
				CCInputSlider mySlider;
				if (components[i].isRelative()) {
					mySlider = new CCInputRelativeSlider(components[i]);
				} else {
					mySlider = new CCInputSlider(components[i]);
				}
				_mySliders.add(mySlider);
				_myInputMap.put(mySlider.name(), mySlider);
			} else {
				CCInputButton myButton;
				if (components[i].getIdentifier() == Component.Identifier.Axis.POV) {
					myButton = new CCInputCoolieHat(components[i]);
				} else {
					myButton = new CCInputButton(components[i]);
				}
				_myButtons.add(myButton);
				_myInputMap.put(myButton.name(), myButton);
			}
		}

		_myInputs.addAll(_mySliders);
		_myInputs.addAll(_myButtons);

		if (_mySliders.size() % 2 == 0) {
			for (int i = 0; i < _mySliders.size(); i += 2) {
				CCInputSlider sliderX = _mySliders.get(i);
				CCInputSlider sliderY = _mySliders.get(i + 1);
				_mySticks.add(new CCInputStick(sliderX, sliderY));
			}
		}

		_myRumblers = _myController.getRumblers();
	}

	/**
	 * Returns the name of the device.
	 * 
	 * @return the name of a device
	 */
	public String name() {
		return _myName;
	}

	/**
	 * Returns the String representation of a device
	 * 
	 * @return the String representation of a device
	 */
	public String toString() {
		return _myController.getName();
	}

	/**
	 * This method is called before each frame to load the 
	 * controller data and update the values.
	 */
	protected void update(final float theDeltaTime) {
		if (_myIsOpen) {
			_myController.poll();
			for (int i = 0; i < _myInputs.size(); i++)
				_myInputs.get(i).update(theDeltaTime);
		}
	}

	/**
	 * Lists the available slider of a device in the console window.
	 * This method is useful to get the name of the different sliders at the start of the application.
	 * 
	 * @see CCInputSlider
	 * @see #printButtons()
	 * @see #printSticks()
	 */
	public void printSliders() {
		if (_mySliders.size() > 0) {
			System.out.println("\n<<< available " + _myName + " sliders: >>>\n");
			for (int i = 0; i < _mySliders.size(); i++) {
				CCInputSlider slider = _mySliders.get(i);
				System.out.print("     " + i + ": ");
				System.out.print(slider.name());
				if (slider.isRelative()) {
					System.out.println(" relative");
				} else {
					System.out.println(" absolute");
				}
			}
			System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}

	/**
	 * Lists the available button of a device in the console window. This method is useful at startup to get the name of
	 * the different buttons.
	 * 
	 * @see CCInputButton
	 * @see #printSliders()
	 * @see #printSticks()
	 */
	public void printButtons() {
		if (_myButtons.size() > 0) {
			System.out.println("\n<<< available " + _myName + " buttons: >>>\n");
			for (int i = 0; i < _myButtons.size(); i++) {
				System.out.print("     " + i + ": ");
				System.out.println(_myButtons.get(i).name());
			}
			System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}
	
	/**
	 * Lists the available rumblers of a device in the console window. This method is useful at startup to get the name of
	 * the different buttons.
	 * 
	 * @see CCInputButton
	 * @see #printSliders()
	 * @see #printSticks()
	 */
	public void printRumblers() {
		if (_myRumblers.length > 0) {
			System.out.println("\n<<< available " + _myName + " buttons: >>>\n");
			for (int i = 0; i < _myRumblers.length; i++) {
				System.out.print("     " + i + ": ");
				System.out.println(_myRumblers[i].getAxisName());
			}
			System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}

	/**
	 * Lists the available sticks of a device in the console window. This method is useful at startup to get the name
	 * of the different sticks.
	 * 
	 * @see CCInputStick
	 * @see #printSliders()
	 * @see #printButtons()
	 * @see #printDeviceInfo()
	 */
	public void printSticks() {
		if (_mySticks.size() > 0) {
			System.out.println("\n<<< available " + _myName + " sticks: >>>\n");
			for (int i = 0; i < _mySticks.size(); i++) {
				System.out.print("     " + i + ": ");
				System.out.println(_mySticks.get(i).name());
			}
			System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}
	
	/**
	 * Lists all available control elements of the device. Use this method
	 * to get the names and ids of the different control elements to open them.
	 * @see CCInputStick
	 * @see #printSliders()
	 * @see #printButtons()
	 * @see #printSticks()
	 */
	public void printDeviceInfo() {
		printSticks();
		printSliders();
		printButtons();
		printRumblers();
	}

	/**
	 * Returns the number of sliders of the device.
	 * 
	 * @return the number of sliders available for a device
	 * @see CCInputSlider
	 * @see #numberOfButtons()
	 * @see #numberOfSticks()
	 * @see #slider(int)
	 */
	public int numberOfSliders() {
		return _mySliders.size();
	}

	/**
	 * Use this method to get a Slider. You can get a slider by its name or its number. Use printSliders to see what
	 * sliders are available for a device.
	 * 
	 * @param theSliderId the number of the slider to return
	 * @return the Slider corresponding to the given number or String
	 * @see CCInputSlider
	 * @see #numberOfSliders()
	 * @see #button(int)
	 * @see #stick(int)
	 */
	public CCInputSlider slider(final int theSliderId) {
		return _mySliders.get(theSliderId);
	}

	/**
	 * Use this method to get a Slider. You can get a slider by its name or its number. Use printSliders to see what
	 * sliders are available for a device.
	 * 
	 * @param theSliderName String, name of the slider to return
	 */
	public CCInputSlider slider(final String theSliderName) {
		try {
			return (CCInputSlider) _myInputMap.get(theSliderName);
		} catch (ClassCastException e) {
		}

		throw new RuntimeException("There is no slider with the name " + theSliderName + ".");
	}

	/**
	 * Tolerance is minimum under which the input is set to zero. Use this method to set the tolerance for all
	 * sliders of the device.
	 * 
	 * @param theTolerance the new tolerance for the device
	 * @see CCInputSlider
	 */
	public void tolerance(final float theTolerance) {
		for (int i = 0; i < _mySliders.size(); i++)
			_mySliders.get(i).tolerance(theTolerance);
	}

	/**
	 * Returns the number of buttons of the device.
	 * 
	 * @return the number of buttons available for a device
	 * @see CCInputButton
	 * @see #numberOfSliders()
	 * @see #numberOfSticks()
	 * @see #button(int)
	 */
	public int numberOfButtons() {
		return _myButtons.size();
	}

	/**
	 * Use this method to get a Button. You can get a button by its name or its number. Use printButtons to see what
	 * buttons are available for a device.
	 * 
	 * @param theButtonId the number of the button to return
	 * @return the button corresponding to the given number or name
	 * @see CCInputButton
	 * @see #slider(int)
	 * @see #stick(int)
	 * @see #coolieHat(int)
	 */
	public CCInputButton button(final int theButtonId) {
		return _myButtons.get(theButtonId);
	}

	/**
	 * Use this method to get a button. You can get a button by its name or its number. Use printButtons to see what
	 * buttons are available for a device.
	 * 
	 * @param theButtonName String, name of the button to return
	 */
	public CCInputButton button(final String theButtonName) {
		try {
			return (CCInputButton) _myInputMap.get(theButtonName);
		} catch (ClassCastException e) {
		}
		throw new RuntimeException("There is no button with the name " + theButtonName + ".");
	}

	/**
	 * Returns the number of sticks of the device.
	 * 
	 * @return the number of _mySticks available for a device
	 * @see CCInputStick
	 * @see #numberOfSliders()
	 * @see #numberOfButtons()
	 * @see #stick(int)
	 */
	public int numberOfSticks() {
		return _mySticks.size();
	}

	/**
	 * Use this method to get a stick. You can get a stick by its name or its number. Use printSticks to see what
	 * sticks are available for a device.
	 * 
	 * @param theStickID int, the number of the button to return
	 * @return ControllStick, the stick corresponding to the given number or name
	 * @see CCInputStick
	 * @see #numberOfButtons()
	 * @see #numberOfSliders()
	 * @see #slider(int)
	 * @see #button(int)
	 * @see #coolieHat(int)
	 */
	public CCInputStick stick(final int theStickID) {
		return _mySticks.get(theStickID);
	}

	/**
	 * Use this method to get a stick. You can get a stick by its name or its number. Use printSticks to see what
	 * sticks are available for a device.
	 * @param theStickName String, name of the button to return
	 */
	public CCInputStick stick(final String theStickName) {
		for (int i = 0; i < numberOfSticks(); i++) {
			CCInputStick stick = _mySticks.get(i);
			if (stick.name().equals(theStickName)) {
				return stick(i);
			}
		}
		throw new RuntimeException("There is no stick with the name " + theStickName + ".");
	}

	/**
	 * Use this method to get a cooliehat. You can get a cooliehat by its name or its number. Use printButtons to see
	 * what _myButtons are a cooliehat.
	 * 
	 * @param theCoolieHatId int, the number of the cooliehat to return
	 * @return ControllCoolieHat, the cooliehat coresponding to the given number or name
	 * @see CCInputCoolieHat
	 * @see #slider(int)
	 * @see #stick(int)
	 * @see #button(int)
	 */
	public CCInputCoolieHat coolieHat(final int theCoolieHatId) {
		return (CCInputCoolieHat) _myButtons.get(theCoolieHatId);
	}

	/**
	 * Use this method to get a cooliehat. You can get a cooliehat by its name or its number. Use printButtons to see
	 * what _myButtons are a cooliehat.
	 * @param theName String, name of the button to return
	 */
	public CCInputCoolieHat coolieHat(final String theName) {
		try {
			return (CCInputCoolieHat) _myInputMap.get(theName);
		} catch (ClassCastException e) {
		}
		throw new RuntimeException("There is no button with the name " + theName + ".");
	}

	/**
	 * Use this method to open a device. A device is automatically opened by default, so you only need to call this when
	 * you have closed it with the close method.
	 */
	public void open() {
		_myIsOpen = true;
	}

	/**
	 * Use this method to close a device. A closed device does not to be updated to get values.
	 */
	public void close() {
		_myIsOpen = false;
	}

	public void rumble(final float theIntensity, final int theId) {
		if (theId >= _myRumblers.length)
			return;
		else
			_myRumblers[theId].rumble(theIntensity);
	}

	public void rumble(final float theIntensity) {
		rumble(theIntensity, 0);
	}
}
