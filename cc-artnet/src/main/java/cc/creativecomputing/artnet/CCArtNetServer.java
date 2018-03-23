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

import cc.creativecomputing.artnet.packets.ArtNetPacket;
import cc.creativecomputing.artnet.packets.ArtPollPacket;
import cc.creativecomputing.artnet.packets.CCArtNetOpCode;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.logging.CCLog;
//import cc.creativecomputing.io.net.CCUDPServer;
import cc.creativecomputing.io.net.CCUDPServer;

public class CCArtNetServer extends CCUDPServer<ArtNetPacket> {
	
	public final CCEventManager<ArtNetPacket> packetBroadcastedEvents = new CCEventManager<>();
	public final CCEventManager<ArtNetPacket> packetReceivedEvents = new CCEventManager<>();
	public final CCEventManager<ArtNetPacket> packetUnicastedEvents = new CCEventManager<>();
	public final CCEventManager<CCArtNetServer> serverStartedEvents = new CCEventManager<>();
	public final CCEventManager<CCArtNetServer> serverStoppedEvents = new CCEventManager<>();

	public static final int DEFAULT_PORT = 0x1936;

	public CCArtNetServer() {
		this(DEFAULT_PORT, DEFAULT_PORT);
	}

	public CCArtNetServer(int thePort, int theTargetPort) {
		super(new CCArtNetCodec());
		_myLocalAddress.port(thePort);
		_myTargetAddress.port(theTargetPort);
		
		bufferSize(2048);
		events.add(myMessage -> {
			ArtNetPacket myPacket = (ArtNetPacket)myMessage.message;
			if (myPacket.opCode == CCArtNetOpCode.POLL) {
				sendArtPollReply(myMessage.address,(ArtPollPacket) myPacket);
			}
			packetReceivedEvents.event(myPacket);
		});
	}
	
	private void sendArtPollReply(SocketAddress address, ArtPollPacket myPacket) {
		// TODO Auto-generated method stub
		
	}

	public void ip(String theIP){
		_myLocalAddress.ip(theIP);
	}
	
	

	public void broadcastPacket(ArtNetPacket thePacket) {
		send(thePacket, new InetSocketAddress(_myLocalAddress.broadcast(), _myTargetAddress.port()));
		packetBroadcastedEvents.event(thePacket);
	}

	/**
	 * Sends the given packet to the specified IP address.
	 * 
	 * @param ap
	 * @param targetAdress
	 */
	public void unicastPacket(ArtNetPacket thePacket, InetAddress theTargetAdress) {
		send(thePacket, new InetSocketAddress(theTargetAdress, _myTargetAddress.port()));
		packetUnicastedEvents.event(thePacket);
	}
	
	public void connect() {
		CCLog.info(_myLocalAddress.port() + ":" + _myLocalAddress.ip());
		connect(_myLocalAddress.getAddress());
	}

    @Override
	public void connect(InetSocketAddress theAddress){
		super.connect(theAddress);
		serverStartedEvents.event(this);
	}

	@Override
	public void disconnect() {
		super.disconnect();
		serverStoppedEvents.event(this);
	}
}