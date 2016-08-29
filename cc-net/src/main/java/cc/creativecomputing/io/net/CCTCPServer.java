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
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

public class CCTCPServer<MessageType> extends CCNetServer<ServerSocketChannel, MessageType> implements Runnable, CCNetListener<MessageType> {
	private final Map<SocketAddress, CCNetIn<SocketChannel, MessageType>> _myInMap = new HashMap<SocketAddress, CCNetIn<SocketChannel, MessageType>>();
	private final Map<SocketAddress, CCNetOut<SocketChannel, MessageType>> _myOutMap = new HashMap<SocketAddress, CCNetOut<SocketChannel, MessageType>>(); 

	@SuppressWarnings("rawtypes")
	private final CCListenerManager<CCNetListener> _myListener = CCListenerManager.create(CCNetListener.class);
	private Thread thread = null;
	private final Object startStopSync = new Object(); // mutual exclusion startListening / stopListening
	private final Object threadSync = new Object(); // communication with receiver thread

	private boolean _myIsListening = false;


	public CCTCPServer(CCNetPacketCodec<MessageType> theCodec, String theLocalAdress, int thePort){
		super(theCodec);
		CCLog.info(theLocalAdress + ":" + thePort);
		_myLocalAddress.ip(theLocalAdress);
		_myLocalAddress.port(thePort);

		
	}
	
	public CCTCPServer(CCNetPacketCodec<MessageType> theCodec){
		super(theCodec);
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCNetListener> events(){
		return _myListener;
	}

	@Override
	public void disconnect() {
		synchronized (startStopSync) {
			if (Thread.currentThread() == thread)
				throw new IllegalStateException("Cannot call stopListening() in the server body thread");

			if (_myIsListening) {
				_myIsListening = false;
				if ((thread != null) && thread.isAlive()) {
					try {
						synchronized (threadSync) {
							final SocketChannel guard;
							guard = SocketChannel.open();
							guard.connect(_myChannel.socket().getLocalSocketAddress());
							guard.close();
							threadSync.wait(5000);
						}
					} catch (InterruptedException e2) {
						CCLog.error(e2.getLocalizedMessage());
					} catch (IOException e1) {
						CCLog.error("TCPServerBody.stopListening : " + e1);
						throw new CCNetException(e1);
					} finally {
						// guard = null;
						if ((thread != null) && thread.isAlive()) {
							try {
								CCLog.error("TCPServerBody.stopListening : rude task killing (" + this.hashCode() + ")");
								_myChannel.close(); // rude task killing
							} catch (IOException e3) {
								CCLog.error("TCPServerBody.stopListening 2: " + e3);
							}
						}
						thread = null;
						stopAll();
					}
				}
			}
		}
	}

	@Override
	public boolean isConnected() {
		return _myIsListening;
	}

	@Override
	public void send(MessageType theMessage, SocketAddress theTarget) {
		final CCNetOut<?,MessageType> myOut;

		synchronized (_myBufferSyncObject) {
			try{
				myOut = _myOutMap.get(theTarget);
				
				if (myOut == null)
					throw new NotYetConnectedException();
				
				myOut.send(theMessage);
			}catch(CCNetException e){
				_myOutMap.remove(theTarget);
				_myInMap.remove(theTarget);
			}
		}
		
	}
	
	/**
	 * Sends a message to all listening clients. Particularly, in <code>TCP</code> mode, trying to send to a client which is not connected will
	 * throw an exception.
	 * 
	 * @param theMessage the message to send
	 */
	public void send(MessageType theMessage) {
		for(SocketAddress myTarget:_myOutMap.keySet()){
			send(theMessage, myTarget);
		}
	}

	@Override
	public void dispose() {
		disconnect();

		try {
			_myChannel.close();
		} catch (IOException e1) {
			new CCNetException(e1);
		}
	}

	private void stopAll() {
		synchronized (_myBufferSyncObject) {
			for (CCNetIn<SocketChannel, MessageType> myIn: _myInMap.values()) {
				myIn.dispose();
			}
			_myInMap.clear();
			for (CCNetOut<SocketChannel, MessageType> myOut:_myOutMap.values()) {
				myOut.dispose();
			}
			_myOutMap.clear();
		}
	}

	@Override
	public void bufferSize(int size) {
		synchronized (_myBufferSyncObject) {
			_myBufferSize = size;

			for (CCNetIn<SocketChannel, MessageType> myIn: _myInMap.values()) {
				myIn.bufferSize(size);
			}
			for (CCNetOut<SocketChannel, MessageType> myOut:_myOutMap.values()) {
				myOut.bufferSize(size);
			}
		}
	}
	
	@Override
	public void run() {
		SocketAddress mySender;
		SocketChannel myChannel;
		CCNetIn<SocketChannel, MessageType> myIn;
		CCNetOut<SocketChannel, MessageType> myOut;

		try {
			listen: while (_myIsListening) {
				try {
					myChannel = _myChannel.accept();
					CCLog.info(myChannel.socket().getPort() + ":" + myChannel.socket().getInetAddress());
					myChannel.configureBlocking(false);
					if (!_myIsListening)
						break listen;

					mySender = myChannel.socket().getRemoteSocketAddress();

					synchronized (_myBufferSyncObject) {
						myIn = new CCTCPIn<MessageType>(_myCodec, myChannel);
						myIn.bufferSize(_myBufferSize);
						_myInMap.put(mySender, myIn);
						myOut = new CCTCPOut<MessageType>(_myCodec,myChannel);
						myOut.bufferSize(_myBufferSize);
						// CCLog.error ("put "+mySender );
						_myOutMap.put(mySender, myOut);
						myIn.events().add(this);
						myIn.connect(_myLocalAddress.getAddress());
					}
				} catch (Exception e) { // bye bye, we have to quit
					e.printStackTrace();
				}
			} // while( isListening )
		} finally {
			synchronized (threadSync) {
				thread = null;
				threadSync.notifyAll(); // stopListening() might be waiting
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void messageReceived(CCNetMessage<MessageType> theMessage) {
		_myListener.proxy().messageReceived(theMessage);
	}

	@Override
	public void connect(InetSocketAddress theAddress) {
		synchronized (startStopSync) {
			try{
				_myChannel = ServerSocketChannel.open();
				_myChannel.socket().bind(_myLocalAddress.getAddress());
				
				if (Thread.currentThread() == thread)
					throw new IllegalStateException("Cannot call startListening() in the server body thread");
	
				if (_myIsListening && ((thread == null) || !thread.isAlive())) {
					_myIsListening = false;
				}
				if (!_myIsListening) {
					_myIsListening = true;
					thread = new Thread(this, "TCPServerBody");
					thread.setDaemon(true);
					thread.start();
				}
			}catch(Exception e){
				System.out.println(_myLocalAddress.getAddress());
				e.printStackTrace();
			}
		}
	}
}
