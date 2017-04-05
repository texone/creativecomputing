package cc.creativecomputing.protocol.serial;

public interface CCSerialStopListener {

	
	
	/**
	 * Called before a serial port object is closed
	 * @param theSerialPort the serial port that received data
	 */
	public void stop(final CCSerialInput theSerialPort);
}
