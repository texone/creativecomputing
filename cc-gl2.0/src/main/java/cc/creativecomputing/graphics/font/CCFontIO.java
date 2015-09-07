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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;


import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.font.CCGlutFont.CCGlutFontType;
import cc.creativecomputing.graphics.font.text.CCTextAlign;


/**
 * Use CCFontIO to create Fonts and text objects. You can create different
 * kinds of fonts and text objects. You can load fonts from font names using
 * the font library of your system. Or by providing a font file. Using a font file
 * also kerning information is loaded.
 * @author texone
 *
 */
public class CCFontIO{
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
	
	/**
	 * Get a list of the fonts installed on the system that can be used
	 * by Java. Not all fonts can be used in Java, in fact it's mostly
	 * only TrueType fonts. OpenType fonts with CFF data such as Adobe's
	 * OpenType fonts seem to have trouble (even though they're sort of
	 * TrueType fonts as well, or may have a .ttf extension). Regular
	 * PostScript fonts seem to work O.K. though.
	 * @return list of accessible system fonts
	 */
	static public String[] list(){

		// getFontList is deprecated in 1.4, so this has to be used
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font fonts[] = ge.getAllFonts();
			String list[] = new String[fonts.length];
			for (int i = 0; i < list.length; i++){
				list[i] = fonts[i].getName();
			}
			return list;

		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error inside PFont.list()");
		}
	}
	
	static public void printFontList(){
		for(String myFontName:list()){
			System.out.println(myFontName);
		}
	}
	
	
	
	
	
	/////////////////////////////////////////////
	//
	// VECTOR FONT HANDLING
	//
	/////////////////////////////////////////////
	
	/**
	 * Creates a vector font. Based on the given font name, size and charset
	 * @param theName name of the font to create
	 * @param theSize size of the font to create
	 * @param theCharset charset to use
	 * @return a vector font
	 * @see CCCharSet
	 * @see CCVectorFont
	 */
	static public CCVectorFont createVectorFont(final String theName, final double theSize, final CCCharSet theCharset){
		CCFontSettings mySettings = new CCFontSettings(theName, theSize, true, theCharset);
		mySettings.detail(30);
		return new CCVectorFont(mySettings);
	}

	/**
	 * Creates a vector font. Based on the given font name and size.
	 * @param theName name of the font to create
	 * @param theSize size of the font to create
	 * @return a vector font
	 * @see CCVectorFont
	 */
	static public CCVectorFont createVectorFont(final String theName,final double theSize){
		return createVectorFont(theName, theSize, CCCharSet.REDUCED_CHARSET);
	}

	
	
	static public CC3DFont create3DFont(final String theName, final double theSize, final CCCharSet theCharset, final double theDepth){
		CCFontSettings mySettings = new CCFontSettings(theName, theSize, true, theCharset);
		mySettings.detail(30);
		mySettings.depth(theDepth);
		return new CC3DFont(mySettings);
	}

	static public CC3DFont create3DFont(final String theName,final double theSize, final double theDepth){
		return create3DFont(theName,theSize, CCCharSet.REDUCED_CHARSET, theDepth);
	}
	
	private static final HashMap<String, CCOutlineFont> _myOutlineFontMap = new HashMap<String, CCOutlineFont>();
	
	static public CCMesh createVectorText(String theText,String name, double theSize){
		return createVectorText(theText,name, theSize, 20);
	}
	
	static public CCMesh createVectorText(String theText,String name, double theSize, int theDetail){
		String lowerName = name.toLowerCase();
		CCOutlineFont baseFont = null;

		try{
			if(_myOutlineFontMap.containsKey(lowerName)){
				baseFont = _myOutlineFontMap.get(lowerName);
			}else{
				baseFont = createOutlineFont(name, theSize,theDetail);
				_myOutlineFontMap.put(lowerName, baseFont);
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + name);
		}
		
		
		return baseFont.createMesh(theText, CCTextAlign.CENTER, theSize, 0, -theSize/2, 0);
	}
	
	/////////////////////////////////////////////
	//
	// OUTLINE FONT HANDLING
	//
	/////////////////////////////////////////////
	
	/**
	 * Creates an outline font.
	 * @param theName
	 * @param theSize
	 * @param theCharset
	 * @param theDetail
	 * @return
	 */
	static public CCOutlineFont createOutlineFont(final String theName, final double theSize, final CCCharSet theCharset, final int theDetail){
		CCFontSettings mySettings = new CCFontSettings(theName, theSize, true, theCharset);
		mySettings.detail(theDetail);
		return new CCOutlineFont(mySettings);
	}

	static public CCOutlineFont createOutlineFont(final String theName,final double theSize, final int theDetail){
		return createOutlineFont(theName,theSize, CCCharSet.REDUCED_CHARSET, theDetail);
	}

	/////////////////////////////////////////////
	//
	// BITMAP FONT HANDLING
	//
	/////////////////////////////////////////////
	
	static private Map<String, CCFont<?>> _myRegisteredFonts = new HashMap<String, CCFont<?>>();
	
	static public void registerFont(final String theKey, final CCFont<?> theFont) {
		_myRegisteredFonts.put(theKey, theFont);
	}
	
	static public CCFont<?> registeredFont(final String theKey) {
		return _myRegisteredFonts.get(theKey);
	}
	
	static public Map<String,Font> _myFontMap = new HashMap<String, Font>();

	/////////////////////////////////////////////////////
	//
	// TEXTURE MAP FONTS
	//
	/////////////////////////////////////////////////////
	
	static public CCTextureMapFont createTextureMapFont(CCFontSettings theFontSettings){
		return new CCTextureMapFont(theFontSettings);
	}
	
	/**
	 * 
	 */
	static public CCTextureMapFont createTextureMapFont(final String theName, final double theSize, final boolean theIsSmooth, final CCCharSet theCharset){
		return new CCTextureMapFont(new CCFontSettings(theName, theSize, theIsSmooth, theCharset));
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final double theSize, final boolean theIsSmooth){
		return createTextureMapFont(theName, theSize, theIsSmooth, CCCharSet.EXTENDED_CHARSET);
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final double theSize, final CCCharSet theCharSet){
		return createTextureMapFont(theName, theSize, true, theCharSet);
	}

	static public CCTextureMapFont createTextureMapFont(final String theName, final double theSize){
		return createTextureMapFont(theName, theSize, true, CCCharSet.EXTENDED_CHARSET);
	}
	
	/////////////////////////////////////////////
	//
	// GLUT FONT HANDLING
	//
	/////////////////////////////////////////////
	
	static public CCGlutFontType BITMAP_8_BY_13 = CCGlutFontType.BITMAP_8_BY_13;
	static public CCGlutFontType BITMAP_9_BY_15 = CCGlutFontType.BITMAP_9_BY_15;
	static public CCGlutFontType BITMAP_HELVETICA_10 = CCGlutFontType.BITMAP_HELVETICA_10;
	static public CCGlutFontType BITMAP_HELVETICA_12 = CCGlutFontType.BITMAP_HELVETICA_12;
	static public CCGlutFontType BITMAP_HELVETICA_18 = CCGlutFontType.BITMAP_HELVETICA_18;
	static public CCGlutFontType BITMAP_TIMES_ROMAN_10 = CCGlutFontType.BITMAP_TIMES_ROMAN_10;
	static public CCGlutFontType BITMAP_TIMES_ROMAN_24 = CCGlutFontType.BITMAP_TIMES_ROMAN_24;
	
	static public CCGlutFont createGlutFont(final CCGlutFontType theFontType, final CCCharSet theCharSet){
		return new CCGlutFont(theFontType,theCharSet);
	}
	
	static public CCGlutFont createGlutFont(final CCGlutFontType theFontType){
		return new CCGlutFont(theFontType,CCCharSet.REDUCED_CHARSET);
	}
	
	static public CCFont<?> defaultFont(){
		return createGlutFont(CCGlutFontType.BITMAP_HELVETICA_10);
	}
}
