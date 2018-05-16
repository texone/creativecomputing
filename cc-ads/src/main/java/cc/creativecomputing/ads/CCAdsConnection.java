package cc.creativecomputing.ads;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;
import de.beckhoff.jni.Convert;
import de.beckhoff.jni.JNIByteBuffer;
import de.beckhoff.jni.tcads.AdsCallDllFunction;
import de.beckhoff.jni.tcads.AmsAddr;

public class CCAdsConnection{
	
	private  AmsAddr _myAddress;
	
	@SuppressWarnings("deprecation")
	public CCAdsConnection(String theId, int thePort){
		AdsCallDllFunction.adsPortOpen();

		_myAddress = new AmsAddr();
        AdsCallDllFunction.getLocalAddress(_myAddress);
       // _myAddress.setNetId(new AmsNetId(new String("2.23.214.58.1.1").toCharArray()));
        _myAddress.setNetIdString(theId);
        
		_myAddress.setPort(thePort);
	}
	
	private void checkError(long theError){

		if(theError != 0){
			throw new RuntimeException("Error: Get handle: 0x"  + Long.toHexString(theError));
		}
	}
	
	public int getHandle(String theHandleName){
		JNIByteBuffer mySymbolBuffer = new JNIByteBuffer(Convert.StringToByteArr(theHandleName,true));
        JNIByteBuffer myHandleBuffer = new JNIByteBuffer(Integer.SIZE / Byte.SIZE);
		
		long err = AdsCallDllFunction.adsSyncReadWriteReq(
			_myAddress,
			AdsCallDllFunction.ADSIGRP_SYM_HNDBYNAME,
			0x0,
			myHandleBuffer.getUsedBytesCount(),
			myHandleBuffer,
			mySymbolBuffer.getUsedBytesCount(),
			mySymbolBuffer
		);
		
		try{
			checkError(err);
		}catch(Exception e){
			return -1;
		}
		return Convert.ByteArrToInt(myHandleBuffer.getByteArray());
	}
	
	public void releaseHandle(int theHandle){
		JNIByteBuffer myHandleBuffer = new JNIByteBuffer(Convert.IntToByteArr(theHandle));
		// Release handle
        long err = AdsCallDllFunction.adsSyncWriteReq(
        	_myAddress,
        	AdsCallDllFunction.ADSIGRP_SYM_RELEASEHND,
        	0,
        	myHandleBuffer.getUsedBytesCount(),
        	myHandleBuffer
        );

        checkError(err);
	}
	
	private void read(int theHandle, JNIByteBuffer theDataBuffer){
		if(theHandle == -1)return;
		long err = AdsCallDllFunction.adsSyncReadReq(
			_myAddress,
			AdsCallDllFunction.ADSIGRP_SYM_VALBYHND,
			theHandle,
			theDataBuffer.getUsedBytesCount(),
			theDataBuffer
		);
		
		checkError(err);
	}
	
	private void write(int theHandle, JNIByteBuffer theDataBuffer){
		if(theHandle == -1)return;
		long err = AdsCallDllFunction.adsSyncWriteReq(
			_myAddress,
			AdsCallDllFunction.ADSIGRP_SYM_VALBYHND,
			theHandle,
			theDataBuffer.getUsedBytesCount(),
			theDataBuffer
		);
		
		checkError(err);
	}
	
	public byte[] data(int theHandle, int theSize){
		JNIByteBuffer dataBuff = new JNIByteBuffer(theSize);
		read(theHandle, dataBuff);
		return dataBuff.getByteArray();
	}
	
	public void data(int theHandle, byte[] theData){
		JNIByteBuffer dataBuff = new JNIByteBuffer(theData);
		write(theHandle, dataBuff);
	}
	
	public void booleanValue(int theHandle, boolean theValue){
		data(theHandle, Convert.BoolToByteArr(theValue));
	}
	
	public boolean booleanValue(int theHandle){
		return Convert.ByteArrToBool(data(theHandle, 1));
	}
	
	public int intValue(int theHandle){
		 return Convert.ByteArrToInt(data(theHandle,Integer.SIZE / Byte.SIZE));
	}
	
	public void intValue(int theHandle, int theValue){
		data(theHandle, Convert.IntToByteArr(theValue));
	}
	
	public long longValue(int theHandle){
		 return Convert.ByteArrToLong(data(theHandle,Long.SIZE / Byte.SIZE));
	}
	
	public void longValue(int theHandle, long theValue){
		data(theHandle, Convert.LongToByteArr(theValue));
	}
	
	public float floatValue(int theHandle){
		 return Convert.ByteArrToFloat(data(theHandle,Float.SIZE / Byte.SIZE));
	}
	
	public void floatValue(int theHandle, float theValue){
		data(theHandle, Convert.FloatToByteArr(theValue));
	}
	
	public double doubleValue(int theHandle){
		 return Convert.ByteArrToDouble(data(theHandle,Double.SIZE / Byte.SIZE));
	}
	
	public void doubleValue(int theHandle, double theValue){
		data(theHandle, Convert.DoubleToByteArr(theValue));
	}
	
	@Override
	protected void finalize() throws Throwable {
		long err = AdsCallDllFunction.adsPortClose();
        checkError(err);
	}
	
    public static void main(String[] args){
    	CCAdsConnection myConnection = new CCAdsConnection("5.52.209.88.1.1",851);
    	int myHandle = myConnection.getHandle("GVL.dmxData");

    	int myHandle2 = myConnection.getHandle("GVL.bStartStillAnimation");

    	byte[] data2 = myConnection.data(myHandle2, 1);
    	CCLog.info(data2);
    	byte[] data = myConnection.data(myHandle, 512);
    	for(int i = 0; i < data.length;i++){
    		System.out.println(i+" : " + (data[i] & 0xFF));
    	}
    	data[0] = 0;
    	data[1] = (byte)255;
    	for(int i = 0; i < 512;i++){
    		data[i] = (byte)CCMath.random(255);
    	}
    	myConnection.data(myHandle, data);
;//    	int myValue = myConnection.intData(myHandle);
//    	System.out.println("Success: PLCVar value: " + myValue);
    	System.out.println(myHandle);
    	myConnection.releaseHandle(myHandle);
    }
}