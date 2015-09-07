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
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

public class CCMidiTrack {

	private Track _myTrack;
	
	CCMidiTrack(Track theTrack){
		_myTrack = theTrack;
	}
	
	public void addEvent(CCMidiMessage theMessage, long theTick){
		_myTrack.add(new MidiEvent(theMessage, theTick));
	}
	
	/**
	 * General MIDI sysex -- turn on General MIDI sound set
	 */
	public void turnOnSoundSet(){
		try {
			byte[] b = { (byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7 };
			SysexMessage mySysexMessage = new SysexMessage();
			mySysexMessage.setMessage(b, 6);
			MidiEvent me = new MidiEvent(mySysexMessage, (long) 0);
			_myTrack.add(me);
		} catch (InvalidMidiDataException e) {
			throw new CCMidiException(e);
		}
	}
	
	/**
	 * set track name
	 * @param theName track name
	 */
	public void trackName(String theName){
		try {
			MetaMessage myMetaMessage = new MetaMessage();
			String TrackName = new String(theName);
			myMetaMessage.setMessage(0x03, TrackName.getBytes(), TrackName.length());
			_myTrack.add(new MidiEvent(myMetaMessage, (long) 0));
		} catch (InvalidMidiDataException e) {
			throw new CCMidiException(e);
		}
	}
	
	/**
	 * Add end of track
	 * @param theTick
	 */
	public void addEndOfTrack(long theTick){
		try {
			// **** set end of track (meta event) 19 ticks later ****
			MetaMessage	myMetaMessage = new MetaMessage();
			byte[] bet = {}; // empty array
			myMetaMessage.setMessage(0x2F, bet, 0);
			_myTrack.add(new MidiEvent(myMetaMessage, theTick));
		} catch (InvalidMidiDataException e) {
			throw new CCMidiException(e);
		}
	}
	
	/*
	 // **** set tempo (meta event) ****
			MetaMessage mt = new MetaMessage();
			byte[] bt = { 0x02, (byte) 0x00, 0x00 };
			mt.setMessage(0x51, bt, 3);
			me = new MidiEvent(mt, (long) 0);
			t.add(me);

			

			// **** set omni on ****
			ShortMessage mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7D, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

			// **** set poly on ****
			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7F, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

			// **** set instrument to Piano ****
			mm = new ShortMessage();
			mm.setMessage(0xC0, 0x00, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

			// **** note on - middle C ****
			mm = new ShortMessage();
			mm.setMessage(0x90, 0x3C, 0x60);
			me = new MidiEvent(mm, (long) 1);
			t.add(me);

			// **** note off - middle C - 120 ticks later ****
			mm = new ShortMessage();
			mm.setMessage(0x80, 0x3C, 0x40);
			me = new MidiEvent(mm, (long) 121);
			t.add(me);
	 */
	
}
