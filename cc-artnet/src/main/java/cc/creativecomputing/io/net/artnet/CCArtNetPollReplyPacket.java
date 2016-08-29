/*
 * This file is part of artnet4j.
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * artnet4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * artnet4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with artnet4j. If not, see <http://www.gnu.org/licenses/>.
 */

package cc.creativecomputing.io.net.artnet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

import cc.creativecomputing.artnet.NodeReportCode;
import cc.creativecomputing.artnet.NodeStyle;
import cc.creativecomputing.artnet.PortDescriptor;
import cc.creativecomputing.core.logging.CCLog;

/**
 * A device, in response to a Controller’s ArtPoll, sends the ArtPollReply. 
 * This packet is also broadcast to the Directed Broadcast address by all Art-Net devices on power up.
 * @author christianr
 *
 */
public class CCArtNetPollReplyPacket extends CCArtNetPacket {

	/**
	 * Node’s IP address
	 */
    private InetAddress ip;

    private int _myPort;
    private int _myVersionInfo;
    
    private int _myNetSubSwitch;
    private int _myOemCode;
    private int _myUbeaVersion;
    private int _myNodeStatus;
    private int _myEstaManufactureCode;

    private String _myShortName;   
    private String _myLongName;

    private int _myNumberOfPorts;
    private CCArtNetPortInfo[] _myPortInfos;

    private CCArtNetNodeStyle _myNodeStyle;
    private CCArtNetNodeReportCode _myReportCode;

    private byte[] dmxIns;
    private byte[] dmxOuts;

    public CCArtNetPollReplyPacket() {
        super(CCArtNetOpCode.POLL_REPLY);
    }

    public CCArtNetPollReplyPacket(byte[] data) {
        super(CCArtNetOpCode.POLL_REPLY);
        setData(data);
    }

    /**
     * @return the dmxIns
     */
    public byte[] getDmxIns() {
        return dmxIns;
    }

    /**
     * @return the dmxOuts
     */
    public byte[] getDmxOuts() {
        return dmxOuts;
    }

    /**
     * @return the ip
     */
    public InetAddress getIPAddress() {
        InetAddress ipClone = null;
        try {
            ipClone = InetAddress.getByAddress(ip.getAddress());
        } catch (UnknownHostException e) {
        }
        return ipClone;
    }

    /**
     * The array represents a null terminated long name for the Node. 
     * The Controller uses the ArtAddress packet to program this string. 
     * Max length is 63 characters plus the null. This is a fixed length field, 
     * although the string it contains can be shorter than the field.
     * @return
     */
    public String longName() {
        return _myLongName;
    }

    public int getNodeStatus() {
        return _myNodeStatus;
    }

    /**
     * The Style code defines the general functionality of a Controller. The Style code is returned in ArtPollReply.
     * @return
     */
    public CCArtNetNodeStyle nodeStyle() {
        return _myNodeStyle;
    }

    /**
     * The Oem word describes the equipment vendor and the feature set available. 
     * Bit 15 high indicates extended features available.
     * @return
     */
    public int oemCode() {
        return _myOemCode;
    }

    public CCArtNetPortInfo[] portInfos() {
        return _myPortInfos;
    }

    /**
     * TheNodeReport code defines generic error, advisory and status messages for
     * both Nodes and Controllers. The NodeReport is returned in ArtPollReply.
     * @return the reportCode
     */
    public CCArtNetNodeReportCode reportCode() {
        return _myReportCode;
    }

    /**
     * The array represents a null terminated short name for the Node. 
     * The Controller uses the ArtAddress packet to program this string. 
     * Max length is 17 characters plus the null. This is a fixed length 
     * field, although the string it contains can be shorter than the field.
     * @return
     */
    public String shortName() {
        return _myShortName;
    }

    public int getSubSwitch() {
        return _myNetSubSwitch;
    }

    @Override
    public boolean parse(byte[] raw) {
        setData(raw);
        // System.out.println(data.toHex(256));
        setIPAddress(getByteChunk(null, 10, 4));
        _myPort = getInt16(14);
        _myVersionInfo = getInt16(16);
        _myNetSubSwitch = getInt16(18);
        _myOemCode = getInt16(20);
        _myUbeaVersion = getInt8(22);
        _myNodeStatus = getInt8(23);
        _myEstaManufactureCode = getInt16(24);
        
        _myShortName = new String(getByteChunk(null, 26, 17));
        _myLongName = new String(getByteChunk(null, 44, 64));
        
        _myReportCode = CCArtNetNodeReportCode.getForID(new String(getByteChunk(null, 108, 5)));
        _myNumberOfPorts = getInt16(172);
        _myPortInfos = new CCArtNetPortInfo[_myNumberOfPorts];
        for (int i = 0; i < _myNumberOfPorts; i++) {
            _myPortInfos[i] = new CCArtNetPortInfo(getInt8(174 + i));
        }
        // @TODO strange numbers seem wrong should be offset 178
        dmxIns = getByteChunk(null, 186, 4);
        dmxOuts = getByteChunk(null, 190, 4);
        for (int i = 0; i < 4; i++) {
            dmxIns[i] &= 0x0f;
            dmxOuts[i] &= 0x0f;
        }
        _myNodeStyle = CCArtNetNodeStyle.fromID(getInt8(200));
        return true;
    }

    /**
     * @param dmxIns
     *            the dmxIns to set
     */
    public void setDmxIns(byte[] dmxIns) {
        this.dmxIns = dmxIns;
    }

    /**
     * @param dmxOuts
     *            the dmxOuts to set
     */
    public void setDmxOuts(byte[] dmxOuts) {
        this.dmxOuts = dmxOuts;
    }

    private void setIPAddress(byte[] address) {
        try {
            ip = InetAddress.getByAddress(address);
            CCLog.info("setting ip address: " + ip);
        } catch (UnknownHostException e) {
            CCLog.warn(e.getMessage(), e);
        }
    }

    /**
     * @param reportCode
     *            the reportCode to set
     */
    public void setReportCode(CCArtNetNodeReportCode reportCode) {
        _myReportCode = reportCode;
    }
}
