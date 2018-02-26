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

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.view.CCTimelineContainerView;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineView;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;

public class CCControlComponent extends CCUIVerticalFlowPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3570221683598509895L;

	private CCControlTreeComponent _myTreeComponent;
	
	private final CCUIHorizontalFlowPane _myControlsTimelinePane;
	
	private JPanel _myInfoPanel;
	
//	private CCPresetComponent _myPresetComponent;
	
	private CCTimelineContainerView _myTimelineView;
	
	public CCControlComponent(CCGLWindow theMainFrame){
		_myInfoPanel = new JPanel();
		_myInfoPanel.setLayout(new BorderLayout());
		_myInfoPanel.setBackground(Color.GRAY);
        
		JScrollPane myScrollPane = new JScrollPane(_myInfoPanel);
		myScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
//		_myScrollPane.setPreferredSize(new Dimension(800,800));
		myScrollPane.setBackground(Color.GREEN);
		myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		_myTreeComponent = new CCControlTreeComponent("app", this);
		_myTimelineView = new CCTimelineContainerView(theMainFrame);
		_myTimelineContainer = new CCTimelineContainer(_myTreeComponent.propertyMap());
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void resetTimelines() {}
			
			@Override
			public void changeTimeline(CCTimelineController theController) {
				_myTimelineContainer.activeTimeline().view().controller(theController);
//				_myControlsTimelinePane.setRightComponent(((SwingTimelineView)theController.view()).container());
			}

			@Override
			public void addTimeline(String theTimeline) {
			}
		});
		_myTimelineContainer.view(_myTimelineView);
//		_myTimelineView.setSize(1900, 500);
		
//        _myPresetComponent = new CCPresetComponent();
        
//        JSplitPane myControlsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
//        CCUIStyler.styleSplitPane(myControlsPane);
//        myControlsPane.setDividerLocation(30 * SwingGuiConstants.SCALE);
//        myControlsPane.setTopComponent(_myPresetComponent);
//        myControlsPane.setBottomComponent(myScrollPane);
        

        _myControlsTimelinePane = new CCUIHorizontalFlowPane();
        _myControlsTimelinePane.addChild(myScrollPane);
        _myControlsTimelinePane.addChild(_myTimelineContainer.activeTimeline().view());
        
        _myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
        	
        	@Override
        	public void resetTimelines() {}
			
			@Override
			public void changeTimeline(CCTimelineController theController) {
//		        _myControlsTimelinePane.setRightComponent(_myTimelineContainer.activeTimeline().view());
			}
			
			@Override
			public void addTimeline(String theTimeline) {
			}
		});
        
        CCUIHorizontalFlowPane myTreeControlsTimelinePane = new CCUIHorizontalFlowPane();
        myTreeControlsTimelinePane.addChild(_myTreeComponent);
        myTreeControlsTimelinePane.addChild(_myControlsTimelinePane);
        
        addChild(_myTimelineView.transportView());
        addChild(myTreeControlsTimelinePane);
	}

	
	
	public CCTimelineContainerView view(){
		return _myTimelineView;
	}
	
	public CCTimelineContainer timeline(){
		return _myTimelineContainer;
	}
	
	public void showContent(JPanel theControlPanel){
        if(_myInfoPanel == null) return;
        if(theControlPanel == null)return;
       
        _myInfoPanel.removeAll();
        _myInfoPanel.add(theControlPanel, BorderLayout.NORTH);
        _myInfoPanel.invalidate(); 
        _myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
        _myInfoPanel.repaint();
        
      
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
	}
	
	public void setData(Object theData, String thePresetPath){
		_myTreeComponent.setData(theData, thePresetPath);
		_myTreeComponent.rootHandle().preset(0);	
	}
	
	
	
	public CCPropertyMap propertyMap(){
		return _myTreeComponent.propertyMap();
	}

}
