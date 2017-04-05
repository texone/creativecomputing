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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CCTCPServer<MessageType> extends CCServer<MessageType>{
	
	public CCTCPServer(CCNetCodec<MessageType> theCodec, String theIP, int port) {
		super(theCodec, theIP, port);
	}
	
	public CCTCPServer(CCNetCodec<MessageType> theCodec) {
		super(theCodec);
	}
	
	@Override
	public void createBootstrap() throws Exception{
		final CCServerHandler myServerHandler = new CCServerHandler();
		_myGroup = new NioEventLoopGroup();
		
		ServerBootstrap myBootStrap = new ServerBootstrap();
		myBootStrap.group(_myGroup);
		myBootStrap.channel(NioServerSocketChannel.class);
		if(_myIP != null){
			myBootStrap.localAddress(new InetSocketAddress(_myIP, _myPort));
		}else{
			myBootStrap.localAddress(new InetSocketAddress(_myPort));
		}
		myBootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder());
				ch.pipeline().addLast(myServerHandler);
			}
		});
		_myFuture = myBootStrap.bind();
		_myFuture.sync();
		
		_myIsConnected = true;
			
		_myFuture.channel().closeFuture().await();
	}

	public static void main(String[] args) throws Exception {
		
		CCTCPServer<String> myServer = new CCTCPServer<String>(new CCNetStringCodec());
		myServer.connect();
	}

}
