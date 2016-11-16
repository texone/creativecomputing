package cc.creativecomputing.protocol.serial;

import jssc.SerialPort;

public enum CCSerialDataBit{
	DATABITS_5(SerialPort.DATABITS_5),
	DATABITS_6(SerialPort.DATABITS_6),
	DATABITS_7(SerialPort.DATABITS_7),
	DATABITS_8(SerialPort.DATABITS_8);
	
	int id;
	
	CCSerialDataBit(int theID){
		id = theID;
	}
}