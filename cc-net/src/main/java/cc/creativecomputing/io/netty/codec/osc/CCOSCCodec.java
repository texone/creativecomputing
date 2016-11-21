package cc.creativecomputing.io.netty.codec.osc;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import cc.creativecomputing.io.netty.codec.CCNetCodec;

public class CCOSCCodec implements CCNetCodec<OSCPacket>{
	
	private Charset _myCharSet;
	
	private int _myMaxLength;
	
	public CCOSCCodec(Charset theCharSet, int theMaxLength){
		_myCharSet = theCharSet;
		_myMaxLength = theMaxLength;
	}
	
	/**
	 * Create a string codec with utf_8 charset and 10000 chars maxlength
	 */
	public CCOSCCodec(){
		this(CharsetUtil.UTF_8, 10000);
	}

	@Override
	public ChannelInboundHandler[] decoder() {
		return new ChannelInboundHandler[]{new DelimiterBasedFrameDecoder(8192, false, Delimiters.nulDelimiter()), new OSCTypeDecoder()};
	}
	
	@Override
	public MessageToByteEncoder<OSCPacket> encoder() {
		return new CCOSCEncoder();
	}

}
