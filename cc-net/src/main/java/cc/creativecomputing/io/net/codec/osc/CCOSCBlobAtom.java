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

class CCOSCBlobAtom extends CCOSCAtom <byte[]>{
	protected CCOSCBlobAtom() {
		super(0x62, 0);
	}

	public byte[] decodeAtom(byte typeTag, ByteBuffer b){
		final byte[] blob = new byte[b.getInt()];
		b.get(blob);
		CCOSCPacketCodec.skipToAlign(b);
		return blob;
	}

	public void encodeAtom(byte[] theBytes, ByteBuffer tb, ByteBuffer db){
		tb.put((byte) 0x62); // 'b'
		db.putInt(theBytes.length);
		db.put(theBytes);
		CCOSCPacketCodec.padToAlign(db);
	}

	@Override
	public int size(byte[] theBytes){
		return ((theBytes.length + 7) & ~3);
	}
}
