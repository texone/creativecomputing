package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

public class CCNetByteCodec implements CCNetPacketCodec<ByteBuffer>{

	@Override
	public ByteBuffer decode(ByteBuffer theBuffer) {
		return theBuffer;
	}

	@Override
	public void encode(ByteBuffer theMessage, ByteBuffer theBuffer) {
		theBuffer.put(theMessage);
	}

}
