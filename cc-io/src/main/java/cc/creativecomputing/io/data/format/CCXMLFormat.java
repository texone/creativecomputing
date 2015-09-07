package cc.creativecomputing.io.data.format;

import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.io.format.CCDataHolder;
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
 * The XMLTokener extends the JSONTokener to provide additional methods for the
 * parsing of XML texts.
 * 
 * @author JSON.org
 * @version 2014-05-03
 */
public class CCXMLFormat extends CCJsonFormat {

	/** The Character '&amp;'. */
	public static final Character AMP = '&';

	/** The Character '''. */
	public static final Character APOS = '\'';

	/** The Character '!'. */
	public static final Character BANG = '!';

	/** The Character '='. */
	public static final Character EQ = '=';

	/** The Character '>'. */
	public static final Character GT = '>';

	/** The Character '&lt;'. */
	public static final Character LT = '<';

	/** The Character '?'. */
	public static final Character QUEST = '?';

	/** The Character '"'. */
	public static final Character QUOT = '"';

	/** The Character '/'. */
	public static final Character SLASH = '/';

	/**
	 * The table of entity values. It initially contains Character values for
	 * amp, apos, gt, lt, quot.
	 */
	public static final java.util.HashMap<String, Character> entity;

	static {
		entity = new java.util.HashMap<String, Character>(8);
		entity.put("amp", AMP);
		entity.put("apos", APOS);
		entity.put("gt", GT);
		entity.put("lt", LT);
		entity.put("quot", QUOT);
	}

	/**
	 * Get the text in the CDATA block.
	 * 
	 * @return The string up to the <code>]]&gt;</code>.
	 * @throws CCDataException If the <code>]]&gt;</code> is not found.
	 */
	private String nextCDATA() throws CCDataException {
		char c;
		int i;
		StringBuilder sb = new StringBuilder();
		for (;;) {
			c = next();
			if (end()) {
				throw syntaxError("Unclosed CDATA");
			}
			sb.append(c);
			i = sb.length() - 3;
			if (i >= 0 && sb.charAt(i) == ']' && sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
				sb.setLength(i);
				return sb.toString();
			}
		}
	}

/**
     * Get the next XML outer token, trimming whitespace. There are two kinds
     * of tokens: the '<' character which begins a markup tag, and the content
     * text between markup tags.
     *
     * @return  A string, or a '<' Character, or null if there is no more
     * source text.
     * @throws CCDataException
     */
	private Object nextContent() throws CCDataException {
		char c;
		StringBuilder sb;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		if (c == 0) {
			return null;
		}
		if (c == '<') {
			return LT;
		}
		sb = new StringBuilder();
		for (;;) {
			if (c == '<' || c == 0) {
				back();
				return sb.toString().trim();
			}
			if (c == '&') {
				sb.append(nextEntity(c));
			} else {
				sb.append(c);
			}
			c = next();
		}
	}

	/**
	 * Return the next entity. These entities are translated to Characters:
	 * <code>&amp;  &apos;  &gt;  &lt;  &quot;</code>.
	 * 
	 * @param ampersand An ampersand character.
	 * @return A Character or an entity String if the entity is not recognized.
	 * @throws CCDataException If missing ';' in XML entity.
	 */
	private Object nextEntity(char ampersand) throws CCDataException {
		StringBuilder sb = new StringBuilder();
		for (;;) {
			char c = next();
			if (Character.isLetterOrDigit(c) || c == '#' || c == '_') {
				sb.append(Character.toLowerCase(c));
			} else if (c == ';') {
				break;
			} else {
				throw syntaxError("Missing ';' in XML entity: &" + sb +" " + c);
			}
		}
		String string = sb.toString();
		Object object = entity.get(string);
		return object != null ? object : ampersand + string + ";";
	}

	/**
	 * Returns the next XML meta token. This is used for skipping over <!...>
	 * and <?...?> structures.
	 * 
	 * @return Syntax characters (<code>< > / = ! ?</code>) are returned as
	 *         Character, and strings and names are returned as Boolean. We
	 *         don't care what the values actually are.
	 * @throws CCDataException If a string is not properly closed or if the XML
	 *             is badly structured.
	 */
	private Object nextMeta() throws CCDataException {
		char c;
		char q;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		switch (c) {
		case 0:
			throw syntaxError("Misshaped meta tag");
		case '<':
			return LT;
		case '>':
			return GT;
		case '/':
			return SLASH;
		case '=':
			return EQ;
		case '!':
			return BANG;
		case '?':
			return QUEST;
		case '"':
		case '\'':
			q = c;
			for (;;) {
				c = next();
				if (c == 0) {
					throw syntaxError("Unterminated string");
				}
				if (c == q) {
					return Boolean.TRUE;
				}
			}
		default:
			for (;;) {
				c = next();
				if (Character.isWhitespace(c)) {
					return Boolean.TRUE;
				}
				switch (c) {
				case 0:
				case '<':
				case '>':
				case '/':
				case '=':
				case '!':
				case '?':
				case '"':
				case '\'':
					back();
					return Boolean.TRUE;
				}
			}
		}
	}

	/**
	 * Get the next XML Token. These tokens are found inside of angle brackets.
	 * It may be one of these characters: <code>/ > = ! ?</code> or it may be a
	 * string wrapped in single quotes or double quotes, or it may be a name.
	 * 
	 * @return a String or a Character.
	 * @throws CCDataException If the XML is not well formed.
	 */
	private Object nextToken() throws CCDataException {
		char c;
		char q;
		StringBuilder sb;
		do {
			c = next();
		} while (Character.isWhitespace(c));
		switch (c) {
		case 0:
			throw syntaxError("Misshaped element");
		case '<':
			throw syntaxError("Misplaced '<'");
		case '>':
			return GT;
		case '/':
			return SLASH;
		case '=':
			return EQ;
		case '!':
			return BANG;
		case '?':
			return QUEST;

			// Quoted string

		case '"':
		case '\'':
			q = c;
			sb = new StringBuilder();
			for (;;) {
				c = next();
				if (c == 0) {
					throw syntaxError("Unterminated string");
				}
				if (c == q) {
					return sb.toString();
				}
				if (c == '&') {
					sb.append(nextEntity(c));
				} else {
					sb.append(c);
				}
			}
		default:

			// Name

			sb = new StringBuilder();
			for (;;) {
				sb.append(c);
				c = next();
				if (Character.isWhitespace(c)) {
					return sb.toString();
				}
				switch (c) {
				case 0:
					return sb.toString();
				case '>':
				case '/':
				case '=':
				case '!':
				case '?':
				case '[':
				case ']':
					back();
					return sb.toString();
				case '<':
				case '"':
				case '\'':
					throw syntaxError("Bad character in a name");
				}
			}
		}
	}

	/**
	 * Skip characters until past the requested string. If it is not found, we
	 * are left at the end of the source with a result of false.
	 * 
	 * @param to A string to skip past.
	 * @throws CCDataException
	 */
	private boolean skipPast(String to) throws CCDataException {
		boolean b;
		char c;
		int i;
		int j;
		int offset = 0;
		int length = to.length();
		char[] circle = new char[length];

		/*
		 * First fill the circle buffer with as many characters as are in the to
		 * string. If we reach an early end, bail.
		 */

		for (i = 0; i < length; i++) {
			c = next();
			if (c == 0) {
				return false;
			}
			circle[i] = c;
		}

		/* We will loop, possibly for all of the remaining characters. */

		for (;;) {
			j = offset;
			b = true;

			/* Compare the circle buffer with the to string. */

			for (i = 0; i < length; i++) {
				if (circle[j] != to.charAt(i)) {
					b = false;
					break;
				}
				j += 1;
				if (j >= length) {
					j -= length;
				}
			}

			/* If we exit the loop with b intact, then victory is ours. */

			if (b) {
				return true;
			}

			/* Get the next character. If there isn't one, then defeat is ours. */

			c = next();
			if (c == 0) {
				return false;
			}
			/*
			 * Shove the character in the circle buffer and advance the circle
			 * offset. The offset is mod n.
			 */
			circle[offset] = c;
			offset += 1;
			if (offset >= length) {
				offset -= length;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void accumulate(Map<String,Object> context, String theKey, Object theValue, CCDataHolder<?, ?> theDataHolder) throws CCDataException {
		CCDataUtil.testValidity(theValue);
		Object object = context.get(theKey);
		if (object == null) {
			context.put(theKey, theValue instanceof List ? theDataHolder.createList().add(theValue) : theValue);
		} else if (object instanceof List) {
			((List<Object>) object).add(theValue);
		} else {
			List<Object> myArray = theDataHolder.createList();
			myArray.add(object);
			myArray.add(theValue);
			context.put(theKey, myArray);
		}
	}

	/**
	 * Scan the content following the named tag, attaching it to the context.
	 * 
	 * @param x The XMLTokener containing the source string.
	 * @param context The JSONObject that will include the new material.
	 * @param name The tag name.
	 * @return true if the close tag is processed.
	 * @throws CCDataException
	 */
	private boolean parse(Map<String,Object> context, String name, CCDataHolder<?, ?> theDataHolder) throws CCDataException {
		char c;
		int i;
		CCDataObject myObject = null;
		String myString;
		String myTagName;

		// Test for and skip past these forms:
		// <!-- ... -->
		// <! ... >
		// <![ ... ]]>
		// <? ... ?>
		// Report errors for these forms:
		// <>
		// <=
		// <<

		Object myToken = nextToken();

		// <!

		if (myToken == BANG) {
			c = next();
			if (c == '-') {
				if (next() == '-') {
					skipPast("-->");
					return false;
				}
				back();
			} else if (c == '[') {
				myToken = nextToken();
				if ("CDATA".equals(myToken)) {
					if (next() == '[') {
						myString = nextCDATA();
						if (myString.length() > 0) {
							accumulate(context, "content", myString, theDataHolder);
						}
						return false;
					}
				}
				throw syntaxError("Expected 'CDATA['");
			}
			i = 1;
			do {
				myToken = nextMeta();
				if (myToken == null) {
					throw syntaxError("Missing '>' after '<!'.");
				} else if (myToken == LT) {
					i++;
				} else if (myToken == GT) {
					i -= 1;
				}
			} while (i > 0);
			return false;
		} else if (myToken == QUEST) {

			// <?

			skipPast("?>");
			return false;
		} else if (myToken == SLASH) {

			// Close tag </

			myToken = nextToken();
			if (name == null) {
				throw syntaxError("Mismatched close tag " + myToken);
			}
			if (!myToken.equals(name)) {
				throw syntaxError("Mismatched " + name + " and " + myToken);
			}
			if (nextToken() != GT) {
				throw syntaxError("Misshaped close tag");
			}
			return true;

		} else if (myToken instanceof Character) {
			throw syntaxError("Misshaped tag");

			// Open tag <

		} else {
			myTagName = (String) myToken;
			myToken = null;
			myObject = new CCDataObject();
			for (;;) {
				if (myToken == null) {
					myToken = nextToken();
				}

				// attribute = myValue

				if (myToken instanceof String) {
					myString = (String) myToken;
					myToken = nextToken();
					if (myToken == EQ) {
						myToken = nextToken();
						if (!(myToken instanceof String)) {
							throw syntaxError("Missing value");
						}
						myObject.accumulate(myString, CCDataUtil.stringToValue((String) myToken));
						myToken = null;
					} else {
						myObject.accumulate(myString, "");
					}

					// Empty tag <.../>

				} else if (myToken == SLASH) {
					if (nextToken() != GT) {
						throw syntaxError("Misshaped tag");
					}
					if (myObject.size() > 0) {
						accumulate(context,myTagName, myObject, theDataHolder);
					} else {
						accumulate(context,myTagName, "", theDataHolder);
					}
					return false;

					// Content, between <...> and </...>

				} else if (myToken == GT) {
					for (;;) {
						myToken = nextContent();
						if (myToken == null) {
							if (myTagName != null) {
								throw syntaxError("Unclosed tag " + myTagName);
							}
							return false;
						} else if (myToken instanceof String) {
							myString = (String) myToken;
							if (myString.length() > 0) {
								myObject.accumulate("content", CCDataUtil.stringToValue(myString));
							}

							// Nested element

						} else if (myToken == LT) {
							if (parse(myObject, myTagName, theDataHolder)) {
								if (myObject.size() == 0) {
									accumulate(context,myTagName, "", theDataHolder);
								} else if (myObject.size() == 1 && myObject.get("content") != null) {
									accumulate(context,myTagName, myObject.get("content"), theDataHolder);
								} else {
									accumulate(context,myTagName, myObject, theDataHolder);
								}
								return false;
							}
						}
					}
				} else {
					throw syntaxError("Misshaped tag");
				}
			}
		}
	}

	/**
	 * Replace special characters with XML escapes:
	 * 
	 * <pre>
	 * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
	 * &lt; <small>(less than)</small> is replaced by &amp;lt;
	 * &gt; <small>(greater than)</small> is replaced by &amp;gt;
	 * &quot; <small>(double quote)</small> is replaced by &amp;quot;
	 * </pre>
	 * 
	 * @param theString The string to be escaped.
	 * @return The escaped string.
	 */
	private String escape(String theString) {
		StringBuilder sb = new StringBuilder(theString.length());
		for (int i = 0, length = theString.length(); i < length; i++) {
			char c = theString.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Convert a {@linkplain CCDataObject} into a well-formed, element-normal
	 * XML string.
	 * 
	 * @param theObject A JSONObject.
	 * @param theTagName The optional name of the enclosing tag.
	 * @return A string.
	 * @throws CCDataException
	 */
	private String toString(Object theObject, String theTagName) throws CCDataException {
		StringBuilder sb = new StringBuilder();

		if (theObject instanceof Map) {

			// Emit <tagName>

			if (theTagName != null) {
				sb.append('<');
				sb.append(theTagName);
				sb.append('>');
			}

			// Loop thru the keys.

			Map<String, Object> myObject = (Map) theObject;
			for (String key : myObject.keySet()) {
				Object myValue = myObject.get(key);
				if (myValue == null) {
					myValue = "";
				}
				String string = myValue instanceof String ? (String) myValue : null;

				// Emit content in body

				if ("content".equals(key)) {
					if (myValue instanceof CCDataArray) {
						CCDataArray myArray = (CCDataArray) myValue;
						int length = myArray.size();
						for (int i = 0; i < length; i++) {
							if (i > 0) {
								sb.append('\n');
							}
							sb.append(escape(myArray.get(i).toString()));
						}
					} else {
						sb.append(escape(myValue.toString()));
					}

					// Emit an array of similar keys

				} else if (myValue instanceof List) {
					List<Object> myArray = (List) myValue;
					int size = myArray.size();
					for (int i = 0; i < size; i++) {
						myValue = myArray.get(i);
						if (myValue instanceof List) {
							sb.append('<');
							sb.append(key);
							sb.append('>');
							sb.append(toString(myValue));
							sb.append("</");
							sb.append(key);
							sb.append('>');
						} else {
							sb.append(toString(myValue, key));
						}
					}
				} else if ("".equals(myValue)) {
					sb.append('<');
					sb.append(key);
					sb.append("/>");

					// Emit a new tag <k>

				} else {
					sb.append(toString(myValue, key));
				}
			}
			if (theTagName != null) {

				// Emit the </tagname> close tag

				sb.append("</");
				sb.append(theTagName);
				sb.append('>');
			}
			return sb.toString();

			// XML does not have good support for arrays. If an array appears in
			// a place
			// where XML is lacking, synthesize an <array> element.

		} else {
			if (theObject.getClass().isArray()) {
				theObject = new CCDataArray(theObject);
			}
			if (theObject instanceof CCDataArray) {
				CCDataArray myArray = (CCDataArray) theObject;
				int size = myArray.size();
				for (int i = 0; i < size; i++) {
					sb.append(toString(myArray.get(i), theTagName == null ? "array" : theTagName));
				}
				return sb.toString();
			} else {
				String string = (theObject == null) ? "null" : escape(theObject.toString());
				return (theTagName == null) ? "\"" + string + "\"" : (string.length() == 0) ? "<" + theTagName + "/>"
						: "<" + theTagName + ">" + string + "</" + theTagName + ">";
			}
		}
	}

	/**
	 * Convert a {@linkplain CCDataObject} into a well-formed, element-normal
	 * XML string.
	 * 
	 * @param theObject A JSONObject.
	 * @return A string.
	 * @throws CCDataException
	 */
	private String toString(Object theObject) throws CCDataException {
		return toString(theObject, null);
	}

	@Override
	protected void read(Map<String,Object> theParent, CCDataHolder<?, ?> theDataHolder) {
		while (more() && skipPast("<")) {
			parse(theParent, null, theDataHolder);
		}
	}

	@Override
	public String toFormatType(Map<String, Object> theObject) {
		return toString(theObject);
	}
}
