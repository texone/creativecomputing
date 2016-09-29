package cc.creativecomputing.io.netty;

import io.netty.channel.socket.nio.NioSocketChannel;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;

public class CCTCPClient<MessageType> extends CCClient<MessageType>{

	public CCTCPClient(CCNetCodec<MessageType> theCodec, String theHost, int thePort) {
		super(NioSocketChannel.class, theCodec, theHost, thePort);
	}

	public static void main(String[] args) throws Exception {
		CCTCPClient<String> myClient = new CCTCPClient<String>(new CCNetStringCodec(),"127.0.0.1", 12345);
		myClient.connect();
		myClient.write("texone");
		myClient.write("textwo");
	}
}
