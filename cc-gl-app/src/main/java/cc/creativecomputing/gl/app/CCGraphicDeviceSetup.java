package cc.creativecomputing.gl.app;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

public class CCGraphicDeviceSetup {
	private final Map<String, GraphicsDevice> _myGraphicsDeviceMap = new HashMap<String, GraphicsDevice>();
	private final String[] _myGraphicDeviceNames;
	private final GraphicsDevice[] _myGraphicDevices;
	
	private final Map<String, GraphicsConfiguration> _myGraphicsConfigurationMap = new HashMap<String, GraphicsConfiguration>();
	private String[] _myGraphicConfigurationNames;
	private GraphicsConfiguration[] _myGraphicConfigurations;
	
	private GraphicsDevice _myGraphicsDevice;
	private GraphicsConfiguration _myGraphicsConfiguration;
	
	/**
	 * Update the available graphic configurations based on the current graphics device
	 */
	private void updateConfigurations() {
		_myGraphicsConfigurationMap.clear();
		_myGraphicsConfiguration = _myGraphicsDevice.getDefaultConfiguration();
		_myGraphicConfigurations = _myGraphicsDevice.getConfigurations();
		_myGraphicConfigurationNames = new String[_myGraphicConfigurations.length];
		
		for (int i = 0; i < _myGraphicConfigurations.length;i++) {
			GraphicsConfiguration myConfig = _myGraphicConfigurations[i];
			String myID = myConfig.getBounds().width + " x " + myConfig.getBounds().height;
			_myGraphicConfigurationNames[i] = myID;
			_myGraphicsConfigurationMap.put(myID, myConfig);
		}
	}
	
	public CCGraphicDeviceSetup(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_myGraphicsDevice = ge.getDefaultScreenDevice();
		
		_myGraphicDevices = ge.getScreenDevices();
		_myGraphicDeviceNames = new String[_myGraphicDevices.length];
		
		for (int i = 0; i < _myGraphicDevices.length;i++) {
			GraphicsDevice myDevice = _myGraphicDevices[i];
			_myGraphicDeviceNames[i] = myDevice.getIDstring();
			_myGraphicsDeviceMap.put(myDevice.getIDstring(), myDevice);
		}
		updateConfigurations();
	}
	
	/**
	 * Returns an array with the available Graphic devices.
	 * @return array with the available Graphic devices
	 */
	public String[] deviceNames() {
		return _myGraphicDeviceNames;
	}

	/**
	 * Sets the display for the application.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(int theDisplay) {
		if(theDisplay >= _myGraphicDevices.length)return;
		_myGraphicsDevice = _myGraphicDevices[theDisplay];
		updateConfigurations();
	}
	
	/**
	 * Sets the display for the application. This is useful for multi
	 * screen setups. If the display does not exist, the application will start
	 * at the first available one.
	 * @param theDisplay display for the application
	 * @see CCApplicationSettings#deviceNames()
	 */
	public void display(String theDisplayName) {
		_myGraphicsDevice = _myGraphicsDeviceMap.get(theDisplayName);
		updateConfigurations();
	}

	/**
	 * Returns the graphics device that should be used for Graphic output.
	 * @return the display of the application
	 */
	public GraphicsDevice display() {
		return _myGraphicsDevice;
	}
	
	/**
	 * Returns an array with the available configurations.
	 * Be aware that this will return the configurations for the current device.
	 * So to get the right configurations set the display first.
	 * @return array with the available configurations
	 */
	public String[] configurationNames() {
		return _myGraphicConfigurationNames;
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theConfiguration display for the application
	 */
	public void displayConfiguration(int theConfiguration) {
		_myGraphicsConfiguration = _myGraphicConfigurations[theConfiguration];
	}
	
	/**
	 * Sets the display configuration for the application. 
	 * Be aware that this will return the configurations for the current device.
	 * So to set the right configurations set the display first.
	 * @param theDisplay display for the application
	 */
	public void displayConfiguration(String theConfigurationName) {
		_myGraphicsConfiguration = _myGraphicsConfigurationMap.get(theConfigurationName);
	}

	/**
	 * Returns the display configuration that should be used for graphic output.
	 * @return the display configuration of the application
	 */
	public GraphicsConfiguration displayConfiguration() {
		return _myGraphicsConfiguration;
	}
}
