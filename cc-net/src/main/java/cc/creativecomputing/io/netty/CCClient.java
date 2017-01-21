package cc.creativecomputing.io.netty;

import java.util.concurrent.TimeUnit;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetDataObjectCodec;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class CCClient<MessageType> {
	
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
	
	protected CCNetCodec<MessageType> _myCodec;
	protected final int _myPort;
	
	protected int _myReconnectTime = 0;
	
	protected EventLoopGroup _myGroup;

	public CCClient(CCNetCodec<MessageType> theCodec, int thePort) {
		_myCodec = theCodec;
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
	
	protected ChannelFuture _myFuture;
	protected boolean _myIsConnected;
	
	public void scheduleReconnect(EventLoop theLoop){
		theLoop.schedule(() -> {
			createBootstrap();
		}, _myReconnectTime, TimeUnit.SECONDS);
	}
	
	public abstract void createBootstrap();

	public void connect(){
		createBootstrap();
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
		CCTCPClient<CCDataObject> myClient = new CCTCPClient<CCDataObject>(new CCNetDataObjectCodec(),"127.0.0.1", 12345);
		myClient.connect();
	}

}
