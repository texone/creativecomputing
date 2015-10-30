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
package cc.creativecomputing.protocol.midi;

import javax.sound.midi.MidiDevice;

abstract class CCMidiDevice{

	/**
	 * the MidiDevice for this input
	 */
	protected MidiDevice _myMidiDevice;
	

	/**
	 * Initializes a new MidiIn.
	 * @param libContext
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	CCMidiDevice(){
	}
	
	String name(){
		return _myMidiDevice.getDeviceInfo().getName();
	}
	
	void open(){
		try{
			if(!_myMidiDevice.isOpen()){
				_myMidiDevice.open();
			}
		}catch (Exception e){
			throw new RuntimeException("You wanted to open an unavailable output device: " + name(), e);
		}
	}

	/**
	 * Closes this device
	 */
	public void close(){
		if(_myMidiDevice.isOpen())_myMidiDevice.close();
	}

}
