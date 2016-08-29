

package cc.creativecomputing.io.net.artnet;

/**
 * The OpCode defines the class of data of an UDP packet.
 * Transmitted low byte first.
 * @author christianr
 *
 */
public enum CCArtNetOpCode {
	/**
	 * This is an ArtPoll packet, no other data is contained in this UDP packet.
	 */
	POLL(0x2000, CCArtNetPollPacket.class), 
	/**
	 * This is an ArtPollReply Packet. It contains device status information.
	 */
	POLL_REPLY(0x2100, CCArtNetPollReplyPacket.class), 
	/**
	 * Diagnostics and data logging packet.
	 */
	DIAG_DATA(0x2300, null), 
	/**
	 * Used to send text based parameter commands.
	 */
	COMMAND(0x2400, null), 
	/**
	 * This is an ArtDmx data packet. It contains zero start code DMX512 information for a single Universe.
	 */
	OUTPUT(0x5000, null), 
	/**
	 * This is an ArtNzs data packet. It contains non-zero start code (except RDM) DMX512 information for a single Universe.
	 */
	NZS(0x5100, null), 
	/**
	 * This is an ArtSync data packet. It is used to force synchronous transfer of ArtDmx packets to a node’s output.
	 */
	SYNC(0x5200, null), 
	/**
	 * This is an ArtAddress packet. It contains remote programming information for a Node.
	 */
	ADDRESS(0x6000, null), 
	/**
	 * This is an ArtInput packet. It contains enable – disable data for DMX inputs.
	 */
	INPUT(0x7000, null), 
	/**
	 * This is an ArtTodRequest packet. It is used to request a Table of Devices (ToD) for RDM discovery.
	 */
	TOD_REQUEST(0x8000, null), 
	/**
	 * This is an ArtTodData packet. It is used to send a Table of Devices (ToD) for RDM discovery.
	 */
	TOD_DATA(0x8100, null), 
	/**
	 * This is an ArtTodControl packet. It is used to send RDM discovery control messages.
	 */
	TOD_CONTROL(0x8200, null), 
	/**
	 * This is an ArtRdm packet. It is used to send all non discovery RDM messages.
	 */
	RDM(0x8300, null), 
	/**
	 * This is an ArtRdmSub packet. It is used to send compressed, RDM Sub-Device data.
	 */
	RDMSUB(0x8400, null), 
	/**
	 * This is an ArtVideoSetup packet. It contains video screen setup information for nodes that implement the extended video features.
	 */
	VIDEO_SETUP(0xa010, null), 
	/**
	 * This is an ArtVideoPalette packet. It contains colour palette setup information for nodes that implement the extended video features.
	 */
	VIDEO_PALETTE(0xa020, null), 
	/**
	 * This is an ArtVideoData packet. It contains display data for nodes that implement the extended video features.
	 */
	VIDEO_DATA(0xa040, null), 
	/**
	 * This packet is deprecated.
	 */
	MAC_MASTER(0xf000, null), 
	/**
	 * This packet is deprecated.
	 */
	MAC_SLAVE(0xf100, null), 
	/**
	 * This is an ArtFirmwareMaster packet. It is used to upload new firmware or firmware extensions to the Node.
	 */
	FIRMWARE_MASTER(0xf200, null), 
	/**
	 * This is an ArtFirmwareReply packet. It is returned by the node to acknowledge receipt of an ArtFirmwareMaster packet or ArtFileTnMaster packet.
	 */
	FIRMWARE_REPLY(0xf300, null), 
	/**
	 * Uploads user file to node.
	 */
	FILE_TO_NODE_MASTER(0xf400, null), 
	/**
	 * Downloads user file from node.
	 */
	FILE_FROM_NODE_MASTER(0xf500, null), 
	/**
	 * Server to Node acknowledge for download packets.
	 */
	FILE_FROM_REPLY(0xf600, null), 
	/**
	 * This is an ArtIpProg packet. It is used to re- programme the IP, Mask and Port address of the Node.
	 */
	IP_PROG(0xf800, null), 
	/**
	 * This is an ArtIpProgReply packet. It is returned by the node to acknowledge receipt of an ArtIpProg packet.
	 */
	IP_PROG_REPLY(0xf900, null), 
	/**
	 * This is an ArtMedia packet. It is Unicast by a Media Server and acted upon by a Controller.
	 */
	MEDIA(0x9000, null), 
	/**
	 * This is an ArtMediaPatch packet. It is Unicast by a Controller and acted upon by a Media Server.
	 */
	MEDIA_PATCH(0x9100, null), 
	/**
	 * This is an ArtMediaControl packet. It is Unicast by a Controller and acted upon by a Media Server.
	 */
	MEDIA_CONTROL(0x9200, null), 
	/**
	 * This is an ArtMediaControlReply packet. It is Unicast by a Media Server and acted upon by a Controller.
	 */
	MEDIA_CONTROL_REPLY(0x9300, null), 
	/**
	 * This is an ArtTimeCode packet. It is used to transport time code over the network.
	 */
	TIME_CODE(0x9700, null), 
	/**
	 * Used to synchronise real time date and clock
	 */
	TIME_SYNC(0x9800, null), 
	/**
	 * Used to send trigger macros
	 */
	TRIGGER(0x9900, null), 
	/**
	 * Requests a node's file list
	 */
	DIRECTORY(0x9a00, null), 
	/**
	 * Replies to OpDirectory with file list
	 */
	DIRECTORY_REPLY(0x9b00, null);

	public final int id;
	private final Class<? extends CCArtNetPacket> packetClass;

	private CCArtNetOpCode(int theID, Class<? extends CCArtNetPacket> theClass) {
		id = theID;
		packetClass = theClass;
	}

	public CCArtNetPacket createPacket() {
		CCArtNetPacket p = null;
		if (packetClass != null) {
			try {
				p = packetClass.newInstance();
			} catch (InstantiationException e) {
				throw new AssertionError(e);
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			}
		}
		return p;
	}

}