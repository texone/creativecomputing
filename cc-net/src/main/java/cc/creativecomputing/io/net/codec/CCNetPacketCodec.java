package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

public interface CCNetPacketCodec <MessageType>{

	MessageType decode(ByteBuffer theBuffer);
	
	void encode(MessageType theMessage, ByteBuffer theBuffer);
}
