package cc.creativecomputing.io.netty.codec.osc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class CCOSCEncoder extends MessageToByteEncoder<CCOSCPacket> {
	
	
	private CharsetEncoder _myStringEncoder = Charset.forName("US-ASCII").newEncoder();
	
	public static final String MESSAGE_BUNDLE_START = "#bundle";

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
	
	private void writeString(String theAddress, ByteBuf theBuf){
		try {
			theBuf.writeBytes(_myStringEncoder.encode(CharBuffer.wrap(theAddress)).array());
		} catch (CharacterCodingException e) {
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
		writeString(theAddress, theBuf);
		theBuf.writeByte(ZERO);
		pad(theBuf, PAD_BYTES);
	}

	/**
	 * Write type.
	 * 
	 * @param theArgument
	 * @param theBuf
	 */
	private void writeType(Object theArgument, ByteBuf theBuf) {
		if (theArgument instanceof String) {
			theBuf.writeByte((byte) CCOSCTypeTag.STRING.typeChar);
		} else if (theArgument instanceof Float) {
			theBuf.writeByte((byte) CCOSCTypeTag.FLOAT.typeChar);
		} else if (theArgument instanceof Double) {
			theBuf.writeByte((byte) CCOSCTypeTag.DOUBLE.typeChar);
		} else if (theArgument instanceof Integer) {
			theBuf.writeByte((byte) CCOSCTypeTag.INT.typeChar);
		} else if (theArgument instanceof Long) {
			theBuf.writeByte((byte) CCOSCTypeTag.LONG.typeChar);
		} else if (theArgument instanceof byte[]) {
			theBuf.writeByte((byte) CCOSCTypeTag.BLOB.typeChar);
		} else if (theArgument instanceof Boolean) {
			theBuf.writeByte((byte) (((Boolean) theArgument) ? CCOSCTypeTag.TRUE.typeChar : CCOSCTypeTag.FALSE.typeChar));
		} else if (theArgument instanceof Object[]) {
			Object[] arrayArguments = (Object[]) theArgument;
			theBuf.writeByte((byte) CCOSCTypeTag.ARRAY_START.typeChar);
			for (Object arrayArgument : arrayArguments) {
				writeType(arrayArgument, theBuf);
			}
			theBuf.writeByte((byte) CCOSCTypeTag.ARRAY_END.typeChar);
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
			} else if (argument instanceof Double) {
				theBuf.writeDouble((Double) argument);
			} else if (argument instanceof Integer) {
				theBuf.writeInt((Integer) argument);
			} else if (argument instanceof Long) {
				theBuf.writeLong((Long) argument);
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
	public void encode(CCOSCMessage theMessage, ByteBuf theBuf) {
		// Write address
		writeAddress(theMessage.address(), theBuf);

		// Types
		writeTypes(theMessage.arguments(), theBuf);

		// Arguments
		writeArguments(theMessage.arguments(), theBuf);
		
		byte[] bytes = new byte[theBuf.capacity()];
		theBuf.getBytes(0, bytes);
	}

	/**
	 * Encode bundle.
	 * 
	 * @param theBundle
	 * @param theBuf
	 */
	public void encode(CCOSCBundle theBundle, ByteBuf theBuf) {
		writeString(MESSAGE_BUNDLE_START, theBuf);

		theBuf.writeByte(ZERO);

		// Time-stamp, note : java time runs from 1970 onwards.
		long millisecs = theBundle.timeTag() + MILLISECONDS_FROM_1900_TO_1970.longValue();
		theBuf.writeLong(millisecs);

		// ---------------------------------------------------------------
		// For each packet stuff bytes into theBuf.
		// ---------------------------------------------------------------
		for (CCOSCPacket message : theBundle.messages()) {
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
	private void encode(CCOSCPacket thePacket, ByteBuf theBuf) {
		if (thePacket instanceof CCOSCBundle) {
			encode((CCOSCBundle) thePacket, theBuf);
		} else {
			encode((CCOSCMessage) thePacket, theBuf);
		}
	}

	@Override
	protected void encode(ChannelHandlerContext theCTX, CCOSCPacket thePacket, ByteBuf theBuf) {
		encode(thePacket, theBuf);
	}
}