package cc.creativecomputing.io.net.artnet;

import cc.creativecomputing.artnet.NodeReportCode;

/**
 * TheNodeReport code defines generic error, advisory and status messages for
 * both Nodes and Controllers. The NodeReport is returned in ArtPollReply.
 * @author christianr
 *
 */
public enum CCArtNetNodeReportCode {

    RcDebug			("#0000", "Booted in debug mode"),
    RcPowerOk		("#0001", "Power On Tests successful"),
    RcPowerFail		("#0002", "Hardware tests failed at Power On"),
    RcSocketWr1		("#0003", "Last UDP from Node failed due to truncated length. Most likely caused by a collision."),
    RcParseFail		("#0004", "Unable to identify last UDP transmission. Check OpCode and packet length."),
    RcUdpFail		("#0005", "Unable to open Udp Socket in last transmission attempt"),
    RcShNameOk		("#0006", "Confirms that Short Name programming via ArtAddress, was successful."),
    RcLoNameOk		("#0007", "Confirms that Long Name programming via ArtAddress, was successful."),
    RcDmxError		("#0008", "DMX512 receive errors detected."), 
    RcDmxUdpFull	("#0009", "Ran out of internal DMX transmit buffers."), 
    RcDmxRxFull		("#000a", "Ran out of internal DMX Rx buffers."), 
    RcSwitchErr		("#000b", "Rx Universe switches conflict."), 
    RcConfigErr		("#000c", "Product configuration does not match firmware."), 
    RcDmxShort		("#000d", "DMX output short detected. See GoodOutput field."),
    RcFirmwareFail	("#000e", "Last attempt to upload new firmware failed."),
    RcUserFail		("#000f", "User changed switch settings when address locked by remote.");

    public final String id;
    public final String description;

    CCArtNetNodeReportCode(String theId, String theDesc) {
        id = theId;
        description = theDesc;
    }

    public static CCArtNetNodeReportCode getForID(String id) {
    	CCArtNetNodeReportCode code = null;
        for (CCArtNetNodeReportCode c : values()) {
            if (c.id.equalsIgnoreCase(id)) {
                code = c;
                break;
            }
        }
        return code;
    }
}