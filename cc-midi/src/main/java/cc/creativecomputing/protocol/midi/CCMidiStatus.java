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

/**
 * 
 * @author texone
 *
 */
public enum CCMidiStatus {
	/**
	 * Status byte for MIDI Sysex message (0xF0, or 240).
	 */
	SYSEX(0xF0),
	/**
	 * Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
	 */
	MIDI_TIME_CODE(0xF1),
	/**
	 * Status byte for Song Position Pointer message (0xF2, or 242).
	 */
	SONG_POSITION_POINTER(0xF2), // 242

	/**
	 * Status byte for MIDI Song Select message (0xF3, or 243).
	 */
	SONG_SELECT(0xF3), // 243

	/**
	 * Status byte for Tune Request message (0xF6, or 246).
	 */
	TUNE_REQUEST(0xF6), // 246

	/**
	 * Status byte for End of System Exclusive message (0xF7, or 247).
	 */
	END_OF_EXCLUSIVE(0xF7), // 247

	// System real-time messages

	/**
	 * Status byte for Timing Clock messagem (0xF8, or 248).
	 */
	TIMING_CLOCK(0xF8), // 248

	/**
	 * Status byte for Start message (0xFA, or 250).
	 */
	START(0xFA), // 250

	/**
	 * Status byte for Continue message (0xFB, or 251).
	 */
	CONTINUE(0xFB), // 251

	/**
	 * Status byte for Stop message (0xFC, or 252).
	 */
	STOP(0xFC), //252

	/**
	 * Status byte for Active Sensing message (0xFE, or 254).
	 */
	ACTIVE_SENSING(0xFE), // 254

	/**
	 * Status byte for System Reset message (0xFF, or 255).
	 */
	SYSTEM_RESET(0xFF), // 255

	// Channel voice message upper nibble defines

	/**
	 * Command value for Note Off message (0x80, or 128)
	 */
	NOTE_OFF(0x80), // 128

	/**
	 * Command value for Note On message (0x90, or 144)
	 */
	NOTE_ON(0x90), // 144

	/**
	 * Command value for Polyphonic Key Pressure (Aftertouch) message (0xA0, or 128)
	 */
	AFTERTOUCH(0xA0), // 160

	/**
	 * Command value for Control Change message (0xB0, or 176)
	 */
	CONTROL_CHANGE(0xB0), // 176

	/**
	 * Command value for Program Change message (0xC0, or 192)
	 */
	PROGRAM_CHANGE(0xC0), // 192

	/**
	 * Command value for Channel Pressure (Aftertouch) message (0xD0, or 208)
	 */
	CHANNEL_PRESSURE(0xD0), // 208

	/**
	 * Command value for Pitch Bend message (0xE0, or 224)
	 */
	PITCH_BEND(0xE0); // 224
	
	private final int _myStatusByte;
	
	CCMidiStatus(final int theByte){
		_myStatusByte = theByte;
	}
	
	public static CCMidiStatus valueOf(int theByte){
		switch(theByte){
		case 0xF0 : return SYSEX;
		case 0xF1 : return MIDI_TIME_CODE;
		case 0xF2 : return SONG_POSITION_POINTER;
		case 0xF3 : return SONG_SELECT;
		case 0xF6 : return TUNE_REQUEST;
		case 0xF7 : return END_OF_EXCLUSIVE;
		case 0xF8 : return TIMING_CLOCK;
		case 0xFA : return START;
		case 0xFB : return CONTINUE;
		case 0xFC : return STOP;
		case 0xFE : return ACTIVE_SENSING;
		case 0xFF : return SYSTEM_RESET;
		case 0x80 : return NOTE_OFF;
		case 0x90 : return NOTE_ON;
		case 0xA0 : return AFTERTOUCH;
		case 0xB0 : return CONTROL_CHANGE;
		case 0xC0 : return PROGRAM_CHANGE;
		case 0xD0 : return CHANNEL_PRESSURE;
		case 0xE0 : return PITCH_BEND;
		}
		return null;
	}
	
	public int statusByte(){
		return _myStatusByte;
	}
	
}
