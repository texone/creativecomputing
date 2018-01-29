package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.font.CCFont;


public class CCText extends CCMultiFontText{
	
	public CCText(CCFont<?> theFont, int theSize){
		super();
		addText("", theFont, theSize);
	}
	
	public CCText(){
		super();
	}
	
	public String text() {
		return _myTextParts.get(0).text();
	}
	
	/**
	 * Set the text to display
	 * 
	 * @param theText
	 */
	@CCProperty(name = "text")
	public void text(final String theText) {
		_myTextParts.get(0).text(theText);
		breakText();
	}
	
	public void text(Number theNumber) {
		text(theNumber.toString());
	}
	
	public void text(final int theText) {
		text(Integer.toString(theText));
	}
	
	public void text(final char theChar) {
		text(Character.toString(theChar));
	}
	
	public void text(final float theText) {
		text(Float.toString(theText));
	}
	
	public void text(final double theText) {
		text(Double.toString(theText));
	}
	
	/**
	 */
	public double size() {
		return _myTextParts.get(0).size();
	}

	@CCProperty(name = "size", min = 0, max = 100)
	public void size(double theSize) {
		_myTextParts.get(0).size(theSize);
	}

	/**
	 */
	public CCFont<?> font() {
		return _myTextParts.get(0).font();
	}

	public void font(CCFont<?> theFont) {
		_myTextParts.get(0).font(theFont);
	}

	public void font(CCFont<?> theFont, double theSize) {
		_myTextParts.get(0).font(theFont, theSize);
	}

	@CCProperty(name = "leading", min = 0, max = 100)
	public void leading(double theTextLeading) {
		_myTextParts.get(0).leading(theTextLeading);
	}

	public double ascent() {
		return _myTextParts.get(0).ascent();
	}

	public double descent() {
		return _myTextParts.get(0).descent();
	}

	@CCProperty(name = "spacing", min = 0, max = 100)
	public void spacing(double theSpacing) {
		_myTextParts.get(0).spacing(theSpacing);
	}
	
}