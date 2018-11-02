package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.font.CCFont;


public class CCText extends CCMultiFontText{
	
	public CCText(CCFont<?> theFont){
		super();
		addText("", theFont);
	}
	
	public CCText(){
		super();
	}
	
	@CCProperty(name = "text")
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
		if(theText.equals(text()))return;
		_myTextParts.get(0).text(theText);
		breakText();
	}
	
	public void text(final String...theLines) {
		StringBuilder myBuilder = new StringBuilder();
		for(int i = 0; i < theLines.length - 1;i++) {
			myBuilder.append(theLines[i]);
			myBuilder.append("\n");
		}
		myBuilder.append(theLines[theLines.length - 1]);
		_myTextParts.get(0).text(myBuilder.toString());
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
	@CCProperty(name = "size")
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

	@CCProperty(name = "leading", min = 0, max = 100, defaultValue = 1)
	public void leading(double theTextLeading) {
		_myTextParts.get(0).leading(theTextLeading);
		breakText();
	}
	
	@CCProperty(name = "leading")
	public double leading() {
		return _myTextParts.get(0).leading();
	}

	public double ascent() {
		return _myTextParts.get(0).ascent();
	}

	public double descent() {
		return _myTextParts.get(0).descent();
	}

	@CCProperty(name = "spacing", min = 0, max = 100, defaultValue = 1)
	public void spacing(double theSpacing) {
		_myTextParts.get(0).spacing(theSpacing);
		breakText();
	}
	
	@CCProperty(name = "spacing")
	public double spacing() {
		return _myTextParts.get(0).spacing();
	}
	
}