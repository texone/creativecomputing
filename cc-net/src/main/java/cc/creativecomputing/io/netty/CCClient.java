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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetDataObjectCodec;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public abstract class CCClient<MessageType>extends CCNetChannel<MessageType> {
	
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
			
			theCtx.close();

			scheduleReconnect(theCtx.channel().eventLoop()); 
			cause.printStackTrace();
//			throw new RuntimeException(cause);
			
		}

		@Override
		public void handlerAdded(ChannelHandlerContext theCtx) {}

		@Override
		public void handlerRemoved(ChannelHandlerContext theCtx) {}

		@Override
		public void channelInactive(ChannelHandlerContext theCtx) {
			if(_myReconnectTime <= 0)return;
//			scheduleReconnect(theCtx.channel().eventLoop()); 
			connect();
			
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext theCtx) {}

		@Override
		public void channelRegistered(ChannelHandlerContext theCtx) {}

		@Override
		public void channelUnregistered(ChannelHandlerContext theCtx) {}

		@Override
		public void channelWritabilityChanged(ChannelHandlerContext theCtx) {}

		@Override
		public void userEventTriggered(ChannelHandlerContext theCtx, Object arg1) {}
	}

	public CCClient(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCClient(CCNetCodec<MessageType> theCodec){
		super(theCodec);
	}
	
	private boolean _myShouldConnect = false;

	@Override
	public void connect(){
		_myShouldConnect = true;
		try {
			createBootstrap();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean lostConnection() {
		return _myShouldConnect && !isConnected();
	}
	
	@Override
	public void write(MessageType theMessage){
		if(!isConnected())return;
		CCLog.info(_myFuture.channel().isActive(),_myFuture.channel().isOpen(),_myFuture.channel().isRegistered(),_myFuture.channel().isWritable());
		_myFuture.channel().writeAndFlush(theMessage);
	}
	
	public static void main(String[] args) {
		CCTCPClient<CCDataObject> myClient = new CCTCPClient<CCDataObject>(new CCNetDataObjectCodec(),"127.0.0.1", 12345);
		myClient.connect();
	}

}
