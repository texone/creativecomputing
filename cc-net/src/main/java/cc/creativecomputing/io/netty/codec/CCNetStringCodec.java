package cc.creativecomputing.io.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;

public class CCNetStringCodec implements CCNetCodec<String>{
	
	private Charset _myCharSet;
	
	private int _myMaxLength;
	
	public CCNetStringCodec(Charset theCharSet, int theMaxLength){
		_myCharSet = theCharSet;
		_myMaxLength = theMaxLength;
	}
	
	/**
	 * Create a string codec with utf_8 charset and 10000 chars maxlength
	 */
	public CCNetStringCodec(){
		this(CharsetUtil.UTF_8, 10000);
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
		return new ChannelInboundHandler[]{new LineBasedFrameDecoder(_myMaxLength), new StringDecoder(_myCharSet)};
	}
	
	@Sharable
	public class CCStringEncoder extends MessageToByteEncoder<String> {

	    private final Charset _myCharset;

	    /**
	     * Creates a new instance with the current system character set.
	     */
	    public CCStringEncoder() {
	        this(Charset.defaultCharset());
	    }

	    /**
	     * Creates a new instance with the specified character set.
	     */
	    public CCStringEncoder(Charset charset) {
	        if (charset == null) {
	            throw new NullPointerException("charset");
	        }
	        _myCharset = charset;
	    }

		@Override
		protected void encode(ChannelHandlerContext theCTX, String theString, ByteBuf theBuf) throws Exception {
			theBuf.writeBytes(ByteBufUtil.encodeString(theCTX.alloc(), CharBuffer.wrap(theString + "\n"), _myCharset));
		}
	}

	@Override
	public MessageToByteEncoder<String> encoder() {
		return new CCStringEncoder(_myCharSet);
	}

}
