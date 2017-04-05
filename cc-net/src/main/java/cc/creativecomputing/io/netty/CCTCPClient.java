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
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CCTCPClient<MessageType> extends CCClient<MessageType>{

	public CCTCPClient(CCNetCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, theIP, thePort);
	}
	
	public CCTCPClient(CCNetCodec<MessageType> theCodec){
		super(theCodec);
	}
	
	@Override
	public void createBootstrap(){
		Bootstrap myBootstrap = new Bootstrap();
		_myGroup = new NioEventLoopGroup();
		myBootstrap.group(_myGroup);
		myBootstrap.channel(NioSocketChannel.class);
		myBootstrap.remoteAddress(new InetSocketAddress(_myIP, _myPort));
		myBootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder(), new CCClientHandler());
			}
		});
		
		try {
			_myFuture = myBootstrap.connect();
			scheduleReconnect();
			
			_myFuture.sync();
			_myIsConnected = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		CCTCPClient<String> myClient = new CCTCPClient<String>(new CCNetStringCodec(),"127.0.0.1", 12345);
		myClient.connect();
		myClient.write("texone");
		myClient.write("textwo");
	}
}
