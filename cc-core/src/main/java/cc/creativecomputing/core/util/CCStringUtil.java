/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.core.util;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCStringUtil {

	/**
	 * Remove whitespace characters from the beginning and ending of a String. Works like String.trim() but includes the
	 * unicode nbsp character as well.
	 */
	static public String trim(String str) {
		return str.replace('\u00A0', ' ').trim();

		/*
		 * int left = 0; int right = str.length() - 1; while ((left <= right) && (WHITESPACE.indexOf(str.charAt(left))
		 * != -1)) left++; if (left == right) return ""; while (WHITESPACE.indexOf(str.charAt(right)) != -1) --right;
		 * return str.substring(left, right-left+1);
		 */
	}

	/**
	 * Join an array of Strings together as a single String, separated by the whatever's passed in for the separator.
	 */
	static public String join(String str[], char separator) {
		return join(str, String.valueOf(separator));
	}

	/**
	 * Join an array of Strings together as a single String, separated by the whatever's passed in for the separator.
	 * <P>
	 * To use this on numbers, first pass the array to nf() or nfs() to get a list of String objects, then use join on
	 * that.
	 * 
	 * <PRE>
	 * 
	 * e.g.String stuff[] = { &quot;apple&quot;, &quot;bear&quot;, &quot;cat&quot; };
	 * String list = join(stuff, &quot;, &quot;); // list is now &quot;apple, bear, cat&quot;
	 * 
	 * </PRE>
	 */
	static public String join(String str[], String separator) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			if (i != 0)
				buffer.append(separator);
			buffer.append(str[i]);
		}
		return buffer.toString();
	}

	public static final String WHITESPACE = " \t\n\r\f\u00A0";

	/**
	 * Split the provided String at wherever whitespace occurs. Multiple whitespace (extra spaces or tabs or whatever)
	 * between items will count as a single break.
	 * <P>
	 * The whitespace characters are "\t\n\r\f", which are the defaults for java.util.StringTokenizer, plus the unicode
	 * non-breaking space character, which is found commonly on files created by or used in conjunction with Mac OS X
	 * (character 160, or 0x00A0 in hex).
	 * 
	 * <PRE>
	 * i.e. splitTokens("a b") -> { "a", "b" }
	 *      splitTokens("a    b") -> { "a", "b" }
	 *      splitTokens("a\tb") -> { "a", "b" }
	 *      splitTokens("a \t  b  ") -> { "a", "b" }
	 * </PRE>
	 */
	static public String[] splitTokens(String what) {
		return splitTokens(what, WHITESPACE);
	}

	/**
	 * Splits a string into pieces, using any of the chars in the String 'delim' as separator characters. For instance,
	 * in addition to white space, you might want to treat commas as a separator. The delimeter characters won't appear
	 * in the returned String array.
	 * 
	 * <PRE>
	 * i.e. splitTokens("a, b", " ,") -> { "a", "b" }
	 * </PRE>
	 * 
	 * To include all the whitespace possibilities, use the variable WHITESPACE, found in PConstants:
	 * 
	 * <PRE>
	 * i.e. splitTokens("a   | b", WHITESPACE + "|");  ->  { "a", "b" }
	 * </PRE>
	 */
	static public String[] splitTokens(String what, String delim) {
		StringTokenizer toker = new StringTokenizer(what, delim);
		String pieces[] = new String[toker.countTokens()];

		int index = 0;
		while (toker.hasMoreTokens()) {
			pieces[index++] = toker.nextToken();
		}
		return pieces;
	}

	/**
	 * Split the provided String at wherever whitespace occurs. Multiple whitespace (extra spaces or tabs or whatever)
	 * between items will count as a single break.
	 * <P>
	 * The whitespace characters are "\t\n\r\f", which are the defaults for java.util.StringTokenizer, plus the unicode
	 * non-breaking space character, which is found commonly on files created by or used in conjunction with Mac OS X
	 * (character 160, or 0x00A0 in hex).
	 * 
	 * <PRE>
	 * 
	 * i.e. split("a b") -> { "a", "b" } split("a b") -> { "a", "b" }
	 * split("a\tb") -> { "a", "b" } split("a \t b ") -> { "a", "b" }
	 * 
	 * </PRE>
	 */
	static public String[] split(String what) {
		return split(what, WHITESPACE);
	}

	/**
	 * Splits a string into pieces, using any of the chars in the String 'delim' as separator characters. For instance,
	 * in addition to white space, you might want to treat commas as a separator. The delimeter characters won't appear
	 * in the returned String array.
	 * 
	 * <PRE>
	 * 
	 * i.e. split("a, b", " ,") -> { "a", "b" }
	 * 
	 * </PRE>
	 * 
	 * To include all the whitespace possibilities, use the variable WHITESPACE, found in PConstants:
	 * 
	 * <PRE>
	 * 
	 * i.e. split("a | b", WHITESPACE + "|"); -> { "a", "b" }
	 * 
	 * </PRE>
	 */
	static public String[] split(String what, String delim) {
		StringTokenizer toker = new StringTokenizer(what, delim);
		String pieces[] = new String[toker.countTokens()];

		int index = 0;
		while (toker.hasMoreTokens()) {
			pieces[index++] = toker.nextToken();
		}
		return pieces;
	}

	/**
	 * Split a string into pieces along a specific character. Most commonly used to break up a String along tab
	 * characters.
	 * <P>
	 * This operates differently than the others, where the single delimeter is the only breaking point, and consecutive
	 * delimeters will produce an empty string (""). This way, one can split on tab characters, but maintain the column
	 * alignments (of say an excel file) where there are empty columns.
	 */
	static public String[] split(String what, char delim) {
		// do this so that the exception occurs inside the user's
		// program, rather than appearing to be a bug inside split()
		if (what == null)
			return null;
		// return split(what, String.valueOf(delim)); // huh

		char chars[] = what.toCharArray();
		int splitCount = 0; // 1;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == delim)
				splitCount++;
		}
		// make sure that there is something in the input string
		// if (chars.length > 0) {
		// if the last char is a delimeter, get rid of it..
		// if (chars[chars.length-1] == delim) splitCount--;
		// on second thought, i don't agree with this, will disable
		// }
		if (splitCount == 0) {
			String splits[] = new String[1];
			splits[0] = new String(what);
			return splits;
		}
		// int pieceCount = splitCount + 1;
		String splits[] = new String[splitCount + 1];
		int splitIndex = 0;
		int startIndex = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == delim) {
				splits[splitIndex++] = new String(chars, startIndex, i - startIndex);
				startIndex = i + 1;
			}
		}
		// if (startIndex != chars.length) {
		splits[splitIndex] = new String(chars, startIndex, chars.length - startIndex);
		// }
		return splits;
	}

	/**
	 * Match a string with a regular expression, and returns the match as an array. The first index is the matching
	 * expression, and array elements [1] and higher represent each of the groups (sequences found in parens).
	 * 
	 * This uses multiline matching (Pattern.MULTILINE) and dotall mode (Pattern.DOTALL) by default, so that ^ and $
	 * match the beginning and end of any lines found in the source, and the . operator will also pick up newline
	 * characters.
	 */
	static public String[] match(String what, String regexp) {
		Pattern p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher(what);
		if (m.find()) {
			int count = m.groupCount() + 1;
			String[] groups = new String[count];
			for (int i = 0; i < count; i++) {
				groups[i] = m.group(i);
			}
			return groups;
		}
		return null;
	}

	/**
	 * Identical to match(), except that it returns an array of all matches in the specified String, rather than just
	 * the first.
	 */
	static public String[][] matchAll(String what, String regexp) {
		Pattern p = Pattern.compile(regexp, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher(what);
		ArrayList<String[]> results = new ArrayList<String[]>();
		int count = m.groupCount() + 1;
		while (m.find()) {
			String[] groups = new String[count];
			for (int i = 0; i < count; i++) {
				groups[i] = m.group(i);
			}
			results.add(groups);
		}
		if (results.isEmpty()) {
			return null;
		}
		String[][] matches = new String[results.size()][count];
		for (int i = 0; i < matches.length; i++) {
			matches[i] = results.get(i);
		}
		return matches;
	}

	/**
	 * <p>
	 * Converts an array of bytes into a string.
	 * 
	 * @param val An array of bytes
	 * @return A string containing a lexical representation of xsd:base64Binary
	 * @throws IllegalArgumentException if {@code val} is null.
	 */
	public static String printBase64Binary(byte[] val) {
		return _printBase64Binary(val);
	}

	public static String _printBase64Binary(byte[] input) {
		return _printBase64Binary(input, 0, input.length);
	}

	public static String _printBase64Binary(byte[] input, int offset, int len) {
		char[] buf = new char[((len + 2) / 3) * 4];
		int ptr = _printBase64Binary(input, offset, len, buf, 0);
		assert ptr == buf.length;
		return new String(buf);
	}

	private static final char[] encodeMap = initEncodeMap();

	private static char[] initEncodeMap() {
		char[] map = new char[64];
		int i;
		for (i = 0; i < 26; i++) {
			map[i] = (char) ('A' + i);
		}
		for (i = 26; i < 52; i++) {
			map[i] = (char) ('a' + (i - 26));
		}
		for (i = 52; i < 62; i++) {
			map[i] = (char) ('0' + (i - 52));
		}
		map[62] = '+';
		map[63] = '/';

		return map;
	}

	public static char encode(int i) {
		return encodeMap[i & 0x3F];
	}

	public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
		// encode elements until only 1 or 2 elements are left to encode
		int remaining = len;
		int i;
		for (i = offset; remaining >= 3; remaining -= 3, i += 3) {
			buf[ptr++] = encode(input[i] >> 2);
			buf[ptr++] = encode(((input[i] & 0x3) << 4) | ((input[i + 1] >> 4) & 0xF));
			buf[ptr++] = encode(((input[i + 1] & 0xF) << 2) | ((input[i + 2] >> 6) & 0x3));
			buf[ptr++] = encode(input[i + 2] & 0x3F);
		}
		// encode when exactly 1 element (left) to encode
		if (remaining == 1) {
			buf[ptr++] = encode(input[i] >> 2);
			buf[ptr++] = encode(((input[i]) & 0x3) << 4);
			buf[ptr++] = '=';
			buf[ptr++] = '=';
		}
		// encode when exactly 2 elements (left) to encode
		if (remaining == 2) {
			buf[ptr++] = encode(input[i] >> 2);
			buf[ptr++] = encode(((input[i] & 0x3) << 4) | ((input[i + 1] >> 4) & 0xF));
			buf[ptr++] = encode((input[i + 1] & 0xF) << 2);
			buf[ptr++] = '=';
		}
		return ptr;
	}

}
