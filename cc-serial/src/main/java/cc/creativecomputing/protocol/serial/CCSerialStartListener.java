package cc.creativecomputing.protocol.serial;

public interface CCSerialStartListener {
	/**
	 * Called when a serial port object is correctly initialized
	 * @param theSerialPort the serial port that received data
	 */
    void start(final CCSerialInput theSerialPort);
}
