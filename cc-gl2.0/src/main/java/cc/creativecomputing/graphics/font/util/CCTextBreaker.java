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
package cc.creativecomputing.graphics.font.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.font.CCFont;

/**
 * @author info
 *
 */
public class CCTextBreaker {

	
	public List<String> breakText(final String theText, final CCFont<?> theFont, final double theTextWidth) {
		final List<String> myResult = new ArrayList<String>();
		final double mySpaceWidth = theFont.spaceWidth();
		double myRunningX = 0; 

		final int myStringLength = theText.length();
		
		char[] _myTextBuffer = new char[myStringLength + 10];
		theText.getChars(0, myStringLength, _myTextBuffer, 0);

		int wordStart = 0;
//		int wordStop = 0;
		int lineStart = 0;
		int index = 0;
		
		while (index < myStringLength){
			if ((_myTextBuffer[index] == ' ') || (index == myStringLength - 1)){
				// boundary of a word
				double wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFont.size();

				if (myRunningX + wordWidth > theTextWidth){
					if (myRunningX == 0){ // boxX1) {
						// if this is the first word, and its width is
						// greater than the width of the text box,
						// then break the word where at the max width,
						// and send the rest of the word to the next line.
						do{
							index--;
							if (index == wordStart){
								// not a single char will fit on this line. screw 'em.
								return myResult;
							}
							wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFont.size();
						}while (wordWidth > theTextWidth);
						myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
					}else{
						// next word is too big, output current line
						// and advance to the next line
						myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
						// only increment index if a word wasn't broken inside the
						// do/while loop above.. also, this is a while() loop too,
						// because multiple spaces don't count for shit when they're
						// at the end of a line like this.
						// index = wordStop; // back that ass up
						while ((index < myStringLength) && (_myTextBuffer[index] == ' ')){
							index++;
						}
					}
					lineStart = index;
					wordStart = index;
//					wordStop = index;
					myRunningX = 0;
				}else{
					myRunningX += wordWidth + mySpaceWidth;
					// on to the next word
//					wordStop = index;
					wordStart = index + 1;
				}
			}else if (_myTextBuffer[index] == '\n'){
				if (lineStart != index){ // if line is not empty
					myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
				}
				lineStart = index + 1;
				wordStart = lineStart;
				myRunningX = 0;
			}
			index++;
		}
		if ((lineStart < myStringLength) && (lineStart != index)){
			myResult.add(new String(_myTextBuffer, lineStart, index - lineStart));
		}
		return myResult;
	}
}
