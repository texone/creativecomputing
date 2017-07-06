package cc.creativecomputing.io.netty.codec;

import java.nio.ByteBuffer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class CCNetByteCodec implements CCNetCodec<ByteBuffer>{

	private static class CCByteBufferEncoder extends MessageToByteEncoder<ByteBuffer>{

		@Override
		protected void encode(ChannelHandlerContext theContext, ByteBuffer theMessage, ByteBuf theBuf) throws Exception {
			theBuf.writeBytes(theMessage);
		}
		
	}
	
	@Override
	public MessageToByteEncoder<ByteBuffer> encoder() {
		return new CCByteBufferEncoder();
	}
	
	private static class CCByteBufferDecoder extends ByteToMessageDecoder{
		@Override
		protected void decode(ChannelHandlerContext theContext, ByteBuf theBuf, List<Object> theObjects) throws Exception {
			ByteBuffer myResult = theBuf.nioBuffer();
			theBuf.readBytes(theBuf.readableBytes()).release();
			try{
				theObjects.add(myResult);
			}catch(Exception e){
				e.printStackTrace();
//				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public ChannelInboundHandler[] decoder() {
		return new  ByteToMessageDecoder[]{new CCByteBufferDecoder()};
	}

}
