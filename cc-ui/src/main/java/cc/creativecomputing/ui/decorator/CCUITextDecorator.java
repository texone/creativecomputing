/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.decorator;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.font.CCTextAlign;
import cc.creativecomputing.gl.font.CCTextArea;
import cc.creativecomputing.gl.font.CCTextField;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 */
@CCXMLPropertyObject(name="text_foreground")
public class CCUITextDecorator extends CCUIForegroundDecorator{
	
	@CCXMLProperty(name="font", node=false)
	private String _myFont;
	
	@CCXMLProperty(name = "color", optional = true)
	private CCColor _myColor = new CCColor(1f);
	
	@CCXMLProperty(name = "background_color", optional = true)
	private CCColor _myBackgroundColor = new CCColor(1f);
	
	@CCXMLProperty(name="linebreak", node=false, optional = true)
	private CCLineBreakMode _myLineBreakMode = CCLineBreakMode.NONE;
	
	@CCXMLProperty(name="text")
	private String _myTextString;
	
	@CCXMLProperty(name="horizontal_alignment", optional = true)
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	@CCXMLProperty(name="vertical_alignment", optional = true)
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.TOP;
	
	
	private CCVector2 _myAlignment = new CCVector2();
	
	private CCTextField _myText;
	
	private boolean _myShowCursor = false;
	private boolean _myIsInSelection = false;
	
	private int _myCursorStartIndex = 0;
	private int _myCursorEndIndex = 0;
	
	@SuppressWarnings("unused")
	private CCUIWidget _myWidget;
	
	/**
	 * @param theID
	 */
	public CCUITextDecorator() {
		super("text");
	}
	
	

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#setup(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theWidget) {
		_myWidget = theWidget;
		_myText = new CCTextArea(theUI.font(_myFont), "");
		_myText.lineBreak(_myLineBreakMode);
		
		if(_myLineBreakMode == CCLineBreakMode.BLOCK) {
			_myText.width(theWidget.width());
			_myText.height(theWidget.height());
		}
		
		horizontalAlignment(_myHorizontalAlignment);
		verticalAlignment(_myVerticalAlignment);
		
		if(_myTextString.startsWith("lorem ipsum")) {
			int myNumberOfWords = Integer.parseInt(_myTextString.split(" ")[2]);
			_myTextString = CCLoremIpsumGenerator.generate(myNumberOfWords);
		}
		
		_myText.text(_myTextString);
	}
	
	public CCUIHorizontalAlignment horizontalAlignment() {
		return _myHorizontalAlignment;
	}

	public void horizontalAlignment(CCUIHorizontalAlignment theHorizontalAlignment) {
		_myHorizontalAlignment = theHorizontalAlignment;
		
		switch(_myHorizontalAlignment) {
		case RIGHT:
			_myText.align(CCTextAlign.RIGHT);
			_myAlignment.x = 1;
			break;
		case CENTER:
			_myText.align(CCTextAlign.CENTER);
			_myAlignment.x = 0.5f;
			break;
		case LEFT:
			_myText.align(CCTextAlign.LEFT);
			_myAlignment.x = 0;
			break;
		}
	}
	

	
	public CCUIVerticalAlignment verticalAlignment() {
		return _myVerticalAlignment;
	}

	public void verticalAlignment(CCUIVerticalAlignment theVerticalAlignment) {
		_myVerticalAlignment = theVerticalAlignment;
		
		switch(_myVerticalAlignment) {
		case TOP:
			_myAlignment.y = 1;
			break;
		case CENTER:
			_myAlignment.y = 0.5f;
			break;
		case BOTTOM:
			_myAlignment.y = 0;
			break;
		}
	}
	
	public CCTextArea text() {
		return _myText;
	}
	
	public boolean showCursor() {
		return _myShowCursor;
	}
	
	public void showCursor(boolean theShowCursor) {
		_myShowCursor = theShowCursor;
	}
	
	public boolean isInSelection() {
		return _myIsInSelection;
	}
	
	public void isInSelection(boolean theIsInSelection) {
		_myIsInSelection = theIsInSelection;
	}
	
	public CCVector2 cursorPosition() {
		return _myText.position(_myCursorStartIndex);
	}
	
	public int startCursorIndex() {
		return _myCursorStartIndex;
	}
	
	public void startCursorIndex(int theIndex) {
		_myCursorStartIndex = theIndex;
	}
	
	public int endCursorIndex() {
		return _myCursorEndIndex;
	}
	
	public void endCursorIndex(int theEndCursorIndex) {
		_myCursorEndIndex = theEndCursorIndex;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
	 */
	@Override
	public void draw(CCGraphics g, CCUIWidget theWidget) {
		switch(_myVerticalAlignment) {
		case TOP:
			_myAlignment.y = theWidget.height() - _myText.ascent();
			break;
		case CENTER:
			_myAlignment.y = (theWidget.height() - _myText.height()/2) / 2;
			break;
		case BOTTOM:
			_myAlignment.y = _myText.descent();
			break;
		}
		
		if(_myIsInSelection) {
			g.color(_myBackgroundColor);
			_myText.textGrid().drawHeighlight(g, _myCursorStartIndex, _myCursorEndIndex);
		}
		
		_myText.position(_myAlignment.x * (theWidget.width() - _myText.width()), _myAlignment.y);
		
		g.color(_myColor);
		_myText.draw(g);
		
		if(_myShowCursor) {
			CCVector2 myCursorPosition = cursorPosition();
			g.color(255,0,0);
			g.line(
				(int)myCursorPosition.x, myCursorPosition.y, 
				(int)myCursorPosition.x, myCursorPosition.y - _myText.size()
			);
		}
	}

}
