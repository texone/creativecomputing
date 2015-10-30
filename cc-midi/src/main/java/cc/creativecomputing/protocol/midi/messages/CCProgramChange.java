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

import cc.creativecomputing.protocol.midi.CCMidiStatus;



/**
 * ProgramChange represents a MIDI program change. It has a MIDI port, a MIDI channel, 
 * and a number. You can receive program changes from MIDI inputs and send 
 * them to MIDI outputs.
 */
public class CCProgramChange extends CCMidiMessage{
	
	/**
	 * Initializes a new ProgramChange object.
	 * @param midiChannel MIDI channel a program change comes from or is send to
	 * @param i_number number of the program change
	 */
	public CCProgramChange(final int theNumber){
		super(CCMidiStatus.PROGRAM_CHANGE.statusByte(), theNumber,-1);
	}
	
	/**
	 * Use this method to get the program change number.
	 * @return the program change number
	 */
	public int number(){
		return data1();
	}
}
