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

/**
 * <p>
 * Class implementing a <tt>ModbusRequest</tt> which is created for illegal or
 * non implemented function codes.
 * 
 * <p>
 * This is just a helper class to keep the implementation patterns the same for
 * all cases.
 * 
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class CCIllegalFunctionRequest extends CCAbstractModbusRequest {

	/**
	 * Constructs a new <tt>IllegalFunctionRequest</tt> instance for a given
	 * function code.
	 * 
	 * <p>
	 * Used to implement slave devices when an illegal function code has been
	 * requested.
	 * 
	 * @param function the function code as <tt>int</tt>.
	 */
	public CCIllegalFunctionRequest(CCModbusFunctionCode theFunctionCode) {
		functionCode(theFunctionCode);
	}

	/**
	 * Constructs a new <tt>IllegalFunctionRequest</tt> instance for a given
	 * function code.
	 * 
	 * <p>
	 * Used to implement slave devices when an illegal function code has been
	 * requested.
	 * 
	 * @param theFunctionCode the function code as <tt>int</tt>.
	 */
	public CCIllegalFunctionRequest(int theUnit, CCModbusFunctionCode theFunctionCode) {
		unitID(theUnit);
		functionCode(theFunctionCode);
	}

	/**
	 * There is no unit number associated with this exception.
	 */
	@Override
	public CCAbstractModbusResponse response() {
		CCIllegalFunctionExceptionResponse response = new CCIllegalFunctionExceptionResponse(functionCode());

		response.unitID(unitID());
		return response;
	}

	@Override
	public CCAbstractModbusResponse createResponse() {
		return createExceptionResponse(CCModbusExceptionCode.ILLEGAL_FUNCTION_EXCEPTION);
	}

	@Override
	public void writeData(DataOutput theDataOut) throws IOException {
		throw new RuntimeException();
	}

	/**
	 * Read all of the data that can be read. This is an unsupported function,
	 * so it may not be possible to know exactly how much data needs to be read.
	 */
	@Override
	public void readData(DataInput theDataIn) throws IOException {
		// skip all following bytes
		int length = dataLength();
		for (int i = 0; i < length; i++) {
			theDataIn.readByte();
		}
	}

	@Override
	public byte[] message() {
		return null;
	}
}
