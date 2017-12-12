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
		public void handlerAdded(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void handlerRemoved(ChannelHandlerContext theCtx) throws Exception {}

		@Override
		public void channelInactive(ChannelHandlerContext theCtx) throws Exception {
			if(_myReconnectTime <= 0)return;
//			scheduleReconnect(theCtx.channel().eventLoop()); 
			connect();
			
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

	public CCClient(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCClient(CCNetCodec<MessageType> theCodec){
		super(theCodec);
	}

	@Override
	public void connect(){
		try {
			createBootstrap();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void write(MessageType theMessage){
		if(!_myIsConnected)return;
		_myFuture.channel().writeAndFlush(theMessage);
	}
	
	public static void main(String[] args) throws Exception {
		CCTCPClient<CCDataObject> myClient = new CCTCPClient<CCDataObject>(new CCNetDataObjectCodec(),"127.0.0.1", 12345);
		myClient.connect();
	}

}
