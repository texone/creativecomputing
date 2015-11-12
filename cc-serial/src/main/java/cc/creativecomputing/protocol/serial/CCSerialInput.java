package cc.creativecomputing.protocol.serial;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;

public class CCSerialInput implements SerialPortEventListener{
	private SerialPort _myPort;


	// read buffer and streams

	protected InputStream _myInput;

	private byte _myBuffer[] = new byte[32768];
	private int _myBufferIndex;
	private int _myBufferLast;

	// boolean bufferUntil = false;
	private int _myBufferSize = 1; // how big before reset or event firing
	private boolean _myDoBufferUntil;
	private int _myBufferUntilByte;
	
	private CCListenerManager<CCSerialListener> _myListeners;
	
	CCSerialInput(CCListenerManager<CCSerialListener> theListeners, SerialPort thePort) throws TooManyListenersException, IOException{
		_myListeners = theListeners;
		_myPort = thePort;
		_myPort.addEventListener(this);
		_myInput = _myPort.getInputStream();
	}
	
	void stop(){
		try {
			// do io streams need to be closed first?
			if (_myInput != null)
				_myInput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		_myInput = null;
	}
	
	/**
	 * 
	 */
	public void setDTR(boolean state) {
		_myPort.setDTR(state);
	}

	@Override
	synchronized public void serialEvent(SerialPortEvent serialEvent) {
		if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				while (_myInput.available() > 0) {
					synchronized (_myBuffer) {
						if (_myBufferLast == _myBuffer.length) {
							byte temp[] = new byte[_myBufferLast << 1];
							System.arraycopy(_myBuffer, 0, temp, 0, _myBufferLast);
							_myBuffer = temp;
						}
						_myBuffer[_myBufferLast++] = (byte) _myInput.read();

						//						
						if (
							(_myDoBufferUntil && (_myBuffer[_myBufferLast - 1] == _myBufferUntilByte)) || 
							(!_myDoBufferUntil && ((_myBufferLast - _myBufferIndex) >= _myBufferSize))
						) {
							_myListeners.proxy().onSerialEvent(this);
						}
						//						
					}
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Sets the number of bytes to buffer before calling onSerialEvent()
	 * @param theBufferSize number of bytes to buffer
	 */
	public void buffer(final int theBufferSize) {
		_myDoBufferUntil = false;
		_myBufferSize = theBufferSize;
	}

	/**
	 * Sets a specific byte to buffer until before calling onSerialEvent().
	 * @param theBufferUntilByte the value to buffer until
	 */
	public void bufferUntil(final int theBufferUntilByte) {
		_myDoBufferUntil = true;
		_myBufferUntilByte = theBufferUntilByte;
	}

	/**
	 * Returns the number of bytes that have been read from serial and are waiting to be dealt with by the user.
	 * @return the number of bytes available
	 */
	public int available() {
		return (_myBufferLast - _myBufferIndex);
	}

	/**
	 * Ignore all the bytes read so far and empty the buffer.
	 */
	public void clear() {
		_myBufferLast = 0;
		_myBufferIndex = 0;
	}

	/**
	 * Returns a number between 0 and 255 for the next byte that's waiting in the buffer. 
	 * Returns -1 if there is no byte, although this should be avoided by first checking 
	 * available() to see if data is available.
	 * @return the next byte waiting in the buffer
	 */
	public int read() {
		if (_myBufferIndex == _myBufferLast)
			return -1;

		synchronized (_myBuffer) {
			int outgoing = _myBuffer[_myBufferIndex++] & 0xff;
			if (_myBufferIndex == _myBufferLast) { // rewind
				_myBufferIndex = 0;
				_myBufferLast = 0;
			}
			return outgoing;
		}
	}

	/**
	 * Same as read() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @return the last byte received
	 */
	public int last() {
		if (_myBufferIndex == _myBufferLast)
			return -1;
		synchronized (_myBuffer) {
			int outgoing = _myBuffer[_myBufferLast - 1];
			_myBufferIndex = 0;
			_myBufferLast = 0;
			return outgoing;
		}
	}
	
	/**
	 * Returns the next byte in the buffer as a char. Returns -1 or 0xffff if nothing is there.
	 * @return
	 */
	public char readChar() {
		if (_myBufferIndex == _myBufferLast)
			return (char) (-1);
		return (char) read();
	}

	/**
	 * Same as readChar() but returns the very last value received and clears the buffer. 
	 * Useful when you just want the most recent value sent over the port.
	 * @return the last byte received as a char
	 */
	public char lastChar() {
		if (_myBufferIndex == _myBufferLast)
			return (char) (-1);
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
		if (_myBufferIndex == _myBufferLast)
			return null;

		synchronized (_myBuffer) {
			int length = _myBufferLast - _myBufferIndex;
			byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, outgoing, 0, length);

			_myBufferIndex = 0; // rewind
			_myBufferLast = 0;
			return outgoing;
		}
	}

	/**
	 * @param outgoing passed in byte array to be altered
	 * @return a byte array of anything that's in the serial buffer
	 */
	public int readBytes(byte[] theBytes) {
		if (_myBufferIndex == _myBufferLast)
			return 0;

		synchronized (_myBuffer) {
			int length = _myBufferLast - _myBufferIndex;
			if (length > theBytes.length)
				length = theBytes.length;
			System.arraycopy(_myBuffer, _myBufferIndex, theBytes, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
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
	public byte[] readBytesUntil(int theLookUpByte) {
		if (_myBufferIndex == _myBufferLast)
			return null;
		byte what = (byte) theLookUpByte;

		synchronized (_myBuffer) {
			int found = -1;
			for (int k = _myBufferIndex; k < _myBufferLast; k++) {
				if (_myBuffer[k] == what) {
					found = k;
					break;
				}
			}
			if (found == -1)
				return null;

			int length = found - _myBufferIndex + 1;
			byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, outgoing, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return outgoing;
		}
	}

	/**
	 * @param theBytes passed in byte array to be altered
	 * @return
	 */
	public int readBytesUntil(int theLookUpByte, byte theBytes[]) {
		if (_myBufferIndex == _myBufferLast)
			return 0;
		byte what = (byte) theLookUpByte;

		synchronized (_myBuffer) {
			int found = -1;
			for (int k = _myBufferIndex; k < _myBufferLast; k++) {
				if (_myBuffer[k] == what) {
					found = k;
					break;
				}
			}
			if (found == -1)
				return 0;

			int length = found - _myBufferIndex + 1;
			if (length > theBytes.length) {
				CCLog.error("readBytesUntil() byte buffer is" + " too small for the " + length + " bytes up to and including char " + theLookUpByte);
				return -1;
			}
			// byte outgoing[] = new byte[length];
			System.arraycopy(_myBuffer, _myBufferIndex, theBytes, 0, length);

			_myBufferIndex += length;
			if (_myBufferIndex == _myBufferLast) {
				_myBufferIndex = 0; // rewind
				_myBufferLast = 0;
			}
			return length;
		}
	}

	/**
	 * Returns all the data from the buffer as a String. This method assumes the incoming characters are ASCII. 
	 * If you want to transfer Unicode data, first convert the String to a byte stream in the representation of 
	 * your choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * @return all the data from the buffer as a String
	 */
	public String readString() {
		if (_myBufferIndex == _myBufferLast)
			return null;
		return new String(readBytes());
	}

	/**
	 * Combination of readBytesUntil and readString. See caveats in each function. 
	 * Returns null if it still hasn't found what you're looking for.
	 * If you want to move Unicode data, you can first convert the String to a byte stream 
	 * in the representation of your choice (i.e. UTF8 or two-byte Unicode data), and send 
	 * it as a byte array.
	 * @param theLookUpByte character designated to mark the end of the data
	 * @return all the data from the buffer as a String
	 */
	public String readStringUntil(int theLookUpByte) {
		byte b[] = readBytesUntil(theLookUpByte);
		if (b == null)
			return null;
		return new String(b);
	}
}
