package cc.creativecomputing.io.netty.codec.osc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class CCOSCEncoder extends MessageToByteEncoder<OSCPacket> {

	private static final BigDecimal MILLISECONDS_FROM_1900_TO_1970 = new BigDecimal("2208988800000");

	private static final byte ZERO = 0;
	private static final int PAD_BYTES = 4;

	/**
	 * Creates a new instance with the current system character set.
	 */
	public CCOSCEncoder() {
	}

	/**
	 * Pad buffer contents out to the nearest 'padByte' bytes.
	 * 
	 * @param start
	 * @param theBuf
	 */
	private void pad(ByteBuf theBuf, int thePadBytes) {
		int myPosition = theBuf.writerIndex();

		int myRemainder = myPosition % thePadBytes;
		if (myRemainder > 0) {
			int myPad = thePadBytes - myRemainder;
			while (myPad > 0) {
				theBuf.writeByte(ZERO);

				myPad--;
			}
		}
	}

	/**
	 * Write address.
	 * 
	 * @param theAddress
	 * @param buffer
	 */
	private void writeAddress(String theAddress, ByteBuf theBuf) {
		// Note: We cache ALL address strings.
		theBuf.writeBytes(OSCString.$().get(theAddress));
		theBuf.writeByte(ZERO);
		pad(theBuf, PAD_BYTES);
	}

	/**
	 * Write type.
	 * 
	 * @param theArgument
	 * @param theBuf
	 */
	public void writeType(Object theArgument, ByteBuf theBuf) {
		if (theArgument instanceof String) {
			theBuf.writeByte((byte) OSCDefinition.TYPE_STRING);
		} else if (theArgument instanceof Float) {
			theBuf.writeByte((byte) OSCDefinition.TYPE_FLOAT);
		} else if (theArgument instanceof Integer) {
			theBuf.writeByte((byte) OSCDefinition.TYPE_INT);
		} else if (theArgument instanceof BigInteger) {
			theBuf.writeByte((byte) OSCDefinition.TYPE_LONG);
		} else if (theArgument instanceof byte[]) {
			theBuf.writeByte((byte) OSCDefinition.TYPE_BLOB);
		} else if (theArgument instanceof Boolean) {
			theBuf.writeByte((byte) (((Boolean) theArgument) ? OSCDefinition.TYPE_TRUE : OSCDefinition.TYPE_FALSE));
		} else if (theArgument instanceof Object[]) {
			Object[] arrayArguments = (Object[]) theArgument;
			theBuf.writeByte((byte) OSCDefinition.TYPE_ARRAY_START);
			for (Object arrayArgument : arrayArguments) {
				writeType(arrayArgument, theBuf);
			}
			theBuf.writeByte((byte) OSCDefinition.TYPE_ARRAY_END);
		}
	}

	/**
	 * Write types.
	 * 
	 * @param theBuf The outgoing theBuf.
	 */
	private void writeTypes(List<Object> theArguments, ByteBuf theBuf) {
		theBuf.writeByte((byte) ',');

		for (Object argument : theArguments) {
			writeType(argument, theBuf);
		}

		theBuf.writeByte(ZERO);

		pad(theBuf, PAD_BYTES);
	}

	/**
	 * Write arguments.
	 * 
	 * @param theBuf
	 */
	private void writeArguments(List<Object> theArguments, ByteBuf theBuf) {
		if (theArguments.size() <= 0)
			return;
		
		for (Object argument : theArguments) {
			if (argument instanceof String) {
				// Adds to heap?
				theBuf.writeBytes(((String) argument).getBytes());

				pad(theBuf, PAD_BYTES);
			} else if (argument instanceof Float) {
				theBuf.writeFloat((Float) argument);
			} else if (argument instanceof Integer) {
				theBuf.writeInt((Integer) argument);
			} else if (argument instanceof BigInteger) {
				theBuf.writeLong(((BigInteger) argument).longValue());
			} else if (argument instanceof byte[]) {
				byte[] bytes = (byte[]) argument;

				theBuf.writeInt(bytes.length);
				theBuf.writeBytes((byte[]) argument);

				pad(theBuf, PAD_BYTES);
			} else if (argument instanceof Boolean) {
				theBuf.writeByte(((Boolean) argument) ? (byte) 0x01 : (byte) 0x00);
			}
		}

		pad(theBuf, PAD_BYTES);

	}

	/**
	 * Encode message.
	 * 
	 * @param theMessage
	 * @param theBuf
	 */
	public void encode(OSCMessage theMessage, ByteBuf theBuf) {
		// Write address
		writeAddress(theMessage.getAddress(), theBuf);

		// Types
		writeTypes(theMessage.getArguments(), theBuf);

		// Arguments
		writeArguments(theMessage.getArguments(), theBuf);
	}

	/**
	 * Encode bundle.
	 * 
	 * @param theBundle
	 * @param theBuf
	 */
	public void encode(OSCBundle theBundle, ByteBuf theBuf) {
		theBuf.writeBytes(OSCString.$().get(OSCDefinition.MESSAGE_BUNDLE_START));

		theBuf.writeByte(ZERO);

		// Time-stamp, note : java time runs from 1970 onwards.
		long millisecs = theBundle.getTimeTag() + MILLISECONDS_FROM_1900_TO_1970.longValue();
		theBuf.writeLong(millisecs);

		// ---------------------------------------------------------------
		// For each packet stuff bytes into theBuf.
		// ---------------------------------------------------------------
		for (OSCPacket message : theBundle.getMessages()) {
			// ---------------------------------------------------------------
			// Make a note of position to write size
			// ---------------------------------------------------------------
			int sizePos = theBuf.writerIndex();

			// Insert holder size.
			theBuf.writeInt(0);

			// ---------------------------------------------------------------
			// Make a note of start of contents
			// ---------------------------------------------------------------
			int startPos = theBuf.writerIndex();

			// Encode OSC packet
			this.encode(message, theBuf);

			// Mark
			theBuf.markWriterIndex();

			// Insert size of message
			int writerIndex = theBuf.writerIndex();
			int size = writerIndex - startPos;
			theBuf.writerIndex(sizePos);
			theBuf.writeInt(size);

			// Reset to mark.
			theBuf.resetWriterIndex();
		}

	}

	/**
	 * Encode appropriately.
	 * 
	 * TODO: Think about moving the encoding back into the domain objects as
	 * this is not good design.
	 * 
	 * @param thePacket
	 * @param theBuf
	 */
	private void encode(OSCPacket thePacket, ByteBuf theBuf) {
		if (thePacket instanceof OSCBundle) {
			encode((OSCBundle) thePacket, theBuf);
		} else {
			encode((OSCMessage) thePacket, theBuf);
		}
	}

	@Override
	protected void encode(ChannelHandlerContext theCTX, OSCPacket thePacket, ByteBuf theBuf) throws Exception {
		encode(thePacket, theBuf);
	}
}