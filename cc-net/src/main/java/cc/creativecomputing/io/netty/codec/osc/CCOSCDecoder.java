package cc.creativecomputing.io.netty.codec.osc;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class CCOSCDecoder extends ByteToMessageDecoder {

	/**
	 * Calculates the number of bytes to read which would pad the current buffer
	 * position out to 'padBytes'.
	 * 
	 * @param position The current position
	 * @param thePadBytes The padding count.
	 * 
	 * @return Number of padding bytes.
	 */
	public static int padBytes(ByteBuf theBuffer, int thePadBytes) {
		int pad = 0;

		int position = theBuffer.readerIndex();

		int remainder = position % thePadBytes;
		if (remainder > 0) {
			pad = thePadBytes - remainder;
		}

		return pad;
	}

	public CCOSCDecoder() {
	}

	/**
	 * Extract argument.
	 * 
	 * @param theType
	 * @param theByteBuf
	 * 
	 * @return The argument object.
	 * @throws Exception
	 */
	private Object extractArgument(char theType, ByteBuf theByteBuf) {
		Object argument = null;

		switch (CCOSCTypeTag.getTye(theType)) {
		case STRING:
			argument = readString(theByteBuf);
			break;
		case FLOAT:
			argument = theByteBuf.readFloat();
			break;
		case DOUBLE:
			argument = theByteBuf.readDouble();
			break;
		case INT:
			argument = theByteBuf.readInt();
			break;
		case LONG:
			argument = theByteBuf.readLong();
			break;
		case BLOB:
			// argument = (ByteBuf) lengthFieldBasedFrameDecoder.decode(ctx,
			// byteBuf);
			skipToAlign(theByteBuf);
			break;
		case TRUE:
			argument = true;
			break;
		case FALSE:
			argument = false;
			break;
		case ARRAY_START:
			// TODO
			break;
		case ARRAY_END:
			// TODO
			break;
		default:
			break;
		}

		return argument;
	}

	/**
	 * Decode message.
	 * 
	 * @param theContext
	 * @param theByteBuf
	 * 
	 * @return Populated message or null if not complete.
	 * 
	 * @throws Exception
	 */
	public CCOSCMessage decodeMessage(String theAddress, ByteBuf theByteBuf) {
		if (theByteBuf.readByte() != 0x2C)
			throw new CCOSCException("Illegal OSC Message Format");
		
		String myTypes = readString(theByteBuf);
		
		
		CCOSCMessage myResult = new CCOSCMessage(theAddress);

		// Note we skip leading comma
		for (char myTypeChar:myTypes.toCharArray()) {
			Object argument = extractArgument(myTypeChar, theByteBuf);
			myResult.addArgument(argument);
		}

		byte[] bytes = new byte[theByteBuf.readableBytes()];
		theByteBuf.readBytes(bytes);
		
		theByteBuf.readBytes(theByteBuf.readableBytes()).release();
		
		return myResult;
	}

	/**
	 * Decode message and populate OSCBundle from pool.
	 * 
	 */
	public CCOSCBundle decodeBundle(ChannelHandlerContext theContext, ByteBuf theBuf){
		CCOSCBundle oscBundle = new CCOSCBundle();
		oscBundle.timeTag(theBuf.readLong());
		while(theBuf.readableBytes() > 0) {
			decode(theContext, theBuf, oscBundle.messages());
		}
		return oscBundle;
	}

	/**
	 * Reads a null terminated string from the current buffer position
	 * 
	 * @param theBuf buffer to read from. position and limit must be set appropriately. new position will be right after the
	 *        terminating zero byte when the method returns
	 * 
	 */
	private String readString(ByteBuf theBuf) {
		final int myStartIndex = theBuf.readerIndex();
		
		while (theBuf.readByte() != 0)
			; 
		
		final int myBytesLength = theBuf.readerIndex() - myStartIndex;
		
		final byte[] myBytes = new byte[myBytesLength];
		theBuf.getBytes(myStartIndex,myBytes);

		skipToAlign(theBuf);
		
		return new String(myBytes, 0, myBytesLength - 1);
	}
	
	/**
	 * Advances the current buffer position to an integer of four bytes. The position is not altered if it is already
	 * aligned to a four byte boundary.
	 * 
	 * @param b the buffer to advance
	 * 
	 * @throws IllegalArgumentException in case the skipping exceeds the provided buffer limit
	 */
	private void skipToAlign(ByteBuf theBuf) {
		theBuf.readerIndex((theBuf.readerIndex() + 3) & ~3);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void decode(ChannelHandlerContext theContext, ByteBuf theBuf, List theObjects) {
		final String myCommand = readString(theBuf);
		
		if(myCommand.equals(CCOSCBundle.TAG)){
			CCOSCBundle myBundle = decodeBundle(theContext, theBuf);
			if (myBundle != null) {
	
				theObjects.add(myBundle);
			}
		}else{
			CCOSCMessage myMessage = decodeMessage(myCommand, theBuf);
			if (myMessage != null) {
				
				theObjects.add(myMessage);
			}
		}
	}

}