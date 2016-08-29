package cc.creativecomputing.io.net.artnet;

/**
 * The ArtPoll packet is used to discover the presence of other Controllers, 
 * Nodes and Media Servers. The ArtPoll packet is only sent by a Controller. 
 * Both Controllers and Nodes respond to the packet.
 * <p>
 * A Controller broadcasts an ArtPoll packet to IP address 2.255.255.255 
 * (sub-net mask 255.0.0.0) at UDP port 0x1936, this is the Directed Broadcast address.
 * <p>
 * The Controller may assume a maximum timeout of 3 seconds between sending ArtPoll 
 * and receiving all ArtPollReply packets. If the Controller does not receive a 
 * response in this time it should consider the Node to have disconnected.
 * <p>
 * The Controller that broadcasts an ArtPoll should also reply to its own message 
 * (to Directed Broadcast address) with an ArtPollReply. This ensures that any other 
 * Controllers listening to the network will detect all devices without the need for 
 * all Controllers connected to the network to send ArtPoll packets. It is a requirement 
 * of Art-Net that all controllers broadcast an ArtPoll every 2.5 to 3 seconds. 
 * This ensures that any network devices can easily detect a disconnect.
 * <p>
 * Art-Net allows and supports multiple controllers on a network. 
 * When there are multiple controllers, Nodes will receive ArtPolls from different 
 * controllers which may contain conflicting diagnostics requirements. 
 * This is resolved as follows:
 * <p>
 * If any controller requests diagnostics, the node will send diagnostics. (ArtPoll->TalkToMe- >2).
 * <p>
 * If there are multiple controllers requesting diagnostics, diagnostics shall be broadcast. (Ignore ArtPoll->TalkToMe->3).
 * <p>
 * The lowest minimum value of Priority shall be used. (Ignore ArtPoll->Priority).
 * @author christianr
 *
 * @TODO implements missing talk to me fields
 */
public class CCArtNetPollPacket extends CCArtNetPacket{
	
	/**
	 * send or not send me diagnostics messages.
	 */
	private boolean _mySendDiagnosticMessages;
	/**
	 * 
	 */
	private boolean _myReplyOnChange;

	/**
	 * 
	 * @param theSendDiagnosticMessages send or not send me diagnostics messages.
	 * @param theReplyOnChange if <code>true</code> Send ArtPollReply whenever Node conditions change. 
	 * This selection allows the Controller to be informed of changes without the need to continuously poll.
	 * if <code>false</code> Only send ArtPollReply in response to an ArtPoll or ArtAddress.
	 */
	public CCArtNetPollPacket(boolean theSendDiagnosticMessages, boolean theReplyOnChange) {
		super(CCArtNetOpCode.POLL, 14);
        setTalkToMe(theSendDiagnosticMessages, theReplyOnChange);
	}
	
	public CCArtNetPollPacket() {
        this(true, true);
    }

    @Override
    public boolean parse(byte[] theRawData) {
        setData(theRawData, 14);
        int talk = getInt8(12);
        _mySendDiagnosticMessages = 0 == (talk & 0x02);
        _myReplyOnChange = 1 == (talk & 0x01);
        return true;
    }

    private void setTalkToMe(boolean theSendDiagnosticMessages, boolean theReplyOnChange) {
    	_mySendDiagnosticMessages = theSendDiagnosticMessages;
        _myReplyOnChange = theReplyOnChange;
        setInt8(
        	(theSendDiagnosticMessages ? 0 : 2) | 
        	(theReplyOnChange ? 0 : 1), 
        	12
        );
    }

    @Override
    public String toString() {
        return opCode + ": reply once:" + _mySendDiagnosticMessages + " direct: " + _myReplyOnChange;
    }
}
