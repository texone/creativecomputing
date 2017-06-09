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
 ***/
package cc.creativecomputing.modbus.cmd;

import java.net.InetAddress;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.modbus.Modbus;
import cc.creativecomputing.modbus.io.ModbusTCPTransaction;
import cc.creativecomputing.modbus.msg.ReadCoilsRequest;
import cc.creativecomputing.modbus.msg.ReadCoilsResponse;
import cc.creativecomputing.modbus.net.TCPMasterConnection;

/**
 * Class that implements a simple command line tool for reading a digital input.
 * 
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ReadCoilsTestTCP {

	private static void printUsage() {
		System.out.println(
				"java com.ghgande.j2mod.modbus.cmd.ReadDiscretesTest <connection [String]> <unit [int8]> <register [int16]> <bitcount [int16]> {<repeat [int]>}");
	}

	public static void main(String[] args) {
		InetAddress addr = null;
		TCPMasterConnection con = null;
		
		ReadCoilsRequest req = null;
		ReadCoilsResponse res = null;
		ModbusTCPTransaction trans = null;
		int ref = 0;
		int count = 0;
		int repeat = 1;
		int unit = 1;

		try {

			// 1. Setup the parameters
			
			addr = InetAddress.getByName("172.18.26.206");
				

			// 2. Open the connection
			con = new TCPMasterConnection(addr);
			con.setPort(Modbus.DEFAULT_PORT);
			con.connect();

			req = new ReadCoilsRequest(0,16);
			req.unitID(1);
			if (Modbus.debug)
				CCLog.info("Request: " + req.hexMessage());

			// 4. Prepare the transaction
			trans = new ModbusTCPTransaction(con);
			trans.setRequest(req);
//06 ce 00 00 00 06 ff 01 00 64 00 00
//00 00 00 00 00 06 ff 01 00 64 00 64
//00 00 00 00 00 06 ff 01 00 64 00 64
//00 00 00 00 00 02 ff 01			
//00 00 00 00 00 00 00 00 04 01 01 10
				trans.setReconnecting(true);

			// 5. Execute the transaction repeat times
			int k = 0;
			do {
				trans.execute();

				res = (ReadCoilsResponse) trans.getResponse();

				if (Modbus.debug)
					CCLog.info("Response: " + res.hexMessage());

				System.out.println("Digital Inputs Status="
						+ res.getCoils().toString());

				k++;
			} while (true);

			// 6. Close the connection

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		System.exit(0);
	}
}