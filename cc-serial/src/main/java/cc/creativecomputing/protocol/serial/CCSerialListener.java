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

/**
 * Implement this interface and pass an object of the class to the CCSerial to listen serial events. This is called when
 * data is available. Use one of the read() methods inside the passed CCSerial instance to capture this data. The
 * onSerialEvent() can be set with buffer() inside CCSerial to only trigger after a certain number of data elements are
 * read and can be set with bufferUntil() inside CCSerial to only trigger after a specific character is read.
 * 
 * @author christian riekoff
 * 
 */
public interface CCSerialListener {

	/**
	 * Implement this method and pass an object of the class to the CCSerial to listen serial events. This is called
	 * when data is available. Use one of the read() methods inside the passed CCSerial instance to capture this data.
	 * The onSerialEvent() can be set with buffer() inside CCSerial to only trigger after a certain number of data
	 * elements are read and can be set with bufferUntil() inside CCSerial to only trigger after a specific character is
	 * read. 
	 * 
	 * 
	 * @param theSerial the serial port that received data
	 */
	public void onSerialEvent(final CCSerialInput theSerialPort);
	

}
