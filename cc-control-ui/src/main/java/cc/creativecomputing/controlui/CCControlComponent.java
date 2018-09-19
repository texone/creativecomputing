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
package cc.creativecomputing.controlui;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.widget.CCUIScrollWidget;
import cc.creativecomputing.ui.widget.CCUITreeWidget;
import cc.creativecomputing.ui.widget.CCUITreeWidget.CCUITreeNode;
import cc.creativecomputing.yoga.CCYogaNode;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCControlComponent extends CCUIWidget{

	private CCUITreeWidget _myTreeWidget;

	private CCUIWidget _myPropertyPane;
	
	public CCControlComponent(CCGLWindow theMainFrame){
		flexDirection(CCYogaFlexDirection.ROW);
		flex(1);
		mouseMoved.add(theE ->{
//			CCLog.info("WHOA");
		});
		style().background(new CCUIFillDrawable(CCColor.MAGENTA));
	
		_myTreeWidget = new CCUITreeWidget("app");
		_myTreeWidget.width(300);
		_myTreeWidget.style().background(new CCUIFillDrawable(CCColor.BLUE));
		addChild(_myTreeWidget);

		_myPropertyPane = new CCUIWidget();
		_myPropertyPane.flexDirection(CCYogaFlexDirection.COLUMN);
		_myPropertyPane.widthPercent(100);
//		_myPropertyPane.flex(1);
		_myPropertyPane.style().background(new CCUIFillDrawable(CCColor.RED));

		CCUIScrollWidget myScrollWidget = new CCUIScrollWidget(_myPropertyPane, CCUIScrollWidget.DEFAULT_SLIDER_WIDTH, false, true);
		myScrollWidget.width(600);
		addChild(myScrollWidget);
		
		
		
//		_myTimelineView = new CCTimelineContainerView(theMainFrame);
//		_myTimelineContainer = new CCTimelineContainer(_myTreeComponent.propertyMap());
//		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
//			
//			@Override
//			public void resetTimelines() {}
//			
//			@Override
//			public void changeTimeline(CCTimelineController theController) {
//				_myTimelineContainer.activeTimeline().view().controller(theController);
////				_myControlsTimelinePane.setRightComponent(((SwingTimelineView)theController.view()).container());
//			}
//
//			@Override
//			public void addTimeline(String theTimeline) {
//			}
//		});
//		_myTimelineContainer.view(_myTimelineView);
////		_myTimelineView.setSize(1900, 500);
//		
////        _myPresetComponent = new CCPresetComponent();
//        
////        JSplitPane myControlsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
////        CCUIStyler.styleSplitPane(myControlsPane);
////        myControlsPane.setDividerLocation(30 * SwingGuiConstants.SCALE);
////        myControlsPane.setTopComponent(_myPresetComponent);
////        myControlsPane.setBottomComponent(myScrollPane);
//        
//
//        _myControlsTimelinePane = new CCUIHorizontalFlowPane();
//        _myControlsTimelinePane.addChild(myScrollPane);
//        _myControlsTimelinePane.addChild(_myTimelineContainer.activeTimeline().view());
//        
//        _myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
//        	
//        	@Override
//        	public void resetTimelines() {}
//			
//			@Override
//			public void changeTimeline(CCTimelineController theController) {
////		        _myControlsTimelinePane.setRightComponent(_myTimelineContainer.activeTimeline().view());
//			}
//			
//			@Override
//			public void addTimeline(String theTimeline) {
//			}
//		});
//        
//        CCUIHorizontalFlowPane myTreeControlsTimelinePane = new CCUIHorizontalFlowPane();
//        myTreeControlsTimelinePane.addChild(_myTreeWidget);
//        myTreeControlsTimelinePane.addChild(_myControlsTimelinePane);
//        
//        addChild(_myTimelineView.transportView());
//        addChild(myTreeControlsTimelinePane);
	}

	public void showContent(CCObjectPropertyHandle theHandle){
		CCLog.info("show content");
        if(_myPropertyPane == null) return;
        _myPropertyPane.removeAllChildren();
        int y = 0;
		CCObjectControl myObjectControl = new CCObjectControl(theHandle, this, 0);
		myObjectControl.addToPane(_myPropertyPane, y, 0);
		CCLog.info(myObjectControl,myObjectControl.parent().get());
	}

	private void createTree(CCUITreeNode theNode, CCObjectPropertyHandle theHandle){
		for(CCPropertyHandle<?> myPropertyHandle:theHandle.children().values()){
			if(myPropertyHandle instanceof CCObjectPropertyHandle){
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCUITreeNode myNode = theNode.addNode(myObjectHandle.name(), e -> {
					showContent(myObjectHandle);
				});
				createTree(myNode, myObjectHandle);
			}
		}
	}
	
	public void setData(CCObjectPropertyHandle theRootHandle){
		createTree(_myTreeWidget.root(), theRootHandle);
		showContent(theRootHandle);	
	}

}
