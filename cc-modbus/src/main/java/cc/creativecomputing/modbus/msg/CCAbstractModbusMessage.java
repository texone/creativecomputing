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

import cc.creativecomputing.modbus.CCModbusFunctionCode;
import cc.creativecomputing.modbus.Modbus;
import cc.creativecomputing.modbus.util.ModbusUtil;

/**
 * Abstract class implementing a <tt>ModbusMessage</tt>. This class provides
 * specialised implementations with the functionality they have in common.
 * 
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public abstract class CCAbstractModbusMessage implements CCModbusMessage {

	// instance attributes
	private int _myTransactionID = Modbus.DEFAULT_TRANSACTION_ID;
	private int _myProtocolID = Modbus.DEFAULT_PROTOCOL_ID;
	private int _myDataLength;
	private int _myUnitID = Modbus.DEFAULT_UNIT_ID;
	private CCModbusFunctionCode _myFunctionCode;
	private boolean _myHeadless = false; // flag for headerless (serial)
											// transport

	/*** Header ******************************************/

	/**
	 * Tests if this message instance is headless.
	 * 
	 * @return true if headless, false otherwise.
	 */
	@Override
	public boolean isHeadless() {
		return _myHeadless;
	}

	@Override
	public void setHeadless() {
		_myHeadless = true;
	}

	/**
	 * Sets the headless flag of this message.
	 * 
	 * @param theHeadless true if headless, false otherwise.
	 */
	public void setHeadless(boolean theHeadless) {
		_myHeadless = theHeadless;
	}

	@Override
	public int transactionID() {
		return _myTransactionID & 0x0000FFFF;
	}

	/**
	 * Sets the transaction identifier of this <tt>ModbusMessage</tt>.
	 * 
	 * <p>
	 * The identifier must be a 2-byte (short) non negative integer value valid
	 * in the range of 0-65535.<br>
	 * 
	 * @param theTransactionID the transaction identifier as <tt>int</tt>.
	 */
	public void transactionID(int theTransactionID) {
		_myTransactionID = theTransactionID & 0x0000FFFF;
	}

	@Override
	public int protocolID() {
		return _myProtocolID;
	}

	/**
	 * Sets the protocol identifier of this <tt>ModbusMessage</tt>.
	 * <p>
	 * The identifier should be a 2-byte (short) non negative integer value
	 * valid in the range of 0-65535.<br>
	 * <p>
	 * 
	 * @param theProtocolID the protocol identifier as <tt>int</tt>.
	 */
	public void protocolID(int theProtocolID) {
		_myProtocolID = theProtocolID;
	}

	@Override
	public int dataLength() {
		return _myDataLength;
	}

	/**
	 * Sets the length of the data appended after the protocol header.
	 * 
	 * <p>
	 * Note that this library, a bit in contrast to the specification, counts
	 * the unit identifier and the function code to the header, because it is
	 * part of each and every message. Thus this method will add two (2) to the
	 * passed in integer value.
	 * 
	 * <p>
	 * This method does not include the length of a final CRC/LRC for those
	 * protocols which requirement.
	 * 
	 * @param theLength the data length as <tt>int</tt>.
	 */
	public void dataLength(int theLength) {
		if (theLength < 0 || theLength + 2 > 255)
			throw new IllegalArgumentException("Invalid length: " + theLength);

		_myDataLength = theLength + 2;
	}

	@Override
	public int unitID() {
		return _myUnitID;
	}

	/**
	 * Sets the unit identifier of this <tt>ModbusMessage</tt>.<br>
	 * The identifier should be a 1-byte non negative integer value valid in the
	 * range of 0-255.
	 * 
	 * @param theID the unit identifier number to be set.
	 */
	public void unitID(int theID) {
		_myUnitID = theID;
	}

	@Override
	public CCModbusFunctionCode functionCode() {
		return _myFunctionCode;
	}

	/**
	 * Sets the function code of this <tt>ModbusMessage</tt>.<br>
	 * The function code should be a 1-byte non negative integer value valid in
	 * the range of 0-127.<br>
	 * Function codes are ordered in conformance classes their values are
	 * specified in <tt>com.ghgande.j2mod.modbus.Modbus</tt>.
	 * 
	 * @param theCode the code of the function to be set.
	 * @see cc.creativecomputing.modbus.CCModbusFunctionCode
	 */
	protected void functionCode(CCModbusFunctionCode theCode) {
		_myFunctionCode = theCode;
	}

	/**
	 * Writes this message to the given <tt>DataOutput</tt>.
	 * 
	 * <p>
	 * This method must be overridden for any message type which doesn't follow
	 * this simple structure.
	 * 
	 * @param theDataOut a <tt>DataOutput</tt> instance.
	 * @throws IOException if an I/O related error occurs.
	 */
	@Override
	public void writeTo(DataOutput theDataOut) throws IOException {

		if (!isHeadless()) {
			theDataOut.writeShort(transactionID());
			theDataOut.writeShort(protocolID());
			theDataOut.writeShort(dataLength());
		}
		theDataOut.writeByte(unitID());
		theDataOut.writeByte(functionCode().id);

		writeData(theDataOut);
	}

	/**
	 * Writes the subclass specific data to the given DataOutput.
	 * 
	 * @param theDataOut the DataOutput to be written to.
	 * @throws IOException if an I/O related error occurs.
	 */
	public abstract void writeData(DataOutput theDataOut) throws IOException;

	/**
	 * readFrom -- Read the headers and data for a message. The sub-classes
	 * readData() method will then read in the rest of the message.
	 * 
	 * @param theDataIn -- Input source
	 */
	@Override
	public void readFrom(DataInput theDataIn) throws IOException {
		if (!isHeadless()) {
			transactionID(theDataIn.readUnsignedShort());
			protocolID(theDataIn.readUnsignedShort());
			_myDataLength = theDataIn.readUnsignedShort();
		}
		unitID(theDataIn.readUnsignedByte());
		functionCode(CCModbusFunctionCode.byID(theDataIn.readUnsignedByte()));
		readData(theDataIn);
	}

	/**
	 * Reads the subclass specific data from the given DataInput instance.
	 * 
	 * @param theDataIn the DataInput to read from.
	 * @throws IOException if an I/O related error occurs.
	 */
	public abstract void readData(DataInput theDataIn) throws IOException;

	/**
	 * getOutputLength -- Return the actual packet size in bytes
	 * 
	 * The actual packet size, plus any CRC or header, will be returned.
	 */
	@Override
	public int outputLength() {
		int l = 2 + dataLength();
		if (!isHeadless()) {
			l = l + 4;
		}
		return l;
	}

	/*** END Transportable *******************************/

	/**
	 * Returns the this message as hexadecimal string.
	 * 
	 * @return the message as hex encoded string.
	 */
	@Override
	public String hexMessage() {
		return ModbusUtil.toHex(this);
	}

}
