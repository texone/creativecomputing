/*
 * Copyright (c) 2017 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io.netty;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public abstract class CCServer<MessageType> extends CCNetChannel<MessageType>{
	
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

	public CCServer(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCServer(CCNetCodec<MessageType> theCodec){
		super(theCodec);
	}
	
	@Override
	public void write(MessageType theMessage){
		if(!_myIsConnected)return;
		for(Channel myChannel:new ArrayList<>(_myConnectedChannels)){
			myChannel.writeAndFlush(theMessage);
		}
	}
	
	private Thread _myThread = null;

	@Override
	public final void connect() {
		_myThread = new Thread(() -> {
			try{
				createBootstrap();
			}catch(Exception e){
				throw new CCNetException(e);
			}
		}, "ServerBody");
		_myThread.setDaemon(true);
		_myThread.start();
	}
}
