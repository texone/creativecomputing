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
import java.nio.BufferUnderflowException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

/**
 * A {@linkplain CCNetIn} manages reception of incoming messages. The input 
 * launches a listening {@linkplain Thread} when {@linkplain #startListening()} is called.
 * <p>
 * The {@linkplain CCNetIn} has methods for registering and unregistering
 * listeners that get informed about incoming messages. Filtering out specific
 * messages must be done by the listeners.
 * <p>
 * The listening thread is stopped using {@linkplain #stopListening()} method.
 * <P>
 * Note that you will most likely want to use preferably one
 * of {@linkplain CCNetClient}> or {@linkplain CCNetServer} over
 * {@linkplain CCNetIn}. Also note that {@linkplain CCNetOut}
 * <P>
 * Note that the input needs to be bound to a valid reachable
 * address, because {@linkplain #stopListening()} will be sending a terminating
 * message to this channel. 
 * <P>
 * Note that someone has reported trouble with the
 * <code>InetAddress.getLocalHost()</code> method on a machine that has no
 * proper IP configuration or DNS problems. In such a case when you need to
 * communicate only on this machine and not a network, use the loopback address
 * &quot;127.0.0.1&quot; as the filtering address or bind the socket to the loop
 * address manually before calling <code>new OSCReceiver()</code>.
 * 
 * @author Christian Riekoff
 * 
 * @see CCNetClient
 * @see CCNetServer
 * @see CCNetOut
 * 
 * @synchronization starting and stopping and listener registration is thread
 *                  safe. starting and stopping listening is thread safe but
 *                  must not be carried out in the receiver thread.
 */
public abstract class CCNetIn<ChannelType extends SelectableChannel, MessageType> extends CCNetChannel<ChannelType, MessageType> implements Runnable {
	@SuppressWarnings("rawtypes")
	private final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);
	protected Thread _myThread = null;

	// private Map map = null;

	protected final Object _myGeneralSync = new Object(); // mutual exclusion startListening /  stopListening
	protected final Object _myThreadSync = new Object(); // communication with receiver thread

	protected CCNetIn(CCNetPacketCodec<MessageType> theCodec) {
		super(theCodec);
	}

	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myEvents;
	}
	
	public abstract ChannelType createChannel(InetSocketAddress theAddress);
	
	public abstract void connectChannel(ChannelType theChannel, InetSocketAddress theAddress);

	/**
	 * Starts to wait for incoming messages. See the class constructor
	 * description to learn how connected and unconnected channels are handled.
	 * You should never modify the the channel's setup between the constructor
	 * and calling {@linkplain #startListening()}. This method will check the
	 * connection status of the channel, using {@linkplain #isConnected()} and
	 * establish the connection if necessary. Therefore, calling
	 * {@linkplain #connect()} prior to {@linkplain #startListening()} is not
	 * necessary.
	 * <p>
	 * To find out at which port we are listening, call
	 * {@linkplain #localAddress()}.
	 * <p>
	 * If the <code>OSCReceiver</code> is already listening, this method does
	 * nothing.
	 */
	@Override
	public void connect(InetSocketAddress theAddress) {
		synchronized (_myGeneralSync) {
			if (Thread.currentThread() == _myThread)
				throw new CCNetException("Method call not allowed in this thread");
			
			if (isConnected())
				throw new CCNetException("Channel is already connected");

			if ((_myChannel != null) && !_myChannel.isOpen()) 
				_myChannel = null;
			
			_myChannel = createChannel(theAddress);
			setChannel(_myChannel);
			connectChannel(_myChannel, theAddress);

			_myConnectedAddress = theAddress;
			
			_myIsConnected = true;
			_myThread = new Thread(this, "OSCReceiver");
			_myThread.setDaemon(true);
			_myThread.start();
		}
	}
	
	public abstract boolean isChannelConnected(ChannelType theChannel);

	/**
	 * Queries whether the {@linkplain CCNetIn} is listening or not.
	 */
	@Override
	public boolean isConnected() {
		synchronized (_myGeneralSync) {
			return ((_myChannel != null) && isChannelConnected(_myChannel));
		}
	}

	/**
	 * Stops waiting for incoming messages. This method returns when the
	 * receiving thread has terminated. To prevent deadlocks, this method
	 * cancels after five seconds, calling {@linkplain DatagramChannel#close()} on the datagram
	 * channel, which causes the listening thread to die because of a
	 * channel-closing exception.
	 * 
	 * @throws IOException
	 *             if an error occurs while shutting down
	 * 
	 * @throws IllegalStateException
	 *             when trying to call this method from within the OSC receiver
	 *             thread (which would obviously cause a loop)
	 */
	@Override
	public void disconnect() {
		synchronized (_myGeneralSync) {
			if (Thread.currentThread() == _myThread)
				throw new CCNetException("Method call not allowed in this thread");

			if (!_myIsConnected) return;
				
			_myIsConnected = false;
			if (_myThread == null || !_myThread.isAlive()) return;
				
			try {
				synchronized (_myThreadSync) {
					try {
						sendGuardSignal();
					} catch (IOException e) {
						throw new CCNetException(e);
					}
						// guard.send( guardPacket );
					_myThreadSync.wait(5000);
				}
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} finally {
				if ((_myThread != null) && _myThread.isAlive()) {
					try {
						CCLog.error("rude task killing ("+ this.hashCode() + ")");
						// ch.close(); // rude task killing
						closeChannel();
					} catch (IOException e3) {
						e3.printStackTrace();
						// System.err.println(
						// "OSCReceiver.stopListening 2: "+e3 );
					}
				}
				_myThread = null;
			}
			_myConnectedAddress = null;
		}
	}

	@Override
	public void bufferSize(int size) {
		if (_myIsConnected)
			throw new CCNetException("Cannot be performed while channel is connected");

		super.bufferSize(size);
	}

	@Override
	public void dispose() {
		disconnect();
		try {
			// ch.close();
			closeChannel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		_myByteBuffer = null;
	}

	protected abstract void sendGuardSignal() throws IOException;

	protected void setChannel(ChannelType theChannel){
		synchronized (_myGeneralSync) {
			if (_myIsConnected)
				throw new CCNetException("Cannot be performed while channel is active");

			_myChannel = theChannel;
			if (!_myChannel.isBlocking()) {
				try{
					_myChannel.configureBlocking(true);
				}catch(Exception e){
					throw new CCNetException(e);
				}
			}
		}
	}

	protected void closeChannel() throws IOException {
		if (_myChannel != null) {
			try {
				_myChannel.close();
			} finally {
				_myChannel = null;
			}
		}
	}

	protected static String debugTimeString() {
		return new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date());
	}

	protected void flipDecodeDispatch(SocketAddress sender) throws IOException {
		final MessageType myMessage;

		try {
			_myByteBuffer.flip();
			myMessage = _myCodec.decode(_myByteBuffer);
			dispatchMessage(new CCNetMessage<MessageType>(myMessage, sender, System.currentTimeMillis()));
		} catch (BufferUnderflowException e1) {
			if (_myIsConnected) {
				System.err.println(new CCNetException("Error while receiving OSC packet " + e1.toString()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void dispatchMessage(CCNetMessage<MessageType> theMessage) {
		_myEvents.proxy().messageReceived(theMessage);
	}

	
}
