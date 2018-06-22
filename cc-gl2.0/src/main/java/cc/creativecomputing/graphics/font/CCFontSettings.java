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

import cc.creativecomputing.io.CCIOUtil;

public class CCFontSettings{
	private String _myName;
	
	private double _mySize;
	
	private boolean _myIsSmooth;
	
	private CCCharSet _myCharSet;
	
	private double _myBlurRadius;
	
	private Font _myJavaFont;
	
	private CCKerningTable _myKerning;
	
	private int _myDetail;
	
	private double _myDepth;
	
	private boolean _myDoSDF = false;
	
	private double _mySDFSpread = 1;
	
	public CCFontSettings(String theName, double theSize,  boolean theIsSmooth, final CCCharSet theCharset) {
		_myName = theName;
		_mySize = theSize;
		_myIsSmooth = theIsSmooth;
		_myCharSet = theCharset;
		_myBlurRadius = 0;
		createFont();
	}
	
	public boolean doSDF() {
		return _myDoSDF;
	}
	
	public void doSDF(boolean theDOSDF) {
		_myDoSDF = theDOSDF;
	}
	
	public double sdfSpread() {
		return _mySDFSpread;
	}
	
	public void sdfSpread(double theSDFSpread) {
		_mySDFSpread = theSDFSpread;
	}
	
	public CCFontSettings(String theName, double theSize) {
		this(theName, theSize, true, CCCharSet.EXTENDED_CHARSET);
	}
	
	private void createFont(){
		final String lowerName = _myName.toLowerCase();
		try{
			if (lowerName.endsWith(".otf") || lowerName.endsWith(".ttf")){
				_myJavaFont = Font.createFont(Font.TRUETYPE_FONT, CCIOUtil.openStream(_myName)).deriveFont((float)_mySize);
				_myKerning = new CCKerningTable(CCIOUtil.openStream(_myName));
			}else{
				_myJavaFont = new Font(_myName, Font.PLAIN, 1).deriveFont((float)_mySize);
			}
			
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem using createFont() " + "with the file " + _myName);
		}
	}
	
	public Font font() {
		return _myJavaFont;
	}
	
	public double size() {
		return _mySize;
	}
	
	public void size(double theSize) {
		_mySize = theSize;
		createFont();
	}
	
	public CCKerningTable kerningTable() {
		return _myKerning;
	}
	
	public CCCharSet charset() {
		return _myCharSet;
	}
	
	public boolean isSmooth() {
		return _myIsSmooth;
	}
	
	public double blurRadius() {
		return _myBlurRadius;
	}
	
	public void blurRadius(double theBlurRadius) {
		_myBlurRadius = theBlurRadius;
	}
	
	public int detail() {
		return _myDetail;
	}
	
	public void detail(int theDetail) {
		_myDetail = theDetail;
	}
	
	public double depth() {
		return _myDepth;
	}
	
	public void depth(double theDepth) {
		_myDepth = theDepth;
	}
}
