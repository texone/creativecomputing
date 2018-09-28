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

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.handles.CCBooleanHandle;
import cc.creativecomputing.control.handles.CCColorHandle;
import cc.creativecomputing.control.handles.CCDoubleHandle;
import cc.creativecomputing.control.handles.CCEnumHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCFloatHandle;
import cc.creativecomputing.control.handles.CCGradientHandle;
import cc.creativecomputing.control.handles.CCIntHandle;
import cc.creativecomputing.control.handles.CCNumberHandle;
import cc.creativecomputing.control.handles.CCObjectHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCSelectionHandle;
import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.control.handles.CCStringHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCObjectPropertyPopUp;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIGradientDrawable;
import cc.creativecomputing.ui.draw.CCUIStrokeDrawable;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public class CCObjectControl extends CCUIWidget implements CCControl{
	
	private interface CCControlCreator{
		CCControl create(CCPropertyHandle<?> thePropertyHandle, CCControlComponent theInfoPanel);
	}
	
	public static Map<Class<?>, Class<?>> typeMappings = new HashMap<>();
	static{
		typeMappings.put(CCEventTriggerHandle.class, CCEventTriggerControl.class);
		typeMappings.put(CCFloatHandle.class, CCFloatControl.class);
		typeMappings.put(CCDoubleHandle.class, CCDoubleControl.class);
		typeMappings.put(CCIntHandle.class, CCIntControl.class);
		typeMappings.put(CCBooleanHandle.class, CCBooleanControl.class);
		
		typeMappings.put(CCEnumHandle.class, CCEnumControl.class);
		typeMappings.put(CCStringHandle.class, CCStringControl.class);
		typeMappings.put(CCPathHandle.class, CCPathControl.class);
		typeMappings.put(CCSelectionHandle.class, CCSelectionControl.class);
		typeMappings.put(CCColorHandle.class, CCColorControl.class);
		typeMappings.put(CCGradientHandle.class, CCGradientControl.class);
		typeMappings.put(CCEnvelopeHandle.class, CCEnvelopeControl.class);
		typeMappings.put(CCSplineHandle.class, CCSplineControl.class);

		typeMappings.put(CCObjectHandle.class, CCObjectControl.class);
	}
	
	private static CCControl createControl(CCPropertyHandle<?> theHandle, CCControlComponent theInfoPanel){
		
		Class<?> myCreatorClass = typeMappings.get(theHandle.getClass());
		
		
		if(myCreatorClass == null) {
			return null;
		}
		
		try {
			Constructor<?> myConstructor = myCreatorClass.getConstructor(theHandle.getClass(), CCControlComponent.class);
			return (CCControl)myConstructor.newInstance(theHandle, theInfoPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
//	private static CCControlCreator handleCreator(Class<?> theClass){
//		if(theClass == null)return null;
//		CCControlCreator myCreator = null;
//		Class<?> myClass = theClass;
//		do{
//			myCreator = typeMappings.get(myClass);
//			myClass = myClass.getSuperclass();
//		}while(myClass != null && myCreator == null && myClass != Object.class);
//		return myCreator;
//	}
	
	private CCControlComponent _myInfoPanel;
	public CCUIWidget _myControlPane = null;
	
	private boolean _myIsSelected = false;
	
	protected CCObjectHandle _myProperty;
	
	private CCUIWidget myBarWidget;
	
	private CCEvent<Object> _myListener;

	public CCObjectControl(CCObjectHandle thePropertyHandle, CCControlComponent theInfoPanel){
		flexDirection(CCYogaFlexDirection.COLUMN);
		
		_myProperty = thePropertyHandle;

		_myControlPane = new CCUIWidget();
		_myControlPane.flexDirection(CCYogaFlexDirection.COLUMN);
//		_myControlPane.padding(CCYogaEdge.VERTICAL, 5);
		_myControlPane.style().background(new CCUIFillDrawable(CCColorMap.getColor(_myProperty.path()).brighter()));
		_myProperty.changeEvents.add(_myListener = theValue ->{
			try{
				if(_myIsSelected){
					removeChild(_myControlPane);
				}
				createUI(false);
				if(_myIsSelected){
					addChild(_myControlPane);
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		
		myBarWidget = new CCUIWidget();
		myBarWidget.flexDirection(CCYogaFlexDirection.ROW);
		myBarWidget.alignItems(CCYogaAlign.CENTER);
		myBarWidget.debugInfo("BAR", CCColor.YELLOW);
		myBarWidget.padding(CCYogaEdge.ALL, 5);
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		CCColor myColor = CCColorMap.getColor(_myProperty.path());
		myGradientBack.gradient().top(myColor);
		myGradientBack.gradient().bottom(myColor.darker());
		myBarWidget.style().background(myGradientBack);

		CCUIIconWidget myIconWidget = new CCUIIconWidget(CCEntypoIcon.ICON_TRIANGLE_RIGHT);
		myIconWidget.padding(CCYogaEdge.ALL, 2);
		myIconWidget.style().background(CCUIWidgetStyle.OFF);
		myIconWidget.mouseReleased.add(event -> {
			switch(event.button){
			case BUTTON_LEFT:
				myIconWidget.active(!myIconWidget.active());
				myIconWidget.icon(myIconWidget.active() ? CCEntypoIcon.ICON_TRIANGLE_DOWN : CCEntypoIcon.ICON_TRIANGLE_RIGHT);
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
			CCLog.info("LABEL");
			switch(event.button){
			case BUTTON_RIGHT:
				new CCObjectPropertyPopUp(CCObjectControl.this, _myProperty, myLabel, event.x, event.y);
				break;
			default:
				break;
			}
			
		});
		myBarWidget.addChild(myLabel);
		addChild(myBarWidget);
		
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
			myIconWidget.active(true);
			myIconWidget.icon(myIconWidget.active() ? CCEntypoIcon.ICON_TRIANGLE_DOWN : CCEntypoIcon.ICON_TRIANGLE_RIGHT);
		}
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
		addChild(_myControlPane);
		CCLog.info(this,parent());
		root().calculateLayout();
		_myIsSelected = true;
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , true + "");
	}
	
	public void close() {
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
		removeChild(_myControlPane);
		root().calculateLayout();
		_myIsSelected = false;	
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , false + "");
	}
	
	public CCUIWidget controlComponent(){
		return _myControlPane;
	}
	
	private List<CCControl> _myControls = new ArrayList<>();
	
	private void createUI(boolean theHideUnchanged){
		_myControlPane.removeAllChildren();
		int myY = 0;
		int myDepth = 0;
		for(CCPropertyHandle<?> myPropertyHandle:_myProperty.children().values()){
			if(theHideUnchanged && !myPropertyHandle.isChanged())continue;
			
			CCControl myControl = createControl(myPropertyHandle, _myInfoPanel);
			if(myControl == null) {
				CCLog.info(myPropertyHandle.name());
				continue;
			}
			myControl.addToPane(_myControlPane, myY, myDepth);
			myY++;
		}
	}
	
	public void hideUnchanged(){
		createUI(true);
	}
	
	public void showUnchanged(){
		createUI(false);
	}
	
	public CCObjectHandle propertyHandle(){
		return _myProperty;
	}
	
	@Override
	public String toString() {
		return _myProperty.name();
	}

	@Override
	public void addToPane(CCUIWidget thePanel, int theY, int theDepth) {
		thePanel.addChild(this);
	}
}
