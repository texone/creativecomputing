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
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIGradientDrawable;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIColorPicker;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;
import cc.creativecomputing.ui.widget.CCUIFileWidget;
import cc.creativecomputing.ui.widget.CCUIGradientWidget;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUISlider;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBox;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaAlign;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public class CCUIWidgetPlacementDemo extends CCGLApp {
	
	public CCUIWidget createObjectBarWidget() {
		CCUIWidget myBarWidget = new CCUIWidget();
		myBarWidget.flexDirection(CCYogaFlexDirection.ROW);
		myBarWidget.alignItems(CCYogaAlign.CENTER);
		myBarWidget.debugInfo("BAR", CCColor.YELLOW);
		myBarWidget.padding(CCYogaEdge.ALL, 5);
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		myGradientBack.gradient().top(new CCColor(CCColor.GREEN));
		myGradientBack.gradient().bottom(new CCColor(CCColor.GREEN.darker()));
		myBarWidget.style().background(myGradientBack);

		CCUIIconWidget myIconWidget = new CCUIIconWidget(CCEntypoIcon.ICON_TRIANGLE_DOWN);
		myIconWidget.padding(CCYogaEdge.ALL, 2);
		myIconWidget.style().background(CCUIWidgetStyle.OFF);
		myIconWidget.mouseReleased.add(event -> {
			myIconWidget.active(!myIconWidget.active());
			myIconWidget.text(myIconWidget.active() ? CCEntypoIcon.ICON_TRIANGLE_DOWN.text : CCEntypoIcon.ICON_TRIANGLE_RIGHT.text);
		});
		myBarWidget.addChild(myIconWidget);
		
		CCUILabelWidget myLabel = new CCUILabelWidget("TEXONE");
		myLabel.margin(CCYogaEdge.LEFT, 10);
		myBarWidget.addChild(myLabel);
		return myBarWidget;
	}

	
	public CCUIDropDownWidget createDropDown() {
		CCUIDropDownWidget myDropDown = new CCUIDropDownWidget();
		myDropDown.padding(CCYogaEdge.ALL, 4);
		myDropDown.addItem("Item 1");
		myDropDown.addItem("item 2");
		myDropDown.addItem("item 3");
		myDropDown.addSeparator();
		myDropDown.addItem("item 4");
		return myDropDown;
	}

//	
//	public CCUIWidget createGradient(){
//		CCUIGradientWidget myWidget = new CCUIGradientWidget(220,14);
//		return myWidget;
//	}
//	
//	public CCUIFileWidget createFile() {
//		CCUIFileWidget myDropDown = new CCUIFileWidget();
//		return myDropDown;
//	}
	
	public CCUIWidget createPropertyWidget() {
		CCUIWidget myPropertyWidget = new CCUIWidget();
		myPropertyWidget.flexDirection(CCYogaFlexDirection.COLUMN);
		myPropertyWidget.padding(CCYogaEdge.ALL, 10);
		myPropertyWidget.style().background(new CCUIFillDrawable(new CCColor(125)));
		
		for(int i = 0; i < 50;i++) {
			CCUIWidget myEntry = new CCUIWidget();
			myPropertyWidget.addChild(myEntry);
			myEntry.flexDirection(CCYogaFlexDirection.ROW);
			myEntry.alignItems(CCYogaAlign.CENTER);
			myEntry.margin(CCYogaEdge.BOTTOM, 10);
			CCUILabelWidget myLabel = new CCUILabelWidget("prop" + i);
			myLabel.textField().align(CCTextAlign.RIGHT);
			myLabel.minWidth(120);
			myLabel.padding(CCYogaEdge.ALL, 4);
			myLabel.margin(CCYogaEdge.RIGHT, 10);
			myEntry.addChild(myLabel);
			
			switch(i % 8) {
			case 0:
				CCUICheckBox myCheckBox = new CCUICheckBox();
				myCheckBox.padding(CCYogaEdge.ALL, 2);
				myEntry.addChild(myCheckBox);
				break;
			case 1:
				CCUITextFieldWidget myTextField = new CCUITextFieldWidget("TEXONE");
				myTextField.flex(1);
				myTextField.padding(CCYogaEdge.ALL, 4);
				myEntry.addChild(myTextField);
				break;
			case 2:
				myEntry.addChild(createDropDown());
				break;
			case 3:
				CCUIValueBox myValueBox = new CCUIValueBox(0);
				myValueBox.maxWidth(120);
				myValueBox.padding(CCYogaEdge.ALL, 4);
				myEntry.addChild(myValueBox);
				CCUISlider mySlider = new CCUISlider(14, 0, 100, 50);
				mySlider.flex(1);
				mySlider.margin(CCYogaEdge.LEFT, 10);
				myEntry.addChild(mySlider);
				break;
			case 4:
				CCUIValueBox myValueBox2 = new CCUIValueBox(0);
				myValueBox2.maxWidth(120);
				myValueBox2.padding(CCYogaEdge.ALL, 4);
				myEntry.addChild(myValueBox2);
				break;
			case 5:
				CCUIColorPicker myPicker = new CCUIColorPicker(CCColor.RED);
				myPicker.minWidth(120);
				myPicker.padding(CCYogaEdge.ALL, 4);
				myEntry.addChild(myPicker);
				break;
			case 6:
				myEntry.addChild(new CCUIGradientWidget(220,14));
				break;
			case 7:
				CCUIFileWidget myFileWidget = new CCUIFileWidget();
				myFileWidget.padding(CCYogaEdge.ALL, 4);
				myEntry.addChild(myFileWidget);
				break;
			}
		}
		
		return myPropertyWidget;
	}
//	
//	private CCUIWidget _myObjectWidget;
//	private CCUIWidget _myPropertyWidget;
//	

//	
//	
//	private CCUITreeWidget _myTreeWidget;
//	
//	
//	private void addNodes(CCUITreeNode theNode, int theDepth){
//		if(theDepth == 0)return;
//		for(int i = 0; i < 3;i++){
//			addNodes(theNode.addNode("node " + i), theDepth - 1);
//		}
//	}
//	
//	private CCUITreeWidget createTreeWidget(){
//		CCUITreeWidget myResult = new CCUITreeWidget(_myFont, "app");
//		myResult.width(400);
//		addNodes(myResult.root(),3);
//		return myResult;
//	}

	private CCTextureMapFont _myFont;
	private CCUIContext _myContext;
	
	@Override
	public void setup() {
		_myFont = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 20, 2, 2);
		
		_myContext = new CCUIContext(this, CCYogaFlexDirection.COLUMN);
		_myContext.debugInfo("", CCColor.CYAN);

//		_myTreeWidget = new CCUITreeWidget(_myFont, "app");
//		_myTreeWidget.width(400);
//		_myContext.widget().addChild(createTreeWidget());
		
		CCUIWidget myVerticalPane = new CCUIWidget();
		myVerticalPane.flexDirection(CCYogaFlexDirection.COLUMN);
		myVerticalPane.debugInfo("", CCColor.MAGENTA);
		myVerticalPane.flex(1);
		myVerticalPane.addChild(createObjectBarWidget());
		myVerticalPane.addChild(createPropertyWidget());

//		CCUIScrollWidget myScrollPane = new CCUIScrollWidget(myVerticalPane, 400, 400);
		_myContext.addChild(myVerticalPane);
		_myContext.calculateLayout();
		scrollEvents.add(pos -> {CCLog.info(pos.x,pos.y);});
		
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
//		_myContext.widget().update(theTimer);
		
	}

	@Override
	public void display(CCGraphics g) {
		g.ortho();
		g.clear();
		g.pushAttribute();
		_myContext.display(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(new CCUIWidgetPlacementDemo());
		myApplicationManager.run();
	}
}

