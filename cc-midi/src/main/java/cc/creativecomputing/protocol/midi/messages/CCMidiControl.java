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
package cc.creativecomputing.protocol.midi.messages;

import javax.sound.midi.ShortMessage;

import cc.creativecomputing.protocol.midi.CCMidiControllerNumber;

/**
 * Controller represents a MIDI controller. It has a number and a value. You can
 * receive Controller values from MIDI ins and send them to MIDI outs.
 */
public class CCMidiControl extends CCMidiMessage{
	
	

	/**
	 * Initializes a new Controller object.
	 * @param theNumber number of a controller
	 * @param theValue value of a controller
	 */
	public CCMidiControl(final CCMidiControllerNumber theNumber, final int theValue){
		super(CONTROL_CHANGE, theNumber.number, theValue);
	}
	
	/**
	 * Initializes a new Note from a java ShortMessage
	 * @param theShortMessage
	 * @invisible
	 */
	CCMidiControl(ShortMessage theShortMessage){
		super(theShortMessage);
	}

	/**
	 * Use this method to get the number of a controller.
	 * @return the number of a controller
	 */
	public int number(){
		return data1();
	}

	/**
	 * Use this method to set the number of a controller.
	 * @return the number of a note
	 */
	public void number(final int theNumber){
		data1(theNumber);
	}

	/**
	 * Use this method to get the value of a controller.
	 * @return the value of a note
	 */
	public int value(){
		return data2();
	}

	/**
	 * Use this method to set the value of a controller.
	 * @return the value of a note
	 */
	public void value(final int theValue){
		data2(theValue);
	}
}
