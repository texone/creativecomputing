package cc.creativecomputing.protocol.serial;

import gnu.io.SerialPort;

public enum CCSerialParityBit{
	EVEN(SerialPort.PARITY_EVEN),
	MARK(SerialPort.PARITY_MARK),
	NONE(SerialPort.PARITY_NONE),
	ODD(SerialPort.PARITY_ODD),
	SPACCE(SerialPort.PARITY_SPACE);
	
	int id;
	
	CCSerialParityBit(int theID){
		id = theID;
	}
}