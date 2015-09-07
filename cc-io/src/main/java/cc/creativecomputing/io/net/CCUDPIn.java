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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCUDPIn<MessageType> extends CCNetIn <DatagramChannel, MessageType>{

	public CCUDPIn(CCNetPacketCodec<MessageType> theCodec, String theIP, int thePort){
		super(theCodec, new InetSocketAddress(theIP, thePort));
	}
	
	public CCUDPIn(CCNetPacketCodec<MessageType> theCodec, int thePort){
		super(theCodec, new InetSocketAddress("0.0.0.0", thePort));
	}

	protected void setChannel(DatagramChannel theChannel) throws IOException {
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCNetException("Cannot be performed while channel is active");

			_myChannel = theChannel;
			if (!_myChannel.isBlocking()) {
				_myChannel.configureBlocking(true);
			}
			if (_myChannel.isConnected())
				throw new CCNetException("channel must not be connected");
		}
	}

	@Override
	public void target(SocketAddress theTarget) {
		_myTarget = theTarget;
	}

	@Override
	public void connect() {
		synchronized (_myGeneralSync) {
			if (_myIsListening)
				throw new CCNetException("Cannot be performed while channel is active");

			if ((_myChannel != null) && !_myChannel.isOpen()) {
				_myChannel = null;
			}
			if (_myChannel == null) {
				try {
					final DatagramChannel newCh = DatagramChannel.open();

					CCLog.info("BIND IN" + _myLocalAddress.toString());
					newCh.socket().bind(_myLocalAddress);
					setChannel(newCh);
				} catch (Exception e) {
					throw new CCNetException(e);
				}
			}
		}
	}

	@Override
	public boolean isConnected() {
		synchronized (_myGeneralSync) {
			return ((_myChannel != null) && _myChannel.isOpen());
		}
	}

	@Override
	protected void closeChannel() throws IOException {
		if (_myChannel != null) {
			try {
				_myChannel.close();
			} finally {
				_myChannel = null;
			}
		}
	}

	/**
	 * This is the body of the listening thread
	 */
	@Override
	public void run() {
		SocketAddress sender;

		checkBuffer();

		try {
			listen: while (_myIsListening) {
				try {
					_myByteBuffer.clear();
					sender = _myChannel.receive(_myByteBuffer);

					if (!_myIsListening)
						break listen;
					if (sender == null)
						continue listen;
					if ((_myTarget != null) && !_myTarget.equals(sender))
						continue listen;

					flipDecodeDispatch(sender);
				} catch (ClosedChannelException e1) { // bye bye, we have to quit
					if (_myIsListening) {
						CCLog.error("UDPIn.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
					}
					return;
				} catch (IOException e1) {
					if (_myIsListening) {
						CCLog.error("UDPIn.run : " + e1.getClass().getName() + " : " + e1.getLocalizedMessage());
					}
				}
			} // while( isListening )
		} finally {
			synchronized (_myThreadSync) {
				_myThread = null;
				_myThreadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	@Override
	protected void sendGuardSignal(){
		try {
			final DatagramSocket guard = new DatagramSocket();
			final DatagramPacket guardPacket = new DatagramPacket(new byte[0], 0);
			guardPacket.setSocketAddress(localAddress());
			guard.send(guardPacket);
			guard.close();
		} catch (Exception e) {
			throw new CCNetException(e);
		}
	}
}
