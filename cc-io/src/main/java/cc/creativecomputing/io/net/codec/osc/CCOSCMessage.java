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
package cc.creativecomputing.io.net.codec.osc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for decoding OSC messages from received datagrams or encoding OSC message for sending to a target socket. See
 * <A HREF="http://opensoundcontrol.org/spec-1_0">opensoundcontrol.org/spec-1_0</A> for the specification of the message
 * format. </P>
 * <P>
 * Here is an example:
 * 
 * <pre>
 * DatagramChannel dch = null;
 * 
 * final ByteBuffer buf = ByteBuffer.allocateDirect(1024);
 * final SocketAddress addr = new InetSocketAddress(&quot;localhost&quot;, 57110);
 * final Random rnd = new Random(System.currentTimeMillis());
 * 
 * try {
 * 	dch = DatagramChannel.open();
 * 	dch.configureBlocking(true);
 * 	new OSCMessage(&quot;/s_new&quot;, new Object[] { &quot;default&quot;, new Integer(1001), new Integer(1), new Integer(0), &quot;out&quot;, new Integer(0), &quot;freq&quot;, new Float(0), &quot;amp&quot;, new Float(0.1f) })
 * 			.encode(buf);
 * 	buf.flip();
 * 	dch.send(buf, addr);
 * 
 * 	for (int i = 0; i &lt; 11; i++) {
 * 		buf.clear();
 * 		// no schoenheitsprize
 * 		new OSCMessage(&quot;/n_set&quot;, new Object[] { new Integer(1001), &quot;freq&quot;, new Float(333 * Math.pow(2, rnd.nextInt(12) / 12.0f)) }).encode(buf);
 * 		buf.flip();
 * 		dch.send(buf, addr);
 * 		Thread.currentThread().sleep(300);
 * 	}
 * 	buf.clear();
 * 	new OSCMessage(&quot;/n_free&quot;, new Object[] { new Integer(1001) }).encode(buf);
 * 	buf.flip();
 * 	dch.send(buf, addr);
 * } catch (InterruptedException e1) {
 * } catch (IOException e2) {
 * 	System.err.println(e2.getLocalizedMessage());
 * } finally {
 * 	if (dch != null) {
 * 		try {
 * 			dch.close();
 * 		} catch (IOException e4) {
 * 		}
 * 		;
 * 	}
 * }
 * </pre>
 * 
 * Note that this example uses the old way of sending messages. A easier way is to create an <code>OSCTransmitter</code>
 * which handles the byte buffer for you. See the <code>OSCReceiver</code> doc for an example using a dedicated
 * transmitter.
 * 
 * @author Hanns Holger Rutz
 * @version 0.33, 28-Apr-07
 * 
 * @see CCOSCIn
 */
public class CCOSCMessage extends CCOSCPacket {
	private List<Object> _myArguments;
	private String _myAddress;

	/**
	 * Shorthand to pass to the constructor if you want to create an OSC message which doesn't contain any arguments.
	 * Note: alternatively you can use the constructor <code>new OSCMessage( String )</code>.
	 * 
	 * @see #OSCMessage(String )
	 */
	public static final Object[] NO_ARGS = new Object[0];

	/**
	 * Creates a generic OSC message with no arguments.
	 * 
	 * @param _myAddress the OSC command, like "/s_new"
	 */
	public CCOSCMessage(String theAddress) {
		super();

		_myAddress = theAddress;
		_myArguments = new ArrayList<Object>();
	}

	public List<Object> arguments(){
		return _myArguments;
	}
	
	/**
	 * Creates a generic OSC message from Primitive arguments.
	 * 
	 * @param theAddress the OSC command, like "/s_new"
	 * @param theArguments array of arguments which are simply assembled. Supported types are <code>Integer</code>,
	 *        <code>Long</code>, <code>Float</code>, <code>Double</code>, <code>String</code>, furthermore
	 *        <code>byte[]</code> and <code>OSCPacket</code> (both of which are written as a blob). Note that in a
	 *        future version of NetUtil, special codecs will allow customization of the way classes are encoded.
	 */
	public CCOSCMessage(String theAddress, List<Object> theArguments) {
		super();

		_myAddress = theAddress;
		_myArguments = theArguments;
	}

	/**
	 * Returns the OSC command of this message
	 * 
	 * @return the message's command, e.g. "/synced" etc.
	 */
	public String address() {
		return _myAddress;
	}

	/**
	 * Returns the number of arguments of the message.
	 * 
	 * @return the number of typed arguments in the message. e.g. for [ "/n_go", 1001, 0, -1, -1, 0 ] it returns 5.
	 */
	public int numberOfArguments() {
		return _myArguments.size();
	}

	/**
	 * Returns the argument at the given index. See <code>decodeMessage()</code> for information about the used java
	 * classes. The most fail-safe way to handle numeric arguments is to assume <code>Number</code> instead of a
	 * particular number subclass. To read a primitive <code>int</code>, the recommended code is
	 * <code>((Number) msg.getArg( index )).intValue()</code>, which will work with any of <code>Integer</code>,
	 * <code>Long</code>, <code>Float</code>, <code>Double</code>.
	 * 
	 * @param theIndex index of the argument, beginning at zero, must be less than <code>getArgCount()</code>
	 * 
	 * @return the primitive type (<code>Integer</code>, <code>Float</code>, <code>String</code> etc.) argument at the
	 *         given index. e.g. for [ "/n_go", 1001, 0, -1, -1, 0 ], requesting index 0 would return
	 *         <code>new Integer( 1001 )</code>.
	 * 
	 * @see #numberOfArguments()
	 * @see #decodeMessage(String, ByteBuffer )
	 * @see Number#intValue()
	 */
	public Object argument(int theIndex) {
		return _myArguments.get(theIndex);
	}
	
	/**
	 * Add an argument to the list of arguments.
	 * @param theArgument a Float, String, Integer, BigInteger, Boolean or array of these
	 */	
	public void add(final Object theArgument) {
		_myArguments.add(theArgument);
	}
	
	public byte[] blobArgument(final int theIndex) {
		return (byte[])_myArguments.get(theIndex);
	}
	
	public int intArgument(final int theIndex){
		return (Integer)_myArguments.get(theIndex);
	}
	
	public float floatArgument(final int theIndex){
		return (Float)_myArguments.get(theIndex);
	}
	
	public String stringArgument(final int theIndex) {
		return (String)_myArguments.get(theIndex);
	}
	
	public List<Float> floatArguments(final int theIndex){
		final List<Float> myResultList = new ArrayList<Float>();
		for(int i = theIndex; i < _myArguments.size();i++){
			if(_myArguments.get(i) instanceof Float){
				myResultList.add((Float)_myArguments.get(i));
			}else{
				break;
			}
		}
		return myResultList;
	}

	
	public List<Integer> intArguments(final int theIndex){
		final List<Integer> myResultList = new ArrayList<Integer>();
		for(int i = theIndex; i < _myArguments.size();i++){
			if(_myArguments.get(i) instanceof Integer){
				myResultList.add((Integer)_myArguments.get(i));
			}else{
				break;
			}
		}
		return myResultList;
	}
	
	public String toString(){
		final StringBuilder myStringBuilder = new StringBuilder();
		myStringBuilder.append(_myAddress);
		myStringBuilder.append("\n");
		
		for(Object myArgument:_myArguments){
			if(myArgument instanceof byte[]) {
				byte[] myBytes = (byte[])myArgument;
				for(byte myByte:myBytes) {
					myStringBuilder.append(myByte);
					myStringBuilder.append("\n");
				}
			}else {
				myStringBuilder.append(myArgument);
				myStringBuilder.append("\n");
			}
		}
		
		return myStringBuilder.toString();
	}

	/**
	 * Creates a new message with arguments decoded from the ByteBuffer, using the default codec. Usually you call
	 * <code>decode</code> from the <code>OSCPacket</code> superclass or directly from the <code>OSCPacketCodec</code>.
	 * 
	 * @param theBuffer ByteBuffer pointing right at the beginning of the type declaration section of the OSC message, i.e. the
	 *        name was skipped before.
	 * 
	 * @return new OSC message representing the received message described by the ByteBuffer.
	 * 
	 * @throws IOException in case some of the reading or decoding procedures failed.
	 * @throws IllegalArgumentException occurs in some cases of buffer underflow
	 * @see CCOSCPacketCodec#decodeMessage(String, ByteBuffer )
	 */
	public static CCOSCMessage decodeMessage(String theAddress, ByteBuffer theBuffer) throws IOException {
		return CCOSCPacketCodec.getDefaultCodec().decodeMessage(theAddress, theBuffer);
	}
}
