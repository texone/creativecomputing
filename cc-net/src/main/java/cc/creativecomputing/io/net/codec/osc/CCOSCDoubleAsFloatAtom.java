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

class CCOSCDoubleAsFloatAtom extends CCOSCAtom<Object> {
	protected CCOSCDoubleAsFloatAtom() {
		super(0x66, 4);
	}

	public Object decodeAtom(byte typeTag, ByteBuffer b){
		return new Float(b.getDouble());
	}

	public void encodeAtom(Object o, ByteBuffer tb, ByteBuffer db){
		tb.put((byte) 0x66); // 'f'
		db.putFloat(((Double) o).floatValue());
	}
}
