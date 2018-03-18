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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCChar;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * @author info
 * 
 */
public class CCMultiFontText {
	
	public static class CCTextPart{
		private String _myText;

		protected char[] _myTextBuffer;
		
		protected CCFont<?> _myFont;
		
		/**
		 * The current text size
		 **/
		protected double _myTextSize;

		/**
		 * The current text leading
		 **/
		protected double _myLeading;
		
		/**
		 * The current text spacing
		 */
		protected double _mySpacing = 1;
		
		protected CCMultiFontText _myTextObject = null;
		
		protected CCColor _myColor = null;
		
		public CCTextPart(String theText, CCFont<?> theFont, int theSize, CCColor theColor){
			_myText = theText;
			_myFont = theFont;
			_myTextSize = theSize;
			_myLeading = (ascent() + descent()) * 1.275f;
			_myColor = theColor;
		}
		
		public CCTextPart(CCFont<?> theFont, String theText){
			this(theText, theFont, theFont.size(), null);
		}
		
		/**
		 * Sets the current font. The font's size will be the "natural" size of this font 
		 * The leading will also be reset.
		 * @param theFont
		 */
		public void font(final CCFont<?> theFont) {
			_myFont = theFont;
			if (_myFont != null)
				size(theFont.size());
		}
		
		/**
		 * Useful function to set the font and size at the same time.
		 * @param theFont the new font for the text
		 * @param theSize the new font size for the text
		 */
		public void font(final CCFont<?> theFont, final double theSize) {
			font(theFont);
			if (theFont != null)
				size(theSize);
		}

		/**
		 * Returns the font currently in use by the text
		 * 
		 * @return
		 */
		public CCFont<?> font() {
			return _myFont;
		}
		
		/**
		 * Sets the leading of the text. 
		 * In typography, leading refers to the amount of added vertical spacing between lines of type. 
		 * This concept is also often referred to as "line spacing".
		 * @param theTextLeading
		 */
		public void leading(double theTextLeading) {
			if(theTextLeading == _myLeading)return;

			_myLeading = theTextLeading;
		}
		
		/**
		 * Returns the used text leading.
		 * @return the used text leading
		 */
		public double leading() {
			return _myLeading;
		}
		
		/**
		 * Returns the ascent of the current font at the current size.
		 * 
		 * @return the ascent of the current font at the current size
		 */
		public double ascent() {
			return _myFont.normedAscent() * _myTextSize;
		}

		public double descent() {
			return _myFont.normedDescent() * _myTextSize;
		}
		
		/**
		 * Returns the font size of the text.
		 * @return the font size of the text
		 */
		public double size() {
			return _myTextSize;
		}

		/**
		 * Sets the font size of the text. Also sets the text leading dependent on the font size. 
		 * @param theSize the font size of the text
		 */
		public void size(final double theSize) {
			_myTextSize = theSize;
			_myLeading = (ascent() + descent()) * 1.275f;
			if(_myText != null && _myTextObject != null)_myTextObject.breakText();
		}
		
		/**
		 * Sets the letter spacing of the text. In typography, letter-spacing, also called 
		 * tracking, refers to the amount of space between a group of letters to affect 
		 * density in a line or block of text. A spacing of one will have no effect, while 
		 * a spacing of 2 will double the space between two letters.
		 * @param theSpacing letter spacing to use
		 */
		public void spacing(final double theSpacing) {
			if(_mySpacing == theSpacing)return;
			_mySpacing = theSpacing;
			if(_myText != null && _myTextObject != null)_myTextObject.breakText();
		}
		
		/**
		 * Returns the used letter spacing
		 * @return the used letter spacing
		 */
		public double spacing() {
			return _mySpacing;
		}

		public void text(final String theText) {
			_myText = theText;
			_myTextBuffer = theText.toCharArray();
			if(_myText != null && _myTextObject != null)_myTextObject.breakText();
		}
		
		public String text(){
			return _myText;
		}
	}
	
	/**
	 * Implement this class to react to text changes
	 * @author christianriekoff
	 *
	 */
	public interface CCTextListener{
		/**
		 * This method is called by the text object, every time its text is changed
		 * @param theText
		 */
        void onChangeText(CCMultiFontText theText);
	}
	
	/**
	 * Class to hold the positions and index information for one line of text
	 * @author christianriekoff
	 *
	 */
	public class CCTextGridLinePart{
		protected CCTextPart _myTextPart;
		protected double _myY;
		private double[] _myX;
		private int[] _myFontCharIndices;
		private int _myIndex;
		private int _mySize;
		
		private int _myStart;
		private int _myEnd;
		
		private boolean _myHasLineBreak;
		
		/**
		 * Creates a new text line
		 * @param theIndex number of the text line
		 * @param theStart position of the the first letter in the text
		 * @param theEnd position of the last letter in the text
		 * @param theTextBuffer buffer holding the complete text as chars
		 * @param theX x position of the line
		 * @param theY y position of the line
		 */
		private CCTextGridLinePart(
			final CCTextPart theTextPart,
			final int theIndex,
			final int theStart, final int theEnd, final char[] theTextBuffer,
			double theX, final double theY
		) {
			_myTextPart = theTextPart;
			_mySize = theEnd - theStart;
			_myX = new double[_mySize + 1];
			_myFontCharIndices = new int[_mySize];
			
			_myStart = theStart;
			_myEnd = theEnd;
			
			_myIndex = theIndex;
			_myY = theY;
			
			CCTextAlign myTextAlign = _myTextAlign;
			if(myTextAlign == CCTextAlign.JUSTIFY && _myLineBreakMode == CCLineBreakMode.NONE)myTextAlign = CCTextAlign.LEFT;
			
			//reset x
			switch (_myTextAlign) {
			case CENTER:
				theX -= _myTextPart.font().width(theTextBuffer, theStart, theEnd) / 2f * _myTextPart.size();
				break;
			case RIGHT:
				theX -= _myTextPart.font().width(theTextBuffer, theStart, theEnd) * _myTextPart.size();
				break;
			default:
			}
			
			// place letters
			
			_myX[0] = theX;
			final double mySpace = (_myWidth - _myTextPart.font().width(theTextBuffer, theStart, theEnd)* _myTextPart.size()) /(theEnd - theStart - 1);
			int myLastIndex = -1;

			for (int index = theStart; index < theEnd; index++) {
				final int myArrayIndex = index - theStart;
				final char myChar = theTextBuffer[index];
				final int myIndex = _myTextPart.font().index(myChar);
					
				if(myTextAlign == CCTextAlign.JUSTIFY) {
					theX += mySpace;
				}else {
					theX += _myTextPart.font().kerning(myLastIndex, myIndex) * _myTextPart.size();
				}
					
				theX += _myTextPart.font().width(myIndex) * _myTextPart.size() * _myTextPart.spacing();
				
				_myFontCharIndices[myArrayIndex] = myIndex;
				_myX[myArrayIndex + 1] = theX;
				myLastIndex = myIndex;
			}
			_myHasLineBreak = theTextBuffer[theEnd - 1] == '\n';
		}
		
		public CCFont<?> font(){
			return _myTextPart.font();
		}
		
		public String text(){
			return _myTextPart.text().substring(_myStart, _myEnd);
		}
		
		/**
		 * Returns the index of the first letter of this line in the text
		 * @return index of the first letter of this line in the text
		 */
		public int start() {
			return _myStart;
		}
		
		/**
		 * Returns the index of the last letter of this line in the text
		 * @return index of the last letter of this line in the text
		 */
		public int end() {
			return _myEnd;
		}
		
		/**
		 * checks if the given index belongs to this line
		 * @param theIndex
		 * @return true if the given index is inside the line otherwise false
		 */
		public boolean isInside(int theIndex) {
			return theIndex >= _myStart && theIndex <= (_myHasLineBreak ? _myEnd -1 :_myEnd);
		}
		
		/**
		 * Number of letters in this line
		 * @return number of letters in this line 
		 */
		public int size() {
			return _mySize + (_myHasLineBreak ? 0 : 1);
		}
		
		int[] charIndices(){
			return _myFontCharIndices;
		}
		
		/**
		 * Draws all chars of the line at the right positions
		 * @param g graphics object for drawing
		 */
		private void drawTextLine(CCGraphics g) {
			if(_myTextPart._myColor != null)g.color(_myTextPart._myColor);
			_myTextPart.font().beginText(g);
			for (int i = 0; i < _myFontCharIndices.length; i++) {
				
				_myTextPart.font().drawChar(
					g, _myFontCharIndices[i], _myTextPart.size(), 
					_myX[i] + _myPosition.x, 
					_myY + _myPosition.y + _myTextPart.ascent(), 
					_myPosition.z
				);
			}
			_myTextPart.font().endText(g);
		}
		
		public CCChar charByIndex(int theCharIndex){
			return _myTextPart.font().chars()[_myFontCharIndices[theCharIndex]];
		}
		
		public void drawChar(CCGraphics g, int theCharIndex) {
			_myTextPart.font().drawChar(
				g, _myFontCharIndices[theCharIndex], _myTextPart.size(), 
				_myX[theCharIndex] + _myPosition.x, 
				_myY + _myPosition.y + _myTextPart.ascent(), 
				_myPosition.z
			);
		}
		
		public int myNumberOfChars() {
			return _myFontCharIndices.length;
		}
		
		/**
		 * Draws the text grid 
		 * @param g graphics object for drawing
		 */
		private void drawGrid(CCGraphics g) {
			for(int i = 0; i < _myX.length;i++) {
				double myX = _myX[i]+ _myPosition.x;
				double myY = _myY + _myPosition.y + _myTextPart.ascent();
				
				g.vertex(myX, myY);
				g.vertex(myX, myY - _myTextPart.size() - _myTextPart.descent());
			}
		}
		
		/**
		 * Helper to draw text highlighting in case of selection
		 * @param g graphics object for drawing
		 * @param theStartIndex index of the first selected letter
		 * @param theEndIndex index of the last selected letter
		 */
		private void drawHighlight(CCGraphics g, int theStartIndex, int theEndIndex) {
			if(theStartIndex > _myEnd)return;
			if(theEndIndex < _myStart)return;
			
			theStartIndex = CCMath.constrain(theStartIndex - _myStart, 0, _myX.length - 1);
			theEndIndex = CCMath.constrain(theEndIndex - _myStart, 0, _myX.length - 1);
			double myX1 = _myX[theStartIndex]+ _myPosition.x;
			double myX2 = _myX[theEndIndex]+ _myPosition.x;
			double myY = _myY + _myPosition.y + _myTextPart.ascent();
			
			g.vertex(myX1, myY);
			g.vertex(myX1, myY - _myTextPart.size() - _myTextPart.descent());
			g.vertex(myX2, myY - _myTextPart.size() - _myTextPart.descent());
			g.vertex(myX2, myY);
		}
		
		/**
		 * Returns the letter index for the given 2d position
		 * @param thePosition
		 * @return letter index for the given 2d position
		 */
		private int gridIndex(CCVector2 thePosition) {
			double myY = _myY + _myPosition.y + _myTextPart.ascent();
			
			if(
				_myIndex != 0 && thePosition.y > myY || 
				_myIndex != _myTextGrid._myGridLines.size() - 1 && thePosition.y < (myY - _myTextPart.leading())
			)return -1;
			
			return gridIndex(thePosition.x);
		}
		
		private int gridIndex(double theX) {
			for(int i = 0; i < _myX.length - 1;i++) {
				if(theX < (_myX[i] + _myX[i + 1]) / 2 + _myPosition.x)return i + _myStart;
			}
			
			return _myStart + size() - 1;
		}
		
		private double gridPos(int theIndex) {
			return _myX[theIndex - _myStart] + _myPosition.x;
		}
		
		double width() {
			return _myX[_myX.length - 1];
		}
		
		/**
		 * returns the x position of the letter with the given index relative to the text position
		 * @param theCharIndex
		 * @return the x position of the letter
		 */
		public double x(int theCharIndex){
			return _myX[theCharIndex];
		}
		
		/**
		 * returns the y position of the textline relative to the text position
		 * @return the y position of the textline
		 */
		public double y(){
			return _myY;
		}
	}
	
	/**
	 * This is a helper class to get the bounding boxes for every letter
	 * as grid. This can be used for text highlighting or selection. 
	 * @author christianriekoff
	 *
	 */
	public class CCTextGrid{
		
		private List<CCTextGridLinePart> _myGridLines = new ArrayList<CCTextGridLinePart>();
		
		public CCTextGrid() {
			
		}
		
		public void clear() {
			_myGridLines.clear();
		}
		
		CCTextGridLinePart createTextPart(
			final CCTextPart thePart,
			final int theIndex,
			final int theStart, final int theStop, final char[] theTextBuffer,
			double theX, final double theY
		) {
			String myText = new String(theTextBuffer, theStart, theStop - theStart);
			if(myText.trim().equals(""))return null;
			
			CCTextGridLinePart myResult = new CCTextGridLinePart(thePart, theIndex, theStart, theStop, theTextBuffer, theX, theY);
			_myGridLines.add(myResult);
			return myResult;
		}
		
		public List<CCTextGridLinePart> gridLines(){
			return _myGridLines;
		}
		
		public void drawText(CCGraphics g) {
			for(CCTextGridLinePart myGridLine:_myGridLines) {
				myGridLine.drawTextLine(g);
			}
		}
		
		public void drawGrid(CCGraphics g) {
			g.beginShape(CCDrawMode.LINES);
			for(CCTextGridLinePart myGridLine:_myGridLines) {
				myGridLine.drawGrid(g);
			}
			g.endShape();
		}
		
		public void drawHeighlight(CCGraphics g, int theStart, int theEnd) {
			if(theEnd < theStart) {
				int myTmp = theEnd;
				theEnd = theStart;
				theStart = myTmp;
			}
			
			g.beginShape(CCDrawMode.QUADS);
			for(CCTextGridLinePart myLine:_myGridLines) {
				myLine.drawHighlight(g, theStart, theEnd);
			}
			g.endShape();
		}
		
		public int gridIndex(CCVector2 thePosition) {
			
			for(CCTextGridLinePart myLine:_myGridLines) {
				int myLineX = myLine.gridIndex(thePosition);
				if(myLineX < 0) {
					continue;
				}
				return myLineX;
			}
			
			return 0;
		}
		
		public int upperIndex(double theX, int theIndex) {
			int myLine = gridLine(theIndex);
			
			if(myLine > 0)myLine--;
			int myResult = _myGridLines.get(myLine).gridIndex(theX);
			return myResult;
		}
		
		public int lowerIndex(double theX, int theIndex) {
			int myLine = gridLine(theIndex);
			if(myLine < _myGridLines.size() - 1)myLine++;
			return _myGridLines.get(myLine).gridIndex(theX);
		}
		
		public int gridLine(int theIndex) {
			for(CCTextGridLinePart myLine:_myGridLines) {
				if(myLine.isInside(theIndex)) {
					return myLine._myIndex;
				}
			}
			return 0;
		}
		//TODO check this
		public CCVector2 gridPosition(int theIndex) {
			CCVector2 myResult = new CCVector2(0,0);
			
			for(CCTextGridLinePart myLine:_myGridLines) {
				if(myLine.isInside(theIndex)) {
					myResult.x = myLine.gridPos(theIndex);
					myResult.y = myLine._myY + _myPosition.y;// + ascent();
					return myResult;
				}
			}
			
			return myResult;
		}
	}
	
	/**
	 * rectangle to save the bounding box around the text
	 */
	private CCAABoundingRectangle _myBoundingRectangle;

	/**
	 * store the current text width
	 */
	private double _myWidth = 0;

	/**
	 * store the current text height
	 */
	protected double _myHeight = 0;
	
	private double _myBlockWidth = 0;
	private double _myBlockHeight = 0;

	@CCProperty(name = "position")
	protected CCVector3 _myPosition = new CCVector3();
	
	protected CCTextGrid _myTextGrid = new CCTextGrid();
	
	private CCLineBreakMode _myLineBreakMode = CCLineBreakMode.NONE;
	private CCLineBreaking _myLineBreaking = new CCLineBreaking();
	
	private CCListenerManager<CCTextListener> _myTextListeners = CCListenerManager.create(CCTextListener.class);
	

	/**
	 * The current text align
	 **/
	protected CCTextAlign _myTextAlign = CCTextAlign.LEFT;

	
	
	protected List<CCTextPart> _myTextParts = new ArrayList<>();
	
	public CCMultiFontText(){
		_myBoundingRectangle = new CCAABoundingRectangle();
	}
	
	/**
	 * 
	 * @param theListener
	 */
	public CCListenerManager<CCTextListener> events() {
		return _myTextListeners;
	}
	
	/**
	 * 
	 * @param theListener
	 */
	public void removeListener(CCTextListener theListener) {
		if(_myTextListeners == null) return;
		_myTextListeners.remove(theListener);
	}
	
	public void breakText() {
		try{
		_myLineBreaking.breakText(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		_myTextListeners.proxy().onChangeText(this);
	}
	
	public void addText(String theText, CCFont<?> theFont, int theSize){
		_myTextParts.add(new CCTextPart(theText, theFont, theSize, null));
	}
	
	public void addText(String theText, CCFont<?> theFont, int theSize, CCColor theColor){
		_myTextParts.add(new CCTextPart(theText, theFont, theSize, theColor));
	}
	
	public void addText(String theText, CCFont<?> theFont){
		addText(theText, theFont, theFont.size());
	}
	
	public void reset(){
		_myTextParts.clear();
		breakText();
	}

	@CCProperty(name = "align")
	public void align(CCTextAlign theTextAlign) {
		if(theTextAlign == _myTextAlign)return;
		_myTextAlign = theTextAlign;
		breakText();
	}

	public CCTextAlign textAlign() {
		return _myTextAlign;
	}
	
	public void lineBreak(CCLineBreakMode theLineBreak) {
		_myLineBreakMode = theLineBreak;
		switch(theLineBreak) {
		case NONE:
			_myLineBreaking = new CCLineBreaking();
			break;
		case BLOCK:
			_myLineBreaking = new CCFirstFitFirst();
			break;
		}
		breakText();
	}
	
	public CCLineBreakMode lineBreak() {
		return _myLineBreakMode;
	}
	
	
	
	public List<CCTextPart> parts(){
		return _myTextParts;
	}
	
//	public char delete(int theIndex) {
//		if(theIndex < 0)return ' ';
//		char myDeletedChar = _myText.charAt(theIndex);
//		String myNewText = _myText.substring(0,theIndex);
//		myNewText = myNewText.concat(_myText.substring(theIndex + 1));
//		_myText = myNewText;
//		breakText();
//		return myDeletedChar;
//	}
	
//	public void append(int theIndex, String theString) {
//		String myNewText = _myText.substring(0,theIndex+1);
//		myNewText = myNewText + theString;
//		myNewText = myNewText.concat(_myText.substring(theIndex+1));
//		_myText = myNewText;
//		breakText();
//	}
	
//	public void delete(int theStartIndex, int theEndIndex) {
//		if(theEndIndex < theStartIndex) {
//			int myTemp = theEndIndex;
//			theEndIndex = theStartIndex;
//			theStartIndex = myTemp;
//		}
//		String myNewText = _myText.substring(0,theStartIndex+1);
//		myNewText = myNewText.concat(_myText.substring(theEndIndex + 1));
//		_myText = myNewText;
//		breakText();
//	}
	
//	public String text(int theStartIndex, int theEndIndex) {
//		if(theEndIndex < theStartIndex) {
//			int myTemp = theEndIndex;
//			theEndIndex = theStartIndex;
//			theStartIndex = myTemp;
//		}
//		return _myText.substring(theStartIndex + 1,theEndIndex + 1);
//	}
	
	public void dimension(final double theWidth, final double theHeight) {
		width(theWidth);
		height(theHeight);
	}

	/**
	 * Return the width of a line of text. If the text has multiple lines, this returns the length of the longest line.
	 * Note this is only recalculated once you have changed the text.
	 * 
	 * @param theString
	 * @return
	 */
	public double width() {
		return _myWidth;
	}
	
	void changeWidth(double theWidth) {
		_myWidth = theWidth;
	}
	
	public void width(double theWidth) {
		_myWidth = theWidth;
		_myBlockWidth = theWidth;
	}
	
	public double height() {
		return _myHeight;
	}
	
	void changeHeight(double theHeight) {
		_myHeight = theHeight;
	}
	
	public void height(double theHeight) {
		_myHeight = theHeight;
		_myBlockHeight = theHeight;
	}
	
	double blockHeight() {
		return _myBlockHeight;
	}
	
	double blockWidth() {
		return _myBlockWidth;
	}
	
	/**
	 * TODO fix bounding box
	 * Returns the bounding rectangle around the text.
	 * @return the bounding rectangle around the text.
	 */
	public CCAABoundingRectangle boundingBox(){
		_myBoundingRectangle.min().x = _myPosition.x;
		_myBoundingRectangle.min().y = _myPosition.y - _myHeight;
		_myBoundingRectangle.width(_myWidth);
		_myBoundingRectangle.height(_myHeight);
		return _myBoundingRectangle;
	}
	
	public CCTextGrid textGrid() {
		return _myTextGrid;
	}
	
	/**
	 * Sets the position of the text to the given coordinates.
	 * @param theX new x position for the text
	 * @param theY new y position for the text
	 */
	public void position(final double theX, final double theY) {
		_myPosition.set(theX, theY, 0);
	}
	
	/**
	 * Sets the position of the text to the given vector.
	 * @param thePosition the new position of the text
	 */
	public void position(final CCVector2 thePosition){
		_myPosition.set(thePosition.x, thePosition.y, 0);
	}
	
	/**
	 * Sets the position of the text to the given coordinates.
	 * @param theX new x position for the text
	 * @param theY new y position for the text
	 */
	public void position(final double theX, final double theY, double theZ) {
		_myPosition.set(theX, theY, theZ);
	}
	
	/**
	 * Sets the 3D position of the text to the given vector.
	 * @param thePosition the new position of the text
	 */
	public void position(final CCVector3 thePosition){
		_myPosition.set(thePosition);
	}

	/**
	 * Returns a reference to the position of the text.
	 * @return reference to the position of the text
	 */
	public CCVector3 position() {
		return _myPosition;
	}
	
	public CCVector2 position(int theIndex) {
		return _myTextGrid.gridPosition(theIndex);
	}
	
	/**
	 * Draws the text
	 */
	public void draw(CCGraphics g) {
		_myTextGrid.drawText(g);
	}

//	@Override
//	public CCText clone() {
//		final CCText _myResult = new CCText(_myFont);
//		_myResult._myPosition = _myPosition.clone();
//		_myResult.text(_myText);
//		_myResult.align(_myTextAlign);
//		return _myResult;
//	}
}
