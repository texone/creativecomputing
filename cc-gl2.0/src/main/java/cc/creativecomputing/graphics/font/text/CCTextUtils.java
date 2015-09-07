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
package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.graphics.font.CCFont;

public class CCTextUtils {

	///keep in mind this might make your string longer; if a single word wont fit on a line, we will split it 
	///by adding a new "\n" in between	
	public static String linebreakStringToFitInWidth(final double theWidth, final CCFont<?> theFont, final double theFontSize,  final String theString){
		
		
		double mySpaceWidth = theFont.spaceWidth();
		double myRunningX = 0;
		int wordStart = 0;
		int wordStop = 0;
		int lineStart = 0;
		int index = 0;

		char _myTextBuffer[] = theString.toCharArray();
		
		while (index < _myTextBuffer.length){
			
			if ( (_myTextBuffer[index] == ' ') || (index == _myTextBuffer.length - 1) ){

				double wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFontSize;

				if (myRunningX + wordWidth > theWidth){ 

					if (myRunningX == 0){ 
						// if this is the first word, and its width is
						// greater than the width of the text box,
						// then break the word where at the max width,
						// and send the rest of the word to the next line.
						do{
							index--;
							if (index == wordStart){
								// not a single char will fit on the supplied Width. screw 'em.
								return theString;
							}
							wordWidth = theFont.width(_myTextBuffer, wordStart, index) * theFontSize;
						}while (wordWidth > theWidth);
						
						String a = theString.substring(0, index);
						String b = theString.substring(index + 1, theString.length());
						String total = a + "\n" + b;
						_myTextBuffer = total.toCharArray();	//uh - oh this might break things

					}else{
						
						if ( _myTextBuffer[index] == ' ' )
							 _myTextBuffer[index] = '\n';
							
						// next word is too big, output current line
						// and advance to the next line						
						// only increment index if a word wasn't broken inside the
						// do/while loop above.. also, this is a while() loop too,
						// because multiple spaces don't count for shit when they're
						// at the end of a line like this.

						index = wordStop; // back that ass up
						while ((index < _myTextBuffer.length) && (_myTextBuffer[index] == ' ')){
							index++;
						}
					}
					lineStart = index;
					wordStart = index;
					wordStop = index;
					myRunningX = 0; // boxX1;

				}else{
					myRunningX += wordWidth + mySpaceWidth;
					// on to the next word
					wordStop = index;
					wordStart = index + 1;
				}

			}else {
				if (_myTextBuffer[index] == '\n'){
			
					lineStart = index + 1;
					wordStart = lineStart;
					myRunningX = 0; // fix for bug 188
				}
			}
			index++;
		}

		return new String (_myTextBuffer);
	}
}
