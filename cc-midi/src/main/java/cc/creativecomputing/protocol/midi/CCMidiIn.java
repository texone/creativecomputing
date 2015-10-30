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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.protocol.midi.messages.CCMidiMessage;

public class CCMidiIn implements Receiver{
	
	private static class CCMidiTransmitter{
		private final Info _myInfo;
		/**
		 * the MidiDevice for this input
		 */
		protected final MidiDevice _myMidiDevice;
		
		private Transmitter _myInputTransmitter;
		private final CCListenerManager<Receiver> _myReceiver = CCListenerManager.create(Receiver.class);
		
		private CCMidiTransmitter(Info theInfo){
			_myInfo = theInfo;
			try {
				_myMidiDevice = MidiSystem.getMidiDevice(theInfo);
			} catch (MidiUnavailableException e) {
				throw new CCMidiException(e);
			}
			_myInputTransmitter = null;
		}
		
		private void open(){
			if(_myInputTransmitter != null)return;
			CCLog.info("OPEN MIDI");
			try {
				_myInputTransmitter = _myMidiDevice.getTransmitter();
				CCLog.info("SET RECEIVER");
				_myInputTransmitter.setReceiver(new Receiver() {
					
					@Override
					public void send(MidiMessage message, long timeStamp) {
						CCLog.info(message);
						_myReceiver.proxy().send(message, timeStamp);
					}
					
					@Override
					public void close() {}
				});
			} catch (MidiUnavailableException e) {
				throw new CCMidiException(e);
			}
		}

		public void close() {
			_myInputTransmitter.close();
			_myInputTransmitter = null;
		}
		
		public void addInput(CCMidiIn theInput){
			if(_myInputTransmitter == null){
				open();
			}
			_myReceiver.add(theInput);
		}
		
		public void removeInput(CCMidiIn theInput){
			_myReceiver.remove(theInput);
			if(_myReceiver.size() == 0 && _myInputTransmitter != null){
				close();
			}
		}
	}
	
	private static class CCTransmitterMap{
		
		private Map<String,CCMidiTransmitter > _myDeviceMap = new LinkedHashMap<>();
		
		private CCTransmitterMap(){
			_myDeviceMap.put("OFF", null);
			for(Info myInfo:MidiSystem.getMidiDeviceInfo()){
				String myName = myInfo.getVendor() + " : " + myInfo.getName();
				CCLog.info(myName);
				try {
					MidiDevice myDevice = MidiSystem.getMidiDevice(myInfo);
					if (myDevice instanceof javax.sound.midi.Sequencer)continue;
					if (myDevice.getMaxTransmitters() == 0) continue;
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
					continue;
				}
				_myDeviceMap.put(myName, new CCMidiTransmitter(myInfo));
			}
		}
		
		public Set<String> deviceNames(){
			return _myDeviceMap.keySet();
		}
		
		public void removeDevice(String theInputID, CCMidiIn theInput){
			if(!_myDeviceMap.containsKey(theInputID))return;
			CCMidiTransmitter myTransmitter = _myDeviceMap.get(theInputID);
			myTransmitter.removeInput(theInput);
		}
		
		public void addDevice(String theInputID, CCMidiIn theInput){
			if(!_myDeviceMap.containsKey(theInputID))return;
			CCMidiTransmitter myTransmitter = _myDeviceMap.get(theInputID);
			myTransmitter.addInput(theInput);
			
		}
		
		public void switchDevice(String thePreviousDevice, String theInputID, CCMidiIn theInput){
			removeDevice(thePreviousDevice, theInput);
			addDevice(theInputID, theInput);
		}
	}
	

	private static CCTransmitterMap transmitterMap = new CCTransmitterMap();

	@CCProperty(name = "device")
	private CCSelection _myPortSelection = new CCSelection();
	
	private CCListenerManager<CCMidiListener> _myListenerManager = CCListenerManager.create(CCMidiListener.class);
	private String _myDevice = null;

	/**
	 * Initializes a new MidiIn.
	 * @param theMidiIO
	 * @param theMidiDevice
	 * @throws MidiUnavailableException
	 */
	public CCMidiIn(){
		super();
		
		for(String myName:transmitterMap.deviceNames()){
			_myPortSelection.add(myName);
		}
		
		_myPortSelection.events().add(new CCSelectionListener() {
			
			@Override
			public void onChangeValues(CCSelection theSelection) {
				
			}
			
			@Override
			public void onChange(String theValue) {
				transmitterMap.switchDevice(_myDevice, theValue, CCMidiIn.this);
				_myDevice = theValue;
			}
		});
	}
	
	public CCListenerManager<CCMidiListener> events(){
		return _myListenerManager;
	}
	
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
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
