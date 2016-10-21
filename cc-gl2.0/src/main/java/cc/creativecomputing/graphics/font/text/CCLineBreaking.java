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
	
import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextGridLinePart;
import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextPart;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCLineBreaking {
	
	public static class CCLineBreakException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -958233021894666245L;

		public CCLineBreakException() {
			super();
		}

		public CCLineBreakException(String theMessage, Throwable theCause, boolean theEnableSuppression, boolean theWritableStackTrace) {
			super(theMessage, theCause, theEnableSuppression, theWritableStackTrace);
		}

		public CCLineBreakException(String theMessage, Throwable theCause) {
			super(theMessage, theCause);
		}

		public CCLineBreakException(String theMessage) {
			super(theMessage);
		}

		public CCLineBreakException(Throwable theCause) {
			super(theCause);
		}
		
	}

	public void breakText(final CCMultiFontText theText){
		theText.textGrid().clear();
		
		double myX = 0;
		double myY = 0;

		int myLineIndex = 0;
		
		double myWidth = 0;
		
		double myMaxLeading = 0;
		
		for(CCTextPart myPart:theText.parts()){
			int myStart = 0;
			int myIndex = 0;
			char[] myTextBuffer = myPart.text().toCharArray();
			
			myMaxLeading = CCMath.max(myMaxLeading, myPart.leading());
			
			while (myIndex < myTextBuffer.length) {
				if (myTextBuffer[myIndex] == '\n') {
					myMaxLeading = myPart.leading();
					//myTextBuffer[myIndex] = '#';
					CCTextGridLinePart myLine = theText.textGrid().createTextPart(myPart, myLineIndex++, myStart, myIndex + 1, myTextBuffer, myX, myY);
					if(myLine != null)myWidth = CCMath.max(myWidth, myLine.width());
					myStart = myIndex + 1;
					myY -= myMaxLeading;
					myX = 0;
				}
				myIndex++;
			}
			if (myStart < myTextBuffer.length) {
				CCTextGridLinePart myLine = theText.textGrid().createTextPart(myPart, myLineIndex++, myStart, myIndex, myTextBuffer, myX, myY);
				if(myLine != null)myWidth = CCMath.max(myWidth, myLine.width());
				myX += myWidth;
			}
		}
		

		theText.changeWidth(myWidth);
		theText.changeHeight(-myY + myMaxLeading);
	}
}
