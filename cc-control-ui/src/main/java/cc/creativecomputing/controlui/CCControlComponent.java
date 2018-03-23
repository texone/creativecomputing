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
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.widget.CCUITreeWidget;
import cc.creativecomputing.ui.widget.CCUITreeWidget.CCUITreeNode;

public class CCControlComponent extends CCUIHorizontalFlowPane{

	private CCUITreeWidget _myTreeWidget;

	private CCUIGridPane _myPropertyPane;
	
	public CCControlComponent(CCGLWindow theMainFrame){
	
		_myTreeWidget = new CCUITreeWidget(CCUIConstants.DEFAULT_FONT, "app");
		_myTreeWidget.width(300);
		_myTreeWidget.background(new CCUIFillDrawable(new CCColor(0.5d)));
		addChild(_myTreeWidget);

		_myPropertyPane = new CCUIGridPane(400, 400);
		_myPropertyPane.background(new CCUIFillDrawable(CCColor.RED));
		_myPropertyPane.inset(0);
		_myPropertyPane.space(10);
		_myPropertyPane.columnWidths(10,10,10);
		_myPropertyPane.rowHeight(25);
		addChild(_myPropertyPane);
		
		
//		
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
        if(_myPropertyPane == null) return;
       
        _myPropertyPane.removeAll();
        int y = 0;
		CCObjectControl myObjectControl = new CCObjectControl(theHandle, this, 0);
		myObjectControl.addToPane(_myPropertyPane, y, 0);
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
