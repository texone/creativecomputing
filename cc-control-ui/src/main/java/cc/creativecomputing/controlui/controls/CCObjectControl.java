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
package cc.creativecomputing.controlui.controls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCShaderSource;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCControlMatrixHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCShaderSourceHandle;
import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.controlui.controls.code.CCShaderCompileControl;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIGradientDrawable;
import cc.creativecomputing.ui.draw.CCUIStrokeDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;

public class CCObjectControl extends CCUIGridPane implements CCControl{
	
	private CCControlComponent _myInfoPanel;
	public CCUIGridPane _myControlPane = null;
	
	private boolean _myIsSelected = false;
	
	private int _myDepth;
	
	protected CCObjectPropertyHandle _myProperty;
	
	private CCUIHorizontalFlowPane myBarWidget;
	
	private CCEvent<Object> _myListener;

	public CCObjectControl(CCObjectPropertyHandle thePropertyHandle, CCControlComponent theInfoPanel, int theDepth){
		stretchWidth(true);
		columnWidths(10,10,10);
		width(200);
		rowHeight(25);
		_myProperty = thePropertyHandle;

		_myControlPane = new CCUIGridPane();
		_myControlPane.rowHeight(25);
		_myControlPane.style().topInset(5);
		_myControlPane.style().bottomInset(15);
		_myControlPane.style().rightInset(5);
		_myControlPane.verticalSpace(10);
		_myControlPane.horizontalSpace(20);
		_myControlPane.columnWidths(10,10,10);
		_myControlPane.stretchWidth(true);
		_myControlPane.style().background(new CCUIFillDrawable(CCColorMap.getColor(_myProperty.path()).brighter()));
		_myProperty.changeEvents.add(_myListener = theValue ->{
			try{
				if(_myIsSelected){
					removeChild(_myControlPane);
				}
				createUI(false);
				if(_myIsSelected){
					CCUITableEntry myConstraints = new CCUITableEntry();
					myConstraints.column = 0;
					myConstraints.row = 1;
					myConstraints.columnSpan = 3;
					addChild(_myControlPane, myConstraints);
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		
		myBarWidget = new CCUIHorizontalFlowPane(0, 0);
		myBarWidget.translation().set(0,0);
		myBarWidget.style().inset(5);
		myBarWidget.space(5);
		myBarWidget.stretchWidth(true);
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		CCColor myColor = CCColorMap.getColor(_myProperty.path());
		myGradientBack.gradient().top(myColor);
		myGradientBack.gradient().bottom(myColor.darker());
		myBarWidget.style().background(myGradientBack);

		CCUIIconWidget myIconWidget = new CCUIIconWidget(CCEntypoIcon.ICON_TRIANGLE_DOWN);
		myIconWidget.style().background(null);
		myIconWidget.mouseReleased.add(event -> {
			switch(event.button){
			case BUTTON_LEFT:
				myIconWidget.active(!myIconWidget.active());
				myIconWidget.textField().text(myIconWidget.active() ? CCEntypoIcon.ICON_TRIANGLE_DOWN.text : CCEntypoIcon.ICON_TRIANGLE_RIGHT.text);
				if(myIconWidget.active()){
					open();
				}else{
					close();
				}
				break;
			default:
				break;
			}
			
		});
		myBarWidget.addChild(myIconWidget);
		
		CCUILabelWidget myLabel = new CCUILabelWidget(_myProperty.name());
		myLabel.mousePressed.add(event -> {
			switch(event.button){
			case BUTTON_RIGHT:
				new CCPropertyPopUp(CCObjectControl.this, _myProperty, myLabel, event.x, event.y);
				break;
			default:
				break;
			}
			
		});
		myBarWidget.addChild(myLabel);
		CCUITableEntry myTableEntry = new CCUITableEntry();
		myTableEntry.column = 0;
		myTableEntry.columnSpan = 3;
		addChild(myBarWidget);
		
		_myDepth = theDepth;
		
		_myInfoPanel = theInfoPanel;
	
		thePropertyHandle.addSelectionListener(isSelected -> {
			if(isSelected){
				style().border(new CCUIStrokeDrawable(CCColor.RED, 1, 0));
			}else{
				style().border(null);
			}
		});
		
		if(CCControlApp.preferences.getBoolean(_myProperty.path().toString() + "/open" , false)){
			open();
		}
	}
	
	private CCUITableEntry entryInfo(int theRow){
		CCUITableEntry myConstraints = new CCUITableEntry();
		myConstraints.column = 0;
		myConstraints.row = theRow;
		myConstraints.columnSpan = 3;
		return myConstraints;
	}
	
	@Override
	public void dispose() {
		_myProperty.changeEvents.remove(_myListener);
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
	}
	
	public void open() {
		createUI(false);
		addChild(_myControlPane, entryInfo(1));
		_myIsSelected = true;
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , true + "");
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
	}
	
	public void close() {
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
		removeChild(_myControlPane);
		_myIsSelected = false;	
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , false + "");
	}
	
	public CCUIGridPane controlComponent(){
		return _myControlPane;
	}
	
	private List<CCControl> _myControls = new ArrayList<>();
	
	private interface CCControlCreator{
		CCControl create(CCPropertyHandle<?> thePropertyHandle, CCControlComponent theInfoPanel);
	}
	
	private static Map<Class<?>, CCControlCreator> creatorMap = new HashMap<>();
	static{
		creatorMap.put(CCTriggerProgress.class, (myHandle, myInfoPanel) -> {return new CCEventTriggerControl((CCEventTriggerHandle)myHandle, myInfoPanel);});
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		CCControlCreator myNumberCreator = (myHandle, myInfoPanel) -> {return new CCNumberControl((CCNumberPropertyHandle)myHandle, myInfoPanel);};
		creatorMap.put(Float.class, myNumberCreator);
		creatorMap.put(Float.TYPE, myNumberCreator);
		creatorMap.put(Double.class, myNumberCreator);
		creatorMap.put(Double.TYPE, myNumberCreator);
		creatorMap.put(Integer.class, myNumberCreator);
		creatorMap.put(Integer.TYPE, myNumberCreator);
		
		CCControlCreator myBooleanCreator = (myHandle, myInfoPanel) -> {return new CCBooleanControl((CCBooleanPropertyHandle)myHandle, myInfoPanel);};
		creatorMap.put(Boolean.class, myBooleanCreator);
		creatorMap.put(Boolean.TYPE, myBooleanCreator);

		creatorMap.put(CCSelection.class, (myHandle, myInfoPanel) -> {return new CCSelectionControl((CCSelectionPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCColor.class, (myHandle, myInfoPanel) -> {return new CCColorControl((CCColorPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCGradient.class, (myHandle, myInfoPanel) -> {return new CCGradientControl((CCGradientPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(String.class, (myHandle, myInfoPanel) -> {return new CCStringControl((CCStringPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCControlMatrix.class, (myHandle, myInfoPanel) -> {return new CCControlMatrixControl((CCControlMatrixHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCEnvelope.class, (myHandle, myInfoPanel) -> {return new CCEnvelopeControl((CCEnvelopeHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCSpline.class, (myHandle, myInfoPanel) -> {return new CCSplineControl((CCSplineHandle)myHandle, myInfoPanel);});
		creatorMap.put(Path.class, (myHandle, myInfoPanel) -> {return new CCPathControl((CCPathHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCShaderSource.class, (myHandle, myInfoPanel) -> {return new CCShaderCompileControl((CCShaderSourceHandle)myHandle, myInfoPanel);});
	}
	
	private static CCControlCreator handleCreator(Class<?> theClass){
		if(theClass == null)return null;
		CCControlCreator myCreator = null;
		Class<?> myClass = theClass;
		do{
			myCreator = creatorMap.get(myClass);
			myClass = myClass.getSuperclass();
		}while(myClass != null && myCreator == null && myClass != Object.class);
		return myCreator;
	}
	
	private void createUI(boolean theHideUnchanged){
		_myControlPane.removeAll();
		int myY = 0;
		
		for(CCPropertyHandle<?> myPropertyHandle:_myProperty.children().values()){
			if(theHideUnchanged && !myPropertyHandle.isChanged())continue;
			Class<?> myClass = myPropertyHandle.type();
			
			CCControl myControl;
			
			CCControlCreator myCreator = handleCreator(myClass);
			if(myCreator != null){
				myControl = myCreator.create(myPropertyHandle, _myInfoPanel);
			}else if(myClass == null){
				myControl = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass.isEnum()){
				myControl = new CCEnumControl((CCEnumPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCObjectControl myObjectControl = new CCObjectControl(myObjectHandle, _myInfoPanel, _myDepth + 1);
				myControl = myObjectControl;
			}

			myControl.addToPane(_myControlPane, myY, _myDepth + 1);
			_myControlPane.updateLayout();
			myY++;
		}
	}
	
	public void hideUnchanged(){
		createUI(true);
	}
	
	public void showUnchanged(){
		createUI(false);
	}
	
	public CCObjectPropertyHandle propertyHandle(){
		return _myProperty;
	}
	
	@Override
	public String toString() {
		return _myProperty.name();
	}

	@Override
	public void addToPane(CCUIGridPane thePanel, int theY, int theDepth) {
		thePanel.addChild(this, entryInfo(theY));
	}
}
