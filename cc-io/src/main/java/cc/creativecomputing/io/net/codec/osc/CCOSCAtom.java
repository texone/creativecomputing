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

import java.nio.ByteBuffer;

/**
 * The <code>Atom</code> class represents a combination of an encoder and decoder of a Java respectively OSC atom.
 * While typically an <code>Atom</code> does a one-to-one mapping between a single Java class and a single OSC type
 * tag, other mappings are possible, such as different type tags for the same Java class, or decoding the same
 * typetag into different Java classes.
 * <p>
 * An example of the <B>first case</B> would be a colour atom: The <code>decodeAtom</code> method would require a
 * <code>'r'</code> typetag and return a <code>java.awt.Color</code> object, with a body similar to this:
 * 
 * <PRE>
 * return new java.awt.Color(b.getInt(), true);
 * </PRE>
 * 
 * The <code>encodeAtom</code> and <code>getTypeTag</code> methods would require its argument to be a
 * <code>java.awt.Color</code>, <code>getTypeTag</code> would return <code>'r'</code>, <code>getAtomSize</code>
 * would return <code>4</code>, and <code>encodeAtom</code> would do something like
 * 
 * <pre>
 * tb.put((byte) 'r');
 * db.putInt(((java.awt.Color) o).getRGB());
 * </pre>
 * <p>
 * And example of the <B>latter case</B> (one-to-many mapping) would be a codec for the <code>'T'</code>
 * (&quot;true&quot;) and <code>'F'</code> (&quot;false&quot;) typetags. This codec would be registered once as an
 * encoder, using <code>putEncoder( Boolean.class, myAtomCodec )</code>, and twice as a decoder, using
 * <code>putDecoder( (byte) 'F', myAtomCodec )</code> and <code>putDecoder( (byte) 'T', myAtomCodec )</code>. The
 * codec's <code>getAtomSize</code> method would return <code>0</code>, <code>getTypeTag</code> would return
 * 
 * <pre>
 * ((Boolean) o).booleanValue() ? (byte) 'T' : (byte) 'F'
 * </pre>
 * 
 * <code>decodeAtom</code> would return
 * 
 * <pre>
 * Boolean.valueOf(typeTag == (byte) 'T')
 * </pre>
 * 
 * and finally <code>encodeAtom</code> would be:
 * 
 * <pre>
 * tb.put(this.getTypeTag(o));
 * </pre>
 * 
 * @see CCOSCPacketCodec#putDecoder(byte, CCOSCPacketCodec.sciss.net.codec.OSCPacketCodec.Atom )
 * @see CCOSCPacketCodec#putEncoder(java.lang.Class, CCOSCPacketCodec.sciss.net.codec.OSCPacketCodec.Atom )
 */
public abstract class CCOSCAtom<Type> {
	
	private int _myTypeTag;
	private int _mySize;
	
	public CCOSCAtom(int theTypeTag, int theSize) {
		_myTypeTag = theTypeTag;
		_mySize = theSize;
	}
	
	public abstract Type decodeAtom(byte typeTag, ByteBuffer b);

	public abstract void encodeAtom(Type theObject, ByteBuffer tb, ByteBuffer db);

	public byte typeTag() {
		return (byte)_myTypeTag;
	}

	public int size(Type theObject) {
		return _mySize;
	}
}
