package cc.creativecomputing.uinano;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

/**
 * Text label widget.
 *
 * The font and color can be customized. When \ref Widget::setFixedWidth() is
 * used, the text is wrapped when it surpasses the specified width.
 */
public class CCUILabel extends CCWidget {

	protected CCColor _myColor = new CCColor();
	protected CCTextField _myText;

	public CCUILabel(CCWidget theParent, String theCaption, CCFont<?> theFont) {
		this(theParent, theCaption, theFont, -1);
	}

//	public Label(Widget parent, String caption) {
//		this(parent, caption, "sans", -1);
//	}

	public CCUILabel(CCWidget parent, String caption, CCFont<?> font, int fontSize) {
		super(parent);
		_myText = new CCTextField(font, caption);
		_myText.text(caption);
		if (_myTheme != null) {
			_myFontSize = _myTheme.mStandardFontSize;
			_myColor = _myTheme.mTextColor;
		}
		if (fontSize >= 0) {
			_myFontSize = fontSize;
		}
	}

	/**
	 * Get the label's text caption
	 * @return the label's text caption
	 */
	public String caption() {
		return _myText.text();
	}
	
	/**
	 * Set the label's text caption
	 * @param theCaption the label's text caption
	 */
	public void setCaption(String theCaption) {
		_myText.text(theCaption);
	}

	/**
	 * Set the currently active font
	 * @param theFont currently active font
	 */
	public void setFont(CCFont<?> theFont) {
		_myText.font(theFont);
	}

	/**
	 * Get the currently active font
	 * @return currently active font
	 */
	public CCFont<?> font() {
		return _myText.font();
	}

	/**
	 * Get the label color
	 * @return label color
	 */
	public final CCColor color() {
		return _myColor;
	}

	/**
	 * Set the label color
	 * @param theColor the label color
	 */
	public final void setCCColor(CCColor theColor) {
		_myColor = theColor;
	}

	/**
	 * Set the {@linkplain Theme} used to draw this widget
	 */
	@Override
	public void setTheme(Theme theTheme) {
		super.setTheme(theTheme);
		if (_myTheme == null) return;
		
		_myFontSize = _myTheme.mStandardFontSize;
		_myColor = _myTheme.mTextColor;
	}

	/**
	 * Compute the size needed to fully display the label
	 */
	@Override
	public CCVector2 preferredSize(CCGraphics g) {
		if (_myText.text().equals("")) {
			return new CCVector2();
		}
		_myText.fontSize(_myFontSize);
		if (_myFixedSize.x > 0) {
			_myText.align(CCTextAlign.LEFT);
			return new CCVector2(_myText.width(),_myText.height());
		} else {
			_myText.align(CCTextAlign.LEFT);
			return new CCVector2(_myText.width(),_myText.height());
		}
	}

	/**
	 * Draw the label
	 */
	@Override
	public void draw(CCGraphics g) {
		super.draw(g);
		g.color(_myColor);
		_myText.fontSize(_myFontSize);
		_myText.draw(g);
	}
	
	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("caption", _myText.text());
		s.add("color", _myColor);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try{
			_myColor.set(s.color("color"));
			_myText.text(s.attribute("caption"));
		}catch(Exception e){
			return false;
		}
		return true;
	}
}