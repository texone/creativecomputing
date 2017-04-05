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

import java.net.InetSocketAddress;

import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * 
 */
public class CCUDPClient<MessageType> extends CCClient<MessageType> {

	public CCUDPClient(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCUDPClient(CCNetCodec<MessageType> theCodec){
		super(theCodec);
	}

	@Override
	public void createBootstrap() {
		try {
		Bootstrap myBootstrap = new Bootstrap();
		_myGroup = new NioEventLoopGroup();
		myBootstrap.group(_myGroup);
		myBootstrap.channel(NioDatagramChannel.class);
		myBootstrap.remoteAddress(new InetSocketAddress(_myIP, _myPort));
		myBootstrap.handler(new ChannelInitializer<DatagramChannel>() {

			@Override
			public void initChannel(DatagramChannel ch) throws Exception {
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder(), new CCClientHandler());
			}
		});
		
		_myFuture = myBootstrap.connect();

		scheduleReconnect();
			
		_myFuture.sync();
		_myIsConnected = true;
			// Broadcast the QOTM request to port 8080.
//			_myFuture.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
//					new InetSocketAddress("255.255.255.255", _myPort))).sync();

			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}