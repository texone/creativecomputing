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

class CCOSCDoubleAtom extends CCOSCAtom<Double> {
	protected CCOSCDoubleAtom() {
		super(0x64, 8);
	}

	public Double decodeAtom(byte typeTag, ByteBuffer b){
		return b.getDouble();
	}

	public void encodeAtom(Double theValue, ByteBuffer tb, ByteBuffer db){
		tb.put((byte) 0x64); // 'd'
		db.putDouble(theValue);
	}
}
