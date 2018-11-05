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

import java.util.List;

import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 
 */
public class CCUDPServer<MessageType> extends CCServer<MessageType>{

	public CCUDPServer(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCUDPServer(CCNetCodec<MessageType> theCodec) {
		super(theCodec);
	}

	@Override
	public void createBootstrap() throws Exception{
		final CCServerHandler myServerHandler = new CCServerHandler();
		_myGroup = new NioEventLoopGroup();
		
		Bootstrap myBootStrap = new Bootstrap();
		myBootStrap.group(_myGroup);
		myBootStrap.channel(NioDatagramChannel.class);
//		myBootStrap.option(ChannelOption.SO_BROADCAST, true);
		myBootStrap.handler(new ChannelInitializer<DatagramChannel>() {
			@Override
			public void initChannel(DatagramChannel ch) {
				ch.pipeline().addLast("udpDecoder", new MessageToMessageDecoder<DatagramPacket>() {
					@Override
					protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
						out.add(msg.content());
						msg.retain();
					}
				});
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder());
				ch.pipeline().addLast(myServerHandler);
			}
		});

		_myFuture = myBootStrap.bind(_myPort);
		_myFuture.sync();
			
		_myFuture.channel().closeFuture().await();
	}
	
}