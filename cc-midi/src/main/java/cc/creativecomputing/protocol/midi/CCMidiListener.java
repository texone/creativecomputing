package cc.creativecomputing.protocol.midi;

import cc.creativecomputing.protocol.midi.messages.CCMidiMessage;

public interface CCMidiListener {

	void receive(CCMidiMessage theMessage);
}
