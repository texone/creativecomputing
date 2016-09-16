/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCTCPIn<MessageType> extends CCNetIn<SocketChannel, MessageType> {

	public CCTCPIn(CCNetPacketCodec<MessageType> theCodec) {
		super(theCodec);
	}
	
	protected CCTCPIn(CCNetPacketCodec<MessageType> theCodec, SocketChannel theChannel) {
		super(theCodec);
		
		_myLocalAddress.ip(theChannel.socket().getLocalAddress().getHostName());
		_myLocalAddress.port(theChannel.socket().getLocalPort());

		_myChannel = theChannel;
	}

	
	@Override
	public SocketChannel createChannel(InetSocketAddress theAddress) {
		try {
			final SocketChannel myResult = SocketChannel.open();
			myResult.socket().bind(theAddress);
			return myResult;
		}catch(Exception e) {
			throw new CCNetException(e);
		}
	}
	
	@Override
	public void connectChannel(SocketChannel theChannel, InetSocketAddress theAddress) {
		try {
			theChannel.connect(theAddress);
		} catch (IOException e) {
			throw new CCNetException(e);
		}
	}
	
	@Override
	public boolean isChannelConnected(SocketChannel theChannel) {
		return theChannel.isOpen() && theChannel.isConnected();
	}

	@Override
	public void run() {

		checkBuffer();
		

		try {
			listen: while (_myIsConnected) {
				final SocketAddress sender = _myChannel.socket().getRemoteSocketAddress();
				int len, packetSize;
				try {
					_myByteBuffer.rewind().limit(4); // in TCP mode, first four bytes are packet size in bytes
					do {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1){
							break listen;
						}
					} while (_myByteBuffer.hasRemaining());

					_myByteBuffer.rewind();
					packetSize = _myByteBuffer.getInt();
					_myByteBuffer.rewind().limit(packetSize);

					while (_myByteBuffer.hasRemaining()) {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1){
							break listen;
						}
					}

					flipDecodeDispatch(sender);
					// flipDecodeDispatch( target );
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		} finally {
			synchronized (_myThreadSync) {
				_myThread = null;
				_myThreadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	/**
	 * @warning this calls socket().shutdownInput() to unblock the listening thread. unfortunately this cannot be
	 *          undone, so it's not possible to revive the receiver in TCP mode ;-( have to check for alternative
	 *          ways
	 */
	protected void sendGuardSignal() throws IOException {
		_myChannel.socket().shutdownInput();
	}
}
