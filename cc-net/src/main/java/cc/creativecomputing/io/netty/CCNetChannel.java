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

import java.util.concurrent.TimeUnit;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;

public abstract class CCNetChannel<MessageType> {

	@SuppressWarnings("rawtypes")
	protected final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);

	@CCProperty(name = "ip")
	protected String _myIP = "127.0.0.1";
	@CCProperty(name = "port")
	protected int _myPort = 12345;
	@CCProperty(name = "reconnect time")
	protected int _myReconnectTime = 0;
	
	protected final CCNetCodec<MessageType> _myCodec;

	protected ChannelFuture _myFuture;
	protected Bootstrap _myBootstrap;
	protected EventLoopGroup _myGroup;
	
	public CCNetChannel(CCNetCodec<MessageType> theCodec, String theIP, int thePort){
		_myCodec = theCodec;
		_myIP = theIP;
		_myPort = thePort;
	}
	
	public CCNetChannel(CCNetCodec<MessageType> theCodec){
		_myCodec = theCodec;
	}
	
	/**
	 * The reconnect time in seconds, if this value is bigger than 0 
	 * a client or server tries to reconnect in case a connection is lost.
	 * @param theReconnectTime the time to wait till the next try to reconnect
	 */
	public void reconnectTime(int theReconnectTime){
		_myReconnectTime = theReconnectTime;
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myEvents;
	}
	
	public abstract void write(MessageType theMessage);

	public abstract void createBootstrap() throws Exception;
	
	public boolean isConnected() {
		if(_myFuture == null)return false;
		if(_myFuture.channel() == null)return false;
		if(!_myFuture.channel().isActive())return false;
		return true;
	}
	
	public abstract void connect();
	
	@CCProperty(name = "connect")
	void connect(boolean theConnect){
		if(theConnect)connect();
		else close();
	}
	
	public void scheduleReconnect(EventLoop theLoop){
		if(_myReconnectTime <= 0)return;
		_myFuture.addListener((channelFuture) -> {
			if (_myFuture.isSuccess())return;
		
			theLoop.schedule(() -> {
				try{
					createBootstrap();
				}catch(Exception e){
					throw new RuntimeException(e);
				}
			}, _myReconnectTime, TimeUnit.SECONDS);
		});
	}
	
	public void scheduleReconnect(){
		scheduleReconnect(_myFuture.channel().eventLoop());
	}
	
	public void close(){
		_myFuture.channel().close();
		_myGroup.shutdownGracefully();
	}
}
