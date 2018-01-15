package cc.creativecomputing.modbus.net;

public interface ModbusListener extends Runnable {
	/**
	 * Main execution loop for this Modbus interface listener
	 */
    void run();

	/**
	 * Sets the unit number for this Modbus interface listener.
	 * 
	 * @param unit
	 *            Modbus unit number. A value of 0 indicates this Modbus
	 *            interface accepts all unit numbers.
	 */
    void setUnit(int unit);

	/**
	 * Gets the unit number for this Modbus interface listener.
	 * 
	 * @returns The Modbus unit number.
	 */
    int getUnit();

	/**
	 * Sets the <i>listening</i> state for this Modbus interface. A Modbus
	 * interface which is not <i>listening</i> will silently discard all
	 * requests.
	 * 
	 * @param listening
	 *            This interface will accept and process requests.
	 */
    void setListening(boolean listening);

	/**
	 * Gets the <i>listening</i> state for this Modbus interface. A Modbus
	 * interface which is not <i>listening</i> will silently discard all
	 * requests. Additionally, an interface which is no longer alive will return
	 * <b>false</b>.
	 * 
	 * @returns The current <i>listening</i> state.
	 */
    boolean isListening();
	
	/**
	 * Starts the listener thread with the <tt>ModbusListener</tt> in
	 * <i>listening</i> mode.
	 * 
	 * @returns The listener Thread.
	 */
    Thread listen();
	
	/**
	 * Stop the listener thread for this <tt>ModbusListener</tt> instance.
	 */
    void stop();
}
