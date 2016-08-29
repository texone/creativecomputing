package cc.creativecomputing.io.net.artnet;

public abstract class CCArtNetPacket extends CCArtNetPacketData{
	/**
	 * Array of 8 characters, the final character is a null termination. Value = ‘A’ ‘r’ ‘t’ ‘-‘ ‘N’ ‘e’ ‘t’ 0x00
	 */
    public static final byte[] ID = "Art-Net\0".getBytes();
    
    /**
     * The OpCode defines the class of data of an UDP packet.
     */
	public final CCArtNetOpCode opCode;
	
	public CCArtNetPacket(CCArtNetOpCode theOpCode, int theLength){
		super(new byte[theLength]);
		opCode = theOpCode;
        setHeader();
        setProtocol();
	}
	
	public CCArtNetPacket(CCArtNetOpCode theOpCode){
		super();
		opCode = theOpCode;
	}
	
	/**
     * Parses the given byte array into semantic values and populates type
     * specific fields for each packet type. Implementing classes do not need to
     * check the packet header anymore since this has already been done at this
     * stage.
     * 
     * @param theRawData
     * @return true, if there were no parse errors
     */
    public abstract boolean parse(byte[] theRawData);
	
    /**
     * Sets the header bytes of the packet consisting of {@link #HEADER} and the
     * type's OpCode.
     */
    protected void setHeader() {
        setByteChunk(ID, 0, 8);
        setInt16LE(opCode.id, 8);
    }

    protected void setProtocol() {
//        setInt16(CCArtNet.PROTOCOL_VERSION, 10);
    }
}
