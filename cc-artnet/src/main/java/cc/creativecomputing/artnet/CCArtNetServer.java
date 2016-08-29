/*
 * This file is part of artnet4j.
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * artnet4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * artnet4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with artnet4j. If not, see <http://www.gnu.org/licenses/>.
 */

package cc.creativecomputing.artnet;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import cc.creativecomputing.artnet.packets.ArtNetPacket;
import cc.creativecomputing.artnet.packets.ArtPollPacket;
import cc.creativecomputing.artnet.packets.CCArtNetOpCode;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
//import cc.creativecomputing.io.net.CCUDPServer;
import cc.creativecomputing.io.net.CCUDPServer;

public class CCArtNetServer extends CCUDPServer<ArtNetPacket> {
	
	public static interface CCArtNetServerPacketBroadcastedListener {
	    void artNetPacketBroadcasted(ArtNetPacket thePacket);
	}
	
	public static interface CCArtNetServerPacketReceivedListener {
	    void artNetPacketReceived(ArtNetPacket thePacket);
	}
	
	public static interface CCArtNetServerPacketUnicastedListener {
	    void artNetPacketUnicasted(ArtNetPacket thePacket);
	}
	
	public static interface CCArtNetServerStartedListener {
	    void artNetServerStarted(CCArtNetServer theServer);
	}
	
	public static interface CCArtNetServerStoppedListener {
	    void artNetServerStopped(CCArtNetServer theServer);
	}
	
	protected final CCListenerManager<CCArtNetServerPacketBroadcastedListener> _myPacketBroadcastedEvents = CCListenerManager.create(CCArtNetServerPacketBroadcastedListener.class);
	protected final CCListenerManager<CCArtNetServerPacketReceivedListener> _myPacketReceivedEvents = CCListenerManager.create(CCArtNetServerPacketReceivedListener.class);
	protected final CCListenerManager<CCArtNetServerPacketUnicastedListener> _myPacketUnicastedEvents = CCListenerManager.create(CCArtNetServerPacketUnicastedListener.class);
	protected final CCListenerManager<CCArtNetServerStartedListener> _myServerStartedEvents = CCListenerManager.create(CCArtNetServerStartedListener.class);
	protected final CCListenerManager<CCArtNetServerStoppedListener> _myServerStoppedEvents = CCListenerManager.create(CCArtNetServerStoppedListener.class);

	public static final int DEFAULT_PORT = 0x1936;

	public CCArtNetServer() {
		this(DEFAULT_PORT, DEFAULT_PORT);
	}

	public CCArtNetServer(int thePort, int theTargetPort) {
		super(new CCArtNetCodec());
		_myLocalAddress.port(thePort);
		_myTargetAddress.port(theTargetPort);
		
		bufferSize(2048);
		events().add(myMessage -> {
			ArtNetPacket myPacket = (ArtNetPacket)myMessage.message;
			if (myPacket.opCode == CCArtNetOpCode.POLL) {
				sendArtPollReply(myMessage.address,(ArtPollPacket) myPacket);
			}
			_myPacketReceivedEvents.proxy().artNetPacketReceived(myPacket);
		});
	}
	
	public void ip(String theIP){
		_myLocalAddress.ip(theIP);
	}
	
	public CCListenerManager<CCArtNetServerPacketBroadcastedListener> packetBroadcastedEvents() {
		return _myPacketBroadcastedEvents;
	}

	public CCListenerManager<CCArtNetServerPacketReceivedListener> packetReceivedEvents() {
		return _myPacketReceivedEvents;
	}

	public CCListenerManager<CCArtNetServerPacketUnicastedListener> packetUnicastedEvents() {
		return _myPacketUnicastedEvents;
	}

	public CCListenerManager<CCArtNetServerStartedListener> serverStartedEvents() {
		return _myServerStartedEvents;
	}

	public CCListenerManager<CCArtNetServerStoppedListener> serverStoppedEvents() {
		return _myServerStoppedEvents;
	}

	public void broadcastPacket(ArtNetPacket thePacket) {
		send(thePacket, new InetSocketAddress(_myLocalAddress.broadcast(), _myTargetAddress.port()));
		_myPacketBroadcastedEvents.proxy().artNetPacketBroadcasted(thePacket);
	}

	/**
	 * Sends the given packet to the specified IP address.
	 * 
	 * @param ap
	 * @param targetAdress
	 */
	public void unicastPacket(ArtNetPacket thePacket, InetAddress theTargetAdress) {
		send(thePacket, new InetSocketAddress(theTargetAdress, _myTargetAddress.port()));
		_myPacketUnicastedEvents.proxy().artNetPacketUnicasted(thePacket);
	}

	private void sendArtPollReply(SocketAddress inetAddress, ArtPollPacket packet) {
		// TODO send reply with self description
	}
	
	public void connect() {
		CCLog.info(_myLocalAddress.port() + ":" + _myLocalAddress.ip());
		connect(_myLocalAddress.getAddress());
	};

	@Override
	public void connect(InetSocketAddress theAddress){
		super.connect(theAddress);
		_myServerStartedEvents.proxy().artNetServerStarted(this);
	}

	@Override
	public void disconnect() {
		super.disconnect();
		_myServerStoppedEvents.proxy().artNetServerStopped(this);
	}
}