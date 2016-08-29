
package cc.creativecomputing.io.net.artnet;

/**
 * 
 * @author christianr
 *
 */
public enum CCArtNetPortType {
    DMX512(0), 
    MIDI(1), 
    AVAB(2), 
    COLORTRAN(3), 
    ADB62_5(4), 
    ARTNET(5);

    public final int id;

    private CCArtNetPortType(int id) {
        this.id = id;
    }
    
    public static CCArtNetPortType fromID(int theID){
    	for (CCArtNetPortType t : values()) {
            if (theID == t.id) {
                return t;
            }
        }
    	throw new RuntimeException("Unavailable Porttype");
    }

 
}
