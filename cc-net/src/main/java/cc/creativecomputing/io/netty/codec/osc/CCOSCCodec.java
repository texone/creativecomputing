package cc.creativecomputing.io.netty.codec.osc;

import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.MessageToByteEncoder;

public class CCOSCCodec implements CCNetCodec<CCOSCPacket>{

	@Override
	public ChannelInboundHandler[] decoder() {
		return new ChannelInboundHandler[]{new CCOSCDecoder()};
	}
	
	@Override
	public MessageToByteEncoder<CCOSCPacket> encoder() {
		return new CCOSCEncoder();
	}

}
