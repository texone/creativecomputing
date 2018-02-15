/*  
 * Copyright (c) 2018  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.demo.ui;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.decorator.background.CCUIBackgroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIFillBackgroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIGradientBackgroundDecorator;
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.input.CCUIContext;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;
import cc.creativecomputing.ui.widget.CCUICheckBoxWidget;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIWidgetPlacementDemo extends CCGLApp {
	
	public CCUIWidget createObjectBarWidget() {
		CCUIHorizontalFlowPane myBarWidget = new CCUIHorizontalFlowPane(400, 0);
		myBarWidget.translation().set(0,0);
		myBarWidget.margin(5);
		myBarWidget.space(5);
		
		CCUIGradientBackgroundDecorator myGradientBack = new CCUIGradientBackgroundDecorator();
		myGradientBack.gradient().top(new CCColor(CCColor.GREEN));
		myGradientBack.gradient().bottom(new CCColor(CCColor.GREEN.darker()));
		myBarWidget.background(myGradientBack);

		CCUIIconWidget myIconWidget = new CCUIIconWidget(CCEntypoIcon.ICON_CHEVRON_DOWN);
		myIconWidget.mouseReleased.add(event -> {
			myIconWidget.active(!myIconWidget.active());
			myIconWidget.text().text(myIconWidget.active() ? CCEntypoIcon.ICON_CHEVRON_DOWN.text : CCEntypoIcon.ICON_CHEVRON_RIGHT.text);
		});
		myBarWidget.addChild(myIconWidget);
		
		CCUILabelWidget myLabel = new CCUILabelWidget(_myFont, "TEXONE");
		myBarWidget.addChild(myLabel);
		return myBarWidget;
	}
	
	public CCUICheckBoxWidget createCheckBox() {
		CCUICheckBoxWidget myCheckBox = new CCUICheckBoxWidget();
		myCheckBox.background(new CCUIFillBackgroundDecorator(new CCColor(0.3d)));
		return myCheckBox;
	}
	
	public CCUITextFieldWidget createTextField() {
		CCUITextFieldWidget myTextField = new CCUITextFieldWidget(_myFont, "TEXONE");
		myTextField.background(new CCUIFillBackgroundDecorator(new CCColor(0.3d)));
		myTextField.width(100);
		return myTextField;
	}
	
	public CCUIDropDownWidget createDropDown() {
		CCUIDropDownWidget myDropDown = new CCUIDropDownWidget(_myFont);
		CCUIBackgroundDecorator myBackground = new CCUIFillBackgroundDecorator(new CCColor(0.3d));
		myDropDown.background(myBackground);
		myDropDown.border(new CCUILineBorderDecorator(new CCColor(1d), 1, 0));
		myDropDown.width(100);
		
		myDropDown.menue().background(myBackground);
		
		myDropDown.addItem("item 1");
		myDropDown.addItem("item 2");
		myDropDown.addItem("item 3");
		myDropDown.addItem("item 4");
		return myDropDown;
	}
	
	public CCUIWidget createPropertyWidget() {
		CCUIGridPane myPropertyWidget = new CCUIGridPane(400,0);
		myPropertyWidget.margin(5);
		myPropertyWidget.space(5);
		myPropertyWidget.columnWidths(10,10,10);
		myPropertyWidget.rowHeight(30);
		
		for(int i = 0; i < 10;i++) {
			CCUITableEntry myEntry = new CCUITableEntry();
			myEntry.column = 0;
			myEntry.row = i;
			myEntry.horizontalAlignment = CCUIHorizontalAlignment.RIGHT;
			myPropertyWidget.addChild(new CCUILabelWidget(_myFont, "prop" + i), myEntry);
			myEntry.column = 1;
			myEntry.horizontalAlignment = CCUIHorizontalAlignment.LEFT;
			switch(i % 3) {
			case 0:
				myPropertyWidget.addChild(createCheckBox(), myEntry);
				break;
			case 1:
				myPropertyWidget.addChild(createTextField(), myEntry);
				break;
			case 2:
				myPropertyWidget.addChild(createDropDown(), myEntry);
				break;
			}
		}
		
		return myPropertyWidget;
	}
	
	private CCUIWidget _myObjectWidget;
	private CCUIWidget _myPropertyWidget;
	
	private CCTextureMapFont _myFont;
	
	private CCUIContext _myContext;

	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {

		_myFont = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 20, 2, 2);
		

		CCUIVerticalFlowPane myVerticalPane = new CCUIVerticalFlowPane();
		myVerticalPane.margin(5);
		myVerticalPane.space(5);
		_myContext = new CCUIContext(_myMainWindow, myVerticalPane);

		_myContext.widget().addChild(createObjectBarWidget());
		_myContext.widget().addChild(createPropertyWidget());

		
//		_myMainWindow.mouseMoveEvents.add(pos -> {
//			if(_myWidget.isInside(pos.x - g.width()/2, g.height()/2 - pos.y)){
//				_myWidget.border(new CCUILineBorderDecorator(CCColor.RED.clone(), 2, 30));
//			}else{
//				_myWidget.border(new CCUILineBorderDecorator(CCColor.WHITE.clone(), 2, 30));
//			}
//		});
	}

	@Override
	public void update(final CCGLTimer theTimer) {
		
		_myContext.widget().update(theTimer);
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.line(-g.width()/2,100,g.width()/2,100);
		g.line(0,-g.height()/2,0,g.height()/2);
		g.pushAttribute();
		_myContext.widget().draw(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCUIWidgetPlacementDemo myDemo = new CCUIWidgetPlacementDemo();
		myDemo.run();
	}
}

