package cc.creativecomputing.modbus;

public enum CCModbusFunctionCode {
	/**
	 * Defines the class 1 function code for <tt>read coils</tt>.
	 */
	READ_COILS(1),

	/**
	 * Defines a class 1 function code for <tt>read input discretes</tt>.
	 */
	READ_INPUT_DISCRETES(2),

	/**
	 * Defines a class 1 function code for <tt>read holding registers</tt>
	 */
	READ_HOLDING_REGISTERS(3),

	/**
	 * Defines the class 0 function code for <tt>read multiple registers</tt>.
	 * The proper name is "Read Holding Registers".
	 */
	READ_MULTIPLE_REGISTERS(3),

	/**
	 * Defines a class 1 function code for <tt>read input registers</tt>.
	 */
	READ_INPUT_REGISTERS(4),

	/**
	 * Defines a class 1 function code for <tt>write coil</tt>.
	 */
	WRITE_COIL(5),

	/**
	 * Defines a class 1 function code for <tt>write single register</tt>.
	 */
	WRITE_SINGLE_REGISTER(6),

	/**
	 * <tt>read exception status</tt>
	 * 
	 * Serial devices only.
	 */
	READ_EXCEPTION_STATUS(7),

	/**
	 * <tt>get serial diagnostics</tt>
	 * 
	 * Serial devices only.
	 */
	READ_SERIAL_DIAGNOSTICS(8),

	/**
	 * <tt>get comm event counter</tt>
	 * 
	 * Serial devices only.
	 */
	READ_COMM_EVENT_COUNTER(11),

	/**
	 * <tt>get comm event log</tt>
	 * 
	 * Serial devices only.
	 */
	READ_COMM_EVENT_LOG(12),

	/**
	 * Defines a standard function code for <tt>write multiple coils</tt>.
	 */
	WRITE_MULTIPLE_COILS(15),

	/**
	 * Defines the class 0 function code for <tt>write multiple registers</tt>.
	 */
	WRITE_MULTIPLE_REGISTERS(16),

	/**
	 * Defines a standard function code for <tt>read slave ID</tt>.
	 */
	REPORT_SLAVE_ID(17),

	/**
	 * <tt>read file record</tt>
	 */
	READ_FILE_RECORD(20),

	/**
	 * <tt>write file record</tt>
	 */
	WRITE_FILE_RECORD(21),

	/**
	 * <tt>mask write register</tt>
	 * 
	 * Update a single register using its current value and an AND and OR mask.
	 */
	MASK_WRITE_REGISTER(22),

	/**
	 * <tt>read / write multiple registers</tt>
	 * 
	 * Write some number of registers, then read some number of potentially
	 * other registers back.
	 */
	READ_WRITE_MULTIPLE(23),

	/**
	 * <tt>read FIFO queue</tt>
	 * 
	 * Read from a FIFO queue.
	 */
	READ_FIFO_QUEUE(24),

	/**
	 * Defines the function code for reading encapsulated data, such as vendor
	 * information.
	 */
	READ_MEI(43), 
	
	READ_MEI_VENDOR_INFO(14),
	
	UNDEFINED(-1);

	public int id;

	private CCModbusFunctionCode(int theID) {
		id = theID;
	}
	
	public static CCModbusFunctionCode byID(int theID){
		for(CCModbusFunctionCode myCode:values()){
			if(myCode.id == theID)return myCode;
		}
		return UNDEFINED;
	}
}
