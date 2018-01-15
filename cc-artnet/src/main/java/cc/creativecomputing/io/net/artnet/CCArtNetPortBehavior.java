package cc.creativecomputing.io.net.artnet;

/**
 * An enum for setting the behaviour of a port. Ports can either input data (DMX
 * -> ArtNet) or output (ArtNet -> DMX) data.
 */
public enum CCArtNetPortBehavior {
	/** < Enables the input for this port */
	ARTNET_ENABLE_INPUT(0x40), 
	/** < Enables the output for this port */
	ARTNET_ENABLE_OUTPUT(0x80);
	
	public final int id;

	CCArtNetPortBehavior(int theID) {
		id = theID;
	}
}