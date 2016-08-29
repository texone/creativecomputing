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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

/**
 * This class dynamically groups together a {@linkplain CCNetIn} and a {@linkplain CCNetIn}, allowing bidirectional network communication from
 * the perspective of a server. It simplifies the need to use several objects by uniting their functionality, and by
 * automatically establishing child connections.
 * <P>
 * In <code>UDP</code> mode, simply one receiver and transmitter are handling all the communication. In <code>TCP</code>
 * mode, a {@linkplain SocketChannel} is set up to wait for incoming connection requests. Requests are satisfied
 * by opening a new receiver and transmitter for each connection.
 * 
 * @see CCNetClient
 * 
 * @author christian riekoff
 */
public abstract class CCNetServer<ChannelType extends SelectableChannel, MessageType> extends CCNetChannel<ChannelType, MessageType> {
	
	/**
	 * @param theAttributes
	 */
	public CCNetServer(CCNetPacketCodec<MessageType> theCodec) {
		super(theCodec);
	}

	/**
	 * Sends a message to the given network address. The address should correspond to one of the
	 * connected clients. Particularly, in <code>TCP</code> mode, trying to send to a client which is not connected will
	 * throw an exception.
	 * 
	 * @param theMessage the message to send
	 * @param theTarget the target address to send the packet to
	 */
	public abstract void send(MessageType theMessage, SocketAddress theTarget);

	@SuppressWarnings("rawtypes")
	public abstract CCListenerManager<CCNetListener> events();

	/**
	 * Starts the server. The server becomes attentive to requests for connections from clients, starts to receive
	 * messages and is able to reply back to connected clients.
	 */
	@Override
	public abstract void connect(InetSocketAddress theAddress);

	/**
	 * Checks whether the server is active (was started) or not (is stopped).
	 * 
	 * @return <code>true</code> if the server is active, <code>false</code> otherwise
	 */
	@Override
	public boolean isConnected(){
		return _myIsConnected;
	}

	/**
	 * Stops the server. For <code>TCP</code> mode, this implies that all client connections are closed. Stops listening
	 * for incoming OSC traffic.
	 */
	@Override
	public abstract void disconnect();

	public abstract void bufferSize(int size);
}
