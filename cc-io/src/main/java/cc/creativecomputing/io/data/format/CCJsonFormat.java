package cc.creativecomputing.io.data.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.io.format.CCDataSerializable;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataException;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataUtil;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 */
public class CCJsonFormat implements CCDataFormat<String> {

	private long _myCharacter;
	private boolean _myEof;
	private long _myIndex;
	private long _myLine;
	private char _myPrevious;
	private Reader _myReader;
	private boolean _myUsePrevious;

	/**
	 * Back up one character. This provides a sort of lookahead capability, so that
	 * you can test for a digit or letter before attempting to parse the next number
	 * or identifier.
	 */
	protected void back() throws CCDataException {
		if (_myUsePrevious || _myIndex <= 0) {
			throw new CCDataException("Stepping back two steps is not supported");
		}
		_myIndex -= 1;
		_myCharacter -= 1;
		_myUsePrevious = true;
		_myEof = false;
	}

	/**
	 * Get the hex value of a character (base16).
	 * 
	 * @param c A character between '0' and '9' or between 'A' and 'F' or between
	 *          'a' and 'f'.
	 * @return An int between 0 and 15, or -1 if c was not a hex digit.
	 */
	protected int dehexchar(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'A' && c <= 'F') {
			return c - ('A' - 10);
		}
		if (c >= 'a' && c <= 'f') {
			return c - ('a' - 10);
		}
		return -1;
	}

	protected boolean end() {
		return _myEof && !_myUsePrevious;
	}

	/**
	 * Determine if the source string still contains characters that next() can
	 * consume.
	 * 
	 * @return true if not yet at the end of the source.
	 */
	protected boolean more() throws CCDataException {
		next();
		if (end()) {
			return false;
		}
		back();
		return true;
	}

	/**
	 * Get the next character in the source string.
	 *
	 * @return The next character, or 0 if past the end of the source string.
	 */
	protected char next() throws CCDataException {
		int c;
		if (_myUsePrevious) {
			_myUsePrevious = false;
			c = _myPrevious;
		} else {
			try {
				c = _myReader.read();
			} catch (IOException exception) {
				throw new CCDataException(exception);
			}

			if (c <= 0) { // End of stream
				_myEof = true;
				c = 0;
			}
		}
		_myIndex += 1;
		if (_myPrevious == '\r') {
			_myLine += 1;
			_myCharacter = c == '\n' ? 0 : 1;
		} else if (c == '\n') {
			_myLine += 1;
			_myCharacter = 0;
		} else {
			_myCharacter += 1;
		}
		_myPrevious = (char) c;
		return _myPrevious;
	}

	/**
	 * Consume the next character, and check that it matches a specified character.
	 * 
	 * @param c The character to match.
	 * @return The character.
	 * @throws CCDataException if the character does not match.
	 */
	protected char next(char c) throws CCDataException {
		char n = next();
		if (n != c) {
			throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
		}
		return n;
	}

	/**
	 * Get the next n characters.
	 *
	 * @param n The number of characters to take.
	 * @return A string of n characters.
	 * @throws CCDataException Substring bounds error if there are not n characters
	 *                         remaining in the source string.
	 */
	private String next(int n) throws CCDataException {
		if (n == 0) {
			return "";
		}

		char[] chars = new char[n];
		int pos = 0;

		while (pos < n) {
			chars[pos] = next();
			if (end()) {
				throw syntaxError("Substring bounds error");
			}
			pos += 1;
		}
		return new String(chars);
	}

	/**
	 * Get the next char in the string, skipping whitespace.
	 * 
	 * @throws CCDataException
	 * @return A character, or 0 if there are no more characters.
	 */
	private char nextClean() throws CCDataException {
		for (;;) {
			char c = next();
			if (c == 0 || c > ' ') {
				return c;
			}
		}
	}

	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in single
	 * quotes, but an implementation is allowed to accept them.
	 * 
	 * @param quote The quoting character, either <code>"</code>&nbsp;<small>(double
	 *              quote)</small> or <code>'</code>&nbsp;<small>(single
	 *              quote)</small>.
	 * @return A String.
	 * @throws CCDataException Unterminated string.
	 */
	private String nextString(char quote) throws CCDataException {
		char c;
		StringBuilder sb = new StringBuilder();
		for (;;) {
			c = next();
			switch (c) {
			case 0:
			case '\n':
			case '\r':
				throw syntaxError("Unterminated string");
			case '\\':
				c = next();
				switch (c) {
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'u':
					sb.append((char) Integer.parseInt(next(4), 16));
					break;
				case '"':
				case '\'':
				case '\\':
				case '/':
					sb.append(c);
					break;
				default:
					throw syntaxError("Illegal escape.");
				}
				break;
			default:
				if (c == quote) {
					return sb.toString();
				}
				sb.append(c);
			}
		}
	}

	/**
	 * Get the text up but not including the specified character or the end of line,
	 * whichever comes first.
	 * 
	 * @param delimiter A delimiter character.
	 * @return A string.
	 */
	protected String nextTo(char delimiter) throws CCDataException {
		StringBuilder sb = new StringBuilder();
		for (;;) {
			char c = next();
			if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	/**
	 * Get the text up but not including one of the specified delimiter characters
	 * or the end of line, whichever comes first.
	 * 
	 * @param delimiters A set of delimiter characters.
	 * @return A string, trimmed.
	 */
	protected String nextTo(String delimiters) throws CCDataException {
		char c;
		StringBuilder sb = new StringBuilder();
		for (;;) {
			c = next();
			if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	/**
	 * Get the next value. The value can be a Boolean, Double, Integer, JSONArray,
	 * JSONObject, Long, or String, or the JSONObject.NULL object.
	 * 
	 * @throws CCDataException If syntax error.
	 *
	 * @return An object.
	 */
	private Object nextValue() throws CCDataException {
		char c = nextClean();
		String string;

		switch (c) {
		case '"':
		case '\'':
			return nextString(c);
		case '{':
			back();
			return read(new CCDataObject());
		case '[':
			back();
			return read(new CCDataArray());
		}

		/*
		 * Handle unquoted text. This could be the values true, false, or null, or it
		 * can be a number. An implementation (such as this one) is allowed to also
		 * accept non-standard forms.
		 *
		 * Accumulate characters until we reach the end of the text or a formatting
		 * character.
		 */

		StringBuilder sb = new StringBuilder();
		while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
			sb.append(c);
			c = next();
		}
		back();

		string = sb.toString().trim();
		if ("".equals(string)) {
			throw syntaxError("Missing value");
		}
		return CCDataUtil.stringToValue(string);
	}

	/**
	 * Skip characters until the next character is the requested character. If the
	 * requested character is not found, no characters are skipped.
	 * 
	 * @param to A character to skip to.
	 * @return The requested character, or zero if the requested character is not
	 *         found.
	 */
	@SuppressWarnings("unused")
	private char skipTo(char to) throws CCDataException {
		char c;
		try {
			long startIndex = _myIndex;
			long startCharacter = _myCharacter;
			long startLine = _myLine;
			_myReader.mark(1000000);
			do {
				c = next();
				if (c == 0) {
					_myReader.reset();
					_myIndex = startIndex;
					_myCharacter = startCharacter;
					_myLine = startLine;
					return c;
				}
			} while (c != to);
		} catch (IOException exception) {
			throw new CCDataException(exception);
		}
		back();
		return c;
	}

	/**
	 * Make a JSONException to signal a syntax error.
	 *
	 * @param message The error message.
	 * @return A JSONException object, suitable for throwing
	 */
	protected CCDataException syntaxError(String message) {
		return new CCDataException(message + toString());
	}

	/**
	 * Make a printable string of this JSONTokener.
	 *
	 * @return " at {index} [character {character} line {line}]"
	 */
	public String toString() {
		return " at " + _myIndex + " [character " + _myCharacter + " line " + _myLine + "]";
	}

	private CCDataArray read(CCDataArray theArray) throws CCDataException {
		if (nextClean() != '[') {
			throw syntaxError("A JSONArray text must start with '['");
		}
		if (nextClean() != ']') {
			back();
			for (;;) {
				if (nextClean() == ',') {
					back();
					theArray.add(null);
				} else {
					back();
					theArray.add(nextValue());
				}
				switch (nextClean()) {
				case ',':
					if (nextClean() == ']') {
						return theArray;
					}
					back();
					break;
				case ']':
					return theArray;
				default:
					throw syntaxError("Expected a ',' or ']'");
				}
			}
		}
		return theArray;
	}

	protected CCDataObject read(CCDataObject theParent) {
		char c;
		String key;
		char nextClean = nextClean();
		if (nextClean != '{') {
			StringBuffer myBuffer = new StringBuffer();
			int ic;
			try {
				ic = _myReader.read();
				while(ic >= 0) {
					myBuffer.append((char)ic);
					ic = _myReader.read();
				}
				CCLog.info(myBuffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			throw syntaxError("A JSONObject text must begin with '{'");
		}
		for (;;) {
			c = nextClean();
			switch (c) {
			case 0:
				throw syntaxError("A JSONObject text must end with '}'");
			case '}':
				return theParent;
			default:
				back();
				key = nextValue().toString();
			}

			// The key is followed by ':'.

			c = nextClean();
			if (c != ':') {
				throw syntaxError("Expected a ':' after a key");
			}
			theParent.putIfAbsent(key, nextValue());

			// Pairs are separated by ','.

			switch (nextClean()) {
			case ';':
			case ',':
				if (nextClean() == '}') {
					return theParent;
				}
				back();
				break;
			case '}':
				return theParent;
			default:
				throw syntaxError("Expected a ',' or '}'");
			}
		}
	}

	private void read(Reader reader, CCDataObject theData) {
		_myReader = reader.markSupported() ? reader : new BufferedReader(reader);
		_myEof = false;
		_myUsePrevious = false;
		_myPrevious = 0;
		_myIndex = 0;
		_myCharacter = 1;
		_myLine = 1;

//		int c;
//		try {
//			c = _myReader.read();
//			while(c!= -1) {
//				System.out.print((char)c);
//				c = _myReader.read();
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		char next = nextClean();
		back();
		CCLog.info(next);
		if(next == '[') {
			CCDataArray myArray = new CCDataArray();
			read(myArray);
			theData.put("array", myArray);
		}else {
			read(theData);
		}

		try {
			_myReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write the contents of the {@linkplain CCDataArray} as JSON text to a writer.
	 * For compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor The number of spaces to add to each level of indentation.
	 * @param indent       The indention of the top level.
	 * @return The writer.
	 * @throws CCDataException
	 */
	private void write(List<?> theArray, Writer writer, int indentFactor, int indent) throws CCDataException {
		try {
			boolean commanate = false;
			int length = theArray.size();
			writer.write('[');

			if (length == 1) {
				writeValue(writer, theArray.get(0), indentFactor, indent);
			} else if (length != 0) {
				final int newindent = indent + indentFactor;

				for (int i = 0; i < length; i += 1) {
					if (commanate) {
						writer.write(',');
					}
					if (indentFactor > 0) {
						writer.write('\n');
					}
					indent(writer, newindent);
					writeValue(writer, theArray.get(i), indentFactor, newindent);
					commanate = true;
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, indent);
			}
			writer.write(']');
		} catch (IOException e) {
			throw new CCDataException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private final void writeValue(Writer writer, Object value, int indentFactor, int indent)
			throws CCDataException, IOException {
		if (value == null || value.equals(null)) {
			writer.write("null");
		} else if (value instanceof CCDataObject) {
			write((CCDataObject) value, writer, indentFactor, indent);
		} else if (value instanceof CCDataArray) {
			write((CCDataArray) value, writer, indentFactor, indent);
		} else if (value instanceof Map) {
			write(new CCDataObject((Map<String, Object>) value), writer, indentFactor, indent);
		} else if (value instanceof Collection) {
			write(new CCDataArray((Collection<Object>) value), writer, indentFactor, indent);
		} else if (value.getClass().isArray()) {
			write(new CCDataArray(value), writer, indentFactor, indent);
		} else if (value instanceof Number) {
			writer.write(CCDataUtil.numberToString((Number) value));
		} else if (value instanceof Boolean) {
			writer.write(value.toString());
		} else if (value instanceof CCDataSerializable) {
			writeValue(writer, ((CCDataSerializable) value).data(), indentFactor, indent);
		} else {
			CCDataUtil.quote(value.toString(), writer);
		}
	}

	private final void indent(Writer writer, int indent) throws IOException {
		for (int i = 0; i < indent; i += 1) {
			writer.write(' ');
		}
	}

	/**
	 * Write the contents of the {@linkplain CCDataObject} as JSON text to a writer.
	 * For compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @return The writer.
	 * @throws CCDataException
	 */
	private void write(Map<String, Object> theObject, Writer theWriter, int indentFactor, int indent)
			throws CCDataException {
		try {
			boolean commanate = false;
			final int length = theObject.size();
			theWriter.write('{');

			if (length == 1) {
				String key = new ArrayList<String>(theObject.keySet()).get(0);
				theWriter.write(CCDataUtil.quote(key.toString()));
				theWriter.write(':');
				if (indentFactor > 0) {
					theWriter.write(' ');
				}
				writeValue(theWriter, theObject.get(key), indentFactor, indent);
			} else if (length != 0) {
				final int newindent = indent + indentFactor;
				for (String key : theObject.keySet()) {
					if (commanate) {
						theWriter.write(',');
					}
					if (indentFactor > 0) {
						theWriter.write('\n');
					}
					indent(theWriter, newindent);
					theWriter.write(CCDataUtil.quote(key.toString()));
					theWriter.write(':');
					if (indentFactor > 0) {
						theWriter.write(' ');
					}
					writeValue(theWriter, theObject.get(key), indentFactor, newindent);
					commanate = true;
				}
				if (indentFactor > 0) {
					theWriter.write('\n');
				}
				indent(theWriter, indent);
			}
			theWriter.write('}');
		} catch (IOException exception) {
			throw new CCDataException(exception);
		}
	}

	@Override
	public CCDataObject load(Path theDocumentPath, boolean theIgnoreLineFeed, OpenOption... theOptions) {
		try {
			CCDataObject myResult = new CCDataObject();
			InputStream myStream = Files.newInputStream(theDocumentPath, theOptions);
			read(new InputStreamReader(Files.newInputStream(theDocumentPath, theOptions)), myResult);
			myStream.close();
			return myResult;
		} catch (IOException e) {
			throw new CCDataException(e);
		}
	}

	

	@Override
	public CCDataObject load(URL theDocumentURL, boolean theIgnoreLineFeed, String theUser, String theKey) {
		try {
			CCDataObject myResult = new CCDataObject();

			URLConnection myUrlConnection = theDocumentURL.openConnection();

			if (theUser != null && theKey != null) {
				String userpass = theUser + ":" + theKey;
				String basicAuth = "Basic " + CCStringUtil.printBase64Binary(userpass.getBytes());

				myUrlConnection.setRequestProperty("Authorization", basicAuth);
			}

			read(new InputStreamReader(myUrlConnection.getInputStream()), myResult);
			return myResult;
		} catch (IOException e) {
			throw new CCDataException(e);
		}
	}

	@Override
	public CCDataObject parse(String theDocument) {
		CCDataObject myResult = new CCDataObject();
		read(new StringReader(theDocument), myResult);
		return myResult;
	}

	@Override
	public void save(Map<String, Object> theObject, Path theDocumentUrl, OpenOption... theOptions) {
		CCNIOUtil.saveString(theDocumentUrl, toFormatType(theObject), theOptions);
	}

	/**
	 * Make a pretty printed JSON text of this {@linkplain CCDataObject}.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation of
	 *         the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 * @throws CCDataException If the object contains an invalid number.
	 */
	@Override
	public String toFormatType(Map<String, Object> theObject) {
		StringWriter w = new StringWriter();
		synchronized (w.getBuffer()) {
			write(theObject, w, 2, 0);
			return w.toString();
		}
	}

	@Override
	public CCDataFormat<String> create() {
		return new CCJsonFormat();
	}
}
