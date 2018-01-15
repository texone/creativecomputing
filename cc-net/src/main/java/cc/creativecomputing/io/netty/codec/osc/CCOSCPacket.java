package cc.creativecomputing.io.netty.codec.osc;

/**
 * The unit of transmission of OSC is an OSC Packet. Any application that sends
 * OSC Packets is an OSC Client; any application that receives OSC Packets is an
 * OSC Server.
 * 
 */
public interface CCOSCPacket {
	void reset();
}