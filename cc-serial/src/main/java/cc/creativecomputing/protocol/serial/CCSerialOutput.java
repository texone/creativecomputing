package cc.creativecomputing.protocol.serial;

import jssc.SerialPort;
import jssc.SerialPortException;

public class CCSerialOutput{
	private SerialPort _myPort;


	
	CCSerialOutput(SerialPort thePort) {
		_myPort = thePort;
	}

	/**
	 * In case you write a String note that this doesn't account for Unicode
	 * (two bytes per char), nor will it send UTF8 characters.. It assumes that
	 * you mean to send a byte buffer (most often the case for networking and
	 * serial i/o) and will only use the bottom 8 bits of each char in the
	 * string. (Meaning that internally it uses String.getBytes) If you want to
	 * move Unicode data, you can first convert the String to a byte stream in
	 * the representation of your choice (i.e. UTF8 or two-byte Unicode data),
	 * and send it as a byte array.
	 * 
	 *  <h3>Advanced</h3> This will handle both ints, bytes and chars
	 *            transparently.
	 * 
	 * @param theValue int or char to write
	 */
	public void write(int theValue) {
		try {
			_myPort.writeInt(theValue);
		} catch (SerialPortException e) {
			throw new RuntimeException("Error writing to serial port " + e.getPortName() + ": " + e.getExceptionType(), e);
		}
	}

	/**
	 * 
	 * @param theBytes bytes to write to the output
	 */
	public void write(byte[] theBytes) {
		try {
			// this might block if the serial device is not yet ready (esp. tty
			// devices under OS X)
			_myPort.writeBytes(theBytes);
			// we used to call flush() here
		} catch (SerialPortException e) {
			throw new RuntimeException("Error writing to serial port " + e.getPortName() + ": " + e.getExceptionType(), e);
		}
	}

	/**
	 * <h3>Advanced</h3> Write a String to the output. Note that this doesn't
	 * account for Unicode (two bytes per char), nor will it send UTF8
	 * characters.. It assumes that you mean to send a byte buffer (most often
	 * the case for networking and serial i/o) and will only use the bottom 8
	 * bits of each char in the string. (Meaning that internally it uses
	 * String.getBytes)
	 *
	 * If you want to move Unicode data, you can first convert the String to a
	 * byte stream in the representation of your choice (i.e. UTF8 or two-byte
	 * Unicode data), and send it as a byte array.
	 *
	 * @param src data to write
	 */
	public void write(String src) {
		try {
			_myPort.writeString(src);
		} catch (SerialPortException e) {
			throw new RuntimeException("Error writing to serial port " + e.getPortName() + ": " + e.getExceptionType(),
					e);
		}
	}
}
