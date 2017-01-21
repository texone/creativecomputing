package cc.creativecomputing.io.netty;

import java.net.InetSocketAddress;

import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CCTCPServer<MessageType> extends CCServer<MessageType>{

	protected final String _myIP;
	
	public CCTCPServer(CCNetCodec<MessageType> theCodec, String theIP, int port) {
		super(theCodec, port);
		_myIP = theIP;
	}
	
	public CCTCPServer(CCNetCodec<MessageType> theCodec, int port) {
		this(theCodec, null, port);
	}
	
	@Override
	public void bootstrap() throws Exception{
		final CCServerHandler myServerHandler = new CCServerHandler();
		EventLoopGroup myLoopGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap myBootStrap = new ServerBootstrap();
			myBootStrap.group(myLoopGroup);
			myBootStrap.channel(NioServerSocketChannel.class);
			if(_myIP != null){
				myBootStrap.localAddress(new InetSocketAddress(_myIP, _myPort));
			}else{
				myBootStrap.localAddress(new InetSocketAddress(_myPort));
			}
			myBootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(_myCodec.decoder());
					ch.pipeline().addLast(_myCodec.encoder());
					ch.pipeline().addLast(myServerHandler);
				}
			});
			ChannelFuture myFuture = myBootStrap.bind().sync();
			myFuture.channel().closeFuture().sync();
		} finally {
			myLoopGroup.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		
		CCTCPServer<String> myServer = new CCTCPServer<String>(new CCNetStringCodec(), 12345);
		myServer.connect();
	}

}
