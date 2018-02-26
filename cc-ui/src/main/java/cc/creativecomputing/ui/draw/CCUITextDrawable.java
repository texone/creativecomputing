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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 */
public class CCUITextDecorator extends CCUIForegroundDecorator{
	
	@CCProperty(name="font")
	private String _myFont;
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor(1f);
	
	@CCProperty(name = "background_color")
	private CCColor _myBackgroundColor = new CCColor(1f);
	
	@CCProperty(name="linebreak")
	private CCLineBreakMode _myLineBreakMode = CCLineBreakMode.NONE;
	
	@CCProperty(name="text")
	private String _myTextString;
	
	@CCProperty(name="horizontal_alignment")
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	@CCProperty(name="vertical_alignment")
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.TOP;
	
	
	private CCVector2 _myAlignment = new CCVector2();
	
	private CCTextField _myText;
	
	private boolean _myShowCursor = false;
	private boolean _myIsInSelection = false;
	
	private int _myCursorStartIndex = 0;
	private int _myCursorEndIndex = 0;
	
	
	/**
	 * @param theID
	 */
	public CCUITextDecorator() {
		super("text");
	}
	
	public CCUITextDecorator(CCTextField theTextField) {
		super("text");
		_myText = theTextField;
	}
	
	

	/* (non-Javadoc)
	 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#setup(cc.creativecomputing.newui.CCUI)
	 */
	@Override
	public void setup(CCUI theUI, CCUIWidget theWidget) {
		_myText = new CCTextField(theUI.font(_myFont), "");
		_myText.lineBreak(_myLineBreakMode);
		
//		if(_myLineBreakMode == CCLineBreakMode.BLOCK) {
//			_myText.width(theWidget.width());
//			_myText.height(theWidget.height());
//		}
		
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
	
	public CCTextField text() {
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
//	
//	public CCVector2 cursorPosition() {
//		return _myText.position(_myCursorStartIndex);
//	}
	
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
			_myAlignment.y = -_myText.ascent();
			break;
		case CENTER:
			_myAlignment.y = (theWidget.height() - _myText.height()/2) / 2;
			break;
		case BOTTOM:
			_myAlignment.y = _myText.descent();
			break;
		}
		
		_myText.position(_myAlignment.x * (theWidget.width() - _myText.width()) + theWidget.inset(), _myAlignment.y - theWidget.inset());
		
		g.color(_myColor);
		_myText.draw(g);
	}

}
