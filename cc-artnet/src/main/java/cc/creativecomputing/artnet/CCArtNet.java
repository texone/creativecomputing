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
import java.net.UnknownHostException;

import cc.creativecomputing.artnet.packets.ArtNetPacket;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;

public class CCArtNet {

    protected static final long ARTPOLL_REPLY_TIMEOUT = 3000;

    protected static final String VERSION = "0001-20091119";

    @CCProperty(name = "server")
    protected CCArtNetServer _myServer;
    @CCProperty(name = "discovery")
    protected CCArtNetNodeDiscovery _myDiscovery;

    public CCArtNet() {
        CCLog.info("Art-Net v" + VERSION);
        init();
        nodeDiscovery();
    }
    
    public void ip(String theIP){
    	_myServer.ip(theIP);
    }

    public void broadcastPacket(ArtNetPacket thePacket) {
        _myServer.broadcastPacket(thePacket);
    }

    public CCArtNetNodeDiscovery nodeDiscovery() {
        if (_myDiscovery == null) {
            _myDiscovery = new CCArtNetNodeDiscovery(this);
            _myServer.packetReceivedEvents.add(_myDiscovery::artNetPacketReceived);
        }
        return _myDiscovery;
    }

    public void init() {
        _myServer = new CCArtNetServer();
        _myServer.packetReceivedEvents.add(myPacket -> {
        	CCLog.fine("packet received: " + myPacket.opCode);
        });
        _myServer.serverStartedEvents.add(myServer -> {CCLog.fine("server started callback");});
        _myServer.serverStoppedEvents.add(myServer -> {CCLog.info("server stopped");});
    }

    public void connect() {
        if (_myServer == null) {
            init();
        }
        _myServer.connect();
    }

    public void startNodeDiscovery() throws CCArtNetException {
    	nodeDiscovery().start();
    }

    public void disconnect() {
        if (_myDiscovery != null) {
            _myDiscovery.stop();
        }
        if (_myServer != null) {
            _myServer.disconnect();
        }
    }

    /**
     * Sends the given packet to the specified Art-Net node.
     * 
     * @param thePacket
     * @param theNode
     */
    public void unicastPacket(ArtNetPacket thePacket, ArtNetNode theNode) {
        _myServer.unicastPacket(thePacket, theNode.getIPAddress());
    }

    /**
     * Sends the given packet to the specified IP address.
     * 
     * @param thePacket
     * @param theAddress
     */
    public void unicastPacket(ArtNetPacket thePacket, InetAddress theAddress) {
        _myServer.unicastPacket(thePacket, theAddress);
    }

    /**
     * Sends the given packet to the specified IP address.
     * 
     * @param thePacket
     * @param theAddress
     */
    public void unicastPacket(ArtNetPacket thePacket, String theAddress) {
        try {
            _myServer.unicastPacket(thePacket, InetAddress.getByName(theAddress));
        } catch (UnknownHostException e) {
            CCLog.warn(e.getMessage(), e);
        }
    }
}