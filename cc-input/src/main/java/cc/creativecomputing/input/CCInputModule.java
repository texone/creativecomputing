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

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectInputEnvironmentPlugin;
import net.java.games.input.LinuxEnvironmentPlugin;
import net.java.games.input.OSXEnvironmentPlugin;
import cc.creativecomputing.app.modules.CCAbstractAppModule;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCBasicAppListener;
import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.nativeutil.CCNativeLibUtil;


/**
 * <p>
 * CCInputIO is the base class for using controllers in creative computing.
 * It provides methods to retrieve information about the connected 
 * devices and get the input data from them.<br>
 * On start you should use the printDevices() function to see if all controllers
 * are connected and found.
 * </p>
 * @see CCInputDevice
 */
public class CCInputModule extends CCAbstractAppModule<CCInputListener> implements CCAnimatorListener, CCBasicAppListener{
	/**
	 * @invisible
	 */
	public static final int ON_PRESS = 0;
	/**
	 * @invisible
	 */
	public static final int ON_RELEASE = 1;
	/**
	 * @invisible
	 */
	public static final int WHILE_PRESS = 2;

	/**
	 * Holds the environment of JInput
	 */
	private ControllerEnvironment _myEnvironment;

	/**
	 * List of the available _myDevices
	 */
	private final List<CCInputDevice> _myDevices = new ArrayList<CCInputDevice>();
	
	/**
	 * Gives back the Environment fitting for your OS
	 * @return
	 */
	public static ControllerEnvironment getEnvironment() {
		// setLibPath();
		switch (CCSystem.os) {
		case WINDOWS:
			return new DirectInputEnvironmentPlugin();
		case LINUX:
			return new LinuxEnvironmentPlugin();
		case MACOSX:
			CCNativeLibUtil.prepareLibraryForLoading (ControllerEnvironment.class, "jinput-osx");
			return new OSXEnvironmentPlugin();
		default:
			throw new RuntimeException("Your operating system is not supported");
		}
	}

	/**
	 * Initialize the CCInputIO instance
	 * @param theApp
	 */
	public CCInputModule(){
		super(CCInputListener.class, "gl");
	}
	
	@Override
	public void start() {
		_myEnvironment = getEnvironment();
		setupDevices();
		_myListeners.proxy().start(this);
	}
	
	@Override
	public void stop() {
		_myListeners.proxy().stop(this);
	}

	@Override
	public void addListener(Object theObject) {
		if(theObject instanceof CCInputListener){
			_myListeners.add((CCInputListener)theObject);
		}
	}

	/**
	 * Puts the available devices into the device list
	 */
	private void setupDevices(){
		final Controller[] controllers = _myEnvironment.getControllers();
		for (int i = 0; i < controllers.length; i++){
			_myDevices.add(new CCInputDevice(controllers[i]));
		}
	}
	
	/**
	 * Lists the available devices in the console window. This method
	 * is useful at start to see if all devices are properly connected
	 * and get the name of the desired device.
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #device(int)
	 */
	public void printDevices() {
		System.out.println("\n<<< available input devices: >>>\n");
		for (int i = 0; i < _myDevices.size(); i++){
			System.out.print("     "+i+": ");
			System.out.println(_myDevices.get(i).name());
		}
		System.out.println("\n<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
	
	/**
	 * Returns the number of available devices
	 * @return the number of available devices
	 * @see CCInputDevice
	 * @see #device(int)
	 */
	public int numberOfDevices() {
		return _myDevices.size();
	}
	
	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceId number of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	public CCInputDevice device(final int theDeviceId) {
		if (theDeviceId >= numberOfDevices()){
			throw new RuntimeException("There is no device with the number " + theDeviceId + ".");
		}
		CCInputDevice result = _myDevices.get(theDeviceId);
		result.open();
		return result;
	}

	/**
	 * Use this method to get a device. You can get a device by its name
	 * or id. Use printDevices to see what devices are 
	 * available on your system.
	 * @param theDeviceName String, name of the device to open
	 * @return the device corresponding to the given number or name
	 * @see CCInputDevice
	 * @see #numberOfDevices()
	 * @see #printDevices()
	 */
	public CCInputDevice device(final String theDeviceName) {

		for (int i = 0; i < numberOfDevices(); i++){
			CCInputDevice device = _myDevices.get(i);
			if (device.name().equals(theDeviceName)){
				device.open();
				return device;
			}
		}
		throw new RuntimeException("There is no device with the name " + theDeviceName + ".");
	}

	/**
	 * Updates the _myDevices, to get the actual data before a new
	 * frame is drawn
	 * @invisible
	 */
	@Override
	public void update(CCAnimator theAnimator){
		for (int i = 0; i < _myDevices.size(); i++){
			_myDevices.get(i).update(theAnimator.deltaTime());
		}
	}
	
	@Override
	public void start(CCAnimator theAnimator) {}
	
	@Override
	public void stop(CCAnimator theAnimator) {}
}
