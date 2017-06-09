package cc.creativecomputing.modbus;

import java.io.EOFException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.modbus.msg.CCAbstractModbusRequest;
import cc.creativecomputing.modbus.util.ModbusUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public abstract class CCModbusRequestCodec extends CCModbusCodec{
	
	private  class CCModbusRequestDecoder extends ByteToMessageDecoder{
		@Override
		protected void decode(ChannelHandlerContext theContext, ByteBuf theBuf, List<Object> theObjects) throws Exception {
			String myJsonString = theBuf.toString(CharsetUtil.UTF_8);
			theBuf.readBytes(theBuf.readableBytes()).release();
			try{
				theObjects.add(CCDataIO.parseToObject(myJsonString, CCDataFormats.JSON));
			}catch(Exception e){
				e.printStackTrace();
//				throw new RuntimeException(e);
			}
			
			try {
				CCAbstractModbusRequest req = null;

				synchronized (theBuf) {

					if (!headless) {
						if (theBuf.readableBytes() > 0)
							throw new EOFException(
									"Premature end of stream (Header truncated).");

						/*
						 * The transaction ID must be treated as an unsigned short in
						 * order for validation to work correctly.
						 */
						int transaction = theBuf.readShort() & 0x0000FFFF;
						int protocol = theBuf.readShort();
						int count = theBuf.readShort();

						if (theBuf.readableBytes() > 0)
							throw new ModbusIOException(
									"Premature end of stream (Message truncated).");

//						if (Modbus.debug)
//							CCLog.error("Read: "
//									+ ModbusUtil.toHex(buffer, 0, count + 6));
						
						theBuf.skipBytes(6);

						int unit = theBuf.readByte();
						int functionCode = theBuf.readUnsignedByte();

						req = CCAbstractModbusRequest.createModbusRequest(CCModbusFunctionCode.byID(functionCode));
						req.unitID(unit);
						req.setHeadless(false);

						req.transactionID(transaction);
						req.protocolID(protocol);
						req.dataLength(count);

						req.readFrom(new ByteBufInputStream(theBuf));
					} else {
						
						/*
						 * This is a headless request.
						 */
						int unit = theBuf.readByte();
						int function = theBuf.readByte();

						req = CCAbstractModbusRequest.createModbusRequest(CCModbusFunctionCode.byID(function));
						req.unitID(unit);
						req.setHeadless(true);

						req.readData(new ByteBufInputStream(theBuf));

						/*
						 * Discard the CRC. This is a TCP/IP connection, which has
						 * proper error correction and recovery.
						 */
						theBuf.readShort();
						if (Modbus.debug)
							System.err.println("Read: "	+ req.hexMessage());
					}
				}
			} catch (EOFException eoex) {
				throw new ModbusIOException("End of File", true);
			} catch (SocketTimeoutException x) {
				throw new ModbusIOException("Timeout reading request");
			} catch (SocketException sockex) {
				throw new ModbusIOException("Socket Exception", true);
			} catch (Exception ex) {
				throw new ModbusIOException("I/O exception - failed to read.");
			}
		}
	}
	
	@Override
	public ChannelInboundHandler[] decoder() {
		return new  ByteToMessageDecoder[]{new JsonObjectDecoder(), new CCModbusRequestDecoder()};
	}

}
