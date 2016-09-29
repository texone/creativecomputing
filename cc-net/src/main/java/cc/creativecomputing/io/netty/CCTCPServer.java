package cc.creativecomputing.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;

public class CCTCPServer<MessageType> {
	
	private List<Channel> _myConnectedChannels = new ArrayList<>();
	
	@Sharable
	public class CCServerHandler implements ChannelInboundHandler {
		@SuppressWarnings("unchecked")
		@Override
		public void channelRead(ChannelHandlerContext theContext, Object theMessage) {
//			CCLog.info("channelRead");
			MessageType myMessage = (MessageType) theMessage;
			_myEvents.proxy().messageReceived(new CCNetMessage<MessageType>(myMessage, null, System.currentTimeMillis()));
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext theContext) {
//			CCLog.info("channelReadComplete");
//			theContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext theContext, Throwable cause) {
//			CCLog.info("exceptionCaught");
			cause.printStackTrace();
			theContext.close();
		}

		@Override
		public void handlerAdded(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("handlerAdded");
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("handlerRemoved");
		}

		@Override
		public void channelActive(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("channelActive");
		}

		@Override
		public void channelInactive(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("channelInactive");
		}

		@Override
		public void channelRegistered(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("channelRegistered:" + theContext.channel().remoteAddress());
			_myConnectedChannels.add(theContext.channel());
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("channelUnregistered:" + theContext.channel().remoteAddress());
			_myConnectedChannels.remove(theContext.channel());
		}

		@Override
		public void channelWritabilityChanged(ChannelHandlerContext theContext) throws Exception {
//			CCLog.info("channelWritabilityChanged");
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext theContext, Object arg1) throws Exception {
//			CCLog.info("userEventTriggered");
		}
	}
	
	@SuppressWarnings("rawtypes")
	private final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);
	
	private final CCNetCodec<MessageType> _myCodec;
	
	private final int _myPort;
	private final String _myIP;
	

	public CCTCPServer(CCNetCodec<MessageType> theCodec, String theIP, int port) {
		_myCodec = theCodec;
		_myIP = theIP;
		_myPort = port;
	}
	
	public CCTCPServer(CCNetCodec<MessageType> theCodec, int port) {
		this(theCodec, null, port);
	}
	
	public void write(MessageType theMessage){
		for(Channel myChannel:new ArrayList<>(_myConnectedChannels)){
			myChannel.writeAndFlush(theMessage);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myEvents;
	}
	
	private Thread _myThread = null;

	public void connect() {
		_myThread = new Thread(() -> {
			try{
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
			}catch(Exception e){
				throw new CCNetException(e);
			}
		}, "TCPServerBody");
		_myThread.setDaemon(true);
		_myThread.start();
		
	}
	
	

	public static void main(String[] args) throws Exception {
		
		CCTCPServer<String> myServer = new CCTCPServer<String>(new CCNetStringCodec(), 12345);
		myServer.connect();
	}

}
