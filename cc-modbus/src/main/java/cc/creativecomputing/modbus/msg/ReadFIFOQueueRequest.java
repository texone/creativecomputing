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
 * Java Modbus Library (j2mod)
 * Copyright 2010-2012, greenHouse Gas and Electric
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
import cc.creativecomputing.modbus.procimg.InputRegister;
import cc.creativecomputing.modbus.procimg.ProcessImage;
import cc.creativecomputing.modbus.procimg.Register;

/**
 * Class implementing a <tt>Read FIFO Queue</tt> request.
 * 
 * @author Julie Haugh (jfh@ghgande.com)
 * @version jamod-1.2rc1-ghpc
 * 
 * @author jfhaugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class ReadFIFOQueueRequest extends CCAbstractModbusRequest {

	private int m_Reference;

	/**
	 * getReference -- get the queue register number.
	 * 
	 * @return
	 */
	public int getReference() {
		return m_Reference;
	}

	/**
	 * setReference -- set the queue register number.
	 * 
	 * @return
	 */
	public void setReference(int ref) {
		m_Reference = ref;
	}

	/**
	 * getResponse -- create an empty response for this request.
	 */
	public CCAbstractModbusResponse response() {
		ReadFIFOQueueResponse response = null;

		response = new ReadFIFOQueueResponse();

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
	 * Create a response using the named register as the queue length count.
	 */
	public CCAbstractModbusResponse createResponse() {
		ReadFIFOQueueResponse response = null;
		InputRegister[] registers = null;

		/*
		 * Get the process image.
		 */
		ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
		
		try {
			/*
			 * Get the FIFO queue location and read the count of available
			 * registers.
			 */
			Register queue = procimg.getRegister(m_Reference);
			int count = queue.getValue();
			if (count < 0 || count > 31)
				return createExceptionResponse(CCModbusExceptionCode.ILLEGAL_VALUE_EXCEPTION);

			registers = procimg.getRegisterRange(m_Reference + 1, count);
		} catch (IllegalAddressException e) {
			return createExceptionResponse(CCModbusExceptionCode.ILLEGAL_ADDRESS_EXCEPTION);
		}
		response = (ReadFIFOQueueResponse) response();
		response.setRegisters(registers);

		return response;
	}

	/**
	 * writeData -- output this Modbus message to dout.
	 */
	public void writeData(DataOutput dout) throws IOException {
		dout.write(message());
	}

	/**
	 * readData -- read the reference word.
	 */
	public void readData(DataInput din) throws IOException {
		m_Reference = din.readShort();
	}

	/**
	 * getMessage -- return an empty array as there is no data for this request.
	 */
	public byte[] message() {
		byte results[] = new byte[2];

		results[0] = (byte) (m_Reference >> 8);
		results[1] = (byte) (m_Reference & 0xFF);

		return results;
	}

	/**
	 * Constructs a new <tt>Read FIFO Queue</tt> request instance.
	 */
	public ReadFIFOQueueRequest() {
		super();

		functionCode(CCModbusFunctionCode.READ_FIFO_QUEUE);
		dataLength(2);
	}
}
