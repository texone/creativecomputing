package cc.creativecomputing.io.markup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.markup.CCMarkUpListElement.CCMarkupListStyle;

class CCMarkUpParser {

	private static class CCCharacterReader {

		private Reader _myReader;
		private Stack<Character> _myCharacterStack = new Stack<Character>();

		public CCCharacterReader(Reader theReader) {
			_myReader = theReader;
		}

		public String peek(int theIndex) throws IOException {
			StringBuffer sb = new StringBuffer();

			if (theIndex <= _myCharacterStack.size()) {
				for (int i = _myCharacterStack.size() - 1; i >= _myCharacterStack.size() - theIndex; i--) {
					sb.append(_myCharacterStack.get(i));
				}

				return sb.toString();
			}

			for (int i = 0; i < theIndex; i++) {
				Character cc = next(true);
				if (cc == null) {
					break;
				}

				sb.append(cc);
			}

			for (int i = sb.length() - 1; i >= 0; i--) {
				_myCharacterStack.push(sb.charAt(i));
			}

			return sb.toString();
		}

		public Character peek() throws IOException {
			Character myChar = next(true);
			if (myChar == null) {
				return null;
			}

			_myCharacterStack.push(myChar);

			return myChar;
		}

		public void skip(int theIndex) throws IOException {
			for (int i = 0; i < theIndex; i++) {
				next();
			}
		}

		public Character next() throws IOException {
			return next(true);
		}

		public Character next(boolean theWithStack) throws IOException {
			if (theWithStack && !_myCharacterStack.isEmpty()) {
				return _myCharacterStack.pop();
			}

			int theIndex = _myReader.read();
			if (theIndex == -1) {
				return null;
			}
			if (theIndex == '\r') {
				return next();
			}

			return new Character((char) theIndex);
		}
	}

	private BufferedReader _myReader;
	private String _myCurrentLine;
	private CCMarkUpElement _myParent;

	public CCMarkUpDocument parse(InputStream theInputStream) throws CCMarkUpException {
		return parse(new InputStreamReader(theInputStream));
	}

	public CCMarkUpDocument parse(Reader theReader) throws CCMarkUpException {
		_myReader = new BufferedReader(theReader);

		CCMarkUpDocument myDocument = new CCMarkUpDocument();
		_myParent = myDocument;
		String line = null;

		boolean newParagraph = true;

		try {
			while ((line = _myReader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					// new block
					_myParent = new CCMarkUpParagraphElement();
					myDocument.add(_myParent);
					newParagraph = true;

				} else if (line.startsWith("=")) {
					myDocument.add(parseHeading(line));

					_myParent = new CCMarkUpParagraphElement();
					myDocument.add(_myParent);
					newParagraph = true;

				} else if (line.matches("^\\*[^\\*].*")) {
					// unordered list
					_myParent.add(parseList(line, false));

				} else if (line.startsWith("#")) {
					// ordered list
					_myParent.add(parseList(line, true));

				} else if (line.startsWith("|")) {
					// table
					_myParent.add(parseTable(line));

				} else if (line.equals("{{{")) {
					_myParent.add(readPreformatted());

				} else if (line.matches("\\{\\{[^\\{].*")) {
					// TODO: find better way to recognize image pattern
					// image
					_myParent.add(readImage(line));

				} else if (line.startsWith("----")) {
					myDocument.add(new CCMarkUpHorizontalRuleElement());

				} else {
					if (!newParagraph) {
						_myParent.add(new CCMarkUpTextElement(" "));
					}
					CCLog.info(line);
					_myParent = parseString(_myParent, line);
					newParagraph = false;
				}
			}

			myDocument.clean();

			return myDocument;

		} catch (IOException e) {
			throw new CCMarkUpException(e);
		}
	}

	private CCMarkUpHeadingElement parseHeading(String theString) {
		if (!theString.startsWith("=")) {
			throw new IllegalArgumentException();
		}

		int level;
		for (level = 0; theString.charAt(level) == '='; level++)
			;

		theString = theString.substring(level);
		theString = theString.replaceAll("=+$", "");
		theString = theString.trim();

		return new CCMarkUpHeadingElement(level, theString);
	}

	private CCMarkUpElement parseString(CCMarkUpElement theElement, String theString) throws IOException {
		CCCharacterReader myReader = new CCCharacterReader(new StringReader((theString)));
		Character c = null;

		boolean escaped = false;

		while ((c = myReader.next()) != null) {
			Character nextChar = myReader.peek();
			if (nextChar == null) {
				nextChar = '\n';
			}

			if (!escaped && c == '*' && nextChar == '*') {
				myReader.skip(1);
				if (theElement instanceof CCMarkUpBoldElement) {
					theElement = theElement.getParent(); // close bold
				} else {
					CCMarkUpBoldElement bold = new CCMarkUpBoldElement();
					theElement.add(bold);
					theElement = bold;
				}

			} else if (!escaped && c == '/' && nextChar == '/') {
				myReader.skip(1);
				if (theElement instanceof CCMarkUpItalicElement) {
					theElement = theElement.getParent(); // close italic
				} else {
					CCMarkUpItalicElement italic = new CCMarkUpItalicElement();
					theElement.add(italic);
					theElement = italic;
				}
			} else if (!escaped && c == '{' && "{{".equals(myReader.peek(2))) {
				myReader.skip(2);
				theElement.add(readInlineNoWiki(myReader));

			} else if (!escaped && c == '[' && nextChar == '[') {
				myReader.skip(1);
				theElement.add(readLink(myReader));

			} else if (!escaped && c == '\\' && nextChar == '\\') {
				myReader.skip(1);
				theElement.add(new CCMarkUpLineBreakElement());

			} else if (c == 'h') {
				String tmp = "h" + myReader.peek(6);
				if ("http://".equals(tmp)) {
					parseRawLink(theElement, myReader, escaped);
				} else {
					theElement.add(new CCMarkUpTextElement(c));
				}

			} else if (!escaped && c == '~' && (nextChar != ' ' && nextChar != '\n')) {
				escaped = true;

			} else {
				theElement.add(new CCMarkUpTextElement(c));
				escaped = false;
			}
		}

		return theElement;
	}

	private void parseRawLink(CCMarkUpElement theElement, CCCharacterReader theReader, boolean escaped)
			throws IOException {
		StringBuffer sb = new StringBuffer("h");
		Character c = null;
		while ((c = theReader.next()) != null && c != ' ') {
			sb.append(c);
		}

		boolean appendLc = false;
		char lc = sb.charAt(sb.length() - 1);

		if (lc == ',' || lc == '.' || lc == '?' || lc == '!' || lc == ':' || lc == ';' || lc == '"' || lc == '\'') {

			appendLc = true;
			sb.deleteCharAt(sb.length() - 1);
		}

		String theString = sb.toString();

		theElement.add(escaped ? new CCMarkUpTextElement(theString) : new CCMarkUpLinkElement(theString));

		if (appendLc) {
			theElement.add(new CCMarkUpTextElement(lc));
		}

		if (c != null) {
			theElement.add(new CCMarkUpTextElement(c));
		}
	}

	private CCMarkUpLinkElement readLink(CCCharacterReader theReader) throws IOException {

		String label = null;
		String target = null;

		StringBuffer sb = new StringBuffer();

		Character c = null;
		while ((c = theReader.next()) != null) {

			Character nextChar = theReader.peek();
			if (nextChar == null) {
				nextChar = '\0';
			}

			if (c == '|') {
				target = sb.toString();
				sb = new StringBuffer();

			} else if (c == ']' && nextChar == ']') {
				theReader.skip(1);
				break;

			} else {
				sb.append(c);
			}
		}

		if (target == null) {
			target = sb.toString();
		}

		if (label == null && sb.length() > 0) {
			label = sb.toString();
		}

		return new CCMarkUpLinkElement(label, target);
	}

	private CCMarkUpImageElement readImage(String line) throws IOException {

		String alt = null;
		String src = null;

		CCCharacterReader myReader = new CCCharacterReader(new StringReader(line));
		myReader.skip(2); // skip '{{'

		StringBuffer sb = new StringBuffer();

		Character c = null;
		while ((c = myReader.next()) != null) {

			Character nextChar = myReader.peek();
			if (nextChar == null) {
				nextChar = '\0';
			}

			if (c == '|') {
				src = sb.toString();
				sb = new StringBuffer();

			} else if (c == '}' && nextChar == '}') {
				myReader.skip(1);
				break;

			} else {
				sb.append(c);
			}
		}

		if (src == null) {
			src = sb.toString();
		}

		if (alt == null && sb.length() > 0) {
			alt = sb.toString();
		} else {
			alt = src;
		}

		return new CCMarkUpImageElement(src, alt);
	}

	private CCMarkUpPreformattedElement readPreformatted() throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ((line = _myReader.readLine()) != null) {
			if (line.startsWith("}}}")) {
				break;
			}

			sb.append(line).append('\n');
		}

		return new CCMarkUpPreformattedElement(sb.toString());
	}

	private CCMarkUpPreformattedElement readInlineNoWiki(CCCharacterReader theReader) throws IOException {
		StringBuffer sb = new StringBuffer();

		Character c = null;
		while ((c = theReader.next()) != null) {
			if (c == '}') {
				int closingBraces;
				String tmp = theReader.peek(10);
				for (closingBraces = 1; closingBraces <= tmp.length()
						&& tmp.charAt(closingBraces - 1) == '}'; closingBraces++)
					;

				int extraBraces = closingBraces >= 3 ? closingBraces - 3 : closingBraces;
				for (int i = 0; i < extraBraces; i++) {
					sb.append('}');
				}

				theReader.skip(closingBraces - 1);

				if (closingBraces >= 3) {
					break;
				}

			} else {
				sb.append(c);
			}
		}

		return new CCMarkUpPreformattedElement(true, sb.toString().trim());
	}

	private CCMarkUpTableElement parseTable(String theString) throws IOException {
		CCMarkUpTableElement table = new CCMarkUpTableElement();

		// parse first row
		table.add(parseRow(theString));

		while ((theString = _myReader.readLine()) != null) {
			theString = theString.trim();
			if (theString.length() == 0) {
				break;
			} else if (!theString.startsWith("|")) {
				break;
			}

			table.add(parseRow(theString));
		}

		return table;
	}

	private CCMarkUpRowElement parseRow(String theString) throws IOException {
		CCMarkUpRowElement row = new CCMarkUpRowElement();
		String columns[] = theString.split("\\|");

		for (int i = 1; i < columns.length; i++) {
			String col = columns[i].trim();
			boolean headline = col.startsWith("=");
			if (headline) {
				col = col.substring(1);
			}

			col = col.trim();

			CCMarkUpCell cell = new CCMarkUpCell();
			cell.setHeading(headline);

			parseString(cell, col);
			row.add(cell);
		}

		return row;
	}

	private CCMarkUpListElement parseList(String theString, boolean ordered) throws IOException {
		CCMarkUpListElement list = new CCMarkUpListElement(
				ordered ? CCMarkupListStyle.ORDERED : CCMarkupListStyle.UNORDERED);

		final Character listChar = ordered ? '#' : '*';

		final int level = countChars(theString, listChar);

		CCMarkUpListItemElement item = parseListItem(theString, listChar);
		list.add(item);

		String line = null;
		while ((line = readLine()) != null) {
			line = line.trim();

			int starCount = countChars(line, listChar);

			if (starCount > level) {
				item.add(parseList(line, ordered));

				line = _myCurrentLine;
				starCount = countChars(line, listChar);
			}

			if (line.length() == 0) {
				break;
			} else if (!line.startsWith(listChar.toString())) {
				break;
			}

			if (starCount == level) {
				item = parseListItem(line, listChar);
				list.add(item);

			} else if (starCount > level) {
				item.add(parseList(line, ordered));

			} else if (starCount < level) {
				break;
			}
		}

		return list;
	}

	private CCMarkUpListItemElement parseListItem(String theString, char listChar) throws IOException {
		theString = theString.replaceAll("^\\" + listChar + "+ *", "");

		CCMarkUpListItemElement item = new CCMarkUpListItemElement();
		parseString(item, theString);

		return item;
	}

	private int countChars(String theString, char c) {
		int i;
		for (i = 0; i < theString.length() && theString.charAt(i) == c; i++)
			;

		return i;
	}

	private String readLine() throws IOException {
		_myCurrentLine = _myReader.readLine();
		return _myCurrentLine;
	}
}
