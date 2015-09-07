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
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Event is the base class for all MidiEvents, like 
 * NoteOn, Controller or SysEx.
 */
public class CCMidiMessage extends ShortMessage{

	/**
	 * field to keep the events midiPort
	 */
	private int midiChannel = 0;

	/**
	 * Constructs a new <code>CCMidiEvent</code>.
	 * @param theData an array of bytes containing the complete message.
	 * The message data may be changed using the <code>message</code>
	 * method.
	 * @see #message
	 */
	private CCMidiMessage(byte[] theData){
		super(theData);
	}

	/**
	 * Constructs a new <code>ShortMessage</code>.  The
	 * contents of the new message are guaranteed to specify
	 * a valid MIDI message.  Subsequently, you may set the
	 * contents of the message using one of the <code>message</code>
	 * methods.
	 * @see #message
	 */
	private CCMidiMessage(){
		this(new byte[3]);
		// Default message data: NOTE_ON on Channel 0 with max volume
		data[0] = (byte) (CCMidiStatus.NOTE_ON.statusByte() & 0xFF);
		data[1] = (byte) 64;
		data[2] = (byte) 127;
		length = 3;
	}
	
	public CCMidiMessage (byte theByte) {
		length = 1;
		data[0] = theByte;
	}

	CCMidiMessage(final MidiMessage theMidiMessage){
		this(theMidiMessage.getMessage());
	}

	/**
	 * Initializes a new Event.
	 * @param _myMidiChannel MIDI channel of the event
	 * @param midiPort MIDI port of the  event
	 * @throws InvalidMidiDataException 
	 */
	CCMidiMessage(int theCommand, int theNumber, int theValue){
		this();
		try{
			message(theCommand, midiChannel, theNumber, theValue);
		}catch (InvalidMidiDataException e){
			e.printStackTrace();
		}
	}
	
	CCMidiMessage(final CCMidiStatus theStatus){
		super(null);
		status(theStatus);
	}

	/**
	 * Sets the parameters for a MIDI message that takes no data bytes.
	 * @param theStatus the MIDI status byte
	 * @throws  <code>InvalidMidiDataException</code> if <code>status</code> does not
	 * specify a valid MIDI status byte for a message that requires no data bytes.
	 * @see #message(int, int, int)
	 * @see #message(int, int, int, int)
	 */
	public void message(int theStatus) throws InvalidMidiDataException{
		// check for valid values
		int dataLength = getDataLength(theStatus); // can throw InvalidMidiDataException
		if (dataLength != 0){
			throw new InvalidMidiDataException("Status byte; " + theStatus + " requires " + dataLength + " data bytes");
		}
		message(theStatus, 0, 0);
	}

	/**
	 * Sets the  parameters for a MIDI message that takes one or two data
	 * bytes.  If the message takes only one data byte, the second data
	 * byte is ignored; if the message does not take any data bytes, both
	 * data bytes are ignored.
	 *
	 * @param theStatus	the MIDI status byte
	 * @param theData1		the first data byte
	 * @param theData2		the second data byte
	 * @throws	<code>InvalidMidiDataException</code> if the
	 * the status byte, or all data bytes belonging to the message, do
	 * not specify a valid MIDI message.
	 * @see #message(int, int, int, int)
	 * @see #message(int)
	 */
	public void message(int theStatus, int theData1, int theData2) throws InvalidMidiDataException{
		// check for valid values
		int dataLength = getDataLength(theStatus); // can throw InvalidMidiDataException
		if (dataLength > 0){
			if (theData1 < 0 || theData1 > 127){
				throw new InvalidMidiDataException("data1 out of range: " + theData1);
			}
			if (dataLength > 1){
				if (theData2 < 0 || theData2 > 127){
					throw new InvalidMidiDataException("data2 out of range: " + theData2);
				}
			}
		}

		// set the length
		length = dataLength + 1;
		// re-allocate array if ShortMessage(byte[]) constructor gave array with fewer elements
		if (data == null || data.length < length){
			data = new byte[3];
		}

		// set the data
		data[0] = (byte) (theStatus & 0xFF);
		if (length > 1){
			data[1] = (byte) (theData1 & 0xFF);
			if (length > 2){
				data[2] = (byte) (theData2 & 0xFF);
			}
		}
	}

	/**
	 * Sets the short message parameters for a  channel message
	 * which takes up to two data bytes.  If the message only
	 * takes one data byte, the second data byte is ignored; if
	 * the message does not take any data bytes, both data bytes
	 * are ignored.
	 *
	 * @param theCommand	the MIDI command represented by this message
	 * @param theChannel	the channel associated with the message
	 * @param theData1		the first data byte
	 * @param theData2		the second data byte
	 * @throws		<code>InvalidMidiDataException</code> if the
	 * status byte or all data bytes belonging to the message, do
	 * not specify a valid MIDI message
	 *
	 * @see #message(int, int, int)
	 * @see #message(int)
	 * @see #command
	 * @see #channel
	 * @see #data1
	 * @see #data2
	 */
	public void message(int theCommand, int theChannel, int theData1, int theData2) throws InvalidMidiDataException{
		// check for valid values
		if (theCommand >= 0xF0 || theCommand < 0x80){
			throw new InvalidMidiDataException("command out of range: 0x" + Integer.toHexString(theCommand));
		}
		if ((theChannel & 0xFFFFFFF0) != 0){ // <=> (channel<0 || channel>15)
			throw new InvalidMidiDataException("channel out of range: " + theChannel);
		}
		message((theCommand & 0xF0) | (theChannel & 0x0F), theData1, theData2);
	}
	
	public byte[] data(){
		return data;
	}

	/**
	 * Obtains the MIDI channel associated with this event.  This method
	 * assumes that the event is a MIDI channel message; if not, the return
	 * value will not be meaningful.
	 * @return MIDI channel associated with the message.
	 */
	public int channel(){
		// this returns 0 if an invalid message is set
		return (getStatus() & 0x0F);
	}

	void channel(final int theMidiChannel){
		data[0] = (byte) (data[0] | (theMidiChannel & 0x0F));
	}

	/**
	 * Obtains the MIDI command associated with this event.  This method
	 * assumes that the event is a MIDI channel message; if not, the return
	 * value will not be meaningful.
	 */
	public CCMidiStatus status(){
		
		// this returns 0 if an invalid message is set
		return CCMidiStatus.valueOf(getStatus() & 0xF0);
	}

	public void status(final CCMidiStatus theStatus){
		data[0] = (byte) (data[0] | (theStatus.statusByte() & 0xF0));
	}

	/**
	 * Obtains the first data byte in the message.
	 * @return the value of the <code>data1</code> field
	 * @see #message(int, int, int)
	 */
	public int data1(){
		if (length > 1){
			return (data[1] & 0xFF);
		}
		return 0;
	}

	public void data1(final int theData1){
		data[1] = (byte) (theData1 & 0xFF);
	}

	/**
	 * Obtains the second data byte in the message.
	 * @return the value of the <code>data2</code> field
	 * @see #message(int, int, int)
	 */
	public int data2(){
		if (length > 2){
			return (data[2] & 0xFF);
		}
		return 0;
	}

	public void data2(final int theData2){
		data[1] = (byte) (theData2 & 0xFF);
	}

	/**
	 * Creates a new object of the same class and with the same contents
	 * as this object.
	 * @return a clone of this instance.
	 */
	public Object clone(){
		byte[] newData = new byte[length];
		System.arraycopy(data, 0, newData, 0, newData.length);

		CCMidiMessage msg = new CCMidiMessage(newData);
		return msg;
	}
}
