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
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.protocol.midi.messages.CCMidiMessage;

/**
 * This class has no accessible constructor use MidiIO.openOutput() to get a MidiOut. 
 * MidiOut is the direct connection to one of your MIDI out ports. You can use different  
 * methods to send notes, control and program changes through one MIDI out port.
 */
public class CCMidiOutDevice extends CCMidiDevice{
	
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
				if (myDevice.getMaxReceivers () == 0) continue;
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

	private Receiver _myOutputReceiver;
	
	@CCProperty(name = "device")
	private CCSelection _myPortSelection = new CCSelection();

	public CCMidiOutDevice() {
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
			_myOutputReceiver = null;
			return;
		}
		try {
			_myMidiDevice = MidiSystem.getMidiDevice(infoMap.get(theDevice));
			if(!_myMidiDevice.isOpen())_myMidiDevice.open();
			_myOutputReceiver = _myMidiDevice.getReceiver();
		} catch (MidiUnavailableException e) {
//			continue;
		}
	}
	
	
	@CCProperty(name = "refresh port list")
	public void refreshPortList(){
		buildInfoMap();
		_myPortSelection.values().clear();
		for(String myValue:infoMap.keySet()){
			_myPortSelection.add(myValue);
		}
	}

	/**
	 * @param theEvent
	 * @throws CCMidiException
	 */
	public void send(final CCMidiMessage theEvent){
		if(_myOutputReceiver == null)return;
		if (theEvent.getChannel() > 15 || theEvent.getChannel() < 0){
			throw new CCMidiException("You tried to send to MIDI channel" + theEvent.getChannel() + ". With MIDI you only have the channels 0 - 15 available.");
		}
		_myOutputReceiver.send(theEvent, -1);
	}
	
	public void send(final MidiMessage theEvent){
		if(_myOutputReceiver == null)return;
		_myOutputReceiver.send(theEvent, -1);
	}

}
