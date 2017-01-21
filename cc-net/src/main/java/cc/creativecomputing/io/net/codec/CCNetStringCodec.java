package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

import cc.creativecomputing.core.logging.CCLog;

public class CCNetStringCodec implements CCNetPacketCodec<String>{

	@Override
	public String decode(ByteBuffer theBuffer) {
		byte[] myBytes = new byte[theBuffer.remaining()];
		theBuffer.get(myBytes);
		CCLog.info(new String(myBytes));
		return new String(myBytes);
	}

	@Override
	public void encode(String theMessage, ByteBuffer theBuffer) {
		CCLog.info(theMessage);
		theBuffer.put(theMessage.getBytes());
	}

}
