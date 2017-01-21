package cc.creativecomputing.io.netty;

import java.util.List;

import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * A UDP server that responds to the QOTM (quote of the moment) request to a
 * {@link QuoteOfTheMomentClient}.
 *
 * Inspired by <a href=
 * "http://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html">the
 * official Java tutorial</a>.
 */
public class CCUDPServer<MessageType> extends CCServer<MessageType>{

	public CCUDPServer(CCNetCodec<MessageType> theCodec, int port) {
		super(theCodec, port);
	}

	@Override
	public void bootstrap() throws Exception{
		final CCServerHandler myServerHandler = new CCServerHandler();
		EventLoopGroup myLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap myBootStrap = new Bootstrap();
			myBootStrap.group(myLoopGroup);
			myBootStrap.channel(NioDatagramChannel.class);
//			myBootStrap.option(ChannelOption.SO_BROADCAST, true);
			myBootStrap.handler(new ChannelInitializer<DatagramChannel>() {
				@Override
				public void initChannel(DatagramChannel ch) throws Exception {
					ch.pipeline().addLast("udpDecoder", new MessageToMessageDecoder<DatagramPacket>() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
                            out.add(msg.content());
                            msg.retain();
                        }
                    });
					ch.pipeline().addLast(_myCodec.decoder());
					ch.pipeline().addLast(_myCodec.encoder());
					ch.pipeline().addLast(myServerHandler);
				}
			});

			ChannelFuture myFuture = myBootStrap.bind(_myPort).sync();
			myFuture.channel().closeFuture().await();
		} finally {
			myLoopGroup.shutdownGracefully();
		}
	}
}