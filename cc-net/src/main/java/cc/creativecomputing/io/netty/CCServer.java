package cc.creativecomputing.io.netty;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public abstract class CCServer<MessageType> {
	
	@Sharable
	public class CCServerHandler implements ChannelInboundHandler {
		@SuppressWarnings("unchecked")
		@Override
		public void channelRead(ChannelHandlerContext theContext, Object theMessage) {
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
	
	protected List<Channel> _myConnectedChannels = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	protected final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);
	
	protected final CCNetCodec<MessageType> _myCodec;
	
	protected final int _myPort;
	
	public CCServer(CCNetCodec<MessageType> theCodec, int port) {
		_myCodec = theCodec;
		_myPort = port;
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

	public abstract void bootstrap() throws Exception;
	
	private Thread _myThread = null;

	public final void connect() {
		_myThread = new Thread(() -> {
			try{
				bootstrap();
			}catch(Exception e){
				throw new CCNetException(e);
			}
		}, "ServerBody");
		_myThread.setDaemon(true);
		_myThread.start();
	}
}
