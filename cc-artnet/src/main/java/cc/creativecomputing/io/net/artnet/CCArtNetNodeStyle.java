
package cc.creativecomputing.io.net.artnet;

/**
 * The Style code defines the general functionality of a Controller. The Style code is returned in ArtPollReply.
 * @author christianr
 *
 */
public enum CCArtNetNodeStyle {
	/**
	 * A DMX to / from Art-Net device
	 */
	ST_NODE(0x00), 
	/**
	 * A lighting console.
	 */
	ST_CONTROLLER(0x01), 
	/**
	 * A Media Server.
	 */
	ST_MEDIA(0x02), 
	/**
	 * A network routing device.
	 */
	ST_ROUTER(0x03), 
	/**
	 * A backup device.
	 */
	ST_BACKUP(0x04), 
	/**
	 * A configuration or diagnostic tool.
	 */
	ST_CONFIG(0x05),
	/**
	 * A visualiser.
	 */
	ST_VISUAL(0x06);

	public final int id;

	CCArtNetNodeStyle(int id) {
		this.id = id;
	}

	public static CCArtNetNodeStyle fromID(int theID){
		for (CCArtNetNodeStyle s : values()) {
            if (theID == s.id) {
                return s;
            }
        }
		throw new RuntimeException("unknown node style:" + theID);
	}
}
