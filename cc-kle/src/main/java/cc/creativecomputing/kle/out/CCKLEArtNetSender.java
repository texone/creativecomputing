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

package cc.creativecomputing.kle.out;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

public class CCKLEArtNetSender implements ArtNetDiscoveryListener {

    private ArtNet artnet;

    private int sequenceID;
    
    private Map<String, ArtNetNode> _myNodeMap = new HashMap<>();
    
    private CCSequenceElements _myElements;
    
    private static class CCKLEUniverse{
    	private CCSequenceChannel[] _myChannels = new CCSequenceChannel[512];
    	int _myUniverse;
    	
    	CCKLEUniverse(int theUniverse){
    		_myUniverse = theUniverse;
    	}
    }
    
    private static class CCKLEInterface{
    	List<CCKLEUniverse> _myUniverses = new ArrayList<>();
    	
    	private String _myName;
    	
    	public CCKLEInterface(String theName){
    		_myName = theName;
    	}
    }
    
    private List<CCKLEInterface> _myInterfaces = new ArrayList<>();
    
    public CCKLEArtNetSender(CCSequenceElements theElements){
    	artnet = new ArtNet();
    	

        Map<String, Map<Integer,CCKLEUniverse>> myInterfaceMap = new HashMap<>();
    	
    	for(CCSequenceElement myElement:theElements){
    		for(CCSequenceChannel myChannel:myElement.channels()){
    			if(myChannel.universe() < 0)continue;
    			if(myChannel.channel() < 0)continue;
    			if(myChannel.interfaceName() == null)continue;
    			
    			if(!myInterfaceMap.containsKey(myChannel.interfaceName())){
    				myInterfaceMap.put(myChannel.interfaceName(), new HashMap<>());
    			}
    			Map<Integer, CCKLEUniverse> myUniverseMap = myInterfaceMap.get(myChannel.interfaceName());
    			if(!myUniverseMap.containsKey(myChannel.universe())){
    				myUniverseMap.put(myChannel.universe(), new CCKLEUniverse(myChannel.universe()));
    			}
    			myUniverseMap.get(myChannel.universe())._myChannels[myChannel.channel()] = myChannel;
    		}
    	}
    	
    	for(String myInterfaceName:myInterfaceMap.keySet()){
    		Map<Integer,CCKLEUniverse> myUniverseMap = myInterfaceMap.get(myInterfaceName);
    		CCKLEInterface myInterface = new CCKLEInterface(myInterfaceName);
    		myInterface._myUniverses.addAll(myUniverseMap.values());
    		Collections.sort(myInterface._myUniverses, (a,b) -> {return Integer.compare(a._myUniverse, b._myUniverse);});
    		_myInterfaces.add(myInterface);
    	}
    	
    	Collections.sort(_myInterfaces, (a,b)->{ return a._myName.compareTo(b._myName);});
    	
    	for(CCKLEInterface myInterface:_myInterfaces){
    		CCLog.info(myInterface._myName);
    		for(CCKLEUniverse myUniverse:myInterface._myUniverses){
        		CCLog.info(myUniverse._myUniverse);
    			for(int i = 0; i < myUniverse._myChannels.length;i++){
    				if(myUniverse._myChannels[i] != null)CCLog.info(myInterface._myName + " : " + myUniverse._myUniverse + " : " + " : " + i);
    			}
    		}
    	}
    }


    @Override
    public void discoveredNewNode(ArtNetNode node) {
    	_myNodeMap.put(node.getIPAddress().getHostAddress(), node);      
    }

    @Override
    public void discoveredNodeDisconnected(ArtNetNode node) {
    	_myNodeMap.remove(node.getIPAddress().getHostAddress());   
    }

    @Override
    public void discoveryCompleted(List<ArtNetNode> nodes) {
        System.out.println(nodes.size() + " nodes found:");
        for (ArtNetNode n : nodes) {
            System.out.println(n);
        }
    }

    @Override
    public void discoveryFailed(Throwable t) {
        System.out.println("discovery failed");
    }
    
    public void start(){
    	
    	try {
	    	artnet.start();
	        artnet.getNodeDiscovery().addListener(this);
	        artnet.startNodeDiscovery();
    	} catch (SocketException e) {
         	throw new AssertionError(e);
    	} catch (ArtNetException e) {
			e.printStackTrace();
		}
    }
    
    public void update(CCAnimator theAnimator){
    	for(CCKLEInterface myInterface:_myInterfaces){
    		if(!_myNodeMap.containsKey(myInterface._myName))continue;
    		
    		ArtNetNode myNode = _myNodeMap.get(myInterface._myName);
    		CCLog.info(myNode.getIPAddress().getHostAddress());
    		for(CCKLEUniverse myUniverse:myInterface._myUniverses){
    			if(myUniverse._myUniverse >= myNode.getNumPorts())continue;
    			
    			ArtDmxPacket dmx = new ArtDmxPacket();
    	        dmx.setUniverse(myNode.getSubNet(),myNode.getDmxOuts()[myUniverse._myUniverse]);
    	        dmx.setSequenceID(sequenceID % 255);
    	        byte[] buffer = new byte[512];
    	        for (int i = 0; i < buffer.length; i++) {
    	        	CCSequenceChannel myChannel = myUniverse._myChannels[i];
    	        	if(myChannel == null){
    	        		
    	        	}else{
//    	        		CCLog.info(myInterface._myName + " : " + myUniverse._myUniverse + " : " + i + " : " + myChannel.value());
    	        		buffer[i] = (byte) (myChannel.value() * 255);
    	        	}
    	        }
    	        dmx.setDMX(buffer, buffer.length);
    	        artnet.unicastPacket(dmx, myNode.getIPAddress());
    	        sequenceID++;
    		}
    	}
    }
    
}
