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
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCTCPIn<MessageType> extends CCNetIn<SocketChannel, MessageType> {

	public CCTCPIn(CCNetPacketCodec<MessageType> theCodec, String theIP, int thePort) {
		super(theCodec, new InetSocketAddress(theIP, thePort));
	}
	
	protected CCTCPIn(CCNetPacketCodec<MessageType> theCodec, SocketChannel theChannel) {
		super(theCodec, new InetSocketAddress(theChannel.socket().getLocalAddress(), theChannel.socket().getLocalPort()));

		_myChannel = theChannel;
	}

	protected void setChannel(SocketChannel theChannel) throws IOException {
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCNetException("Cannot be performed while channel is active");

			_myChannel = theChannel;
			if (!_myChannel.isBlocking()) {
				_myChannel.configureBlocking(true);
			}
		}
	}

	@Override
	public void target(SocketAddress theTarget) {
		synchronized (_myGeneralSync) {
			if (isConnected())
				throw new AlreadyConnectedException();

			_myTarget = theTarget;
		}
	}

	@Override
	public void connect(){
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCNetException("Cannot be performed while channel is active");

			if ((_myChannel != null) && !_myChannel.isOpen()) {
				_myChannel = null;
			}
			try {
				if (_myChannel == null) {
					final SocketChannel newCh = SocketChannel.open();
					newCh.socket().bind(_myLocalAddress);
					_myChannel = newCh;
				}
				if (!_myChannel.isConnected()) {
					_myChannel.connect(_myTarget);
				}
			}catch(Exception e) {
				throw new CCNetException(e);
			}
		}
	}

	@Override
	public boolean isConnected() {
		synchronized (_myGeneralSync) {
			return ((_myChannel != null) && _myChannel.isConnected());
		}
	}

	protected void closeChannel() throws IOException {
		if (_myChannel != null) {
			try {
				// CCLog.error( "TCPOSCReceiver.closeChannel()" );
				_myChannel.close();
				// CCLog.error( "...ok" );
			} finally {
				_myChannel = null;
			}
		}
	}

	@Override
	public void run() {
		final SocketAddress sender = _myChannel.socket().getRemoteSocketAddress();
		int len, packetSize;

		checkBuffer();

		try {
			listen: while (_myIsListening) {
				try {
					_myByteBuffer.rewind().limit(4); // in TCP mode, first four bytes are packet size in bytes
					do {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1)
							break listen;
					} while (_myByteBuffer.hasRemaining());

					_myByteBuffer.rewind();
					packetSize = _myByteBuffer.getInt();
					_myByteBuffer.rewind().limit(packetSize);

					while (_myByteBuffer.hasRemaining()) {
						len = _myChannel.read(_myByteBuffer);
						if (len == -1)
							break listen;
					}

					flipDecodeDispatch(sender);
					// flipDecodeDispatch( target );
				} catch (IllegalArgumentException e1) { // thrown on illegal byteBuf.limit() calls
					if (_myIsListening) {
						// CCLog.error( new OSCException( OSCException.RECEIVE, e1.toString() ));
						final CCNetException e2 = new CCNetException("Error while receiving OSC packet " + e1.toString());
						CCLog.error("OSCReceiver.run : " + e2.getClass().getName() + " : " + e2.getLocalizedMessage());
					}
				} catch (ClosedChannelException e1) { // bye bye, we have to quit
					if (_myIsListening) {
						CCLog.error("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
					}
					return;
				} catch (IOException e1) {
					if (_myIsListening) {
						CCLog.error("OSCReceiver.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
						// CCLog.error( new OSCException( OSCException.RECEIVE, e1.toString() ));
					}
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
