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
package cc.creativecomputing.controlui.timeline.view.transport;

import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIGradientDrawable;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBox;

public class CCTransportView extends CCUIHorizontalFlowPane{
	
	private static final int MAX_SLIDER_VALUE = 100;
	
	private static final float MAX_RANGE = 3600 * 5;
	
	private static final float CURVE_POW = 4;

	private CCTimelineContainer _myTimelineContainer;
	private CCTimelineController _myTimelineController;
	
	private CCUITextFieldWidget _myTimeField;
	
	private CCUIValueBox _mySpeedValue;
	private CCUIValueBox _myBPMValue;
	private CCUICheckBox _myShowBPMButton;
	
	private CCUIValueBox _myZoomValue;
//	
	private CCUIDropDownWidget _myTimelines;
//	private ComboBoxEditor _myTimelinesComboEditor;
	
	private String timeToString(double theTime) {
		long myTime = (long)(theTime * 1000);
		long myMillis = myTime % 1000;
		myTime /= 1000;
		long mySeconds = myTime % 60;
		myTime /= 60;
		long myMinutes = myTime % 60;
		myTime /= 60;
		long myHours = myTime;
		
		StringBuffer myResult = new StringBuffer();
		
		myResult.append(CCFormatUtil.nf((int)myHours, 2));
		myResult.append(":");
		
		myResult.append(CCFormatUtil.nf((int)myMinutes, 2));
		myResult.append(":");
		
		myResult.append(CCFormatUtil.nf((int)mySeconds, 2));
		myResult.append(":");
		
		myResult.append(CCFormatUtil.nf((int)myMillis,3));
		return myResult.toString();
	}
	
	public CCTransportView(CCTimelineContainer theTimelineContainer) {
		style().inset(10);
		space(10);
		stretchWidth(true);
		_myTimelineContainer = theTimelineContainer;
		
		CCEvent<Double> myTimeEvent = t -> {
			_myTimeField.text(timeToString(t), true);
		};
		
		CCEvent<CCTimeRange> myZoomEvent = t -> {
			double myRange = CCMath.pow(t.length() / MAX_RANGE , 1f / CURVE_POW);
			double myValue = CCMath.constrain(myRange * MAX_SLIDER_VALUE, 0, MAX_SLIDER_VALUE);
			_myZoomValue.value(myValue, false);
		};
		
		_myTimelineContainer.changeEvents.add(theController -> {
			_myTimelineController.zoomController().events.remove(myZoomEvent);
			_myTimelineController.transportController().timeEvents.remove(myTimeEvent);
			
			_myTimelineController = theController;
			
			_myTimelineController.zoomController().events.add(myZoomEvent);
			_myTimelineController.transportController().timeEvents.add(myTimeEvent);
//			zoomFromSlider();
		});
		_myTimelineContainer.resetEvents.add(e -> {
			_myTimelines.removeAllItems();
		});
		_myTimelineContainer.addEvents.add(theTimeline -> {
			_myTimelines.addItem(theTimeline);
			_myTimelines.selectedItem(_myTimelineContainer.defaultTimelineKey(), true);
		});
		
		_myTimelineController = _myTimelineContainer.activeTimeline();
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		myGradientBack.gradient().top(new CCColor(CCColor.GRAY));
		myGradientBack.gradient().bottom(new CCColor(CCColor.GRAY.darker()));
		style().background(myGradientBack);
		
//		addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				if(_myTimelineContainer.activeTimeline().transportController() == null)return;
//				
//				_myTimelineContainer.activeTimeline().transportController().mouseReleased(e);
//				
//			}
//			
//			@Override
//			public void mousePressed(MouseEvent e) {
//				if(_myTimelineContainer.activeTimeline().transportController() == null)return;
//				_myTimelineContainer.activeTimeline().transportController().mousePressed(e);
//			}
//		});
//		
//		addMouseMotionListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				
//				if (_myTimelineContainer.activeTimeline().transportController() == null)return;
//				
//				_myTimelineContainer.activeTimeline().transportController().mouseDragged(e);
//				updateUI();
//			}	
//		});
		
		CCUIIconWidget myFastBackButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_FAST_BACKWARD);
		myFastBackButton.textField().fontSize(40);
		myFastBackButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myFastBackButton);
		
		CCUIIconWidget myFastForwardButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_FAST_FORWARD);
		myFastForwardButton.textField().fontSize(40);
		myFastForwardButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myFastForwardButton);
		
		CCUIIconWidget myStopButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_STOP);
		myStopButton.textField().fontSize(40);
		myStopButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myStopButton);
		
		CCUIIconWidget myPlayButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_PLAY);
		myPlayButton.textField().fontSize(40);
		myPlayButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().play();
		});
		addChild(myPlayButton);
		
		CCUIIconWidget myLoopButton = new CCUIIconWidget(CCEntypoIcon.ICON_CYCLE);
		myLoopButton.textField().fontSize(40);
		myLoopButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().loop();
		});
		addChild(myLoopButton);
		
		_myTimeField = new CCUITextFieldWidget("00:00:00:0000");
		_myTimeField.style().font(CCUIContext.FONT_40);
		_myTimeField.style().background(new CCUIFillDrawable(new CCColor(0.3d)));
		_myTimeField.width(250);
		_myTimeField.textField().align(CCTextAlign.CENTER);
		addChild(_myTimeField);
		
		_myTimelineController.transportController().timeEvents.add(t -> {
			long myTime = (long)(t * 1000);
			long myMillis = myTime % 1000;
			myTime /= 1000;
			long mySeconds = myTime % 60;
			myTime /= 60;
			long myMinutes = myTime % 60;
			myTime /= 60;
			long myHours = myTime;
				
			StringBuffer myResult = new StringBuffer();
				
			myResult.append(CCFormatUtil.nf((int)myHours, 2));
			myResult.append(":");
			
			myResult.append(CCFormatUtil.nf((int)myMinutes, 2));
			myResult.append(":");
				
			myResult.append(CCFormatUtil.nf((int)mySeconds, 2));
			myResult.append(":");
				
			myResult.append(CCFormatUtil.nf((int)myMillis,3));
				
			_myTimeField.text(myResult.toString(), false);
			
		});
		
		_mySpeedValue = new CCUIValueBox(1,0.1,10d, 2);
		_mySpeedValue.style().font(CCUIContext.FONT_40);
		_mySpeedValue.width(150);
		_mySpeedValue.style().background(new CCUIFillDrawable(new CCColor(0.3d)));
		addChild(_mySpeedValue);
		_mySpeedValue.changeEvents.add(theValue -> {
			_myTimelineContainer.activeTimeline().transportController().speed(theValue);
		});
		
		CCUILabelWidget mySpeedLabel = new CCUILabelWidget("speed");
		mySpeedLabel.style().verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(mySpeedLabel);
		
		_myBPMValue = new CCUIValueBox( 120,1, 240, 2);
		_myBPMValue.style().font(CCUIContext.FONT_40);
		_myBPMValue.width(150);
		addChild(_myBPMValue);
		_myBPMValue.changeEvents.add(theValue -> {
			_myTimelineContainer.activeTimeline().transportController().bpm(theValue);
		});
		
		_myShowBPMButton = new CCUICheckBox(false);
		_myShowBPMButton.changeEvents.add(e ->{
			_myTimelineContainer.activeTimeline().transportController().useBeats(e);
		});
		_myShowBPMButton.size(36);
		addChild(_myShowBPMButton);
		
		CCUILabelWidget myBPMLabel = new CCUILabelWidget("BPM");
		myBPMLabel.style().verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myBPMLabel);
		
		_myZoomValue = new CCUIValueBox( 0, 0, MAX_SLIDER_VALUE, 2);
		_myZoomValue.style().font(CCUIContext.FONT_40);
		_myZoomValue.width(150);
		addChild(_myZoomValue);
		_myZoomValue.changeEvents.add(theValue -> {
			double myBlend = CCMath.pow(theValue / MAX_SLIDER_VALUE, CURVE_POW);
			_myTimelineController.zoomController().setRange(
				_myTimelineController.zoomController().lowerBound(), 
				_myTimelineController.zoomController().lowerBound() + myBlend * MAX_RANGE
			);
		});
		
		CCUILabelWidget myZoomLabel = new CCUILabelWidget("zoom");
		myZoomLabel.style().verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myZoomLabel);

		_myTimelines = new CCUIDropDownWidget();
		_myTimelines.style().font(CCUIContext.FONT_40);
		_myTimelines.width(250);
		
		for(String myKey:_myTimelineContainer.timelineKeys()){
			_myTimelines.addItem(myKey);
		}
		_myTimelines.selectedItem(_myTimelineContainer.defaultTimelineKey(),false);
		_myTimelines.editable(true);
      
		_myTimelines.changeEvents.add(e ->{
			_myTimelineContainer.setActiveTimeline(e);
		});

        addChild(_myTimelines);
		CCUILabelWidget myTimelineLabel = new CCUILabelWidget("timeline");
		myTimelineLabel.style().verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myTimelineLabel);
		
		_myTimelineController.zoomController().events.add(myZoomEvent);
	}

	public void speed(double theSpeed){
		if(_mySpeedValue != null)_mySpeedValue.value(theSpeed,false);
	}
	

	
}
