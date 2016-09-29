package cc.creativecomputing.io.netty.codec;

import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.MessageToByteEncoder;

public interface CCNetCodec <MessageType> {

	public ChannelInboundHandler[] decoder();
	
	public MessageToByteEncoder<MessageType> encoder();
}
