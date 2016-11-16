package cc.creativecomputing.protocol.serial;

import jssc.SerialPort;

public enum CCSerialStopBit{
	STOPBITS_1(SerialPort.STOPBITS_1),
	STOPBITS_1_5(SerialPort.STOPBITS_1_5),
	STOPBITS_2(SerialPort.STOPBITS_2);
	
	int id;
	
	CCSerialStopBit(int theID){
		id = theID;
	}
}