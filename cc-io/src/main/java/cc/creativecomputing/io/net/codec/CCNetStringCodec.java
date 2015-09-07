package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

public class CCNetStringCodec implements CCNetPacketCodec<String>{

	@Override
	public String decode(ByteBuffer theBuffer) {
		return new String(theBuffer.array());
	}

	@Override
	public void encode(String theMessage, ByteBuffer theBuffer) {
		theBuffer.put(theMessage.getBytes());
	}

}
