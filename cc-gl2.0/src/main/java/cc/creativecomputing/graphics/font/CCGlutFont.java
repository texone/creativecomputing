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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.util.gl2.GLUT;


public class CCGlutFont extends CCFont<CCGlutChar>{
	
	public static enum CCGlutFontType{
		BITMAP_8_BY_13(GLUT.BITMAP_8_BY_13),
		BITMAP_9_BY_15(GLUT.BITMAP_9_BY_15),
		BITMAP_HELVETICA_10(GLUT.BITMAP_HELVETICA_10),
		BITMAP_HELVETICA_12(GLUT.BITMAP_HELVETICA_12),
		BITMAP_HELVETICA_18(GLUT.BITMAP_HELVETICA_18),
		BITMAP_TIMES_ROMAN_10(GLUT.BITMAP_TIMES_ROMAN_10),
		BITMAP_TIMES_ROMAN_24(GLUT.BITMAP_TIMES_ROMAN_24);
		
		public final int glID;
		
		private CCGlutFontType(final int theGLID){
			glID = theGLID;
		}
	}
	
	private final CCGlutFontType _myFontType;
	private final GLUT _myGlut;

	public CCGlutFont(final CCGlutFontType theFontType,final CCCharSet theCharSet) {
		super(theCharSet);
		_myFontType = theFontType;
		_myGlut = new GLUT();
		_mySpaceWidth = width(' ');
		
		createChars(null, null);
	}
	
	

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CCGlutChar[] createCharArray(int theSize) {
		return new CCGlutChar[theSize];
	}

	@Override
	protected double charWidth(final char theChar) {
		return _myGlut.glutBitmapWidth(_myFontType.glID, theChar);
	}

	@Override
	protected void createChars(BufferedImage theCharImage, Graphics2D theGraphics) {
		int index = 0;
		
		for (int i = 0; i < _myCharCount; i++){
			char c = (char)index;
			
			_myCharCodes[index] = c;

			try{
				_myChars[index] = new CCGlutChar(c, charWidth(c), height(), _myGlut, _myFontType.glID);
			}catch (Exception theException){
				theException.printStackTrace();
				throw new RuntimeException(theException);
			}
			index++;
		}
		
		_myCharCount = index;
	}
	
	@Override
	public int index(final char theChar){
		return theChar;
	}
	
	@Override
	public void beginText(CCGraphics g){
		g.gl.glPushMatrix();
//		g.gl.glPushAttrib(GL.GL_CURRENT_BIT);		//save current raster position
	};
	
	@Override
	public void endText(CCGraphics g){
//		g.gl.glPopAttrib();
		g.gl.glPopMatrix();
	};

	/**
	 * Width of this character for a font of size 1.
	 */
	public double width(final char theChar){
		return (double)_myGlut.glutBitmapWidth(_myFontType.glID, theChar)/size();
	}
	
	public double width(final int theIndex){
		return width((char)theIndex);
	}
	
	public double width(final char[] theTextBuffer, final int theStart, final int theStop){
		return width(new String(theTextBuffer,theStart,theStop-theStart));
	}
	
	public double width(final String theText){
		return (double)_myGlut.glutBitmapLength(_myFontType.glID, theText)/size();
	}

	public double height() {
		return 1.0f;
	}
	
	@Override
	public double normedDescent(){
		return 1;
	}

	public int size() {
		switch (_myFontType) {
		case BITMAP_8_BY_13:
			return 13;
		case BITMAP_9_BY_15:
			return 15;
		case BITMAP_HELVETICA_10:
			return 10;
		case BITMAP_HELVETICA_12:
			return 12;
		case BITMAP_HELVETICA_18:
			return 18;
		case BITMAP_TIMES_ROMAN_10:
			return 10;
		case BITMAP_TIMES_ROMAN_24:
			return 24;
		default:
			return 0;
		}
	}

}
