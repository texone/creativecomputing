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
package cc.creativecomputing.io;

import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.Reader;


public abstract class CCAbstractFileParser extends StreamTokenizer {

	private static final char BACKSLASH = '\\';
	

	// ObjectFileParser constructor
	protected CCAbstractFileParser(Reader r) throws CCParsingException{
		super(r);
		setup();
	}
	
	public abstract void readFile()throws CCParsingException;

	/**
	 * Sets up StreamTokenizer
	 */
	public void setup() {
		resetSyntax();
		eolIsSignificant(true);
		lowerCaseMode(true);

		// All printable ascii characters
		wordChars('!', '~');

		whitespaceChars(' ', ' ');
		whitespaceChars('\n', '\n');
		whitespaceChars('\r', '\r');
		whitespaceChars('\t', '\t');

		ordinaryChar(BACKSLASH);
	}

	/**
	 * Gets the next token from the stream.  Puts one of the four constants 
	 * (TT_WORD, TT_NUMBER, TT_EOL, or TT_EOF) or the token value for single 
	 * character tokens into ttype. Handles backslash continuation of lines.
	 */
	public void getToken() throws CCParsingException {
		int t;
		boolean done = false;

		try {
			do {
				t = nextToken();
				if (t == BACKSLASH) {
					t = nextToken();
					if (ttype != TT_EOL)
						done = true;
				} else
					done = true;
			} while (!done);
		} catch (IOException e) {
			throw new CCParsingException("IO error on line " + lineno() + ": " + e.getMessage());
		}
	}

	/**
	 * Skips all tokens on the rest of this line.  
	 * Doesn't do anything if we're already at the end of a line
	 */
	public void skipToNextLine() throws CCParsingException {
		while (ttype != TT_EOL) {
			getToken();
		}
	}

	/**
	 * Gets a number from the stream.  Note that we don't recognize
	 * numbers in the tokenizer automatically because numbers might be in
	 * scientific notation, which isn't processed correctly by StreamTokenizer.  
	 * The number is returned in nval.
	 */
	public void getNumber() throws CCParsingException {

		try {
			getToken();
			if (ttype != TT_WORD)
				throw new CCParsingException("Expected number on line " + lineno());
			nval = Double.valueOf(sval);
		} catch (NumberFormatException e) {
			throw new CCParsingException("Expected number on line " + lineno()+":"+e.getMessage());
		}
	}
	
	public float getFloat()throws CCParsingException {
		getNumber();
		return (float)nval;
	}
	
	public int getInt()throws CCParsingException {
		getNumber();
		return (int)nval;
	}
}
