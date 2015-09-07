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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;

public class CCMidiFile {
	
	Sequence _mySequence;

	public CCMidiFile(int theResolution){
		this(Sequence.PPQ, theResolution);
	}
	
	public CCMidiFile(float theDivision, int theResolution){
		try {
			_mySequence = new Sequence(theDivision, theResolution);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	public CCMidiTrack createTrack(){
		return new CCMidiTrack(_mySequence.createTrack());
	}
	
}
