/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cc.creativecomputing.io.netty;

import java.net.InetSocketAddress;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * A UDP broadcast client that asks for a quote of the moment (QOTM) to
 * {@link QuoteOfTheMomentServer}.
 *
 * Inspired by <a href=
 * "http://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html">the
 * official Java tutorial</a>.
 */
public class CCUDPClient<MessageType> extends CCClient<MessageType> {

	public CCUDPClient(CCNetCodec<MessageType> theCodec, int thePort) {
		super(theCodec, thePort);
	}

	@Override
	public void createBootstrap() {
		Bootstrap myBootstrap = new Bootstrap();
		myBootstrap.group(_myGroup);
		myBootstrap.channel(NioDatagramChannel.class);
		myBootstrap.remoteAddress(new InetSocketAddress("127.0.0.1", _myPort));
		myBootstrap.handler(new ChannelInitializer<DatagramChannel>() {

			@Override
			public void initChannel(DatagramChannel ch) throws Exception {
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

			// Broadcast the QOTM request to port 8080.
//			_myFuture.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
//					new InetSocketAddress("255.255.255.255", _myPort))).sync();

			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}