package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

public class CCNetStringCodec implements CCNetPacketCodec<String>{

	@Override
	public String decode(ByteBuffer theBuffer) {
		byte[] myBytes = new byte[theBuffer.remaining()];
		theBuffer.get(myBytes);
		return new String(myBytes);
	}

	@Override
	public void encode(String theMessage, ByteBuffer theBuffer) {
		theBuffer.put(theMessage.getBytes());
	}

}
