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

import javax.sound.midi.ShortMessage;

/**
 * Note represents a MIDI note. It has a MIDI port, a MIDI channel, a pitch and a velocity. You can receive Notes from
 * MIDI inputs and send them to MIDI outputs.
 */
public abstract class CCNote extends CCMidiMessage {
	
	/**
	 * Initializes a new Note object. You can build a Note to send it to a MIDI output.
	 * 
	 * @param thePitch pitch of a note
	 * @param theVelocity velocity of a note
	 * @param theLength length of the note in milliseconds
	 */
	public CCNote(final int theCommand, final int thePitch, final int theVelocity) {
		super(theCommand, thePitch, theVelocity);
	}

	/**
	 * Initializes a new Note from a java ShortMessage
	 * 
	 * @param theShortMessage
	 * @invisible
	 */
	CCNote(final ShortMessage theShortMessage) {
		super(theShortMessage);
	}

	/**
	 * Use this method to get the pitch of a note.
	 * 
	 * @return the pitch of a note
	 */
	public int pitch() {
		return data1();
	}

	/**
	 * Use this method to set the pitch of a note
	 * 
	 * @param pitch new pitch for the note
	 */
	public void pitch(final int thePitch) {
		data1(thePitch);
	}

	/**
	 * Use this method to get the velocity of a note.
	 * 
	 * @return the velocity of a note
	 */
	public int velocity() {
		return data2();
	}

	/**
	 * Use this method to set the velocity of a note.
	 * 
	 * @param velocity new velocity for the note
	 */
	public void velocity(final int theVelocity) {
		data2(theVelocity);
	}
}
