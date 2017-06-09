package cc.creativecomputing.modbus;

import java.net.SocketException;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;
import cc.creativecomputing.modbus.msg.CCModbusMessage;
import cc.creativecomputing.modbus.util.ModbusUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public abstract class CCModbusCodec implements CCNetCodec<CCModbusMessage>{
	
	protected boolean headless = false;

	private class CCModbusEncoder extends MessageToByteEncoder<CCModbusMessage>{

		@Override
		protected void encode(ChannelHandlerContext theContext, CCModbusMessage theMessage, ByteBuf theBuf) throws Exception {
			byte message[] = theMessage.message();

			if (!headless) {
				theBuf.writeShort(theMessage.transactionID());
				theBuf.writeShort(theMessage.protocolID());
				theBuf.writeShort((message != null ? message.length : 0) + 2);
			}
			theBuf.writeByte(theMessage.unitID());
			theBuf.writeByte(theMessage.functionCode().id);
			if (message != null && message.length > 0)
				theBuf.writeBytes(message);

			if (Modbus.debug)
				CCLog.error("Sent: " + ModbusUtil.toHex(theBuf.array()));
				// write more sophisticated exception handling
			
		}
		
	}
	
	public void setHeadless() {
		headless = true;
	}
	
	@Override
	public MessageToByteEncoder<CCModbusMessage> encoder() {
		return new CCModbusEncoder();
	}

}
