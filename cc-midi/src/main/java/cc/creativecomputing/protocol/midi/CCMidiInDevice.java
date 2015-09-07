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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;

public class CCMidiInDevice extends CCMidiDevice implements Receiver{
	
private static Map<String,Info > infoMap = new LinkedHashMap<>();
	
	private static void buildInfoMap(){
		infoMap.clear();
		infoMap.put("OFF", null);
		for(Info myInfo:MidiSystem.getMidiDeviceInfo()){
			String myName = myInfo.getVendor() + " : " + myInfo.getName();
			CCLog.info(myName);
			try {
				MidiDevice myDevice = MidiSystem.getMidiDevice(myInfo);
				CCLog.info(myName + ":" + myDevice.getMaxReceivers());
				if (myDevice instanceof javax.sound.midi.Sequencer)continue;
				if (myDevice.getMaxTransmitters() == 0) continue;
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				continue;
			}
			infoMap.put(myName, myInfo);
		}
	}
	
	static{
		buildInfoMap();
	}

	private Transmitter _myInputTransmitter;
	

	@CCProperty(name = "device")
	private CCSelection _myPortSelection = new CCSelection();
	
	private CCListenerManager<CCMidiListener> _myListenerManager = CCListenerManager.create(CCMidiListener.class);

	/**
	 * Initializes a new MidiIn.
	 * @param theMidiIO
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	public CCMidiInDevice(){
		super();
		
		for(String myName:infoMap.keySet()){
			_myPortSelection.add(myName);
		}
		
		_myPortSelection.events().add(new CCSelectionListener() {
			
			@Override
			public void onChangeValues(CCSelection theSelection) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChange(String theValue) {
				start(theValue);
			}
		});
	}
	
	private void start(String theDevice){
		if(infoMap.get(theDevice) == null){
			if(_myInputTransmitter != null)_myInputTransmitter.setReceiver(null);
			_myInputTransmitter = null;
			return;
		}
		try {
			
			_myMidiDevice = MidiSystem.getMidiDevice(infoMap.get(theDevice));
			if(!_myMidiDevice.isOpen())_myMidiDevice.open();
			_myInputTransmitter = _myMidiDevice.getTransmitter();
			_myInputTransmitter.setReceiver(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public CCListenerManager<CCMidiListener> events(){
		return _myListenerManager;
	}
	
	String name(){
		return _myMidiDevice.getDeviceInfo().getName();
	}

	private CCMidiTimeCode myTimeCode = new CCMidiTimeCode();
	
	/**
	 * Sorts the incoming MidiIO data in the different Arrays.
	 * @param theMessage MidiMessage
	 * @param theDeltaTime long
	 */
	@Override
	public void send(final MidiMessage theMessage, final long theDeltaTime){
//		final ShortMessage shortMessage = (ShortMessage) theMessage;
//
//		// get messageInfos
//		final int midiChannel = shortMessage.getChannel();
//
////		if (_myMidiIns[midiChannel] == null)
////			return;
//
//		final int midiCommand = shortMessage.getCommand();
//		final int midiData1 = shortMessage.getData1();
//		final int midiData2 = shortMessage.getData2();
//		CCLog.info(midiCommand + ":" + Integer.toHexString(shortMessage.getData1()) + ":" + Integer.toHexString(shortMessage.getData2()));
//		myTimeCode.receive();
		_myListenerManager.proxy().receive(new CCMidiMessage(theMessage));
		
		

//		if (midiCommand == CCMidiMessage.NOTE_ON && midiData2 > 0){
//			final CCNoteOn note = new CCNoteOn(midiData1, midiData2);
//			_myMidiIns[midiChannel].sendNoteOn(note,midiChannel);
//		}else if (midiCommand == CCMidiMessage.NOTE_OFF || midiData2 == 0){
//			final CCNoteOff note = new CCNoteOff(midiData1);
//			_myMidiIns[midiChannel].sendNoteOff(note,midiChannel);
//		}else if (midiCommand == CCMidiMessage.CONTROL_CHANGE){
//			final CCController controller = new CCController(midiData1, midiData2);
//			_myMidiIns[midiChannel].sendController(controller,midiChannel);
//		}else if (midiCommand == CCMidiMessage.PROGRAM_CHANGE){
//			final CCProgramChange programChange = new CCProgramChange(midiData1);
//			_myMidiIns[midiChannel].sendProgramChange(programChange,midiChannel);
//		}
	}
}
