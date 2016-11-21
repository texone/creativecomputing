package cc.creativecomputing.io.netty.codec.osc;

import java.util.ArrayList;
import java.util.List;

/**
 * An OSC message consists of an OSC Address Pattern followed by an OSC Type Tag
 * String followed by zero or more OSC Arguments.
 * 
 */
public class OSCMessage implements OSCPacket {
	public static final int POOL_INITIAL_CAPACITY = 32;

	private String address;
	private List<Character> types;
	private List<Object> arguments;

	public OSCMessage(String theAddress) {
		types = new ArrayList<>();
		arguments = new ArrayList<>();
		address = theAddress;
	}

	/**
	 * Reset attributes.
	 * 
	 */
	public void reset() {
		types.clear();
		arguments.clear();
		address = "";
	}

	public String getAddress() {
		return address;
	}

	public void addArgument(Object argument) {
		arguments.add(argument);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Object> getArguments() {
		return arguments;
	}

	public List<Character> getTypes() {
		return types;
	}

	/**
	 * For debug only.
	 */
	@Override
	public String toString() {
		String message = address;

		int size = arguments.size();
		for (int i = 0; i < size; i++) {
			message += arguments.get(i) + ",";
		}

		return message;
	}

}