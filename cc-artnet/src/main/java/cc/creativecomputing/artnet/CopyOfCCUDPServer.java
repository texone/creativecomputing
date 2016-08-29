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
package cc.creativecomputing.artnet;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.CCNetException;
import cc.creativecomputing.io.net.CCNetListener;
import cc.creativecomputing.io.net.CCNetMessage;
import cc.creativecomputing.io.net.CCNetServer;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CopyOfCCUDPServer<MessageType> extends CCNetServer<DatagramChannel, MessageType> implements Runnable{
	
	@SuppressWarnings("rawtypes")
	private final CCListenerManager<CCNetListener> _myEvents = CCListenerManager.create(CCNetListener.class);
	
	protected DatagramSocket socket;
	
	protected Thread _myServerThread;
	
	protected final Object _myBufferSyncObject = new Object(); // buffer (re)allocation
	protected final Object _myThreadSync = new Object(); // communication with receiver thread

	public CopyOfCCUDPServer(CCNetPacketCodec<MessageType> theCodec, String theTargetAddress, int theTargetPort){
		super(theCodec);
		_myTargetAddress.ip(theTargetAddress);
		_myTargetAddress.port(theTargetPort);
	}
	
	public CopyOfCCUDPServer(CCNetPacketCodec<MessageType> theCodec){
		super(theCodec);
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myEvents;
	}
	
	@Override
	public void connect(InetSocketAddress theAddress) {
		if (socket != null) {
			throw new CCNetException("Couldn't create server socket, server already running?");
		}
		synchronized (_myBufferSyncObject) {
			try {
				socket = new DatagramSocket(_myLocalAddress.getAddress());
			}catch(BindException be){
				throw new CCNetException("cant bind server to address:"  + theAddress.toString(), be);
			} catch (IOException e) {
				throw new CCNetException(e);
			}
			
			_myIsConnected = true;
			_myServerThread = new Thread(this, getClass().getName());
			_myServerThread.setDaemon(true);
			_myServerThread.start();
			
		}
	}

	@Override
	public void disconnect() {
		_myIsConnected = false;
		if (socket != null) {
			socket.close();
		
		}
	}

	@Override
	public void send(MessageType theMessage, SocketAddress theTarget){
		try {
			synchronized (_myBufferSyncObject) {
				// if( socket == null ) throw new NotYetConnectedException();
				if (socket == null)
					throw new CCNetException("Channel not connected");

				checkBuffer();
				_myByteBuffer.clear();
				_myCodec.encode(theMessage, _myByteBuffer);
				_myByteBuffer.flip();
				System.out.println(theTarget);
				
				
				DatagramPacket packet = new DatagramPacket(_myByteBuffer.array(), _myByteBuffer.limit(), _myTargetAddress.getAddress().getAddress(), _myTargetAddress.port());
				socket.send(packet);
			}
		} catch (BufferOverflowException e1) {
			throw new CCNetException("Buffer Overflow or Underflow" + theMessage.getClass().getName());
		} catch (IOException e) {
			throw new CCNetException(e);
		}
	}

	@Override
	public void dispose() {
		_myByteBuffer = null;
		if (socket != null) {
				socket.close();
			
			socket = null;
		}
	}

	@Override
	public void bufferSize(int theSize) {
		synchronized (_myBufferSyncObject) {
			if (_myBufferSize != theSize) {
				_myBufferSize = theSize;
				_myAllocateBuffer = true;
			}
		}
	}

	@Override
	public int bufferSize() {
		synchronized (_myBufferSyncObject) {
			return _myBufferSize;
		}
	}
	
	protected void checkBuffer() {
		synchronized (_myBufferSyncObject) {
			if (_myAllocateBuffer) {
				_myByteBuffer = ByteBuffer.allocate(_myBufferSize);
				_myAllocateBuffer = false;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void dispatchMessage(CCNetMessage<MessageType> theMessage) {
		_myEvents.proxy().messageReceived(theMessage);
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

	@Override
	public void run() {

		ByteBuffer receiveBuffer = ByteBuffer.allocate(_myBufferSize);
		byte[] receiveArray = new byte[_myBufferSize];
		DatagramPacket receivedPacket = new DatagramPacket(receiveArray, receiveArray.length);

		try {
			while (_myIsConnected) {
				socket.receive(receivedPacket);
				receiveBuffer = ByteBuffer.wrap(receivedPacket.getData());
				CCLog.info("RECEIVE");
				dispatchMessage(new CCNetMessage<MessageType>(_myCodec.decode(receiveBuffer), receivedPacket.getSocketAddress(), System.currentTimeMillis()));
			}
			socket.close();
			CCLog.info("server thread terminated.");

		} catch (SocketException e) {
			CCLog.info(e.getMessage());
		} catch (IOException e) {
			CCLog.info(e.getMessage());
		}
	}
}
