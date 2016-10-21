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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextGridLinePart;
import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextPart;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class CCFirstFitFirst extends CCLineBreaking {
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.text.CCLineBreaking#breakText(cc.creativecomputing.graphics.font.text.CCText)
	 */
	@Override
//	public void breakText(CCMultiFontText theText) {
//		theText.textGrid().clear();
//		
//		double myBlockWidth = theText.blockWidth();
//		double myBlockHeight = theText.blockHeight();
//
//		double myRunningX = 0;
//		double myCurrentX = 0;
//		double myCurrentY = 0;
//		
//		// if the box is already too small, tell em to f off
//		if (myCurrentY > myBlockHeight){
//			throw new CCLineBreakException("Textbox is to small for textbreaking:" + myBlockHeight);
//		}
//
//		double myMaxLeading = 0;
//		
//		List<CCTextGridLinePart> myLineParts = new ArrayList<>();
//		int myLineIndex = 0;
//		
//		for(CCTextPart myPart:theText.parts()){
//			int myWordStart = 0;
//			int myWordStop = 0;
//			int lineStart = 0;
//			int index = 0;
//			
//			final double mySpaceWidth = myPart.font().spaceWidth() * myPart.size();
//			char[] myTextBuffer = myPart.text().toCharArray();
//			
////			myMaxLeading = myPart.leading();//CCMath.max(myMaxLeading, );
//			
//			while (index < myTextBuffer.length){
//				if ((myTextBuffer[index] == ' ') || (index == myTextBuffer.length - 1)){
//					// boundary of a word
//					double wordWidth = myPart.font().width(myTextBuffer, myWordStart, index) * myPart.size();
//	
//					if (myRunningX + wordWidth > myBlockWidth){ // boxX2) {
//						if (myRunningX == 0){ // boxX1) {
//							// if this is the first word, and its width is
//							// greater than the width of the text box,
//							// then break the word where at the max width,
//							// and send the rest of the word to the next line.
//							do{
//								index--;
//								if (index == myWordStart){
//									throw new CCLineBreakException("Textbox is to small for textbreaking:" + myBlockHeight);
//								}
//								wordWidth = myPart.font().width(myTextBuffer, myWordStart, index) * myPart.size();
//							}while (wordWidth > theText.width());
//						}else{
//							// next word is too big, output current line
//							// and advance to the next line
//							CCTextGridLinePart myNewPart = theText.textGrid().createGridLine(myPart, myLineIndex, lineStart, myWordStop, myTextBuffer, myCurrentX, myCurrentY);
//							if(myNewPart != null){
//								myLineParts.add(myNewPart);
//								myMaxLeading = CCMath.max(myMaxLeading, myPart.leading());
//							}
//							// only increment index if a word wasn't broken inside the
//							// do/while loop above.. also, this is a while() loop too,
//							// because multiple spaces don't count for shit when they're
//							// at the end of a line like this.
//	
//							index = myWordStop; // back that ass up
//							myCurrentX = 0;
//							while ((index < myTextBuffer.length) && (myTextBuffer[index] == ' ')){
//								index++;
//							}
//						}
//						lineStart = index;
//						myWordStart = index;
//						myWordStop = index;
//						myRunningX = 0; // boxX1;
//						if(myLineIndex > 0)myCurrentY -= myMaxLeading;
//						
//						for(CCTextGridLinePart myLinePart:myLineParts){
//							if(myLinePart == null)continue;
//							myLinePart._myY = myCurrentY;
//						}
//						myLineParts.clear();
//						myLineIndex++;
//
//						myMaxLeading = myPart.leading();
//						
//						if (myCurrentY > myBlockHeight){
//							return; // box is now full
//						}
//	
//					}else{
//						myRunningX += wordWidth + mySpaceWidth;
//						// on to the next word
//						myWordStop = index;
//						myWordStart = index + 1;
//					}
//	
//				}else if (myTextBuffer[index] == '\n'){
//					if (lineStart != index){ // if line is not empty
//						CCTextGridLinePart myNewPart = theText.textGrid().createGridLine(myPart, myLineIndex, lineStart, index, myTextBuffer, 0, myCurrentY);
//						myLineParts.add(myNewPart);
//					}
//					lineStart = index + 1;
//					myWordStart = lineStart;
//					myRunningX = 0;
//					myCurrentX = 0;
//					if(myLineIndex > 0)myCurrentY -= myMaxLeading;
//					myMaxLeading = myPart.leading();
//					// if (currentY > boxY2) return; // box is now full
//					if (myCurrentY > myBlockHeight){
//						return; // box is now full
//					}
//					myLineIndex++;
//				}
//				index++;
//			}
//			if ((lineStart < myTextBuffer.length) && (lineStart != index)){
//				CCTextGridLinePart myNewPart = theText.textGrid().createGridLine(myPart, myLineIndex, lineStart, index, myTextBuffer, myCurrentX, myCurrentY);
//				myLineParts.add(myNewPart);
//			}
//			myCurrentX = myRunningX;
//		}
//		if(myLineIndex > 0)myCurrentY -= myMaxLeading;
//		for(CCTextGridLinePart myLinePart:myLineParts){
//			if(myLinePart == null)continue;
//			myLinePart._myY = myCurrentY;
//		}
//		myLineParts.clear();
//		
//		theText.changeWidth(theText.blockWidth());
//		theText.changeHeight(-myCurrentY);
//	}
	
	public void breakText(CCMultiFontText theText) {
		theText.textGrid().clear();
		
		double myBlockWidth = theText.blockWidth();
		double myBlockHeight = theText.blockHeight();

		double myPartY = 0;
		
		// if the box is already too small, tell em to f off
		if (myPartY > myBlockHeight){
			throw new CCLineBreakException("Textbox is to small for textbreaking:" + myBlockHeight);
		}
		int myLineIndex = 0;
		
		double myLineWidth = 0;
		
		double myPartX = 0;
		int myStartIndex = 0;
		int myStop = 0;
		boolean myHasSpace = false;
		
		double myLineLeading = 0;
		int myTextLine = 0;
		List<CCTextGridLinePart> myLineTextParts = new ArrayList<>();
		
		for(CCTextPart myPart:theText.parts()){
			
			int index = 0;
			myStartIndex = 0;
			
			char[] myTextBuffer = myPart.text().toCharArray();
			
			myLineLeading = CCMath.max(myLineLeading, myPart.leading());
			
//			myMaxLeading = myPart.leading();//CCMath.max(myMaxLeading, );
			while (index < myTextBuffer.length){
				char myChar = myTextBuffer[index];
				double myCharWidth = myPart.font().width(myChar) * myPart.size();
				myLineWidth += myCharWidth;
				
				boolean myCreateTextLine = false;
				
				switch(myChar){
				case '_':
				case ' ':
					myHasSpace = true;
					myStop = index + 1;
					break;
				case '\n':
					myCreateTextLine = true;
					break;
				}
				if(myLineWidth > myBlockWidth){
					myCreateTextLine = true;
					if(myHasSpace){
						index = myStop;
					}else{
						index--;
					}
				}
				if(myCreateTextLine){
					myLineWidth = myCharWidth;
					
					if(myTextLine > 0)myPartY -= myLineLeading;
					
					CCTextGridLinePart myNewPart = theText.textGrid().createTextPart(myPart, myLineIndex, myStartIndex, index, myTextBuffer, myPartX, myPartY);
//					CCLog.info(myNewPart.text() + ":" + myHasSpace + ":" + myStartIndex + ":" + index);
					myLineTextParts.add(myNewPart);
					for(CCTextGridLinePart myTextPart:myLineTextParts){
						if(myTextPart == null)continue;
						myTextPart._myY = myPartY;
					}
					
					myLineTextParts.clear();
					myPartX = 0;
					myStartIndex = index;
					myTextLine++;
				}
				index++;
			}
			CCTextGridLinePart myNewPart = theText.textGrid().createTextPart(myPart, myLineIndex, myStartIndex, index, myTextBuffer, myPartX, myPartY);
			myLineTextParts.add(myNewPart);
			myPartX = myNewPart != null ? myNewPart.width() : 0;
			myStop = 0;
			
		}
		
		if(myTextLine > 0)myPartY -= myLineLeading;
		
		for(CCTextGridLinePart myTextPart:myLineTextParts){
			if(myTextPart == null)continue;
			myTextPart._myY = myPartY;
		}
		
		myLineTextParts.clear();
		
		
		theText.changeWidth(theText.blockWidth());
		theText.changeHeight(-myPartY + myLineLeading);
	}
}
