package cc.creativecomputing.io.net.artnet;

public class CCArtNetPortInfo {

    protected boolean canOutput;
    protected boolean canInput;
    protected CCArtNetPortType type;

    public CCArtNetPortInfo(int id) {
        canOutput = (id & 0x80) > 0;
        canInput = (id & 0x40) > 0;
        id &= 0x3f;
        type = CCArtNetPortType.fromID(id);
    }

    /**
     * @return the canInput
     */
    public boolean canInput() {
        return canInput;
    }

    /**
     * @return the canOutput
     */
    public boolean canOutput() {
        return canOutput;
    }

    /**
     * @return the type
     */
    public CCArtNetPortType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PortDescriptor: " + type + " out: " + canOutput + " in: " + canInput;
    }
}
