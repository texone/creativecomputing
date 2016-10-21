package cc.creativecomputing.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetDataObjectCodec;

public class CCClient<MessageType> {
	
	@Sharable
	public class CCClientHandler implements ChannelInboundHandler {
		
		@Override
		public void channelActive(ChannelHandlerContext theCtx) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public void channelRead(ChannelHandlerContext theCtx, Object in) {
			MessageType myMessage = (MessageType)in;
			_myEvents.proxy().messageReceived(new CCNetMessage<MessageType>(myMessage, null, System.currentTimeMillis()));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext theCtx, Throwable cause) {
			cause.printStackTrace();
			theCtx.close();
		}

		@Override
		public void handlerAdded(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void handlerRemoved(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void channelInactive(ChannelHandlerContext theCtx) throws Exception {
			if(_myReconnectTime <= 0)return;
			
			scheduleReconnect(theCtx.channel().eventLoop()); 
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void channelRegistered(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void channelUnregistered(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void channelWritabilityChanged(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void userEventTriggered(ChannelHandlerContext theCtx, Object arg1) throws Exception {}
	}
	
	@SuppressWarnings("rawtypes")
	private final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);
	
	private Class<Channel> _myChannelClass;
	private CCNetCodec<MessageType> _myCodec;
	private final String _myHost;
	private final int _myPort;
	
	private int _myReconnectTime = 0;
	
	private EventLoopGroup _myGroup;

	@SuppressWarnings("unchecked")
	public CCClient(Class<?> theChannelClass, CCNetCodec<MessageType> theCodec, String theHost, int thePort) {
		_myChannelClass = (Class<Channel>)theChannelClass;
		_myCodec = theCodec;
		_myHost = theHost;
		_myPort = thePort;

		_myGroup = new NioEventLoopGroup();
	}
	
	public void reconnectTime(int theReconnectTime){
		_myReconnectTime = theReconnectTime;
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myEvents;
	}
	
	private ChannelFuture _myFuture;
	private boolean _myIsConnected;
	
	private void scheduleReconnect(EventLoop theLoop){
		theLoop.schedule(() -> {
			createBootStrap();
		}, _myReconnectTime, TimeUnit.SECONDS);
	}
	
	private void createBootStrap(){
		Bootstrap myBootstrap = new Bootstrap();
		myBootstrap.group(_myGroup);
		myBootstrap.channel(_myChannelClass);
		myBootstrap.remoteAddress(new InetSocketAddress(_myHost, _myPort));
		myBootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder(), new CCClientHandler());
			}
		});
		try {
			_myFuture = myBootstrap.connect();
			
			if(_myReconnectTime > 0){
				_myFuture.addListener((channelFuture) -> {
					if (_myFuture.isSuccess())return;

					CCLog.info("SCHEDULE RECONNECT");
					scheduleReconnect(_myFuture.channel().eventLoop());
				});
			}
			
			_myFuture.sync();
			_myIsConnected = true;
		} catch (Exception e) {
			e.printStackTrace();
//			throw new CCNetException(e);
		}
	}

	public void connect(){
		createBootStrap();
	}
	
	public void write(MessageType theMessage){
		_myFuture.channel().writeAndFlush(theMessage);
	}
	
	public boolean isConnected() {
		return _myIsConnected;
	}
	
	public void close(){
		try {
			_myFuture.channel().closeFuture().sync();
			_myGroup.shutdownGracefully().sync();
			_myIsConnected = false;
		} catch (InterruptedException e) {
			throw new CCNetException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		CCClient<CCDataObject> myClient = new CCClient<CCDataObject>(NioSocketChannel.class, new CCNetDataObjectCodec(),"127.0.0.1", 12345);
		myClient.connect();
	}

}
