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
	
	public enum CCMidiDivsion{
		/**
	     * The tempo-based timing type, for which the resolution is expressed in pulses (ticks) per quarter note.
	     */
	    PPQ(Sequence.PPQ),
	    /**
	     * The SMPTE-based timing type with 24 frames per second (resolution is expressed in ticks per frame).
	     */
	    SMPTE_24(Sequence.SMPTE_24),
	    /**
	     * The SMPTE-based timing type with 25 frames per second (resolution is expressed in ticks per frame).
	     */
	    SMPTE_25(Sequence.SMPTE_25),
	    /**
	     * The SMPTE-based timing type with 29.97 frames per second (resolution is expressed in ticks per frame).
	     */
	    SMPTE_30DROP(Sequence.SMPTE_30DROP),
	    /**
	     * The SMPTE-based timing type with 30 frames per second (resolution is expressed in ticks per frame).
	     */
	    SMPTE_30(Sequence.SMPTE_30);
	    
	    private float _myDivision;
		
		CCMidiDivsion(float theDivision){
			_myDivision = theDivision;
		}
	}
	
	Sequence _mySequence;

	public CCMidiFile(int theResolution){
		this(CCMidiDivsion.PPQ, theResolution);
	}
	
	 /**
     * Constructs a new MIDI file with the specified timing division
     * type and timing resolution.  The division type must be one of the
     * recognized MIDI timing types.  For tempo-based timing,
     * <code>divisionType</code> is PPQ (pulses per quarter note) and
     * the resolution is specified in ticks per beat.  For SMTPE timing,
     * <code>divisionType</code> specifies the number of frames per
     * second and the resolution is specified in ticks per frame.
     * The sequence will contain no initial tracks.  Tracks may be
     * added to or removed from the sequence using <code>{@link #createTrack}</code>
     * and <code>{@link #deleteTrack}</code>.
     *
     * @param divisionType the timing division type (PPQ or one of the SMPTE types)
     * @param resolution the timing resolution
     * @throws InvalidMidiDataException if <code>divisionType</code> is not valid
     *
     * @see CCMidiDivsion#PPQ
     * @see CCMidiDivsion#SMPTE_24
     * @see CCMidiDivsion#SMPTE_25
     * @see CCMidiDivsion#SMPTE_30DROP
     * @see CCMidiDivsion#SMPTE_30
     */
	public CCMidiFile(CCMidiDivsion theDivision, int theResolution){
		try {
			_mySequence = new Sequence(theDivision._myDivision, theResolution);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	public CCMidiTrack createTrack(){
		return new CCMidiTrack(_mySequence.createTrack());
	}
	
}
