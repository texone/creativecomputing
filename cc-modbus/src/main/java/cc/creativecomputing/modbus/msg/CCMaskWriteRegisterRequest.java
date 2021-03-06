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
 * Copyright 2010, greenHouse Computers, LLC
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
import cc.creativecomputing.modbus.ModbusCoupler;
import cc.creativecomputing.modbus.procimg.IllegalAddressException;
import cc.creativecomputing.modbus.procimg.ProcessImage;
import cc.creativecomputing.modbus.procimg.Register;

/**
 * Class implementing a <tt>Mask Write Register</tt> request.
 * 
 * @author Julie Haugh (jfh@ghgande.com)
 * @version jamod-1.2rc1-ghpc
 * 
 * @author jfhaugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class CCMaskWriteRegisterRequest extends CCAbstractModbusRequest {
	private int _myReference;
	private int _myAndMask;
	private int _myOrMask;

	/**
	 * Constructs a new <tt>Mask Write Register</tt> request.
	 * 
	 * @param theReference
	 * @param theAndMask
	 * @param theOrMask
	 */
	public CCMaskWriteRegisterRequest(int theReference, int theAndMask, int theOrMask) {
		super();

		functionCode(CCModbusFunctionCode.MASK_WRITE_REGISTER);
		reference(theReference);
		andMask(theAndMask);
		orMask(theOrMask);

		dataLength(6);
	}

	/**
	 * Constructs a new <tt>Mask Write Register</tt> request. instance.
	 */
	public CCMaskWriteRegisterRequest() {
		super();

		functionCode(CCModbusFunctionCode.MASK_WRITE_REGISTER);
		dataLength(6);
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
	public void reference(int theReference) {
		_myReference = theReference;
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
	 * getResponse -- create an empty response for this request.
	 */
	@Override
	public CCAbstractModbusResponse response() {
		CCMaskWriteRegisterResponse response = null;

		response = new CCMaskWriteRegisterResponse();

		/*
		 * Copy any header data from the request.
		 */
		response.setHeadless(isHeadless());
		if (!isHeadless()) {
			response.transactionID(transactionID());
			response.protocolID(protocolID());
		}

		/*
		 * Copy the unit ID and function code.
		 */
		response.unitID(unitID());
		response.functionCode(functionCode());

		return response;
	}

	/**
	 * The ModbusCoupler doesn't have a means of reporting the slave state or ID
	 * information.
	 */
	@Override
	public CCAbstractModbusResponse createResponse() {
		CCMaskWriteRegisterResponse response = null;

		/*
		 * Get the process image.
		 */
		ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
		try {
			Register register = procimg.getRegister(_myReference);

			/*
			 * Get the original value. The AND mask will first be applied to
			 * clear any bits, then the OR mask will be applied to set them.
			 */
			int value = register.getValue();

			value = (value & _myAndMask) | (_myOrMask & ~_myAndMask);

			/*
			 * Store the modified value back where it came from.
			 */
			register.setValue(value);
		} catch (IllegalAddressException iaex) {
			return createExceptionResponse(CCModbusExceptionCode.ILLEGAL_ADDRESS_EXCEPTION);
		}
		response = (CCMaskWriteRegisterResponse) response();

		response.reference(_myReference);
		response.andMask(_myAndMask);
		response.orMask(_myOrMask);

		return response;
	}

	/**
	 * writeData -- output this Modbus message to dout.
	 */
	@Override
	public void writeData(DataOutput theDataOut) throws IOException {
		theDataOut.write(message());
	}

	/**
	 * readData -- dummy function. There is no data with the request.
	 */
	@Override
	public void readData(DataInput theDataIn) throws IOException {
		_myReference = theDataIn.readShort();
		_myAndMask = theDataIn.readShort();
		_myOrMask = theDataIn.readShort();
	}

	/**
	 * getMessage -- return an empty array as there is no data for this request.
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
