package cc.creativecomputing.io.net;

import java.nio.channels.SocketChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCTCPClient<MessageType> extends CCNetClient<SocketChannel, MessageType>{

	public CCTCPClient(CCNetPacketCodec<MessageType> theCodec, String theIP, int thePort, String theTargetIP, int theTargetPort) {
		super(
			theCodec, 
			new CCTCPIn<MessageType>(theCodec), 
			new CCTCPOut<MessageType>(theCodec)
		);
		_myLocalAddress.ip(theIP);
		_myLocalAddress.port(thePort);
		
		_myTargetAddress.ip(theTargetIP);
		_myTargetAddress.port(theTargetPort);
	}
	
	public CCTCPClient(CCNetPacketCodec<MessageType> theCodec, String theTargetIP, int theTargetPort) {
		super(
			theCodec, 
			new CCTCPIn<MessageType>(theCodec), 
			new CCTCPOut<MessageType>(theCodec)
		);
			
		_myTargetAddress.ip(theTargetIP);
		_myTargetAddress.port(theTargetPort);
	}
	
	public CCTCPClient(CCNetPacketCodec<MessageType> theCodec) {
		super(
			theCodec, 
			new CCTCPIn<MessageType>(theCodec), 
			new CCTCPOut<MessageType>(theCodec)
		);
	}

}
