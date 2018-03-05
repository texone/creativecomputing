
package cc.creativecomputing.kle.out;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.math.CCMath;

public class CCKLEArtNetSender extends CCKLESender implements ArtNetDiscoveryListener {

	private ArtNet artnet;

	private int sequenceID;

	private Map<String, ArtNetNode> _myNodeMap = new HashMap<>();

	public CCKLEArtNetSender(List<CCKleEffectable> theElements) {
		super(theElements);
		artnet = new ArtNet();

		

//		for (CCKLEInterface myInterface : _myInterfaces) {
//			CCLog.info(myInterface._myName);
//			for (CCKLEUniverse myUniverse : myInterface._myUniverses) {
//				CCLog.info(myUniverse._myUniverse);
//				for (int i = 0; i < myUniverse._myChannels.length; i++) {
//					if (myUniverse._myChannels[i] != null)
//						CCLog.info(myInterface._myName + " : " + myUniverse._myUniverse + " : " + " : " + i);
//				}
//			}
//		}
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
			CCLog.info(n);
		}
	}

	@Override
	public void discoveryFailed(Throwable t) {
		System.out.println("discovery failed");
	}

	public void start() {

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

	@Override
	public void send() {
		for (CCKLEInterface myInterface : _myInterfaces) {
			// if(!_myNodeMap.containsKey(myInterface._myName))continue;

			ArtNetNode myNode = _myNodeMap.get(myInterface.name);
//			if (myNode != null)
//				CCLog.info(myNode.getIPAddress().getHostAddress());
			for (CCKLEUniverse myUniverse : myInterface.universes) {
				if (myNode != null && myUniverse.universe >= myNode.getNumPorts())
					continue;

				ArtDmxPacket dmx = new ArtDmxPacket();
				if (myNode != null)
					dmx.setUniverse(myNode.getSubNet(), myNode.getDmxOuts()[myUniverse.universe]);
				dmx.setSequenceID(sequenceID % 255);
				byte[] buffer = new byte[512];
				for (int i = 0; i < buffer.length; i++) {
					CCKleChannel myChannel = myUniverse.channels[i];
					if (myChannel == null) {

					} else {
//						CCLog.info(myInterface._myName + " : " + myUniverse._myUniverse + " : " + i + " : " + myChannel.value() + " : " + ((byte) (myChannel.value() * 255)));
						buffer[i] = (byte) (CCMath.saturate(myChannel.value()) * 255);
					}
				}
				dmx.setDMX(buffer, buffer.length);
				if (myNode != null)
					artnet.unicastPacket(dmx, myNode.getIPAddress());
				sequenceID++;
			}
		}
	}

}
