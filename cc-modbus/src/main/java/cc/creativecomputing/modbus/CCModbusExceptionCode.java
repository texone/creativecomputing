package cc.creativecomputing.modbus;

public enum CCModbusExceptionCode {
	/**
	 * Defines the Modbus slave exception type <tt>illegal function</tt>. This
	 * exception code is returned if the slave:
	 * <ul>
	 * <li>does not implement the function code <b>or</b></li>
	 * <li>is not in a state that allows it to process the function</li>
	 * </ul>
	 */
	ILLEGAL_FUNCTION_EXCEPTION(1),

	/**
	 * Defines the Modbus slave exception type <tt>illegal data address</tt>.
	 * This exception code is returned if the reference:
	 * <ul>
	 * <li>does not exist on the slave <b>or</b></li>
	 * <li>the combination of reference and length exceeds the bounds of the
	 * existing registers.</li>
	 * </ul>
	 */
	ILLEGAL_ADDRESS_EXCEPTION(2),

	/**
	 * Defines the Modbus slave exception type <tt>illegal data value</tt>. This
	 * exception code indicates a fault in the structure of the data values of a
	 * complex request, such as an incorrect implied length.<br>
	 * <b>This code does not indicate a problem with application specific
	 * validity of the value.</b>
	 */
	ILLEGAL_VALUE_EXCEPTION(3),

	/**
	 * Defines the Modbus slave exception type <tt>slave device failure</tt>.
	 * This exception code indicates a fault in the slave device itself.
	 */
	SLAVE_DEVICE_FAILURE(4),

	
	ACKNOWLEDGE(5),

	/**
	 * Defines the Modbus slave exception type <tt>slave busy</tt>. This
	 * exception indicates the the slave is unable to perform the operation
	 * because it is performing an operation which cannot be interrupted.
	 */
	SLAVE_BUSY_EXCEPTION(6),

	/**
	 * Defines the Modbus slave exception type <tt>negative acknowledgment</tt>.
	 * This exception code indicates the slave cannot perform the requested
	 * action.
	 */
	NEGATIVE_ACKNOWLEDGEMENT(7),
	
	MEMORY_PARITY_ERROR(8),

	/**
	 * Defines the Modbus slave exception type <tt>Gateway target failed to
	 * respond</tt>. This exception code indicates that a Modbus gateway failed
	 * to receive a response from the specified target.
	 */
	GATEWAY_TARGET_NO_RESPONSE(11),

	UNDEFINED(-1);

	public int id;

	CCModbusExceptionCode(int theID) {
		id = theID;
	}

	public static CCModbusExceptionCode byID(int theID) {
		for (CCModbusExceptionCode myCode : values()) {
			if (myCode.id == theID)
				return myCode;
		}
		return UNDEFINED;
	}
}
