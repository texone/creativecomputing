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
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

class CCUDPServer<MessageType> extends CCNetServer<DatagramChannel, MessageType> {

	private final CCUDPIn<MessageType> _myIn;
	private final CCUDPOut<MessageType> _myOut;

	public CCUDPServer(CCNetPacketCodec<MessageType> theCodec, int thePort, String theTargetAddress, int theTargetPort){
		super(theCodec, new InetSocketAddress(theTargetAddress, thePort));

		_myIn = new CCUDPIn<MessageType>(theCodec, thePort);
		_myOut = new CCUDPOut<MessageType>(theCodec, theTargetAddress, theTargetPort);
	}

	@Override
	public void addNetListener(CCNetListener<MessageType> listener) {
		_myIn.addListener(listener);
	}

	@Override
	public void removeNetListener(CCNetListener<MessageType> listener) {
		_myIn.removeListener(listener);
	}

	@Override
	public void start()  {
		if (!_myOut.isConnected()) {
			_myOut.connect();
			try {
				_myIn.setChannel(_myOut.channel());
			} catch (IOException e) {
				throw new CCNetException(e);
			}
		}
		_myIn.startListening();
	}

	@Override
	public void stop(){
		_myIn.stopListening();
	}

	@Override
	public boolean isActive() {
		return _myIn.isListening();
	}

	@Override
	public void send(MessageType p, SocketAddress target){
		_myOut.send(p, target);
	}

	@Override
	public void dispose() {
		_myIn.dispose();
		_myOut.dispose();
	}

	@Override
	public void bufferSize(int size) {
		_myIn.bufferSize(size);
		_myOut.bufferSize(size);
	}

	@Override
	public int bufferSize() {
		return _myIn.bufferSize();
	}

	@Override
	public void connect() {
	}

	@Override
	public boolean isConnected() {
		return false;
	}
}
