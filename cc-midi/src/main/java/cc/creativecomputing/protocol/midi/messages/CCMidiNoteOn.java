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

public class CCMidiNoteOn extends CCNote{
	
	/**
	 * the length of the note in milliSeconds
	 */
	private int _myLength;

	public CCMidiNoteOn(int thePitch, int theVelocity, int theLength) {
		super(ShortMessage.NOTE_ON, thePitch, theVelocity);
		_myLength = theLength;
	}

	public CCMidiNoteOn(int thePitch, int theVelocity) {
		this( thePitch, theVelocity,0);
	}

	public CCMidiNoteOn(ShortMessage theShortMessage) {
		super(theShortMessage);
		_myLength = 0;
	}
	
	/**
	 * Returns the length of the note in milliseconds
	 * 
	 * @return the length of the note
	 * @related Note
	 */
	public int length() {
		return _myLength;
	}

	/**
	 * Sets the length of the note
	 * 
	 * @param theLength new length of the note
	 */
	public void length(final int theLength) {
		_myLength = theLength;
	}
}
