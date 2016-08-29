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

import cc.creativecomputing.artnet.packets.ArtDmxPacket;

public class CCPollTest {

    public static void main(String[] args) {
        new CCPollTest().test();
    }

    private ArtNetNode _myNode;

    private int sequenceID;

    private void test() {
        CCArtNet myArtnet = new CCArtNet();
        try {
        	myArtnet.ip("10.0.0.100");
            myArtnet.connect();
            myArtnet.nodeDiscovery().newNodeEvents().add(myNode -> {
            	if (_myNode == null) {
                    _myNode = myNode;
                    System.out.println("found net lynx");
                }
            });
            myArtnet.nodeDiscovery().nodeDisconnectedEvents().add(myNode -> {
            	 System.out.println("node disconnected: " + myNode);
                 if (myNode == _myNode) {
                     _myNode = null;
                 }
            });
            myArtnet.nodeDiscovery().discoveryCompletedEvents().add(myNodes -> {
            	System.out.println(myNodes.size() + " nodes found:");
                for (ArtNetNode n : myNodes) {
                    System.out.println(n);
                }
            });
            myArtnet.nodeDiscovery().discoveryFailedEvents().add(t -> {System.out.println("discovery failed");});
            myArtnet.startNodeDiscovery();
            while (true) {
                if (_myNode != null) {
                    ArtDmxPacket dmx = new ArtDmxPacket();
                    dmx.setUniverse(_myNode.getSubNet(),_myNode.getDmxOuts()[0]);
                    dmx.setSequenceID(sequenceID % 255);
                    byte[] buffer = new byte[510];
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] = (byte) (Math.sin(sequenceID * 0.05 + i * 0.8) * 127 + 128);
                    }
                    dmx.setDMX(buffer, buffer.length);
                    myArtnet.unicastPacket(dmx, _myNode.getIPAddress());
                    dmx.setUniverse(_myNode.getSubNet(), _myNode.getDmxOuts()[1]);
                    myArtnet.unicastPacket(dmx, _myNode.getIPAddress());
                    sequenceID++;
                }
                Thread.sleep(30);
            }
        } catch (CCArtNetException e) {
        	throw new AssertionError(e);
        } catch (InterruptedException e) {
        	throw new AssertionError(e);
        }
    }
}
