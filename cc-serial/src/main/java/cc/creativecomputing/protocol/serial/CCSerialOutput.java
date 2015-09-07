package cc.creativecomputing.protocol.serial;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class CCSerialOutput{
	private SerialPort _myPort;


	// read buffer and streams

	private OutputStream _myOutput;
	
	CCSerialOutput(SerialPort thePort) throws TooManyListenersException, IOException{
		_myPort = thePort;
		_myOutput = _myPort.getOutputStream();
	}
	
	void stop(){
		try {
			if (_myOutput != null)
				_myOutput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		_myOutput = null;
	}
	
	/**
	 * 
	 */
	public void setDTR(boolean state) {
		_myPort.setDTR(state);
	}

	/**
	 * In case you write a String note that this doesn't account for Unicode (two bytes per char), nor will it send UTF8
	 * characters.. It assumes that you mean to send a byte buffer (most often the case for networking and serial i/o) and will
	 * only use the bottom 8 bits of each char in the string. (Meaning that internally it uses String.getBytes)
	 * If you want to move Unicode data, you can first convert the String to a byte stream in the representation of your choice
	 * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * @param theValue int or char to write
	 */
	public void write(int theValue) { // will also cover char
		try {
			_myOutput.write(theValue & 0xff); // for good measure do the &
			_myOutput.flush(); // hmm, not sure if a good idea

		} catch (Exception e) { // null pointer or serial port dead
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param theBytes bytes to write to the output
	 */
	public void write(byte[] theBytes) {
		try {
			_myOutput.write(theBytes);
			_myOutput.flush(); // hmm, not sure if a good idea

		} catch (Exception e) { // null pointer or serial port dead
			// errorMessage("write", e);
//			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param theString string to write to output
	 */
	public void write(String theString) {
		write(theString.getBytes());
	}
}
