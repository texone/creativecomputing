/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.demo.ui;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIGradientDrawable;
import cc.creativecomputing.ui.draw.CCUIRoundedFillDrawable;
import cc.creativecomputing.ui.draw.CCUIStrokeDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIGridPane.CCUITableEntry;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUISlider;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBox;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIWidgetPlacementDemo extends CCGLApp {
	
	public CCUIWidget createObjectBarWidget() {
		CCUIHorizontalFlowPane myBarWidget = new CCUIHorizontalFlowPane(400, 0);
		myBarWidget.translation().set(0,0);
		myBarWidget.inset(5);
		myBarWidget.space(5);
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		myGradientBack.gradient().top(new CCColor(CCColor.GREEN));
		myGradientBack.gradient().bottom(new CCColor(CCColor.GREEN.darker()));
		myBarWidget.background(myGradientBack);

		CCUIIconWidget myIconWidget = new CCUIIconWidget(CCEntypoIcon.ICON_TRIANGLE_DOWN);
		myIconWidget.mouseReleased.add(event -> {
			myIconWidget.active(!myIconWidget.active());
			myIconWidget.text().text(myIconWidget.active() ? CCEntypoIcon.ICON_TRIANGLE_DOWN.text : CCEntypoIcon.ICON_TRIANGLE_RIGHT.text);
		});
		myBarWidget.addChild(myIconWidget);
		
		CCUILabelWidget myLabel = new CCUILabelWidget(_myFont, "TEXONE");
		myBarWidget.addChild(myLabel);
		return myBarWidget;
	}
	
	public CCUICheckBox createCheckBox() {
		CCUICheckBox myCheckBox = new CCUICheckBox();
		myCheckBox.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myCheckBox.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myCheckBox;
	}
	
	public CCUITextFieldWidget createTextField() {
		CCUITextFieldWidget myTextField = new CCUITextFieldWidget(_myFont, "TEXONE");
		myTextField.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myTextField.width(100);
		myTextField.inset(4);
		myTextField.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myTextField;
	}
	
	public CCUIDropDownWidget createDropDown() {
		CCUIDropDownWidget myDropDown = new CCUIDropDownWidget(_myFont);
		myDropDown.inset(4);
		myDropDown.verticalAlignment(CCUIVerticalAlignment.CENTER);
		CCUIFillDrawable myBackground = new CCUIFillDrawable(new CCColor(0.3d));
		myDropDown.background(myBackground);
		myDropDown.width(100);
		
		myDropDown.menue().background(myBackground);
		myDropDown.itemSelectBackground(new CCUIFillDrawable(new CCColor(0.5d)));
		myDropDown.itemBackground(new CCUIFillDrawable(new CCColor(0.3d)));
		
		myDropDown.addItem("Item 1");
		myDropDown.addItem("item 2");
		myDropDown.addItem("item 3");
		myDropDown.addSeparator();
		myDropDown.addItem("item 4");
		return myDropDown;
	}
	
	public CCUITextFieldWidget createValueBox() {
		CCUIValueBox myTextField = new CCUIValueBox(_myFont, 0);
		myTextField.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myTextField.width(100);
		myTextField.inset(4);
		myTextField.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myTextField;
	}
	
	public CCUISlider createSlider() {
		CCUISlider mySlider = new CCUISlider(100,14, 0, 100, 50);
		mySlider.background(new CCUIRoundedFillDrawable(new CCColor(0.3d), 7));
		mySlider.foreground(new CCUIFillDrawable(new CCColor(0.7d)));
		mySlider.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return mySlider;
	}
	
	public CCUIWidget createPropertyWidget() {
		CCUIGridPane myPropertyWidget = new CCUIGridPane(400,0);
		myPropertyWidget.inset(5);
		myPropertyWidget.space(10);
		myPropertyWidget.columnWidths(10,10,10);
		myPropertyWidget.rowHeight(25);
		
		for(int i = 0; i < 50;i++) {
			CCUITableEntry myEntry = new CCUITableEntry();
			myEntry.column = 0;
			myEntry.row = i;
			CCUILabelWidget myLabel = new CCUILabelWidget(_myFont, "prop" + i);
			myLabel.horizontalAlignment (CCUIHorizontalAlignment.RIGHT);
			myLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
			myPropertyWidget.addChild(myLabel, myEntry);
			myEntry.column = 1;
			
			switch(i % 5) {
			case 0:
				myPropertyWidget.addChild(createCheckBox(), myEntry);
				break;
			case 1:
				myPropertyWidget.addChild(createTextField(), myEntry);
				break;
			case 2:
				myPropertyWidget.addChild(createDropDown(), myEntry);
				break;
			case 3:
				myPropertyWidget.addChild(createSlider(), myEntry);
				break;
			case 4:
				myPropertyWidget.addChild(createValueBox(), myEntry);
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
		myVerticalPane.inset(5);
		myVerticalPane.space(5);
		myVerticalPane.translation().set(-window().framebufferSize().x / 2, window().framebufferSize().y / 2);
		_myContext = new CCUIContext(_myMainWindow, myVerticalPane);

		_myContext.widget().addChild(createObjectBarWidget());
		_myContext.widget().addChild(createPropertyWidget());

		_myMainWindow.scrollEvents.add((x,y) -> {CCLog.info(x,y);});
		
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

