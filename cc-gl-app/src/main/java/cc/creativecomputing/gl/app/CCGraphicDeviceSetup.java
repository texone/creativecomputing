package cc.creativecomputing.gl.app;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.core.CCProperty;

public class CCGraphicDeviceSetup {
	private final Map<String, GraphicsDevice> _myGraphicsDeviceMap = new HashMap<String, GraphicsDevice>();
	
	@CCProperty(name = "devices")
	private final CCSelection _myGraphicDeviceNames = new CCSelection();
	private final GraphicsDevice[] _myGraphicDevices;
	
	private final Map<String, GraphicsConfiguration> _myGraphicsConfigurationMap = new HashMap<String, GraphicsConfiguration>();
	
	@CCProperty(name = "configurations")
	private CCSelection _myGraphicConfigurationNames = new CCSelection();
	private GraphicsConfiguration[] _myGraphicConfigurations;
	
	private GraphicsDevice _myGraphicsDevice;
	private GraphicsConfiguration _myGraphicsConfiguration;
	
	
	/**
	 * Update the available graphic configurations based on the current graphics device
	 */
	private void updateConfigurations() {
		_myGraphicsConfigurationMap.clear();
		if(_myGraphicsDevice == null)return;
		_myGraphicsConfiguration = _myGraphicsDevice.getDefaultConfiguration();
		_myGraphicConfigurations = _myGraphicsDevice.getConfigurations();
		
		_myGraphicConfigurationNames.values().clear();
		
		for (int i = 0; i < _myGraphicConfigurations.length;i++) {
			GraphicsConfiguration myConfig = _myGraphicConfigurations[i];
			String myID = myConfig.getBounds().width + " x " + myConfig.getBounds().height;
			_myGraphicConfigurationNames.add(myID);
			_myGraphicsConfigurationMap.put(myID, myConfig);
		}
	}
	
	public CCGraphicDeviceSetup(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		_myGraphicsDevice = ge.getDefaultScreenDevice();
		
		_myGraphicDevices = ge.getScreenDevices();
		
		for (int i = 0; i < _myGraphicDevices.length;i++) {
			GraphicsDevice myDevice = _myGraphicDevices[i];
		
			_myGraphicDeviceNames.add(myDevice.getIDstring());
			_myGraphicDeviceNames.events().add(new CCSelectionListener() {
				
				@Override
				public void onChangeValues(CCSelection theSelection) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onChange(String theValue) {
					display(theValue);
				}
			});
			_myGraphicsDeviceMap.put(myDevice.getIDstring(), myDevice);
		}
		updateConfigurations();
	}
	
	/**
	 * Returns an array with the available Graphic devices.
	 * @return array with the available Graphic devices
	 */
	public List<String> deviceNames() {
		return _myGraphicDeviceNames.values();
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
	public List<String> configurationNames() {
		return _myGraphicConfigurationNames.values();
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
