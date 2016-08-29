package cc.creativecomputing.artnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cc.creativecomputing.artnet.packets.ArtNetPacket;
import cc.creativecomputing.artnet.packets.ByteUtils;
import cc.creativecomputing.artnet.packets.CCArtNetOpCode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCArtNetCodec implements CCNetPacketCodec<ArtNetPacket>{



	@Override
	public ArtNetPacket decode(ByteBuffer theData) {
		
		if(theData.remaining() <= 10){
			CCLog.warn("invalid packet length: " + theData.remaining());
			return null;
		}
		
		// check if header is valid
		if(!ByteUtils.compareBytes(theData, ArtNetPacket.HEADER)){
			CCLog.info("invalid header");
			return null;
		}
		
		theData.order(ByteOrder.LITTLE_ENDIAN);
		
		int opCode = ByteUtils.getInt16LE(theData);
		
		theData.rewind();
		byte[] myData = theData.array();
		
		CCLog.info("creating packet instance for opcode: 0x" + ByteUtils.hex(opCode, 4));
		ArtNetPacket myPacket = CCArtNetOpCode.createPacket(opCode);
		if(myPacket == null)return null;
		myPacket.decode(myData);
		return myPacket;
	}
	
//	@Override
//	public ArtNetPacket decode(ByteBuffer theData) {
//		byte[] myData = theData.array();
//		// check if packet size is valid
//		if(theData.remaining() <= 10){
//			CCLog.warn("invalid packet length: " + theData.remaining());
//			return null;
//		}
//		
//		// check if header is valid
//		if(!ByteUtils.compareBytes(theData, ArtNetPacket.HEADER)){
//			CCLog.warn("invalid header");
//			return null;
//		}
//		
//		ArtNetPacket packet = null;
//		ByteUtils data = new ByteUtils(myData);
//		
//		int opCode = data.getInt16LE(8);
//		packet = createPacketForOpCode(opCode, myData);
//		
//		return packet;
//	}

	@Override
	public void encode(ArtNetPacket theMessage, ByteBuffer theBuffer) {
		theBuffer.put(theMessage.getData());
	}

}
