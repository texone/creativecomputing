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
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;
import cc.creativecomputing.io.netty.codec.osc.CCOSCException;


/**
 * A packet codec defines how the translation between Java objects and OSC atoms is accomplished. For example, by
 * default, when an OSC message is assembled for transmission, the encoder will translate a
 * <code>java.lang.Integer</code> argument into a four byte integer with typetag <code>'i'</code>. Or when a received
 * message is being decoded, finding an atom typetagged <code>'f'</code>, the decoder will create a
 * <code>java.lang.Float</code> out of it.
 * <p>
 * This example sounds trivial, but the codec is also able to handle type conversions. For instance, in the strict OSC
 * 1.0 specification, only 32bit numeric atoms are defined (<code>'i'</code> and <code>'f'</code>). A codec with mode
 * <code>MODE_STRICT_V1</code> will reject a <code>java.lang.Double</code> in the encoding process and not be able to
 * decode a typetag <code>'d'</code>. A codec with mode <code>MODE_MODEST</code> automatically breaks down everything
 * the 32bit, so a <code>java.lang.Double</code> gets encoded as 32bit <code>'f'</code> and a received atom tagged
 * <code>'d'</code> becomes a <code>java.lang.Float</code>. Other configurations exist.
 * <p>
 * Another important function of the codec is to specify the charset encoding of strings, something that was overseen in
 * the OSC 1.0 spec. By default, <code>UTF-8</code> is used so all special characters can be safely encoded.
 * <p>
 * Last but not least, using the <code>putDecoder</code> and <code>putEncoder</code> methods, the codec can be extended
 * to support additional Java classes or OSC typetags, without the need to subclass <code>OSCPacketCodec</code>.
 * 
 * @author Hanns Holger Rutz
 */
public class CCOSCPacketCodec implements CCNetPacketCodec<CCOSCPacket>{
	private static final CCOSCPacketCodec defaultCodec = new CCOSCPacketCodec();

	public static final int MODE_READ_DOUBLE = 0x0001;
	public static final int MODE_READ_DOUBLE_AS_FLOAT = 0x0002;
	private static final int MODE_READ_DOUBLE_MASK = 0x0003;
	
	public static final int MODE_READ_LONG = 0x0004;
	public static final int MODE_READ_LONG_AS_INTEGER = 0x0008;
	private static final int MODE_READ_LONG_MASK = 0x000C;
	
	public static final int MODE_WRITE_DOUBLE = 0x0010;
	public static final int MODE_WRITE_DOUBLE_AS_FLOAT = 0x0020;
	private static final int MODE_WRITE_DOUBLE_MASK = 0x0030;
	
	public static final int MODE_WRITE_LONG = 0x0040;
	public static final int MODE_WRITE_LONG_AS_INTEGER = 0x0080;
	private static final int MODE_WRITE_LONG_MASK = 0x00C0;
	
	public static final int MODE_READ_SYMBOL_AS_STRING = 0x0100;
	public static final int MODE_WRITE_PACKET_AS_BLOB = 0x0200;

	private final CCOSCAtom<?>[] atomDecoders = new CCOSCAtom[128];
	private final Class<?>[] atomEncoderC = new Class[128];
	@SuppressWarnings("rawtypes")
	private final CCOSCAtom[] atomEncoderA = new CCOSCAtom[128];

	protected String _myCharset;

	private static final byte[] bndlIdentifier = { 0x23, 0x62, 0x75, 0x6E, 0x64, 0x6C, 0x65, 0x00 }; // "#bundle"
	// (4-aligned)

	private static final byte[] pad = new byte[4];

	/**
	 * Creates a new codec with <code>MODE_GRACEFUL</code> and <code>UTF-8</code> encoding. Note that since a codec and
	 * be shared between <code>OSCServer</code> or <code>OSCClient</code> instances, usually you will just want to call
	 * <code>getDefaultCodec</code>!
	 * 
	 * @see #MODE_GRACEFUL
	 * @see #getDefaultCodec()
	 */
	public CCOSCPacketCodec() {
		this(CCOSCCodecMode.GRACEFUL);
	}

	/**
	 * Creates a new codec with a given support mode and <code>UTF-8</code> encoding.
	 * 
	 * @param mode the support mode flag field to use
	 */
	public CCOSCPacketCodec(CCOSCCodecMode theMode) {
		this(theMode, "UTF-8");
	}

	/**
	 * Creates a new codec with a given support mode and a given charset for string encoding.
	 * 
	 * @param theMode the support mode flag mask to use
	 * @param theCharset the name of the charset to use for string coding and decoding, like <code>&quot;UTF-8&quot;</code>
	 *        , <code>&quot;ISO-8859-1&quot;</code> etc.
	 * @see java.nio.charset.Charset
	 */
	public CCOSCPacketCodec(CCOSCCodecMode theMode, String theCharset) {
		_myCharset = theCharset;
		int encIdx = 0;

		// OSC version 1.0 strict type tag support
		CCOSCAtom<?> a = new CCOSCIntegerAtom();
		atomDecoders[a.typeTag()] = a;
		atomEncoderC[encIdx] = Integer.class;
		atomEncoderA[encIdx++] = a;
		a = new CCOSCFloatAtom();
		atomDecoders[a.typeTag()] = a;
		atomEncoderC[encIdx] = Float.class;
		atomEncoderA[encIdx++] = a;
		a = new CCOSCStringAtom(_myCharset);
		atomDecoders[a.typeTag()] = a;
		atomEncoderC[encIdx] = String.class;
		atomEncoderA[encIdx++] = a;
		a = new CCOSCBlobAtom();
		atomDecoders[a.typeTag()] = a;
		atomEncoderC[encIdx] = byte[].class;
		atomEncoderA[encIdx++] = a;

		_myCharset = theCharset;
		setSupportMode(theMode);
	}
	
	public String charSet() {
		return _myCharset;
	}

	/**
	 * Queries the standard codec which is used in all implicit client and server creations. This codec adheres to the
	 * <code>MODE_GRACEFUL</code> scheme and uses <code>UTF-8</code> string encoding.
	 * <p>
	 * Note that although it is not recommended, it is possible to modify the returned codec. That means that upon your
	 * application launch, you could query the default codec and switch its behaviour, e.g. change the string charset,
	 * so all successive operations with the default codec will be subject to those customizations.
	 * 
	 * @return the default codec
	 * @see #MODE_GRACEFUL
	 */
	public static CCOSCPacketCodec getDefaultCodec() {
		return defaultCodec;
	}

	/**
	 * Registers an atomic decoder with the packet codec. This decoder is called whenever an OSC message with the given
	 * typetag is encountered.
	 * 
	 * @param typeTag the typetag which is to be decoded with the new <code>Atom</code>. <code>typeTag</code> must be in
	 *        the ASCII value range 0 to 127.
	 * @param a the decoder to use
	 * 
	 * @see CCOSCAtom
	 */
	public void putDecoder(byte typeTag, CCOSCAtom<?> a) {
		atomDecoders[typeTag] = a;
	}

	/**
	 * Registers an atomic encoder with the packet codec. This encoder is called whenever an OSC message to be assembled
	 * contains an argument of the given Java class.
	 * 
	 * @param javaClass the class for which the encoder is responsible
	 * @param a the encoder to use
	 * 
	 * @see CCOSCAtom
	 */
	public void putEncoder(Class<?> javaClass, CCOSCAtom<?> a) {
		int encIdx = 0;
		// atomEncoders.put( javaClass, a );
		while ((atomEncoderC[encIdx] != javaClass) && (atomEncoderC[encIdx] != null))
			encIdx++;
		if (a != null) {
			atomEncoderC[encIdx] = javaClass;
			atomEncoderA[encIdx] = a;
		} else if (atomEncoderC[encIdx] != null) {
			int encIdx2;
			for (encIdx2 = encIdx + 1; atomEncoderC[encIdx2] != null; encIdx2++)
				;
			System.arraycopy(atomEncoderC, encIdx + 1, atomEncoderC, encIdx, encIdx2 - encIdx);
			System.arraycopy(atomEncoderA, encIdx + 1, atomEncoderA, encIdx, encIdx2 - encIdx);
		}
	}

	/**
	 * Adjusts the support mode for type tag handling. Usually you specify the mode directly in the instantiation of
	 * <code>OSCPacketCodec</code>, but you can change it later using this method.
	 * 
	 * @param mode the new mode to use. A flag field combination of <code>MODE_READ_DOUBLE</code> or
	 *        <code>MODE_READ_DOUBLE_AS_FLOAT</code> etc., or a ready made combination such as <code>MODE_FAT_V1</code>.
	 * 
	 * @see #OSCPacketCodec(int )
	 */
	public void setSupportMode(CCOSCCodecMode mode) {

		switch (mode.mask & MODE_READ_DOUBLE_MASK) {
		case MODE_READ_DOUBLE:
			atomDecoders[0x64] = new CCOSCDoubleAtom();
			break;
		case MODE_READ_DOUBLE_AS_FLOAT:
			atomDecoders[0x64] = new CCOSCDoubleAsFloatAtom();
			break;
		default:
			atomDecoders[0x64] = null; // 'd' double
		}

		switch (mode.mask & MODE_READ_LONG_MASK) {
		case MODE_READ_LONG:
			atomDecoders[0x68] = new CCOSCLongAtom();
			break;
		case MODE_READ_LONG_AS_INTEGER:
			atomDecoders[0x68] = new CCOSCLongAsIntegerAtom();
			break;
		default:
			atomDecoders[0x68] = null; // 'h' long
		}

		switch (mode.mask & MODE_WRITE_DOUBLE_MASK) {
		case MODE_WRITE_DOUBLE:
			putEncoder(Double.class, new CCOSCDoubleAtom());
			break;
		case MODE_WRITE_DOUBLE_AS_FLOAT:
			putEncoder(Double.class, new CCOSCDoubleAsFloatAtom());
			break;
		default:
			putEncoder(Double.class, null);
		}

		switch (mode.mask & MODE_WRITE_LONG_MASK) {
		case MODE_WRITE_LONG:
			putEncoder(Long.class, new CCOSCLongAtom());
			break;
		case MODE_WRITE_LONG_AS_INTEGER:
			putEncoder(Long.class, new CCOSCLongAsIntegerAtom());
			break;
		default:
			putEncoder(Long.class, null);
		}

		if ((mode.mask & MODE_READ_SYMBOL_AS_STRING) != 0) {
			atomDecoders[0x53] = new CCOSCStringAtom(_myCharset); // 'S' symbol
		} else {
			atomDecoders[0x53] = null;
		}

		if ((mode.mask & MODE_WRITE_PACKET_AS_BLOB) != 0) {
			PacketAtom a = new PacketAtom();
			putEncoder(CCOSCBundle.class, a);
			putEncoder(CCOSCMessage.class, a);
		} else {
			putEncoder(CCOSCBundle.class, null);
			putEncoder(CCOSCMessage.class, null);
		}
	}

	/**
	 * Creates a new packet decoded from the ByteBuffer. This method tries to read a null terminated string at the
	 * beginning of the provided buffer. If it equals the bundle identifier, the <code>decode</code> of
	 * <code>CCOSCBundle</code> is called (which may recursively decode nested bundles), otherwise the one from
	 * <code>CCOSCMessage</code>.
	 * 
	 * @param b <code>ByteBuffer</code> pointing right at the beginning of the packet. the buffer's limited should be
	 *        set appropriately to allow the complete packet to be read. when the method returns, the buffer's position
	 *        is right after the end of the packet.
	 * 
	 * @return new decoded OSC packet
	 * 
	 * @throws IOException in case some of the reading or decoding procedures failed.
	 * @throws BufferUnderflowException in case of a parsing error that causes the method to read past the buffer limit
	 * @throws IllegalArgumentException occurs in some cases of buffer underflow
	 */
	public CCOSCPacket decode(ByteBuffer b) {
		final String command = readString(b);
		skipToAlign(b);

		if (command.equals(CCOSCBundle.TAG)) {
			return decodeBundle(b);
		} else {
			return decodeMessage(command, b);
		}
	}

	/**
	 * Encodes the contents of this packet into the provided <code>ByteBuffer</code>, beginning at the buffer's current
	 * position. To write the encoded packet, you will typically call <code>flip()</code> on the buffer, then
	 * <code>write()</code> on the channel.
	 * 
	 * @param b <code>ByteBuffer</code> pointing right at the beginning of the OSC packet. buffer position will be right
	 *        after the end of the packet when the method returns.
	 * 
	 * @throws IOException in case some of the writing procedures failed.
	 */
	public void encode(CCOSCPacket p, ByteBuffer b){
		if (p instanceof CCOSCBundle) {
			encodeBundle((CCOSCBundle) p, b);
		} else {
			encodeMessage((CCOSCMessage) p, b);
		}
	}

	/**
	 * Calculates and returns the packet's size in bytes
	 * 
	 * @return the size of the packet in bytes, including the initial OSC command and aligned to 4-byte boundary. this
	 *         is the amount of bytes written by the <code>encode</code> method.
	 * 
	 * @throws IOException if an error occurs during the calculation
	 */
	public int getSize(CCOSCPacket p){
		if (p instanceof CCOSCBundle) {
			return getBundleSize((CCOSCBundle) p);
		} else {
			return getMessageSize((CCOSCMessage) p);
		}
	}

	protected int getBundleSize(CCOSCBundle bndl){
		synchronized (bndl.packets()) {
			int result = bndlIdentifier.length + 8 + (bndl.packets().size() << 2); // name, timetag, size of each
			// bundle element

			for (CCOSCPacket myPacket:bndl.packets()) {
				result += getSize(myPacket);
			}

			return result;
		}
	}

	/**
	 * Calculates the byte size of the encoded message
	 * 
	 * @return the size of the OSC message in bytes
	 * 
	 * @throws IOException if the message contains invalid arguments
	 */
	@SuppressWarnings("unchecked")
	protected int getMessageSize(CCOSCMessage msg){
		final int numArgs = msg.numberOfArguments();
		int result = ((msg.address().length() + 4) & ~3) + ((1 + numArgs + 4) & ~3);
		Object o;
		Class<?> cl;
		// Class oldCl = null;
		// Atom a = null;
		int j;

		for (int i = 0; i < numArgs; i++) {
			o = msg.argument(i);
			cl = o.getClass();
			j = 0;
			try {
				while (atomEncoderC[j] != cl)
					j++;
				// a = (Atom) atomEncoders.get( cl );
				// result += a.getAtomSize( o );
				result += atomEncoderA[j].size(o);
			} catch (NullPointerException e1) {
				throw new CCOSCException("OSC message cannot contain argument of class " + cl.getName());
			}
		}

		return result;
	}

	private CCOSCBundle decodeBundle(ByteBuffer theBuffer){
		final CCOSCBundle bndl = new CCOSCBundle();
		final int totalLimit = theBuffer.limit();

		bndl.setTimeTagRaw(theBuffer.getLong());

		try {
			while (theBuffer.hasRemaining()) {
				theBuffer.limit(theBuffer.getInt() + theBuffer.position()); // msg size
				// bndl.addPacket( CCOSCPacket.decode( b, m ));
				bndl.addPacket(decode(theBuffer));
				theBuffer.limit(totalLimit);
			}
			return bndl;
		} catch (IllegalArgumentException e1) { // throws by b.limit if bundle size is corrupted
			throw new CCOSCException("Illegal OSC Message Format " + e1.getLocalizedMessage());
		}
	}

	/**
	 * Creates a new message with arguments decoded from the ByteBuffer. Usually you call <code>decode</code> from the
	 * <code>CCOSCPacket</code> superclass which will invoke this method of it finds an OSC message.
	 * 
	 * @param b ByteBuffer pointing right at the beginning of the type declaration section of the OSC message, i.e. the
	 *        name was skipped before.
	 * 
	 * @return new OSC message representing the received message described by the ByteBuffer.
	 * 
	 * @throws IOException in case some of the reading or decoding procedures failed.
	 * @throws BufferUnderflowException in case of a parsing error that causes the method to read past the buffer limit
	 * @throws IllegalArgumentException occurs in some cases of buffer underflow
	 */
	public CCOSCMessage decodeMessage(String command, ByteBuffer b){
		final int numArgs;
		final ByteBuffer b2;
		final int pos1;
		// int pos1, pos2;
		byte typ = 0;

		if (b.get() != 0x2C)
			throw new CCOSCException("Illegal OSC Message Format");
		
		b2 = b.slice(); // faster to slice than to reposition all the time!
		pos1 = b.position();
		// b2.position( pos1 );
		// pos1 = b.position();
		// CCOSCPacket.skipToValues( b );
		while (b.get() != 0x00)
			;
		numArgs = b.position() - pos1 - 1;
		List<Object> myArguments = new ArrayList<Object>();
		skipToAlign(b);
		// pos2 = (b.position() + 3) & ~3;

		try {
			for (int argIdx = 0; argIdx < numArgs; argIdx++) {
				typ = b2.get();
				// if( typ == 0 ) break;
				myArguments.add(atomDecoders[typ].decodeAtom(typ, b));
			}
		} catch (NullPointerException e1) {
			throw new CCOSCException("Unsupported OSC Type Tag " + String.valueOf((char) typ));
		}
		
		return new CCOSCMessage(command, myArguments);
	}

	protected void encodeBundle(CCOSCBundle bndl, ByteBuffer b){
		int pos1, pos2;

		b.put(bndlIdentifier).putLong(bndl.getTimeTag());

		synchronized (bndl.packets()) {
			for (CCOSCPacket myPacket:bndl.packets()) {
				b.mark();
				b.putInt(0); // calculate size later
				pos1 = b.position();
				encode(myPacket, b);
				pos2 = b.position();
				b.reset();
				b.putInt(pos2 - pos1).position(pos2);
			}
		}
	}

	/**
	 * Encodes the message onto the given <code>ByteBuffer</code>, beginning at the buffer's current position. To write
	 * the encoded message, you will typically call <code>flip()</code> on the buffer, then <code>write()</code> on the
	 * channel.
	 * 
	 * @param b <code>ByteBuffer</code> pointing right at the beginning of the OSC packet. buffer position will be right
	 *        after the end of the message when the method returns.
	 * 
	 * @throws IOException in case some of the writing procedures failed (buffer overflow, illegal arguments).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void encodeMessage(CCOSCMessage msg, ByteBuffer b) throws BufferOverflowException{
		final int numArgs = msg.numberOfArguments(); // args.length;
		final ByteBuffer b2;
		// int pos1, pos2;
		int j;
		Object o = null;
		Class cl = null;
		// Class oldCl = null;
		CCOSCAtom a = null;

		b.put(msg.address().getBytes());
		terminateAndPadToAlign(b);
		// it's important to slice at a 4-byte boundary because
		// the position will become 0 and terminateAndPadToAlign
		// will be malfunctioning otherwise
		b2 = b.slice();
		b2.put((byte) 0x2C); // ',' to announce type string
		b.position(b.position() + ((numArgs + 5) & ~3)); // comma + numArgs + zero + align
		try {
			for (int i = 0; i < numArgs; i++) {
				o = msg.argument(i);
				cl = o.getClass();
				j = 0;
				while (atomEncoderC[j] != cl) {
					j++;
				}
				a = atomEncoderA[j];
				a.encodeAtom(o, b2, b);
			}
		} catch (NullPointerException e1) {
			e1.printStackTrace();
			throw new CCOSCException("OSC message cannot contain argument of class " + (o == null ? "null" : cl.getName()));
		}
		terminateAndPadToAlign(b2);
	}

	/**
	 * Reads a null terminated string from the current buffer position
	 * 
	 * @param b buffer to read from. position and limit must be set appropriately. new position will be right after the
	 *        terminating zero byte when the method returns
	 * 
	 * @throws BufferUnderflowException in case the string exceeds the provided buffer limit
	 */
	public static String readString(ByteBuffer b) {
		final int pos = b.position();
		final byte[] bytes;
		// int len = 1;
		while (b.get() != 0)
			; // len++;
		final int len = b.position() - pos;
		bytes = new byte[len];
		b.position(pos);
		b.get(bytes);
		return new String(bytes, 0, len - 1);
	}

	/**
	 * Adds as many zero padding bytes as necessary to stop on a 4 byte alignment. if the buffer position is already on
	 * a 4 byte alignment when calling this function, another 4 zero padding bytes are added. buffer position will be on
	 * the new aligned boundary when return from this method
	 * 
	 * @param b the buffer to pad
	 * 
	 * @throws BufferOverflowException in case the padding exceeds the provided buffer limit
	 */
	public static void terminateAndPadToAlign(ByteBuffer b) {
		b.put(pad, 0, 4 - (b.position() & 0x03));
	}

	/**
	 * Adds as many zero padding bytes as necessary to stop on a 4 byte alignment. if the buffer position is already on
	 * a 4 byte alignment when calling this function, this method does nothing.
	 * 
	 * @param b the buffer to align
	 * 
	 * @throws BufferOverflowException in case the padding exceeds the provided buffer limit
	 */
	public static void padToAlign(ByteBuffer b) {
		b.put(pad, 0, -b.position() & 0x03); // nearest 4-align
	}

	/**
	 * Advances in the buffer as long there are non-zero bytes, then advance to a four byte alignment.
	 * 
	 * @param b the buffer to advance
	 * 
	 * @throws BufferUnderflowException in case the reads exceed the provided buffer limit
	 * @throws IllegalArgumentException in case the skipping exceeds the provided buffer limit
	 */
	public static void skipToValues(ByteBuffer b) throws BufferUnderflowException {
		while (b.get() != 0x00)
			;
		b.position((b.position() + 3) & ~3);
	}

	/**
	 * Advances the current buffer position to an integer of four bytes. The position is not altered if it is already
	 * aligned to a four byte boundary.
	 * 
	 * @param b the buffer to advance
	 * 
	 * @throws IllegalArgumentException in case the skipping exceeds the provided buffer limit
	 */
	public static void skipToAlign(ByteBuffer b) {
		b.position((b.position() + 3) & ~3);
	}

	private class PacketAtom extends CCOSCAtom<CCOSCPacket> {
		protected PacketAtom() {
			super(0x62, 0);
		}

		public CCOSCPacket decodeAtom(byte typeTag, ByteBuffer b){
			throw new CCOSCException("Not supported");
		}

		public void encodeAtom(CCOSCPacket o, ByteBuffer tb, ByteBuffer db){
			tb.put((byte) 0x62); // 'b'
			final int pos = db.position();
			final int pos2 = pos + 4;
			db.position(pos2);
			encode(o, db); // XXX
			db.putInt(pos, db.position() - pos2);
		}

		@Override
		public int size(CCOSCPacket o){
			return (getSize(o) + 4);
		}
	}
}
