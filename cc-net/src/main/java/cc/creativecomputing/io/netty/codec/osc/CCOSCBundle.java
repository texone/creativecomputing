package cc.creativecomputing.io.netty.codec.osc;

import java.util.ArrayList;
import java.util.List;

/**
 * An OSC Bundle consists of the OSC-string "#bundle" followed by an OSC Time
 * Tag, followed by zero or more OSC Bundle Elements. The OSC-timetag is a
 * 64-bit fixed point time tag.
 * 
 */
public class CCOSCBundle implements CCOSCPacket {
	/**
	 * This is the initial string of an OSC bundle datagram
	 */
	public static final String TAG = "#bundle";
	
	private long _myTimeTag;
	private List<CCOSCPacket> _myMessages;

	/**
	 * Construct bundle.
	 * 
	 */
	public CCOSCBundle() {
		_myTimeTag = 0;
		_myMessages = new ArrayList<>();
	}

	/**
	 * Time tag as milliseconds in normal java time.
	 * 
	 * @return milliseconds in normal java time.
	 */
	public long timeTag() {
		return _myTimeTag;
	}

	/**
	 * Set time tag as milliseconds in normal java time.
	 * 
	 * @param theTimeTag
	 */
	public void timeTag(long theTimeTag) {
		_myTimeTag = theTimeTag;
	}

	/**
	 * Add message to bundle.
	 * 
	 * @param theMessage
	 */
	public void addMessage(CCOSCPacket theMessage) {
		_myMessages.add(theMessage);
	}

	/**
	 * Return list of associated messages.
	 * 
	 * @return
	 */
	public List<CCOSCPacket> messages() {
		return _myMessages;
	}

	/**
	 * Reset attributes.
	 * 
	 */
	public void reset() {
		_myTimeTag = 0;

		for (CCOSCPacket message : _myMessages) {
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