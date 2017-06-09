package cc.creativecomputing.modbus;

import java.util.List;

import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.modbus.msg.CCModbusMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public abstract class CCModbusResponseCodec implements CCNetCodec<CCModbusMessage>{
	
	private  class CCModbusResponseDecoder extends ByteToMessageDecoder{
		@Override
		protected void decode(ChannelHandlerContext theContext, ByteBuf theBuf, List<Object> theObjects) throws Exception {
			String myJsonString = theBuf.toString(CharsetUtil.UTF_8);
			theBuf.readBytes(theBuf.readableBytes()).release();
			try{
				theObjects.add(CCDataIO.parseToObject(myJsonString, CCDataFormats.JSON));
			}catch(Exception e){
				e.printStackTrace();
//				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public ChannelInboundHandler[] decoder() {
		return new  ByteToMessageDecoder[]{new JsonObjectDecoder(), new CCModbusResponseDecoder()};
	}

}
