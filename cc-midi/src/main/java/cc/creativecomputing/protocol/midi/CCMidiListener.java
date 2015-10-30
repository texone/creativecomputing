package cc.creativecomputing.protocol.midi;

import cc.creativecomputing.protocol.midi.messages.CCMidiMessage;

public interface CCMidiListener {

	public void receive(CCMidiMessage theMessage);
}
