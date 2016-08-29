package cc.creativecomputing.io.net;

import java.net.SocketAddress;

public class CCNetMessage<MessageType> {

	public final MessageType message;
	public final SocketAddress address;
	public final long timeStamp;
	
	public CCNetMessage(MessageType theMessage, SocketAddress theAdress, long theTimeStamp){
		message = theMessage;
		address = theAdress;
		timeStamp = theTimeStamp;
	}
}
