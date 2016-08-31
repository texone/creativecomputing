package cc.creativecomputing.modbus.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import cc.creativecomputing.modbus.Modbus;
import cc.creativecomputing.modbus.io.ModbusRTUTransport;
import cc.creativecomputing.modbus.io.ModbusTCPTransport;
import cc.creativecomputing.modbus.io.ModbusTransport;
import cc.creativecomputing.modbus.io.ModbusUDPTransport;
import cc.creativecomputing.modbus.util.SerialParameters;

/**
 * Create a <tt>ModbusListener</tt> from an URI-like specifier.
 * 
 * @author Julie
 * 
 */
public class ModbusMasterFactory {
	public static ModbusTransport createModbusMaster(String address) {
		String parts[] = address.split(":");
		if (parts == null || parts.length < 2)
			throw new IllegalArgumentException("missing connection information");

		if (parts[0].toLowerCase().equals("device")) {
			/*
			 * Create a ModbusSerialListener with the default Modbus values of
			 * 19200 baud, no parity, using the specified device. If there is an
			 * additional part after the device name, it will be used as the
			 * Modbus unit number.
			 */
			SerialParameters parms = new SerialParameters();
			parms.setPortName(parts[1]);
			parms.setBaudRate(19200);
			parms.setDatabits(8);
			parms.setEcho(false);
			parms.setParity(SerialPort.PARITY_NONE);
			parms.setFlowControlIn(SerialPort.FLOWCONTROL_NONE);

			try {
				ModbusRTUTransport transport = new ModbusRTUTransport();
				CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(parms.getPortName());
				if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) return null;
				
				CommPort port = (SerialPort) portId.open("serial madness", 2000);
//				CommPort port = new RXTXPort(parms.getPortName());

				transport.setCommPort(port);
				transport.setEcho(false);

				return transport;
			} catch (PortInUseException e) {
				return null;
			} catch (IOException e) {
				return null;
			} catch (NoSuchPortException e) {
				return null;
			}
		} else if (parts[0].toLowerCase().equals("tcp")) {
			/*
			 * Create a ModbusTCPListener with the default interface value. The
			 * second optional value is the TCP port number and the third
			 * optional value is the Modbus unit number.
			 */
			String hostName = parts[1];
			int port = Modbus.DEFAULT_PORT;

			if (parts.length > 2)
				port = Integer.parseInt(parts[2]);

			try {
				Socket socket = new Socket(hostName, port);
				if (Modbus.debug)
					System.err.println("connecting to " + socket);
				
				ModbusTCPTransport transport = new ModbusTCPTransport(socket);

				return transport;
			} catch (UnknownHostException x) {
				return null;
			} catch (IOException e) {
				return null;
			}
		} else if (parts[0].toLowerCase().equals("udp")) {
			/*
			 * Create a ModbusUDPListener with the default interface value. The
			 * second optional value is the TCP port number and the third
			 * optional value is the Modbus unit number.
			 */
			String hostName = parts[1];
			int port = Modbus.DEFAULT_PORT;

			if (parts.length > 2)
				port = Integer.parseInt(parts[2]);

			UDPMasterTerminal terminal;
			try {
				terminal = new UDPMasterTerminal(
						InetAddress.getByName(hostName));
				terminal.setRemotePort(port);
				terminal.activate();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			ModbusUDPTransport transport = terminal.getModbusTransport();

			return transport;
		} else
			throw new IllegalArgumentException("unknown type " + parts[0]);
	}
}
