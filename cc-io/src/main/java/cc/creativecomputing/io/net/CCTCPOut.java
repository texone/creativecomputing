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
import java.nio.BufferOverflowException;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCTCPOut<MessageType> extends CCNetOut<SocketChannel, MessageType> {

	public CCTCPOut(CCNetPacketCodec<MessageType> theCodec, String theTargetAddress, int theTargetPort) {
		super(theCodec, theTargetAddress, theTargetPort);
	}

	public CCTCPOut(
		CCNetPacketCodec<MessageType> theCodec, 
		String theLocalAddress, int theLocalPort, 
		String theTargetAddress, int theTargetPort
	) {
		super(theCodec, theLocalAddress, theLocalPort, theTargetAddress, theTargetPort);
	}
	
	protected CCTCPOut(CCNetPacketCodec<MessageType> theCodec, SocketChannel theChannel) {
		super(theCodec, new InetSocketAddress(theChannel.socket().getLocalAddress(), theChannel.socket().getLocalPort()));

		_myChannel = theChannel;

		if (_myChannel.isConnected())
			target(_myChannel.socket().getRemoteSocketAddress());
	}

	@Override
	public void connect() {
		synchronized (_myBufferSyncObject) {
			if ((_myChannel != null) && !_myChannel.isOpen()) {
				_myChannel = null;
			}
			try {
				if (_myChannel == null) {
					_myChannel = SocketChannel.open();
					_myChannel.socket().bind(_myLocalAddress);
				}
				if (!_myChannel.isConnected()) {
					_myChannel.connect(_myTarget);
				}
			} catch (IOException e) {
				throw new CCNetException(e);
			}
		}
	}

	@Override
	public boolean isConnected() {
		synchronized (_myBufferSyncObject) {
			return ((_myChannel != null) && _myChannel.isConnected());
		}
	}

	@Override
	public void send(MessageType thePacket, SocketAddress theTarget) {
		synchronized (_myBufferSyncObject) {
			if ((theTarget != null) && !theTarget.equals(this._myTarget))
				throw new CCNetException("Not bound to address : " + theTarget);

			send(thePacket);
		}
	}

	@Override
	public void send(MessageType theMessage) {
		final int len;

		try {
			synchronized (_myBufferSyncObject) {
				// if( _myChannel == null ) throw new NotYetConnectedException();
				if (_myChannel == null)
					throw new CCNetException("Channel not connected");

				checkBuffer();
				_myByteBuffer.clear();
				_myByteBuffer.position(4);
				_myCodec.encode(theMessage, _myByteBuffer);
				len = _myByteBuffer.position() - 4;
				_myByteBuffer.flip();
				_myByteBuffer.putInt(0, len);
				_myChannel.write(_myByteBuffer);
			}
		} catch (BufferOverflowException e1) {
			throw new CCNetException("Buffer Overflow or Underflow " + theMessage.getClass().getName());
		} catch (IOException e) {
			throw new CCNetException(e);
		}
	}
}
