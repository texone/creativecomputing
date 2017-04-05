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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import cc.creativecomputing.io.netty.codec.osc.CCOSCException;

class CCOSCStringAtom extends CCOSCAtom<String> {

	private String _myCharSet;
	
	protected CCOSCStringAtom(String theCharSet) {
		super(0x73, 0);
		_myCharSet = theCharSet;
	}

	public String decodeAtom(byte typeTag, ByteBuffer b){
		final int pos1 = b.position();
		final String s;
		final int pos2;
		final byte[] bytes;
		final int len;
		while (b.get() != 0)
			;
		pos2 = b.position() - 1;
		b.position(pos1);
		len = pos2 - pos1;
		bytes = new byte[len];
		b.get(bytes, 0, len);
		try {
			s = new String(bytes, _myCharSet);
		} catch (UnsupportedEncodingException e) {
			throw new CCOSCException(e);
		}
		b.position((pos2 + 4) & ~3);
		return s;
	}

	public void encodeAtom(String o, ByteBuffer tb, ByteBuffer db){
		tb.put((byte) 0x73); // 's'
		try {
			db.put(o.getBytes(_myCharSet));
		} catch (UnsupportedEncodingException e) {
			throw new CCOSCException(e);
		} // faster than using Charset or CharsetEncoder
		CCOSCPacketCodec.terminateAndPadToAlign(db);
	}

	@Override
	public int size(String o){
		try {
			return ((o.getBytes(_myCharSet).length + 4) & ~3);
		} catch (UnsupportedEncodingException e) {
			throw new CCOSCException(e);
		}
	}
}
