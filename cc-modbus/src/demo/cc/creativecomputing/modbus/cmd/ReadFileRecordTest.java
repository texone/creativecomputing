/**
 * Java Modbus Library (j2mod)
 * Copyright 2012, Julianne Frances Haugh
 * d/b/a greenHouse Gas and Electric
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
 */
package cc.creativecomputing.modbus.cmd;

import java.util.Arrays;

import cc.creativecomputing.modbus.Modbus;
import cc.creativecomputing.modbus.CCModbusException;
import cc.creativecomputing.modbus.ModbusIOException;
import cc.creativecomputing.modbus.CCModbusSlaveException;
import cc.creativecomputing.modbus.io.ModbusSerialTransport;
import cc.creativecomputing.modbus.io.ModbusTransaction;
import cc.creativecomputing.modbus.io.ModbusTransport;
import cc.creativecomputing.modbus.msg.CCExceptionResponse;
import cc.creativecomputing.modbus.msg.CCAbstractModbusResponse;
import cc.creativecomputing.modbus.msg.ReadCommEventCounterRequest;
import cc.creativecomputing.modbus.msg.ReadCommEventCounterResponse;
import cc.creativecomputing.modbus.msg.ReadFileRecordRequest;
import cc.creativecomputing.modbus.msg.ReadFileRecordResponse;
import cc.creativecomputing.modbus.msg.ReadFileRecordRequest.RecordRequest;
import cc.creativecomputing.modbus.msg.ReadFileRecordResponse.RecordResponse;
import cc.creativecomputing.modbus.net.ModbusMasterFactory;

/**
 * ReadFileRecordText -- Exercise the "READ FILE RECORD" Modbus
 * message.
 * 
 * @author Julie
 * @version 0.96
 */
public class ReadFileRecordTest {

	/**
	 * usage -- Print command line arguments and exit.
	 */
	private static void usage() {
		System.out.println(
				"Usage: ReadFileRecord connection unit file record registers [repeat]");
		
		System.exit(1);
	}

	public static void main(String[] args) {
		ModbusTransport	transport = null;
		ReadFileRecordRequest request = null;
		ReadFileRecordResponse response = null;
		ModbusTransaction	trans = null;
		int			unit = 0;
		int			file = 0;
		int			record = 0;
		int			registers = 0;
		int			requestCount = 1;

		/*
		 * Get the command line parameters.
		 */
		if (args.length < 5 || args.length > 6)
			usage();
		
		try {
			transport = ModbusMasterFactory.createModbusMaster(args[0]);
			if (transport instanceof ModbusSerialTransport) {
				((ModbusSerialTransport) transport).setReceiveTimeout(500);
				if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null)
					((ModbusSerialTransport) transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
				else
					((ModbusSerialTransport) transport).setBaudRate(19200);
				
				Thread.sleep(2000);
			}
			unit = Integer.parseInt(args[1]);
			file = Integer.parseInt(args[2]);
			record = Integer.parseInt(args[3]);
			registers = Integer.parseInt(args[4]);
			
			if (args.length > 5)
				requestCount = Integer.parseInt(args[5]);
		} catch (NumberFormatException x) {
			System.err.println("Invalid parameter");
			usage();
		} catch (Exception ex) {
			ex.printStackTrace();
			usage();
			System.exit(1);
		}

		try {
			for (int i = 0; i < requestCount; i++) {
				/*
				 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
				request = new ReadFileRecordRequest();
				request.unitID(unit);
				
				RecordRequest recordRequest =
						request.new RecordRequest(file, record + i, registers);
				request.addRequest(recordRequest);
				
				if (Modbus.debug)
					System.out.println("Request: " + request.hexMessage());

				/*
				 * Setup the transaction.
				 */
				trans = transport.createTransaction();
				trans.setRequest(request);

				/*
				 * Execute the transaction.
				 */
				try {
					trans.execute();
				} catch (CCModbusSlaveException x) {
					System.err.println("Slave Exception: " +
							x.getLocalizedMessage());
					continue;
				} catch (ModbusIOException x) {
					System.err.println("I/O Exception: " +
							x.getLocalizedMessage());
					continue;					
				} catch (CCModbusException x) {
					System.err.println("Modbus Exception: " +
							x.getLocalizedMessage());
					continue;					
				}

				CCAbstractModbusResponse dummy = trans.getResponse();
				if (dummy == null) {
					System.err.println("No response for transaction " + i);
					continue;
				}
				if (dummy instanceof CCExceptionResponse) {
					CCExceptionResponse exception = (CCExceptionResponse) dummy;

					System.err.println(exception);

					continue;
				} else if (dummy instanceof ReadFileRecordResponse) {
					response = (ReadFileRecordResponse) dummy;

					if (Modbus.debug)
						System.out.println("Response: "
								+ response.hexMessage());

					int count = response.getRecordCount();
					for (int j = 0;j < count;j++) {
						RecordResponse data = response.getRecord(j);
						short values[] = new short[data.getWordCount()];
						for (int k = 0;k < data.getWordCount();k++)
							values[k] = data.getRegister(k).toShort();
						
						System.out.println("data[" + i + "][" + j + "] = " +
								Arrays.toString(values));
					}
					continue;
				}

				/*
				 * Unknown message.
				 */
				System.out.println(
						"Unknown Response: " + dummy.hexMessage());
			}
			
			/*
			 * Now read the number of events sent by the device.  Maybe it will
			 * tell us something useful.
			 */
			ReadCommEventCounterRequest eventRequest = new ReadCommEventCounterRequest();
			eventRequest.unitID(unit);
			
			/*
			 * Setup the transaction.
			 */
			trans = transport.createTransaction();
			trans.setRequest(eventRequest);

			/*
			 * Execute the transaction.
			 */
			try {
				trans.execute();
				CCAbstractModbusResponse dummy = trans.getResponse();
				
				if (dummy instanceof ReadCommEventCounterResponse) {
					ReadCommEventCounterResponse eventResponse = (ReadCommEventCounterResponse) dummy;
					System.out.println("  Events: " + eventResponse.getEventCount());
				}
			} catch (CCModbusException x) {
				// Do nothing -- this isn't required.					
			}

			/*
			 * Teardown the connection.
			 */
			transport.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}
}
