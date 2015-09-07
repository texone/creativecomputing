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
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

/**
 * This class groups together a {@linkplain CCNetIn} and a {@linkplain CCNetOut}, allowing bidirectional network 
 * communication from the perspective of a client. It simplifies the need to use several objects by uniting their functionality.
 * 
 * @see CCNetOut
 * @see CCNetIn
 * @see CCNetServer
 * 
 * @author Christian Riekoff
 */
public class CCNetClient<ChannelType extends SelectableChannel, MessageType> extends CCNetBidirectionalChannel<ChannelType, MessageType> {
	private final CCNetIn<ChannelType, MessageType> _myIn;
	private final CCNetOut<ChannelType, MessageType> _myOut;

	protected CCNetClient(
		CCNetPacketCodec<MessageType> theCodec, 
		InetSocketAddress theAddress,
		CCNetIn<ChannelType, MessageType> theIn,
		CCNetOut<ChannelType, MessageType> theOut
	) {
		super(theCodec, theAddress);
		_myIn = theIn;
		_myOut = theOut;
	}
	
	/**
	 * Specifies the client's target address, that is the address of the server to talk to. You should call this method
	 * only once and you must call it before starting the client or sending messages.
	 * 
	 * @param theTargetIP the address of the server
	 * @param theTargetPort the port of the server
	 * 
	 * @see InetSocketAddress
	 */
	public void target(String theTargetIP, int theTargetPort){
		target(new InetSocketAddress(theTargetIP, theTargetPort));
	}

	/**
	 * Specifies the client's target address, that is the address of the server to talk to. You should call this method
	 * only once and you must call it before starting the client or sending messages.
	 * 
	 * @param theTarget the address of the server. Usually you construct an appropriate <code>InetSocketAddress</code>
	 * 
	 * @see InetSocketAddress
	 */
	public void target(InetSocketAddress theTarget) {
		_myIn.target(theTarget);
		_myOut.target(theTarget);
	}

	/**
	 * Initializes network channel (if necessary) and establishes connection for transports requiring connectivity (e.g.
	 * TCP). Do not call this method when the client is already connected. Note that {@linkplain #start()} implicitly calls
	 * {@linkplain #connect()} if necessary, so usually you will not need to call {@linkplain #connect()} yourself.
	 * 
	 * @see #isConnected()
	 * @see #start()
	 */
	@Override
	public void connect()  {
		_myOut.connect();
	}

	/**
	 * Queries the connection state of the client.
	 * 
	 * @return <code>true</code> if the client is connected, <code>false</code> otherwise. For transports that do not
	 *         use connectivity (e.g. UDP) this returns <code>false</code>, if the underlying {@linkplain DatagramChannel}
	 *         has not yet been created.
	 * 
	 * @see #connect()
	 */
	@Override
	public boolean isConnected() {
		return _myOut.isConnected();
	}

	/**
	 * Sends a message to the target network address. Make sure that the client's target has
	 * been specified before by calling {@linkplain #target(String, int)}
	 * 
	 * @param theMessage the message to send
	 * 
	 * @throws NullPointerException for a UDP client if the target has not been specified
	 * 
	 * @see #setTarget(SocketAddress )
	 */
	public void send(MessageType theMessage) {
		_myOut.send(theMessage);
	}

	/**
	 * Registers a listener that gets informed about incoming messages. You can call this both when the client is active
	 * or inactive.
	 * 
	 * @param theListener the listener to register
	 */
	public void addListener(CCNetListener<MessageType> theListener) {
		_myIn.addListener(theListener);
	}

	/**
	 * Unregisters a listener that gets informed about incoming messages
	 * 
	 * @param theListener the listener to remove from the list of notified objects.
	 */
	public void removeListener(CCNetListener<MessageType> theListener) {
		_myIn.removeListener(theListener);
	}

	/**
	 * Starts the client. This calls {@linkplain #connect()} if the transport requires connectivity (e.g. TCP) and the
	 * channel is not yet connected. It then tells the underlying OSC receiver to start listening.
	 * 
	 * @warning in the current version, it is not possible to &quot;revive&quot; clients after the server has closed the
	 *          connection. Also it's not possible to start a TCP client more than once. This might be possible in a
	 *          future version.
	 */
	public void start() {
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

	/**
	 * Queries whether the client was activated or not. A client is activated by calling its {@linkplain #start()} method
	 * and deactivated by calling {@linkplain #stop()}.
	 * 
	 * @return <code>true</code> if the client is active (connected and listening), <code>false</code> otherwise.
	 * 
	 * @see #start()
	 * @see #stop()
	 */
	@Override
	public boolean isActive() {
		return _myIn.isListening();
	}

	/**
	 * Stops the client from listening.
	 */
	public void stop()  {
		_myIn.stopListening();
	}

	/**
	 * Adjusts the buffer size for messages (both for sending and receiving). This is the maximum size a message 
	 * can grow to. The initial buffer size is <code>DEFAULTBUFSIZE</code>. Do not call this method
	 * while the client is active!
	 * 
	 * @param theSize the new size in bytes.
	 * 
	 * @see #isActive()
	 * @see #bufferSize()
	 */
	@Override
	public void bufferSize(int theSize) {
		_myBufferSize = theSize;
		_myIn.bufferSize(theSize);
		_myOut.bufferSize(theSize);
	}

	/**
	 * Destroys the client and frees resources associated with it. This automatically stops the client and closes the
	 * networking channel. Do not use this client instance any more after calling dispose.
	 */
	@Override
	public void dispose() {
		_myIn.dispose();
		_myOut.dispose();
	}
}
