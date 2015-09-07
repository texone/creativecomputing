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

class CCOSCLongAtom extends CCOSCAtom <Long>{
	protected CCOSCLongAtom() {
		super(0x68, 8);
	}

	public Long decodeAtom(byte typeTag, ByteBuffer b) {
		return b.getLong();
	}

	public void encodeAtom(Long o, ByteBuffer tb, ByteBuffer db){
		tb.put((byte) 0x68); // 'h'
		db.putLong(o.longValue());
	}
}
