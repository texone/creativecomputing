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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.midi.MidiSystem;

/**
 * MidiIO is the base class for managing the available MIDI ports. 
 * It provides you with methods to get information on your ports and 
 * to open them. There are various changes on the new proMIDI version
 * in handling inputs and outputs. Instead of opening a complete port
 * you can now open inputs and outputs with a channel number and a 
 * port name or number. To start use the printDevices method to get
 * all devices available on your system.
 */
public class CCMidiIO{


	/**
	 * Stores the MidiIO instance;
	 */
	private static CCMidiIO instance = new CCMidiIO();

	

	/**
	 * Use this method to get instance of MidiIO. It makes sure that only one 
	 * instance of MidiIO is initialized. 
	 * @return MidiIO, an instance of MidiIO for MIDI communication
	 */
	public static CCMidiIO getInstance(){
		if (instance == null){
			instance = new CCMidiIO();
		}
		return instance;
	}

	
	
	public static void addNoteListener(final CCINoteListener theListener, final int theDevice){
		
	}
	
	public static void writeFile(CCMidiFile theFile, Path thePath){
		try {
			MidiSystem.write(theFile._mySequence, 1, thePath.toFile());
		} catch (IOException e) {
			throw new CCMidiException("Problem writing midi file! ", e);
		}
	}
}
