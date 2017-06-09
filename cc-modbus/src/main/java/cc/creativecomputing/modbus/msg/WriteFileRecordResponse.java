//License
/***
 * Java Modbus Library (a2mod)
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
 * Java Modbus Library (a2mod)
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

import cc.creativecomputing.modbus.CCModbusFunctionCode;
import cc.creativecomputing.modbus.procimg.SimpleRegister;

/**
 * Class implementing a <tt>WriteFileRecordResponse</tt>.
 * 
 * @author Julie
 * @version 0.96
 */
public final class WriteFileRecordResponse extends CCAbstractModbusResponse {
	private int _myByteCount;
	private RecordResponse[] _myRecords;

	/**
	 * Constructs a new <tt>WriteFileRecordResponse</tt> instance.
	 */
	public WriteFileRecordResponse() {
		super();

		functionCode(CCModbusFunctionCode.WRITE_FILE_RECORD);
		dataLength(7);
	}

	public class RecordResponse {
		private int _myFileNumber;
		private int _myRecordNumber;
		private int _myWordCount;
		private byte _myData[];

		public int getFileNumber() {
			return _myFileNumber;
		}

		public int getRecordNumber() {
			return _myRecordNumber;
		}

		public int getWordCount() {
			return _myWordCount;
		}

		public SimpleRegister getRegister(int register) {
			if (register < 0 || register >= _myWordCount) {
				throw new IndexOutOfBoundsException("0 <= " + register + " < " + _myWordCount);
			}
			byte b1 = _myData[register * 2];
			byte b2 = _myData[register * 2 + 1];

			SimpleRegister result = new SimpleRegister(b1, b2);
			return result;
		}

		/**
		 * getResponseSize -- return the size of the response in bytes.
		 */
		public int getResponseSize() {
			return 7 + _myWordCount * 2;
		}

		public void getResponse(byte[] response, int offset) {
			response[offset++] = 6;
			response[offset++] = (byte) (_myFileNumber >> 8);
			response[offset++] = (byte) (_myFileNumber & 0xFF);
			response[offset++] = (byte) (_myRecordNumber >> 8);
			response[offset++] = (byte) (_myRecordNumber & 0xFF);
			response[offset++] = (byte) (_myWordCount >> 8);
			response[offset++] = (byte) (_myWordCount & 0xFF);

			System.arraycopy(_myData, 0, response, offset, _myData.length);
		}

		public byte[] getResponse() {
			byte[] response = new byte[7 + 2 * _myWordCount];

			getResponse(response, 0);

			return response;
		}

		public RecordResponse(int file, int record, short[] values) {
			_myFileNumber = file;
			_myRecordNumber = record;
			_myWordCount = values.length;
			_myData = new byte[_myWordCount * 2];

			int offset = 0;
			for (int i = 0; i < _myWordCount; i++) {
				_myData[offset++] = (byte) (values[i] >> 8);
				_myData[offset++] = (byte) (values[i] & 0xFF);
			}
		}
	}

	/**
	 * getRequestSize -- return the total request size. This is useful for
	 * determining if a new record can be added.
	 * 
	 * @returns size in bytes of response.
	 */
	public int getResponseSize() {
		if (_myRecords == null)
			return 1;

		int size = 1;
		for (int i = 0; i < _myRecords.length; i++)
			size += _myRecords[i].getResponseSize();

		return size;
	}

	/**
	 * getRequestCount -- return the number of record requests in this message.
	 */
	public int getRequestCount() {
		if (_myRecords == null)
			return 0;

		return _myRecords.length;
	}

	/**
	 * getRecord -- return the record request indicated by the reference
	 */
	public RecordResponse getRecord(int index) {
		return _myRecords[index];
	}

	/**
	 * addResponse -- add a new record response.
	 */
	public void addResponse(RecordResponse response) {
		if (response.getResponseSize() + getResponseSize() > 248)
			throw new IllegalArgumentException();

		if (_myRecords == null)
			_myRecords = new RecordResponse[1];
		else {
			RecordResponse old[] = _myRecords;
			_myRecords = new RecordResponse[old.length + 1];

			System.arraycopy(old, 0, _myRecords, 0, old.length);
		}
		_myRecords[_myRecords.length - 1] = response;

		dataLength(getResponseSize());
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(message());
	}

	@Override
	public void readData(DataInput din) throws IOException {
		_myByteCount = din.readUnsignedByte();

		_myRecords = new RecordResponse[0];

		for (int offset = 1; offset + 7 < _myByteCount;) {
			int function = din.readUnsignedByte();
			int file = din.readUnsignedShort();
			int record = din.readUnsignedShort();
			int count = din.readUnsignedShort();

			offset += 7;

			if (function != 6)
				throw new IOException();

			if (record < 0 || record >= 10000)
				throw new IOException();

			if (count < 0 || count >= 126)
				throw new IOException();

			short registers[] = new short[count];
			for (int j = 0; j < count; j++) {
				registers[j] = din.readShort();
				offset += 2;
			}
			RecordResponse dummy[] = new RecordResponse[_myRecords.length + 1];
			if (_myRecords.length > 0)
				System.arraycopy(_myRecords, 0, dummy, 0, _myRecords.length);

			_myRecords = dummy;
			_myRecords[_myRecords.length - 1] = new RecordResponse(file, record, registers);
		}
	}

	@Override
	public byte[] message() {
		byte results[] = new byte[getResponseSize()];

		results[0] = (byte) (getResponseSize() - 1);

		int offset = 1;
		for (int i = 0; i < _myRecords.length; i++) {
			_myRecords[i].getResponse(results, offset);
			offset += _myRecords[i].getResponseSize();
		}
		return results;
	}
}