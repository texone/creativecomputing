package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

public interface CCNetPacketCodec <MessageType>{

	public MessageType decode(ByteBuffer theBuffer);
	
	public void encode(MessageType theMessage, ByteBuffer theBuffer);
}
