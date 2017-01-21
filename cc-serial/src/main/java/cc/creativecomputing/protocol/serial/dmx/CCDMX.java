/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.protocol.serial.dmx;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCBitUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.protocol.serial.CCSerialInput;
import cc.creativecomputing.protocol.serial.CCSerialListener;
import cc.creativecomputing.protocol.serial.CCSerialModule;

/**
 * Use this class to send and receive DMX data width an 
 * <a href="http://www.enttec.com/index.php?main_menu=Products&pn=70304&show=description&name=dmxusbpro">enttec DMX USB PRO</a>.
 * This way you can enable light control. And also visualize dmx data in your application.
 */
public class CCDMX implements CCSerialListener {

	private final static int DMX_PRO_MESSAGE_START = 126;
    private final static int DMX_PRO_MESSAGE_END = 231;
    
	public final static int GET_INTERFACE_PARAMETER = 3;
	public final static int GET_INTERFACE_PARAMETER_REPLY = 3;
	public final static int SET_INTERFACE_PARAMETER = 4;
	public final static int SET_DMX_RX_MODE = 5;
	public final static int SET_DMX_TX_MODE = 6;
	public final static int SEND_DMX_RDM_TX = 7;
	public final static int GET_SERIAL = 10;
	
	/**
	 * serial number of the DMX interface
	 */
	@SuppressWarnings("unused")
	private String _mySerialNumber;
	
	/**
	 * version of the interface firmware
	 */
	@SuppressWarnings("unused")
	private String _myFirmwareVersion;
    
    /**
     * DMX output break time 
     */
	private double _myBreakTime;
    
    /**
     * DMX output Mark After Break
     */
	private double _myMarkAfterBreakTime;
    
    /**
     * 1 DMX output rate in packets per second. Valid range is 1 to 40.
     */
	private int _myRefreshRate;
	
	/**
	 * Number of channels.
	 */ 
    private int _myUniverseSize;
    
    private boolean _mySendAlways = true;
    
    private byte[] _myMessage;
    
    @CCProperty(name = "serial")
    private CCSerialModule _mySerial;
    
    private List<CCDMXListener> _myListener = new ArrayList<CCDMXListener>();
    
	/**
	 * 
	 * @param theApp app that listens to dmx messages
	 * @param thePort the serial port to use for communication
	 * @param theUniverseSize the size of the dmx universe
	 */
	public CCDMX(int theUniverseSize) {
		
		_mySerial = new CCSerialModule("enttec dmx", 115200);
		_mySerial.listener().add(this);
		_myUniverseSize = theUniverseSize;
		int myDataSize = _myUniverseSize + 1;
		
		_myMessage = new byte[_myUniverseSize + 6];
        // Format of the DMX message:
        _myMessage[0] = DMX_PRO_MESSAGE_START;
        _myMessage[1] = SET_DMX_RX_MODE;
        _myMessage[2] = (byte)(myDataSize & 255);  
        _myMessage[3] = (byte)((myDataSize >> 8) & 255);
        _myMessage[4] = 0;
        for (int i = 0; i < _myUniverseSize; i++){
            _myMessage[5 + i] = 0;
        }
        _myMessage[_myUniverseSize + 5] = (byte)DMX_PRO_MESSAGE_END;
	}
	
	/**
	 * Constructor where the parent PApplet object, the string identifying the
	 * port ("COM1", "COM2", etc.), the baudrate and the number of channels are
	 * specified.
	 * 
	 */
	public CCDMX(){
        this(512); 
    }
	
	public void addDMXListener(final CCDMXListener theListener){
		_myListener.add(theListener);
	}
  
	/**
	 * Writes value to the channel.
	 * 
	 * @param theChannel
	 * @param theValue
	 */
    public void setDMXChannel(final int theChannel, final int theValue){
    	if(theChannel > _myUniverseSize)return;
    	_myMessage[theChannel + 5] = (byte)theValue;
    }
    
    /**
	 * Writes the given value in 8bit resolution to the given and the next channel.
	 * 
	 * @param theChannel the dmx channel
	 * @param theValue the value in the range from 0 to 1
	 */
    public void setDMXChannel(final int theChannel, final double theValue){
    	setDMXChannel(theChannel, (int)(theValue * 255));
    }
    
    /**
     * Writes the given value in 16bit resolution to the given and the next channel.
     * @param theChannel the dmx channel
     * @param theValue the value in the range from 0 to 1
     */
    public void setDMXChannel16bit(final int theChannel, final double theValue){
    	int my16BitValue = (int)(theValue * 65535);
    	setDMXChannel(theChannel, CCBitUtil.bit(my16BitValue, 1));
    	setDMXChannel(theChannel + 1, CCBitUtil.bit(my16BitValue, 0));
    }
    
    /**
	 * Writes value to the channel.
	 * 
	 * @param theChannel
	 * @param theValue
	 */
    public void setDMXChannel(final int theChannel, final CCColor theColor){
    	setDMXChannel(theChannel, theColor.r);
    	setDMXChannel(theChannel + 1, theColor.g);
    	setDMXChannel(theChannel + 2, theColor.b);
    }
    
    /**
     * 
     * @param theSendAlways
     */
    public void sendAlways(final boolean theSendAlways){
    	_mySendAlways = theSendAlways;
    }
    
    /**
     * Requests the Widget to send an RDM packet out of the Widget DMX port, and then 
     * change the DMX port direction to input, so that RDM or DMX packets can be received. 
     * <p>
     * This message requests the interface to periodically send a DMX packet out of the Widget DMX port 
     * at the configured DMX output rate. This message causes the interface to leave the DMX port direction 
     * as output after each DMX packet is sent, so no DMX packets will be received as a result of this 
     * request. 
     * </p>
     * <p>
     * The periodic DMX packet output will stop and the interface DMX port direction will change to input 
     * when the interface receives any request message other than the Output Only Send DMX Packet 
     * request, or the Get interface Parameters request. 
     * </p>
     */
    public void send(){
    	if(_mySerial == null)return;
    	if(_mySerial.output() == null)return;
    	if(_mySendAlways)_myMessage[1] = SET_DMX_TX_MODE;
    	else _myMessage[1] = SET_DMX_RX_MODE;
    	_mySerial.output().write(_myMessage);
    }
   
	/**
	 * Sets the fields holding the different interface parameters. 
	 * Call this function to get the information. Be aware that these parameters are
	 * set after the interface has send them.
	 */
    public void getInterfaceData(){
    	sendData(GET_INTERFACE_PARAMETER,new byte[]{0,0},2);
    }
    
    /**
     * This method sets the interface configuration. 
     * The interface configuration is preserved when the interface loses power.
     */
    public void setInterfaceData(){
    	byte[]myData = new byte[5];
    	myData[0] = 0;
    	myData[1] = 0;
    	myData[2] = (byte)(_myBreakTime/10.67f);
    	myData[3] = (byte)(_myMarkAfterBreakTime/10.67f);
    	myData[4] = (byte)_myRefreshRate;
    	sendData(SET_INTERFACE_PARAMETER,myData,5);
    }
   
	
	/**
	 * Sets the fields holding the different interface parameters. 
	 * Call this function to get the information. Be aware that these parameters are
	 * set after the interface has send them.
	 */
    public void getSerial(){
    	sendData(GET_SERIAL,new byte[]{},0);
    }
	
	private static class DMXMessage{
		int label;
		int length;
		byte[] data;
	}
	
	private List<Integer> _myMessageBuffer = new ArrayList<Integer>();

	private void readMessage(){
		DMXMessage myMessage = new DMXMessage();
		myMessage.label = _myMessageBuffer.get(0);
		myMessage.length = _myMessageBuffer.get(1);
		myMessage.length += _myMessageBuffer.get(2);

		myMessage.data = new byte[myMessage.length];

		for (int i = 0; i < myMessage.length; i++) {
			myMessage.data[i] = _myMessageBuffer.get(i + 3).byteValue();
		}
		handleDMXMessage(myMessage);
	}

	public void onSerialEvent(final CCSerialInput theSerial) {
		while (theSerial.available() > 0) {
			int myValue = theSerial.read();
			CCLog.info(myValue + ":" + DMX_PRO_MESSAGE_END);
			switch(myValue){
			case DMX_PRO_MESSAGE_START:
				_myMessageBuffer.clear();
				break;
			case DMX_PRO_MESSAGE_END:
				readMessage();
				break;
			default: 
				_myMessageBuffer.add(myValue);
			}
		}
	}

	
	private void handleDMXMessage(DMXMessage theMessage){
		switch(theMessage.label){
		case GET_INTERFACE_PARAMETER:
			_myFirmwareVersion = theMessage.data[1] + "." + theMessage.data[0];
			
			//times are multiplied according to specs
			_myBreakTime = theMessage.data[2] * 10.67f;
			_myMarkAfterBreakTime = theMessage.data[3] * 10.67f;
			
			_myRefreshRate = theMessage.data[4];
			break;
		case GET_SERIAL:
			_mySerialNumber = 
				Integer.toHexString(theMessage.data[3]) + "X" + 
				Integer.toHexString(theMessage.data[2]) + "X" + 
				Integer.toHexString(theMessage.data[1]) + "X" +
				Integer.toHexString(theMessage.data[0]);
		case SET_DMX_RX_MODE:
			int[] myData = new int[theMessage.length];
			for(int i = 0;i < theMessage.length;i++){
				myData[i] = theMessage.data[i] & 0xFF;
			}
			CCDMXMessage myMessage = new CCDMXMessage(myData);
			for(CCDMXListener myLister:_myListener){
				myLister.onDMXIn(myMessage);
			}
		}
	}

	private void sendData(int label, byte[] data, int length) {
		_mySerial.output().write(DMX_PRO_MESSAGE_START);
		_mySerial.output().write(label);
		_mySerial.output().write(length & 0xFF);
		_mySerial.output().write(length >> 8);
		_mySerial.output().write(data);
		_mySerial.output().write(DMX_PRO_MESSAGE_END);
	}

	@Override
	public void start(CCSerialInput theSerialPort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(CCSerialInput theSerialPort) {
		// TODO Auto-generated method stub
		
	}

	public int universeSize() {
		return _myUniverseSize;
	}
}
