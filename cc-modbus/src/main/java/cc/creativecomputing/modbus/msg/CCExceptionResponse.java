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
package cc.creativecomputing.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import cc.creativecomputing.modbus.CCModbusExceptionCode;
import cc.creativecomputing.modbus.CCModbusFunctionCode;
import cc.creativecomputing.modbus.Modbus;

/**
 * Class implementing a<tt>ModbusResponse</tt> that represents an exception.
 * 
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * 
 * @version 1.2rc1-ghpc (04/26/2011) Cleaned up a bit and added some Javadocs.
 */
public class CCExceptionResponse extends CCAbstractModbusResponse {

	// instance attributes
	private CCModbusExceptionCode _myExceptionCode = CCModbusExceptionCode.UNDEFINED;

	/**
	 * Constructs a new <tt>ExceptionResponse</tt> instance with a given
	 * function code and an exception code. The function code will be
	 * automatically increased with the exception offset.
	 * 
	 * @param theFunctionCode the function code as <tt>int</tt>.
	 * @param theExceptionCode the theExceptionCodeeption code as <tt>int</tt>.
	 */
	public CCExceptionResponse(CCModbusFunctionCode theFunctionCode, CCModbusExceptionCode theExceptionCode) {

		/*
		 * One byte of data.
		 */
		dataLength(1);
		functionCode(CCModbusFunctionCode.byID(theFunctionCode.id | Modbus.EXCEPTION_OFFSET));

		_myExceptionCode = theExceptionCode;
	}

	/**
	 * Constructs a new <tt>ExceptionResponse</tt> instance with a given
	 * function code. ORs the exception offset automatically.
	 * 
	 * @param theFunctionCode the function code as <tt>int</tt>.
	 */
	public CCExceptionResponse(CCModbusFunctionCode theFunctionCode) {
		this(theFunctionCode, CCModbusExceptionCode.UNDEFINED);
	}
	
	/**
	 * Constructs a new <tt>ExceptionResponse</tt> instance with a given
	 * exception code. ORs the exception offset automatically.
	 * 
	 * @param theFunctionCode the function code as <tt>int</tt>.
	 */
	public CCExceptionResponse(CCModbusExceptionCode theExceptionCode) {
		this(CCModbusFunctionCode.UNDEFINED, theExceptionCode);
	}

	/**
	 * Constructs a new <tt>ExceptionResponse</tt> instance with no function or
	 * exception code.
	 */
	public CCExceptionResponse() {

		/*
		 * One byte of data.
		 */
		dataLength(1);
	}
	
	/**
	 * Returns the Modbus exception code of this <tt>ExceptionResponse</tt>.
	 * <p>
	 * 
	 * @return the exception code as <tt>int</tt>.
	 */
	public CCModbusExceptionCode exceptionCode() {
		return _myExceptionCode;
	}
	
	/**
	 * 
	 */
	public void functionCode(CCModbusFunctionCode theFunctionCode) {
		super.functionCode(CCModbusFunctionCode.byID(theFunctionCode.id | Modbus.EXCEPTION_OFFSET));
	}

	@Override
	public void writeData(DataOutput theDataOut) throws IOException {
		theDataOut.writeByte(exceptionCode().id);
	}

	/**
	 * readData()
	 * 
	 * read the single byte of data, which is the exception code.
	 */
	@Override
	public void readData(DataInput theDataIn) throws IOException {
		_myExceptionCode = CCModbusExceptionCode.byID(theDataIn.readUnsignedByte());
	}

	/**
	 * getMessage()
	 * 
	 * return the exception type, which is the "message" for this response.
	 * 
	 * @return -- byte array containing the 1 byte exception code.
	 */
	@Override
	public byte[] message() {
		byte result[] = new byte[1];
		result[0] = (byte) exceptionCode().id;
		return result;
	}

	
}
