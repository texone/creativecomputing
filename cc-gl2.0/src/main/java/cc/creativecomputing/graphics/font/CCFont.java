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
package cc.creativecomputing.graphics.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;


import cc.creativecomputing.graphics.CCGraphics;

/**
 * This class provides the basic font features. It contains the font metrics as well as the charset
 * information. Overwrite this class to create you own font class, which can be handled by the text
 * methods of {@link CCGraphics}. To create your own font you have to at least overwrite the 
 * {@link #createChars(BufferedImage, Graphics2D)} method to build the chars for drawing and the
 * {@link #drawChar(CCGraphics, int, double, double, double, double)} to define how a char is drawn.
 * If needed you can overwrite the {@link CCFont#beginText(CCGraphics)} and @link {@link #endText(CCGraphics)}
 * text method to make calls that are needed when the drawing of text begins or ends.
 * 
 * @author Christian Riekoff
 *
 */
public abstract class CCFont<CharType extends CCChar>{
	protected static final Color WHITE = Color.WHITE;
	protected static final Color TRANSPARENT = new Color(0F,0F,0F,0F);
	
	protected Font _myFont;
	protected FontMetrics _myFontMetrics;
	
	protected CCKerningTable _myKerningTable;
	
	protected int _mySize;
	
//	protected CCGraphics g;
	
	protected int _myAsciiLookUpTable[]; // quick lookup for the ascii chars
	
	protected int _myCharCount;
	protected CCCharSet _myCharSet;
	protected int _myCharCodes[];
	
	//Arrays for saving the metrics
	protected CharType[] _myChars;
	
	protected double _mySpaceWidth;
	protected double _myHeight;
	
	protected double _myAscent;
	protected double _myDescent;
	protected double _myNormalizedAscent;
	protected double _myNormalizedDescent;
	protected double _myNormalizedHeight;
	
	protected double _myLeading;
	
	protected boolean _myIsAntialiase;
	
	protected CCFontSettings _mySettings;
	
	
	protected CCFont(final CCCharSet theCharSet){
		_myFont = null;
		_myAsciiLookUpTable = new int[128];
		Arrays.fill(_myAsciiLookUpTable, -1);
		
		if(theCharSet == null){
			_myCharSet = CCCharSet.REDUCED_CHARSET;
		}else{
			_myCharSet = theCharSet;
		}
		_myCharCount = _myCharSet.size();
		
		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myFontMetrics = null;
		_mySize = 0;
		_myNormalizedHeight = 0;
		_myIsAntialiase = true;
	}
	
	protected double getScaledDescent(){
		return _myFontMetrics.getDescent() * 1.25f;
	}
	
	protected CCFont(CCFontSettings theSettings){
		_mySettings = theSettings;
		setFont(theSettings);
		_myKerningTable = theSettings.kerningTable();
	}
	
	protected void setFont(CCFontSettings theSettings) {
		_myFont = theSettings.font();
		_mySize = _myFont.getSize();
		
		_myAsciiLookUpTable = new int[128];
		Arrays.fill(_myAsciiLookUpTable, -1);
		
		_myCharSet = theSettings.charset();
		_myCharCount = _myCharSet.size();

		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myIsAntialiase = theSettings.isSmooth();

		final int myImageSize = _mySize * 3;
		
		final Object myAntialias = _myIsAntialiase ? 
		RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
			
		final BufferedImage myCharImage = new BufferedImage(myImageSize, myImageSize, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D myGraphics = (Graphics2D) myCharImage.getGraphics();
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, myAntialias);
//			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		myGraphics.setFont(_myFont);
		myGraphics.setBackground(TRANSPARENT);
			
		_myFontMetrics = myGraphics.getFontMetrics();
		_mySpaceWidth = (double)_myFontMetrics.charWidth(' ') / _mySize;
		_myHeight = _myFontMetrics.getMaxAscent() + _myFontMetrics.getMaxDescent() * 1.25f;
		_myNormalizedHeight = _myHeight / _mySize;
		
		_myAscent = _myFontMetrics.getAscent();
		_myDescent = _myFontMetrics.getDescent() * 1.25f;
		
		_myLeading = _myFontMetrics.getLeading();
		
		_myNormalizedAscent = _myAscent / _mySize;
		_myNormalizedDescent = _myDescent / _mySize;
		
		createChars(myCharImage,myGraphics);
	}
	
	public double kerning(char theChar1, char theChar2) {
		return kerning(index(theChar1), index(theChar2));
	}
	
	public double kerning(int theIndex1, int theIndex2) {
		if(theIndex1 < 0 || theIndex2 < 0)return 0;
		if(_myKerningTable == null)return 0;
		return _myKerningTable.kerning(_myChars[theIndex1].glyphCode(), _myChars[theIndex2].glyphCode());
	}
	
	protected abstract CharType[] createCharArray(final int theSize);
	
	protected double charWidth(final char theChar) {
		return ((double)_myFontMetrics.charWidth(theChar)) / _mySize;
	}
	
	protected abstract void createChars(final BufferedImage theCharImage, final Graphics2D theGraphics);
	
	public CharType[] chars() {
		return _myChars;
	}
	
	/**
	 * Called by the graphics object when rendering text, th
	 * @param g
	 */
	public void beginText(CCGraphics g){}

    public double drawChar(CCGraphics g, int theIndex, double theSize, double theX, double theY, double theZ) {
		if(theIndex < 0 || theIndex > _myChars.length || _myChars[theIndex] == null)return 0;
		return _myChars[theIndex].draw(g, theX, theY, theZ, theSize);
	}
	
	public void endText(CCGraphics g){}

    /**
	 * Get index for the char (convert from unicode to bagel charset).
	 * @return index into arrays or -1 if not found
	 */
	public int index(final char theChar){
		// degenerate case, but the find function will have trouble
		// if there are somehow zero chars in the lookup
		if (_myCharCodes.length == 0)
			return -1;
		
		// quicker lookup for the ascii fellers
		if (theChar < 128)
			return _myAsciiLookUpTable[theChar];

		// some other unicode char, hunt it out
		return index_hunt(theChar, 0, _myCharCount - 1);
	}

	protected int index_hunt(int c, int start, int stop){
		int pivot = (start + stop) / 2;

		// if this is the char, then return it
		if (c == _myCharCodes[pivot])
			return pivot;

		// char doesn't exist, otherwise would have been the pivot
		//if (start == stop) return -1;
		if (start >= stop)
			return -1;

		// if it's in the lower half, continue searching that
		if (c < _myCharCodes[pivot])
			return index_hunt(c, start, pivot - 1);

		// if it's in the upper half, continue there
		return index_hunt(c, pivot + 1, stop);
	}
	
	/**
	 * Returns the ascent of this font from the baseline.
	 * The value is based on a font of size 1.
	 */
	public double normedAscent(){
		return _myNormalizedAscent;
	}
	
	public double ascent() {
		return _myAscent;
	}

	/**
	 * Returns how far this font descends from the baseline.
	 * The value is based on a font size of 1.
	 */
	public double normedDescent(){
		return _myNormalizedDescent;
	}
	
	public double descent() {
		return _myDescent;
	}
	
	public double width(final int theIndex){
		if(theIndex < 0 || theIndex > _myChars.length || _myChars[theIndex] == null)return 0;
		return _myChars[theIndex].width();
	}

	/**
	 * Width of this character for a font of size 1.
	 */
	public double width(final char theChar){
		return width(index(theChar));
	}
	
	public double width(final char[] theTextBuffer, final int theStart, final int theStop){
		double myResult = 0;
		for(int i = theStart; i < theStop;i++) {
			myResult += width(theTextBuffer[i]);
		}
		return myResult;
	}
	
	public double width(final String theText){
		return width(theText.toCharArray(), 0, theText.length());
	}
	
	public double height(){
		return _myNormalizedHeight;
	}
	
	public double spaceWidth(){
		return _mySpaceWidth;
	}
	
	public double leading() {
		return _myLeading;
	}
	
	public void leading(int theLeading) {
		_myLeading = theLeading;
	}
	
	public int size(){
		return _mySize;
	}
	
	public boolean canDisplay(final char theChar) {
		return _myFont.canDisplay(theChar) && _myFontMetrics.charWidth(theChar) > 0;
	}
	
	/**
	 * Returns the family name of this Font.
	 * <p>
	 * The family name of a font is font specific. Two fonts such as Helvetica Italic and Helvetica Bold 
	 * have the same family name, Helvetica, whereas their font face names are Helvetica Bold and Helvetica 
	 * Italic. The list of available family names may be obtained by using the GraphicsEnvironment.getAvailableFontFamilyNames() 
	 * method.
	 * <p>
	 * Use {@linkplain #name()} to get the logical name of the font. Use {@linkplain #fontName()} to get the font face name of the font.
	 * @see #name()
	 * @see #fontName()
	 * @return a String that is the family name of this Font.
	 */
	public String family(){
		return _myFont.getFamily();
	}

	/**
	 * Returns the logical name of this Font. Use {@linkplain #family()} to get the family name of the font. 
	 * Use {@linkplain #fontName()} to get the font face name of the font.
	 * 
	 * @see #family()
	 * @see #fontName()
	 * @return a String representing the logical name of this Font.
	 */
	public String name() {
		return _myFont.getName();
	}
	
	/**
	 * Returns the font face name of this Font. For example, Helvetica Bold could be returned as a font face name. 
	 * Use {@linkplain #family()} to get the family name of the font. Use {@linkplain #name()} to get the logical name of the font.
	 * @see #family()
	 * @see #name()
	 * @return a String representing the font face name of this Font.
	 */
	public String fontName(){
		return _myFont.getFontName();
	}
}
