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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cc.creativecomputing.artnet.packets.ArtNetPacket;
import cc.creativecomputing.artnet.packets.ArtPollPacket;
import cc.creativecomputing.artnet.packets.ArtPollReplyPacket;
import cc.creativecomputing.artnet.packets.CCArtNetOpCode;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.logging.CCLog;

public class CCArtNetNodeDiscovery implements Runnable {

	public static final int POLL_INTERVAL = 10000;

	protected final CCArtNet _myArtNet;
	protected ConcurrentHashMap<InetAddress, ArtNetNode> _myDiscoveredNodes = new ConcurrentHashMap<InetAddress, ArtNetNode>();
	protected List<ArtNetNode> _myLastDiscovered = new ArrayList<ArtNetNode>();

	protected boolean _myIsActive = true;

	protected long _myDiscoveryInterval;

	private Thread _myDiscoveryThread;
	
	public final CCEventManager<ArtNetNode> newNodeEvents = new CCEventManager<>();
	public final CCEventManager<ArtNetNode> nodeDisconnectedEvents = new CCEventManager<>();
	public final CCEventManager<List<ArtNetNode>> discoveryCompletedEvents = new CCEventManager<>();
	public final CCEventManager<Throwable> discoveryFailedEvents = new CCEventManager<>();

	public CCArtNetNodeDiscovery(CCArtNet theArtNet) {
		_myArtNet = theArtNet;
		setInterval(POLL_INTERVAL);
	}
	
	private void discoverNode(ArtPollReplyPacket theReply) {
		InetAddress nodeIP = theReply.getIPAddress();
		ArtNetNode myNode = _myDiscoveredNodes.get(nodeIP);
		if (myNode == null) {
			CCLog.info("discovered new node: " + nodeIP);
			myNode = theReply.getNodeStyle().createNode();
			myNode.extractConfig(theReply);
			_myDiscoveredNodes.put(nodeIP, myNode);
			newNodeEvents.event(myNode);
		} else {
			myNode.extractConfig(theReply);
		}
		_myLastDiscovered.add(myNode);
	}

	public void artNetPacketReceived(ArtNetPacket thePacket) {
		CCLog.info(thePacket);
		if (thePacket.opCode != CCArtNetOpCode.POLL_REPLY) return;
        
		discoverNode((ArtPollReplyPacket) thePacket);
	}

	@Override
	public void run() {
		try {
			while (_myIsActive) {
				_myLastDiscovered.clear();
				ArtPollPacket poll = new ArtPollPacket();
				_myArtNet.broadcastPacket(poll);
				Thread.sleep(CCArtNet.ARTPOLL_REPLY_TIMEOUT);
				if (_myIsActive) {
					synchronized (nodeDisconnectedEvents) {
						for (ArtNetNode myNode : _myDiscoveredNodes.values()) {
							if (!_myLastDiscovered.contains(myNode)) {
								_myDiscoveredNodes.remove(myNode.getIPAddress());
								nodeDisconnectedEvents.event(myNode);
							}
						}

					}
					synchronized(discoveryCompletedEvents){
						discoveryCompletedEvents.event(new ArrayList<ArtNetNode>(_myDiscoveredNodes.values()));
					}

					Thread.sleep(_myDiscoveryInterval - CCArtNet.ARTPOLL_REPLY_TIMEOUT);
				}
			}
		} catch (InterruptedException e) {
			CCLog.warn("node discovery interrupted");
		}
	}

	public void setInterval(int interval) {
		_myDiscoveryInterval = Math.max(interval, CCArtNet.ARTPOLL_REPLY_TIMEOUT);
	}
	
	public void start(boolean theStart){
		if(theStart){
			start();
		}else{
			stop();
		}
	}

	public void start() {
		if (_myDiscoveryThread != null) {
			throw new CCArtNetException("discovery already started.");
		}
		_myDiscoveryThread = new Thread(this, getClass().getName());
		_myDiscoveryThread.setDaemon(true);
		_myDiscoveryThread.start();
	}

	public void stop() {
		_myIsActive = false;
	}

}