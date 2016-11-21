package cc.creativecomputing.io.netty.codec.osc;

import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;

public class OSCTypeDecoder extends ByteToMessageDecoder {

	public static final int PAD_BYTES = 4;

	public static byte[] BUNDLE = { (byte) 0x23, /* [#] */
			(byte) 0x62, /* [b] */
			(byte) 0x75, /* [u] */
			(byte) 0x6e, /* [n] */
			(byte) 0x64, /* [d] */
			(byte) 0x6c, /* [l] */
			(byte) 0x65, /* [e] */
			(byte) 0x0 /* [^@ (NUL)] */
	};

	/**
	 * Compare byte buffers.
	 * 
	 * @param theBufferA
	 * @param theBufferB
	 * 
	 * @return true if buffers are the same.
	 */
	public static boolean compareBuffer(byte[] theBufferA, byte[] theBufferB) {
		boolean status = false;

		if (theBufferA.length == theBufferB.length) {
			boolean match = true;
			int index = theBufferA.length - 1;
			while (index >= 0 && match) {
				match = theBufferA[index] == theBufferB[index];
				index--;
			}

			status = match;
		}

		return status;
	}

	/**
	 * Calculates the number of bytes to read which would pad the current buffer
	 * position out to 'padBytes'.
	 * 
	 * @param position The current position
	 * @param padBytes The padding count.
	 * 
	 * @return Number of padding bytes.
	 */
	public static int padBytes(ByteBuf buffer, int padBytes) {
		int pad = 0;

		int position = buffer.readerIndex();

		int remainder = position % padBytes;
		if (remainder > 0) {
			pad = padBytes - remainder;
		}

		return pad;
	}

	public static enum OSCType {
		OSC_MESSAGE, OSC_BUNDLE
	};
	
	private static class CCOSCDelimiterDecoder extends DelimiterBasedFrameDecoder{
		
		CCOSCDelimiterDecoder(){
			super(8192, false, Delimiters.nulDelimiter());
		}
		
		public ByteBuf decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception{
			return (ByteBuf)super.decode(ctx, byteBuf);
		}
		
	}
	
	private static class CCOSCStringDecoder extends StringDecoder{
		public String decode(ChannelHandlerContext ctx, ByteBuf byteBuf){
			return null;
		}
	}

	private CCOSCDelimiterDecoder delimiterBasedFrameDecoder;
	private CCOSCStringDecoder stringDecoder;

	public OSCTypeDecoder() {
		delimiterBasedFrameDecoder = new CCOSCDelimiterDecoder();
		
		stringDecoder = new CCOSCStringDecoder();
	}
	
	/**
	 * Skip pad
	 * 
	 * @param byteBuf
	 */
	private void skipPadding(ByteBuf byteBuf, int padding)
	{
		// Calculate padding.
		int padBytes = padBytes(byteBuf, padding);
		
		// Skip the padding.
		byteBuf.readerIndex(byteBuf.readerIndex() + padBytes);
	}
	
	/**
	 * Extract argument.
	 * 
	 * @param type
	 * @param byteBuf
	 * 
	 * @return The argument object.
	 * @throws Exception
	 */
	private Object extractArgument(ChannelHandlerContext ctx, char type, ByteBuf byteBuf) throws Exception
	{
		Object argument = null;
		
		switch (type)
		{
			case OSCDefinition.TYPE_STRING:
				ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
				argument = stringDecoder.decode(ctx, data);
				break;
			case OSCDefinition.TYPE_FLOAT:
				argument = byteBuf.readFloat();
				break;
			case OSCDefinition.TYPE_INT:
				argument = byteBuf.readInt();
				break;
			case OSCDefinition.TYPE_LONG:
				argument = byteBuf.readLong();
				break;
			case OSCDefinition.TYPE_BLOB:
//				argument = (ByteBuf) lengthFieldBasedFrameDecoder.decode(ctx, byteBuf);
				skipPadding(byteBuf, PAD_BYTES);
				break;
			case OSCDefinition.TYPE_TRUE:
				argument = byteBuf.readBoolean();
				break;
			case OSCDefinition.TYPE_FALSE:
				argument = byteBuf.readBoolean();
				break;
			case OSCDefinition.TYPE_ARRAY_START:
				// TODO
				break;
			case OSCDefinition.TYPE_ARRAY_END:
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
	 * @param ctx
	 * @param byteBuf
	 * 
	 * @return Populated message or null if not complete.
	 * 
	 * @throws Exception
	 */
	public OSCMessage decodeMessage(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception
	{
		OSCMessage oscMessage = null;
		
		skipPadding(byteBuf, PAD_BYTES);
		
		ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
		
		if (data != null)
		{
			String address = stringDecoder.decode(ctx, data);
			
			if (address != null)
			{
				oscMessage = new OSCMessage(address);
				
				skipPadding(byteBuf, PAD_BYTES);
				
				data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
				
				if (data != null)
				{
					String types = stringDecoder.decode(ctx, data);
					
					if (types != null)
					{
						skipPadding(byteBuf, PAD_BYTES);
						
						int typeCount = types.length();
						
						// Note we skip leading comma
						for (int index = 1; index < typeCount; index++)
						{
							char typeChar = types.charAt(index);
							
							Object argument = extractArgument(ctx, typeChar, byteBuf);
							
							// Note type
							oscMessage.getTypes().add(typeChar);
							
							// Note argument object.
							oscMessage.addArgument(argument);
						}
					}
				}
			}
		}
		
		return oscMessage;
	}
	
	/**
	 * Decode message and populate OSCBundle from pool.
	 * 
	 */
	public OSCBundle decodeBundle(ChannelHandlerContext ctx, ByteBuf theBuf) throws Exception
	{
		OSCBundle oscBundle = null;


		// Read past "bundle message"
		ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, theBuf);

		if (data != null)
		{
			// Time-stamp (2208988800000)
			long timeStamp = theBuf.readLong();

			while (data != null)
			{
				// Message length
//				data = (ByteBuf) lengthFieldBasedFrameDecoder.decode(ctx, theBuf);

				if (data != null)
				{
					// Message
					OSCMessage oscMessage = decodeMessage(ctx, theBuf);

					if (oscMessage != null)
					{
						if (oscBundle == null)
						{
							oscBundle = new OSCBundle();
							oscBundle.setTimeTag(timeStamp);
						}

						oscBundle.addMessage(oscMessage);
					}
				}
			}
		}

		CCLog.info(oscBundle.toString());

		return oscBundle;
	}

	@Override
	public void decode(ChannelHandlerContext theContext, ByteBuf theBuf, List<Object> theObjects) throws Exception {
		OSCType type = null;

		int readerIndex = theBuf.readerIndex();

		ByteBuf typeBuf = (ByteBuf) delimiterBasedFrameDecoder.decode(theContext, theBuf);

		if (typeBuf != null)
		{
			if (typeBuf.getByte(0) == BUNDLE[0])
			{
				int readableBytes = typeBuf.readableBytes();
				
				if (readableBytes >= BUNDLE.length)
				{
					boolean match = compareBuffer(typeBuf.array(), BUNDLE);

					if (match)
					{
						type = OSCType.OSC_BUNDLE;

					}
				}
			}
			else
			{
				type = OSCType.OSC_MESSAGE;
			}
		}

		// We must reset our source buffer
		theBuf.readerIndex(readerIndex);

		// We must reset our source buffer
		theBuf.readerIndex(readerIndex);

		if (type != null) {
			switch (type) {
			case OSC_MESSAGE:
				OSCMessage oscMessage = decodeMessage(theContext, theBuf);

				if (oscMessage != null) {
					CCLog.info("Receive OSCMessage: " + oscMessage.getAddress() + ", " + oscMessage.getArguments());
					
					theObjects.add(oscMessage);
				}
				break;
			case OSC_BUNDLE:
				OSCBundle oscBundle = decodeBundle(theContext, theBuf);

				if (oscBundle != null) {
					CCLog.info("Receive OSCBundle: " + oscBundle.getMessages().size());

					theObjects.add(oscBundle);
				}
				break;

			default:
				break;
			}

		}
	}

}