package cc.creativecomputing.io.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.data.CCDataObject;

public class CCNetDataObjectCodec implements CCNetCodec<CCDataObject>{
	
	private static class CCJsonEncoder extends MessageToByteEncoder<CCDataObject>{

		@Override
		protected void encode(ChannelHandlerContext theContext, CCDataObject theMessage, ByteBuf theBuf) throws Exception {
			String myXMLString = (String)CCDataIO.toFormatType(theMessage, CCDataFormats.JSON);
			theBuf.writeBytes(myXMLString.getBytes());
		}
		
	}
	
	@Override
	public MessageToByteEncoder<CCDataObject> encoder() {
		return new CCJsonEncoder();
	}
	
	private static class CCJsonDecoder extends ByteToMessageDecoder{
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
		return new  ByteToMessageDecoder[]{new JsonObjectDecoder(), new CCJsonDecoder()};
	}

}
