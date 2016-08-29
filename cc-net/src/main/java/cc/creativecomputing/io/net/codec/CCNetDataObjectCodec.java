package cc.creativecomputing.io.net.codec;

import java.nio.ByteBuffer;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;

public class CCNetDataObjectCodec implements CCNetPacketCodec<CCDataObject>{

	@Override
	public CCDataObject decode(ByteBuffer theBuffer) {
		byte[] myArray = new byte[theBuffer.limit()];
		theBuffer.get(myArray);
		String myXMLString = new String(myArray);
		return CCDataIO.parseToObject(myXMLString, CCDataFormats.JSON);
	}

	@Override
	public void encode(CCDataObject theMessage, ByteBuffer theBuffer) {
		String myXMLString = (String)CCDataIO.toFormatType(theMessage, CCDataFormats.JSON);
		theBuffer.put(myXMLString.getBytes());
	}

}
