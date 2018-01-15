package cc.creativecomputing.io.netty.codec.osc;

/**
 * An OSC Type Tag String is an OSC-string beginning with the character ',' 
 * (comma) followed by a sequence of characters corresponding exactly to 
 * the sequence of OSC Arguments in the given message. 
 * <p>
 * Each character after the comma is called an OSC Type Tag and represents 
 * the type of the corresponding OSC Argument. (The requirement for OSC 
 * Type Tag Strings to start with a comma makes it easier for the recipient 
 * of an OSC Message to determine whether that OSC Message is lacking an OSC 
 * Type Tag String.)
 */
public enum CCOSCTypeTag {
	/**
	 * int32
	 */
	INT('i'),
	/**
	 * 64 bit big-endian two's complement integer
	 */
	LONG('h'),
	/**
	 * float32
	 */
	FLOAT('f'),
	/**
	 * 64 bit ("double") IEEE 754 floating point number
	 */
	DOUBLE('d'),
	/**
	 * OSC-string
	 */
	STRING('s'),
	/**
	 * OSC-blob
	 */
	BLOB('b'),
	/**
	 * OSC-timetag
	 */
	TIMETAG('t'),
	/**
	 * an ascii character, sent as 32 bits
	 */
	CHARACTER('c'),
	/**
	 * Alternate type represented as an OSC-string (for example, for systems that differentiate "symbols" from "strings")
	 */
	ALTERNATE_STRING('S'),
	/**
	 * 32 bit RGBA color
	 */
	RGBA_COLOR('r'),
	/**
	 * 4 byte MIDI message. Bytes from MSB to LSB are: port id, status byte, data1, data2
	 */
	MIDI('m'),
	/**
	 * Nil. No bytes are allocated in the argument data.
	 */
	NIL('N'),
	/**
	 * Infinitum. No bytes are allocated in the argument data.
	 */
	INFINITUM('I'),
	/**
	 * True. No bytes are allocated in the argument data.
	 */
	TRUE('T'),
	/**
	 * False. No bytes are allocated in the argument data.
	 */
	FALSE('F'),
	/**
	 * Indicates the beginning of an array. The tags following are for data in the Array until a close brace tag is reached.
	 */
	ARRAY_START('['),
	/**
	 * Indicates the end of an array.
	 */
	ARRAY_END(']');

	public final char typeChar;
	
	CCOSCTypeTag(char theTypeChar){
		typeChar = theTypeChar;
	}
	
	public static CCOSCTypeTag getTye(char theType){
		switch(theType){
		case 'i': return INT;
		case 'h': return LONG;
		case 'f': return FLOAT;
		case 'd': return DOUBLE;
		case 's': return STRING;
		case 'b': return BLOB;

		case 't': return TIMETAG;
		case 'S': return ALTERNATE_STRING;

		case 'I': return INFINITUM;
		case 'T': return TRUE;
		case 'F': return FALSE;
		case '[': return ARRAY_START;
		case ']': return ARRAY_END;
		}
		throw new RuntimeException("Undifined type");
	}
}