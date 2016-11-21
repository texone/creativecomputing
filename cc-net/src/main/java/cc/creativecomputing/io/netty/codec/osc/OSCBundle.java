package cc.creativecomputing.io.netty.codec.osc;

import java.util.ArrayList;
import java.util.List;

/**
 * An OSC Bundle consists of the OSC-string "#bundle" followed by an OSC Time
 * Tag, followed by zero or more OSC Bundle Elements. The OSC-timetag is a
 * 64-bit fixed point time tag.
 * 
 */
public class OSCBundle implements OSCPacket {

	private long _myTimeTag;
	private List<OSCPacket> _myMessages;

	/**
	 * Construct bundle.
	 * 
	 */
	public OSCBundle() {
		_myTimeTag = 0;
		_myMessages = new ArrayList<>();
	}

	/**
	 * Time tag as milliseconds in normal java time.
	 * 
	 * @return milliseconds in normal java time.
	 */
	public long getTimeTag() {
		return _myTimeTag;
	}

	/**
	 * Set time tag as milliseconds in normal java time.
	 * 
	 * @param timeTag
	 */
	public void setTimeTag(long timeTag) {
		this._myTimeTag = timeTag;
	}

	/**
	 * Add message to bundle.
	 * 
	 * @param message
	 */
	public void addMessage(OSCMessage message) {
		_myMessages.add(message);
	}

	/**
	 * Return list of associated messages.
	 * 
	 * @return
	 */
	public List<OSCPacket> getMessages() {
		return _myMessages;
	}

	/**
	 * Reset attributes.
	 * 
	 */
	public void reset() {
		_myTimeTag = 0;

		for (OSCPacket message : _myMessages) {
			message.reset();
		}

		_myMessages.clear();
	}

	/**
	 * For debug only.
	 */
	@Override
	public String toString() {
		String message = _myTimeTag + ": ";

		int size = _myMessages.size();
		for (int i = 0; i < size; i++) {
			message += _myMessages.get(i) + ",";
		}

		return message;
	}
}