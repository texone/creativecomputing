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

import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.CCZoomable;
import cc.creativecomputing.core.events.CCDoubleEvent;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
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

public class CCTransportView extends CCUIHorizontalFlowPane implements CCZoomable{
	
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
		inset(10);
		space(10);
		_myTimelineContainer = theTimelineContainer;
		
		CCDoubleEvent myTimeEvent = t -> {
			_myTimeField.text(timeToString(t), true);
		};
		
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void changeTimeline(CCTimelineController theController) {
				_myTimelineController.zoomController().removeZoomable(CCTransportView.this);
				_myTimelineController.transportController().timeEvents.remove(myTimeEvent);
				
				_myTimelineController = theController;
				
				_myTimelineController.zoomController().addZoomable(CCTransportView.this);
				_myTimelineController.transportController().timeEvents.add(myTimeEvent);
//				zoomFromSlider();
			}
			
			@Override
			public void resetTimelines() {
//				_myTimelines.removeAllItems();
			}
			
			@Override
			public void addTimeline(String theTimeline) {
//				_myTimelines.addItem(theTimeline);
//				_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
			}
		});
		_myTimelineController = _myTimelineContainer.activeTimeline();
		
		CCUIGradientDrawable myGradientBack = new CCUIGradientDrawable();
		myGradientBack.gradient().top(new CCColor(CCColor.GRAY));
		myGradientBack.gradient().bottom(new CCColor(CCColor.GRAY.darker()));
		background(myGradientBack);
		
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
		myFastBackButton.text().fontSize(40);
		myFastBackButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myFastBackButton);
		
		CCUIIconWidget myFastForwardButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_FAST_FORWARD);
		myFastForwardButton.text().fontSize(40);
		myFastForwardButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myFastForwardButton);
		
		CCUIIconWidget myStopButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_STOP);
		myStopButton.text().fontSize(40);
		myStopButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().stop();
		});
		addChild(myStopButton);
		
		CCUIIconWidget myPlayButton = new CCUIIconWidget(CCEntypoIcon.ICON_CONTROLLER_PLAY);
		myPlayButton.text().fontSize(40);
		myPlayButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().play();
		});
		addChild(myPlayButton);
		
		CCUIIconWidget myLoopButton = new CCUIIconWidget(CCEntypoIcon.ICON_CYCLE);
		myLoopButton.text().fontSize(40);
		myLoopButton.mouseReleased.add(e -> {
			_myTimelineContainer.activeTimeline().transportController().loop();
		});
		addChild(myLoopButton);
		
		_myTimeField = new CCUITextFieldWidget(CCUIConstants.DEFAULT_FONT_2, "00:00:00:0000");
		_myTimeField.background(new CCUIFillDrawable(new CCColor(0.3d)));
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
		
		_mySpeedValue = new CCUIValueBox(CCUIConstants.DEFAULT_FONT_2, 1,0.1,10d, 2);
		_mySpeedValue.width(150);
		_mySpeedValue.background(new CCUIFillDrawable(new CCColor(0.3d)));
		addChild(_mySpeedValue);
		_mySpeedValue.changeEvents.add(theValue -> {
			_myTimelineContainer.activeTimeline().transportController().speed(theValue);
		});
		
		CCUILabelWidget mySpeedLabel = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT,"speed");
		mySpeedLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(mySpeedLabel);
		
		_myBPMValue = new CCUIValueBox(CCUIConstants.DEFAULT_FONT_2, 120,1, 240, 2);
		_myBPMValue.width(150);
		_myBPMValue.background(new CCUIFillDrawable(new CCColor(0.3d)));
		addChild(_myBPMValue);
		_myBPMValue.changeEvents.add(theValue -> {
			_myTimelineContainer.activeTimeline().transportController().bpm(theValue);
		});
		
		_myShowBPMButton = new CCUICheckBox(false);
		_myShowBPMButton.changeEvents.add(e ->{
			_myTimelineContainer.activeTimeline().transportController().useBeats(e);
		});
		_myShowBPMButton.background(new CCUIFillDrawable(new CCColor(0.3d)));
		_myShowBPMButton.size(36);
		addChild(_myShowBPMButton);
		
		CCUILabelWidget myBPMLabel = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT,"BPM");
		myBPMLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myBPMLabel);
		
		_myZoomValue = new CCUIValueBox(CCUIConstants.DEFAULT_FONT_2, 0, 0, MAX_SLIDER_VALUE, 2);
		_myZoomValue.width(150);
		_myZoomValue.background(new CCUIFillDrawable(new CCColor(0.3d)));
		addChild(_myZoomValue);
		_myZoomValue.changeEvents.add(theValue -> {
			double myBlend = CCMath.pow(theValue / MAX_SLIDER_VALUE, CURVE_POW);
			_myTimelineController.zoomController().setRange(
				_myTimelineController.zoomController().lowerBound(), 
				_myTimelineController.zoomController().lowerBound() + myBlend * MAX_RANGE
			);
		});
		
		CCUILabelWidget myZoomLabel = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT,"zoom");
		myZoomLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myZoomLabel);

		_myTimelines = new CCUIDropDownWidget(CCUIConstants.DEFAULT_FONT_2);
		_myTimelines.inset(4);
		_myTimelines.verticalAlignment(CCUIVerticalAlignment.CENTER);
		CCUIFillDrawable myBackground = new CCUIFillDrawable(new CCColor(0.3d));
		_myTimelines.background(myBackground);
		_myTimelines.width(250);
		
		_myTimelines.menue().background(myBackground);
		_myTimelines.itemSelectBackground(new CCUIFillDrawable(new CCColor(0.5d)));
		_myTimelines.itemBackground(new CCUIFillDrawable(new CCColor(0.3d)));
		
		for(String myKey:_myTimelineContainer.timelineKeys()){
			_myTimelines.addItem(myKey);
		}
		_myTimelines.selectedItem(_myTimelineContainer.defaultTimelineKey(),false);
		_myTimelines.editable(true);
      
		_myTimelines.changeEvents.add(e ->{
			_myTimelineContainer.setActiveTimeline(e);
		});

        addChild(_myTimelines);
		CCUILabelWidget myTimelineLabel = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT,"timeline");
		myTimelineLabel.verticalAlignment(CCUIVerticalAlignment.CENTER);
		addChild(myTimelineLabel);
		
		_myTimelineController.zoomController().addZoomable(this);
	}

	public void speed(double theSpeed){
		if(_mySpeedValue != null)_mySpeedValue.value(theSpeed,false);
	}
	

	@Override
	public void setRange(double theLowerBound, double theUpperBound) {
		double myRange = CCMath.pow((theUpperBound - theLowerBound) / MAX_RANGE , 1f / CURVE_POW);
		double myValue = CCMath.constrain(myRange * MAX_SLIDER_VALUE, 0, MAX_SLIDER_VALUE);
		_myZoomValue.value(myValue, false);
	}
}
