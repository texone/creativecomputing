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

import cc.creativecomputing.app.modules.CCAbstractAppModule;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

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
	
	/**
	 * Return true if this port is still active and hasn't run into any trouble.
	 */
	public boolean active() {
		return _myPort.isOpened();
	}
	
	/**
     * Get state of CTS line
     *
     * @return If line is active, method returns true, otherwise false
     */
	public boolean cts() {
		try {
			return _myPort.isCTS();
		} catch (SerialPortException e) {
			throw new RuntimeException("Error reading the CTS line: " + e.getExceptionType(), e);
		}
	}

	/**
     * Get state of DSR line
     *
     * @return If line is active, method returns true, otherwise false
     */
	public boolean dsr() {
		try {
			return _myPort.isDSR();
		} catch (SerialPortException e) {
			throw new RuntimeException("Error reading the DSR line: " + e.getExceptionType());
		}
	}
	
	/**
	 * Set the DTR line
	 * there is no way to influence the behavior of the DTR line when opening the serial port
     * this means that at least on Linux and OS X, Arduino devices are always reset
	 * @param state
	 */
	public void dtr(boolean state) {
		try {
			_myPort.setDTR(state);
		} catch (SerialPortException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Set the RTS line
	 */
	public void rts(boolean state) {
		try {
			_myPort.setRTS(state);
		} catch (SerialPortException e) {
			throw new RuntimeException("Error setting the RTS line: " + e.getExceptionType());
		}
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
			
			_myPort = new SerialPort(thePort);
			_myPort.openPort();
			_myPort.setParams(rate, dataBits.id, stopBits.id, parityBit.id);
					
			_myInput = new CCSerialInput(_myListeners, _myPort);
			_myOutput = new CCSerialOutput(_myPort);
		} catch (SerialPortException e) {
			throw new RuntimeException("Error opening serial port " + e.getPortName() + ": " + e.getExceptionType(), e);
		} 
	}
	
	@Override
	public void start() {
		try {
			String[] myPortNames = SerialPortList.getPortNames();
			for (String myPortName:myPortNames) {
				if (!myPortName.equals(_myPortSelection.value())) continue;
				
				start(myPortName);
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
		if (_myInput == null)
			return;
		
		_myListeners.proxy().stop(_myInput);
		_myInput.stop();
		_myInput = null;
		_myOutput = null;

		if (_myPort != null)
			try {
				_myPort.closePort();
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
	}

	/**
	 * Gets a list of all available serial ports.
	 * @return list of all available serial ports.
	 */
	public static String[] list() {
		// returns list sorted alphabetically, thus cu.* comes before tty.*
		// this was different with RXTX
		return SerialPortList.getPortNames();
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
