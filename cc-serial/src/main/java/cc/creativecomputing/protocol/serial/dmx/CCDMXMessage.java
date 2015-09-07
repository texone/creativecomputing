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
package cc.creativecomputing.protocol.serial.dmx;


public class CCDMXMessage {
	private int[] _myData;
	public CCDMXMessage(final int[] theData){
		_myData = theData;
	}
	
	public int[] data(){
		return _myData;
	}
}
