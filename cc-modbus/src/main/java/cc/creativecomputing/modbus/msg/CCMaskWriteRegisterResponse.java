//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/

/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2010, greenHouse Computers, LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package cc.creativecomputing.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import cc.creativecomputing.modbus.CCModbusFunctionCode;

/**
 * Class implementing a <tt>ReadMEIResponse</tt>.
 * 
 * Derived from similar class for Read Coils response.
 * 
 * @author Julie Haugh (jfh@ghgande.com)
 * @version 1.2rc1-ghpc (09/27/2010)
 */
public final class CCMaskWriteRegisterResponse extends CCAbstractModbusResponse {

	/*
	 * Message fields.
	 */
	private int _myReference;
	private int _myAndMask;
	private int _myOrMask;

	/**
	 * Constructs a new <tt>ReportSlaveIDResponse</tt> instance.
	 */
	public CCMaskWriteRegisterResponse() {
		super();
		functionCode(CCModbusFunctionCode.MASK_WRITE_REGISTER);
	}

	/**
	 * getReference -- return the reference field.
	 */
	public int reference() {
		return _myReference;
	}

	/**
	 * setReference -- set the reference field.
	 */
	public void reference(int ref) {
		_myReference = ref;
	}

	/**
	 * getAndMask -- return the AND mask value;
	 * 
	 * @return
	 */
	public int andMask() {
		return _myAndMask;
	}

	/**
	 * setAndMask -- set AND mask
	 */
	public void andMask(int mask) {
		_myAndMask = mask;
	}

	/**
	 * getOrMask -- return the OR mask value;
	 * 
	 * @return
	 */
	public int orMask() {
		return _myOrMask;
	}

	/**
	 * setOrMask -- set OR mask
	 */
	public void orMask(int mask) {
		_myOrMask = mask;
	}

	/**
	 * writeData -- output the completed Modbus message to dout
	 */
	@Override
	public void writeData(DataOutput theDataOut) throws IOException {
		theDataOut.write(message());
	}

	/**
	 * readData -- input the Modbus message from din. If there was a header,
	 * such as for Modbus/TCP, it will have been read already.
	 */
	@Override
	public void readData(DataInput theDataIn) throws IOException {
		_myReference = theDataIn.readShort();
		_myAndMask = theDataIn.readShort();
		_myOrMask = theDataIn.readShort();
	}

	/**
	 * getMessage -- format the message into a byte array.
	 */
	@Override
	public byte[] message() {
		byte results[] = new byte[6];

		results[0] = (byte) (_myReference >> 8);
		results[1] = (byte) (_myReference & 0xFF);
		results[2] = (byte) (_myAndMask >> 8);
		results[3] = (byte) (_myAndMask & 0xFF);
		results[4] = (byte) (_myOrMask >> 8);
		results[5] = (byte) (_myOrMask & 0xFF);

		return results;
	}
}
