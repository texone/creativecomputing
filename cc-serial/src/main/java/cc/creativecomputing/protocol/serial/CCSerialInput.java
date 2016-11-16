package cc.creativecomputing.protocol.serial;

import cc.creativecomputing.core.events.CCListenerManager;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class CCSerialInput implements SerialPortEventListener{
	private SerialPort _myPort;


	 private byte[] buffer = new byte[32768];
	 private int inBuffer = 0;
	 private  int readOffset = 0;

	 private int bufferUntilSize = 1;
	 private byte bufferUntilByte = 0;
	
	private CCListenerManager<CCSerialListener> _myListeners;
	
	CCSerialInput(CCListenerManager<CCSerialListener> theListeners, SerialPort thePort) {
		_myListeners = theListeners;
		_myPort = thePort;
		try {
			_myPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		} catch (SerialPortException e) {
			throw new RuntimeException(e);
		}
	}
	
	void stop(){
		inBuffer = 0;
	    readOffset = 0;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() != SerialPortEvent.RXCHAR) return;
		
		int toRead;
		try {
			while (0 < (toRead = _myPort.getInputBufferBytesCount())) {
				// this method can be called from the context of another
				// thread
				synchronized (buffer) {
					// read one byte at a time if the sketch is using
					// serialEvent
					toRead = 1;
					// enlarge buffer if necessary
					if (buffer.length < inBuffer + toRead) {
						byte temp[] = new byte[buffer.length << 1];
						System.arraycopy(buffer, 0, temp, 0, inBuffer);
						buffer = temp;
					}
					// read an array of bytes and copy it into our buffer
					byte[] read = _myPort.readBytes(toRead);
					System.arraycopy(read, 0, buffer, inBuffer, read.length);
					inBuffer += read.length;
				}
				if ((0 < bufferUntilSize && bufferUntilSize <= inBuffer - readOffset) || (0 == bufferUntilSize && bufferUntilByte == buffer[inBuffer - 1])) {
					try {
						// serialEvent() is invoked in the context of the
						// current (serial) thread
						// which means that serialization and atomic
						// variables need to be used to
						// guarantee reliable operation (and better not
						// draw() etc..)
						// serialAvailable() does not provide any real
						// benefits over using
						// available() and read() inside draw - but this
						// function has no
						// thread-safety issues since it's being invoked
						// during pre in the context
						// of the Processing applet
						_myListeners.proxy().onSerialEvent(this);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

			}
		} catch (SerialPortException e) {
			throw new RuntimeException("Error reading from serial port " + e.getPortName() + ": " + e.getExceptionType());
		}
	}

	/**
	 * Sets the number of bytes to buffer before calling onSerialEvent()
	 * @param theBufferSize number of bytes to buffer
	 */
	public void buffer(final int theBufferSize) {
		bufferUntilSize = theBufferSize;
	}

	/**
	 * Sets a specific byte to buffer until before calling onSerialEvent().
	 * @param theBufferUntilByte the value to buffer until
	 */
	public void bufferUntil(final int theBufferUntilByte) {
		bufferUntilSize = 0;
	    bufferUntilByte = (byte)theBufferUntilByte;
	}

	/**
	 * Returns the number of bytes that have been read from serial and are waiting to be dealt with by the user.
	 * @return the number of bytes available
	 */
	public int available() {
		return (inBuffer-readOffset);
	}

	/**
	 * Ignore all the bytes read so far and empty the buffer.
	 */
	public void clear() {
		synchronized (buffer) {
			inBuffer = 0;
			readOffset = 0;
		}
	}

	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in the buffer. 
	 * Returns -1 if there is no byte, although this should be avoided by first checking 
	 * available() to see if data is available.
	 * @return the next byte waiting in the buffer
	 */
	public int read() {
		if (inBuffer == readOffset) {
			return -1;
		}

		synchronized (buffer) {
			int ret = buffer[readOffset++] & 0xFF;
			if (inBuffer == readOffset) {
				inBuffer = 0;
				readOffset = 0;
			}
			return ret;
		}
	}

	/**
	 * Same as read() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @return the last byte received
	 */
	public int last() {
		if (inBuffer == readOffset) {
			return -1;
		}

		synchronized (buffer) {
			int ret = buffer[inBuffer - 1] & 0xFF;
			inBuffer = 0;
			readOffset = 0;
			return ret;
		}
	}
	
	/**
	 * Returns the next byte in the buffer as a char. Returns -1 or 0xffff if nothing is there.
	 * @return
	 */
	public char readChar() {
		return (char) read();
	}

	/**
	 * Same as readChar() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @return the last byte received as a char
	 */
	public char lastChar() {
		return (char) last();
	}
	
	/**
	 * <p>
	 * Reads a group of bytes from the buffer. The version with no parameters returns a byte array 
	 * of all data in the buffer. This is not efficient, but is easy to use. The version with the 
	 * byteBuffer parameter is more memory and time efficient. It grabs the data in the buffer and 
	 * puts it into the byte array passed in and returns an int value for the number of bytes read. 
	 * If more bytes are available than can fit into the byteBuffer, only those that fit are read.
	 * </p>
	 * @return a byte array of anything that's in the serial buffer
	 */
	public byte[] readBytes() {
		if (inBuffer == readOffset) {
			return null;
		}

		synchronized (buffer) {
			byte[] ret = new byte[inBuffer - readOffset];
			System.arraycopy(buffer, readOffset, ret, 0, ret.length);
			inBuffer = 0;
			readOffset = 0;
			return ret;
		}
	}

	/**
	 * @param outgoing passed in byte array to be altered
	 * @return a byte array of anything that's in the serial buffer
	 */
	public byte[] readBytes(int max) {
		if (inBuffer == readOffset) {
			return null;
		}

		synchronized (buffer) {
			int length = inBuffer - readOffset;
			if (length > max)
				length = max;
			byte[] ret = new byte[length];
			System.arraycopy(buffer, readOffset, ret, 0, length);

			readOffset += length;
			if (inBuffer == readOffset) {
				inBuffer = 0;
				readOffset = 0;
			}
			return ret;
		}
	}
	
	/**
	 * <h3>Advanced</h3> Grab whatever is in the serial buffer, and stuff it
	 * into a byte buffer passed in by the user. This is more memory/time
	 * efficient than readBytes() returning a byte[] array.
	 *
	 * Returns an int for how many bytes were read. If more bytes are available
	 * than can fit into the byte array, only those that will fit are read.
	 */
	public int readBytes(byte[] dest) {
		if (inBuffer == readOffset) {
			return 0;
		}

		synchronized (buffer) {
			int toCopy = inBuffer - readOffset;
			if (dest.length < toCopy) {
				toCopy = dest.length;
			}
			System.arraycopy(buffer, readOffset, dest, 0, toCopy);
			readOffset += toCopy;
			if (inBuffer == readOffset) {
				inBuffer = 0;
				readOffset = 0;
			}
			return toCopy;
		}
	}

	/**
	 * Reads from the port into a buffer of bytes up to and including a particular character. 
	 * If the character isn't in the buffer, 'null' is returned. The version with without the 
	 * byteBuffer parameter returns a byte array of all data up to and including the interesting 
	 * byte. This is not efficient, but is easy to use. The version with the byteBuffer parameter 
	 * is more memory and time efficient. It grabs the data in the buffer and puts it into the 
	 * byte array passed in and returns an int value for the number of bytes read. If the byte 
	 * buffer is not large enough, -1 is returned and an error is printed to the message area. 
	 * If nothing is in the buffer, 0 is returned.
	 * 
	 * @param theLookUpByte character designated to mark the end of the data
	 * @return the bytes 
	 */
	public byte[] readBytesUntil(int inByte) {
		if (inBuffer == readOffset) {
			return null;
		}

		synchronized (buffer) {
			// look for needle in buffer
			int found = -1;
			for (int i = readOffset; i < inBuffer; i++) {
				if (buffer[i] == (byte) inByte) {
					found = i;
					break;
				}
			}
			if (found == -1) {
				return null;
			}

			int toCopy = found - readOffset + 1;
			byte[] dest = new byte[toCopy];
			System.arraycopy(buffer, readOffset, dest, 0, toCopy);
			readOffset += toCopy;
			if (inBuffer == readOffset) {
				inBuffer = 0;
				readOffset = 0;
			}
			return dest;
		}
	}

	/**
	 * <h3>Advanced</h3> If dest[] is not big enough, then -1 is returned, and
	 * an error message is printed on the console. If nothing is in the buffer,
	 * zero is returned. If 'interesting' byte is not in the buffer, then 0 is
	 * returned.
	 * 
	 * @param dest passed in byte array to be altered
	 */
	public int readBytesUntil(int inByte, byte[] dest) {
		if (inBuffer == readOffset) {
			return 0;
		}

		synchronized (buffer) {
			// look for needle in buffer
			int found = -1;
			for (int i = readOffset; i < inBuffer; i++) {
				if (buffer[i] == (byte) inByte) {
					found = i;
					break;
				}
			}
			if (found == -1) {
				return 0;
			}

			// check if bytes to copy fit in dest
			int toCopy = found - readOffset + 1;
			if (dest.length < toCopy) {
				System.err.println("The buffer passed to readBytesUntil() is to small " + "to contain " + toCopy
						+ " bytes up to and including " + "char " + (byte) inByte);
				return -1;
			}
			System.arraycopy(buffer, readOffset, dest, 0, toCopy);
			readOffset += toCopy;
			if (inBuffer == readOffset) {
				inBuffer = 0;
				readOffset = 0;
			}
			return toCopy;
		}
	}

	/**
	 * Returns all the data from the buffer as a String. This method assumes the incoming characters are ASCII. 
	 * If you want to transfer Unicode data, first convert the String to a byte stream in the representation of 
	 * your choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * @return all the data from the buffer as a String
	 */
	public String readString() {
		if (inBuffer == readOffset) {
			return null;
		}
		return new String(readBytes());
	}

	/**
	 * Combination of readBytesUntil and readString. See caveats in each
	 * function. Returns null if it still hasn't found what you're looking for.
	 * If you want to move Unicode data, you can first convert the String to a
	 * byte stream in the representation of your choice (i.e. UTF8 or two-byte
	 * Unicode data), and send it as a byte array.
	 * 
	 * <h3>Advanced</h3> If you want to move Unicode data, you can first convert
	 * the String to a byte stream in the representation of your choice (i.e.
	 * UTF8 or two-byte Unicode data), and send it as a byte array.
	 * 
	 * @param inByte character designated to mark the end of the data
	 * @return all the data from the buffer as a String
	 */
	public String readStringUntil(int inByte) {
		byte temp[] = readBytesUntil(inByte);
		if (temp == null) {
			return null;
		} else {
			return new String(temp);
		}
	}
}
