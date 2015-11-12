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
package cc.creativecomputing.protocol.serial;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import cc.creativecomputing.app.modules.CCAbstractAppModule;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;

/**
 * <p>
 * The cc serial library allows for easily reading and writing data to and from external machines. 
 * It allows two computers to send and receive data and gives you the flexibility to communicate 
 * with custom micro controller devices, using them as the input or output to Processing programs.
 * </p>
 * <p>
 * This class is based on the <a href="http://processing.org/reference/libraries/serial/index.html">serial</a> 
 * library of processing written by ben fry and tom igoe.
 * </p>
 * @author Christian Riekoff
 *
 */
public class CCSerialModule extends CCAbstractAppModule<CCSerialListener>{

	private CCSerialInput _myInput;
	private CCSerialOutput _myOutput;
	private SerialPort _myPort;
	
//	@CCProperty(name = "port", desc = "name of the serial port to open default is COM1")
//	public String port = "COM1";
	
	@CCProperty(name = "rate", desc = "rate of the serial communication default is 9600")
	public int rate = 9600;
	
	@CCProperty(name = "paritybit", desc = "parity bit for communication default is NONE")
	public CCSerialParityBit parityBit = CCSerialParityBit.NONE;
	
	@CCProperty(name = "databits", desc = "databits for communication default is DATABITS_8")
	public CCSerialDataBit dataBits = CCSerialDataBit.DATABITS_8;
	
	@CCProperty(name = "stopbits", desc = "stopbits for communication default is STOPBITS_1")
	public CCSerialStopBit stopBits = CCSerialStopBit.STOPBITS_1;
	
	@CCProperty(name = "port")
	private CCSelection _myPortSelection = new CCSelection();
	
	public CCSerialModule(String theName) {
		super(CCSerialListener.class, theName);
		for(String myValue:list()){
			_myPortSelection.add(myValue);
		}	
	}
	
	public CCSerialModule(String theName, int theRate){
		this(theName);
		rate = theRate;
	}
	
	public CCSerialInput input(){
		return _myInput;
	}
	
	public CCSerialOutput output(){
		return _myOutput;
	}

	public CCSerialModule() {
		this("serial");
	}
	
	@CCProperty(name = "connect")
	public void connect(boolean theConnect){
		if(theConnect)start(_myPortSelection.value());
		else stop();
	}
	
	@CCProperty(name = "refresh port list")
	public void refreshPortList(){
		_myPortSelection.values().clear();
		for(String myValue:list()){
			_myPortSelection.add(myValue);
		}
	}
	
	private void start(String thePort){
		try{
			CCLog.info(thePort);
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(thePort);
			if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) return;
			
			_myPort = (SerialPort) portId.open("serial madness", 2000);
			_myPort.setSerialPortParams(rate, dataBits.id, stopBits.id, parityBit.id);
			_myPort.notifyOnDataAvailable(true);
					
			_myInput = new CCSerialInput(_myListeners, _myPort);
			_myOutput = new CCSerialOutput(_myPort);
		} catch (Throwable e) {
//			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void start() {
		try {
			Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) continue;
				if (!portId.getName().equals(_myPortSelection.value())) continue;
				
				_myPort = (SerialPort) portId.open("serial madness", 2000);
				_myPort.setSerialPortParams(rate, dataBits.id, stopBits.id, parityBit.id);
				_myPort.notifyOnDataAvailable(true);
						
				_myInput = new CCSerialInput(_myListeners, _myPort);
				_myOutput = new CCSerialOutput(_myPort);
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stops data communication on this port. Use to shut the connection when you're finished with the Serial.
	 */
	@Override
	public void stop() {
		if(_myInput == null)return;
		_myListeners.proxy().stop(_myInput);
		_myInput.stop();
		_myOutput.stop();
		_myInput = null;
		_myOutput = null;
		
		if (_myPort != null)
			_myPort.close();
	}

	/**
	 * Gets a list of all available serial ports.
	 * @return list of all available serial ports.
	 */
	static public List<String> list() {
		List<String> myResult = new ArrayList<String>();
		try {
			Enumeration<?> myPorts = CommPortIdentifier.getPortIdentifiers();
			while (myPorts.hasMoreElements()) {
				CommPortIdentifier myportId = (CommPortIdentifier) myPorts.nextElement();
				if (myportId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					String name = myportId.getName();
					myResult.add(name);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);

		} 
		return myResult;
	}
	
	public static void printPorts() {
		for(String myPort:list()) {
			System.out.println(myPort);
		}
	}
	
	public static void main(String[] args) {
		printPorts();
	}
}
