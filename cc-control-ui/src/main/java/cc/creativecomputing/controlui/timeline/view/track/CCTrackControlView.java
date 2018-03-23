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
package cc.creativecomputing.controlui.timeline.view.track;

import java.text.DecimalFormat;

import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCBooleanTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBox;


public class CCTrackControlView extends CCUIHorizontalFlowPane{
	
	static final CCVector2 SMALL_BUTTON_SIZE = new CCVector2(20,15);
	static final int RESIZE_HANDLE_HEIGHT = 5;
	static final CCVector2 ADDRESS_FIELD_SIZE = new CCVector2(100,20);


    static final public DecimalFormat VALUE_FORMAT;
    static {
    		VALUE_FORMAT = new DecimalFormat();
    		VALUE_FORMAT.applyPattern("#0.000");
    }
	
	private class CCSingleTrackControlPopup extends CCUIMenu{

		public CCSingleTrackControlPopup() {
			super(CCUIConstants.DEFAULT_FONT);
			
			addItem("Track Edit Funtions");
			addSeparator();
			addItem("remove track", e ->{_myTimelineController.removeTrack(_myTrackController.property().path());});
			addItem("reset zoom");
		}
	
	}
	
	private CCTimelineController _myTimelineController;
	private CCTrackController _myTrackController;
	private CCUICheckBox _myMuteButton;
	private CCUIValueBox _myMinField;
	private CCUIValueBox _myMaxField;
	private CCUITextFieldWidget _myValueField;
	private CCUILabelWidget _myAddressField;
	
	private CCSingleTrackControlPopup _myPopUp = new CCSingleTrackControlPopup();
	
	public CCTrackControlView(
		CCTimelineController theTimelineController,
		CCTrackController theTrackController
	) {
		width(150);
		height(50);
		_myTimelineController = theTimelineController;
		_myTrackController = theTrackController;
		
		
		_myMuteButton = new CCUICheckBox(CCEntypoIcon.ICON_CIRCLE, CCEntypoIcon.ICON_BLOCK, true);
		_myMuteButton.changeEvents.add(e -> {
			_myTrackController.mute(e);
		});
		
		addChild(_myMuteButton);
		
		if(
			theTrackController instanceof CCCurveTrackController && 
			!(theTrackController instanceof CCBooleanTrackController)
		) {
			_myMinField = new CCUIValueBox(CCUIConstants.DEFAULT_FONT, _myTrackController.track().min(), -Float.MAX_VALUE, Float.MAX_VALUE, 2);
			_myMinField.changeEvents.add(theValue -> {
				_myTrackController.min(theValue);
			});
			addChild(_myMinField);
			
			_myMaxField = new CCUIValueBox(CCUIConstants.DEFAULT_FONT, _myTrackController.track().max(), -Float.MAX_VALUE, Float.MAX_VALUE, 2);
			_myMaxField.changeEvents.add(theValue -> {
				_myTrackController.max(theValue);
			});
			addChild(_myMaxField);
			
			_myValueField = new CCUITextFieldWidget(CCUIConstants.DEFAULT_FONT, "");
			_myValueField.text(_myTrackController.property().valueString(), false);
		}
		
		_myAddressField = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT, "");
		addChild(_myAddressField);
		
//		setBorder(BorderFactory.createLineBorder(Color.gray));
		
//		addMouseListener(new MouseAdapter()  {
//			
//			@Override
//			public void mousePressed(MouseEvent theE) {
//				if (theE.getButton() == MouseEvent.BUTTON3) _myPopUp.show(SwingTrackControlView.this, theE.getX(), theE.getY());
//			}
//		});
		
	}
	
	public void color(CCColor theColor) {
		background(new CCUIFillDrawable(theColor));
	}
	
	public void mute(final boolean theMute) {
		if(_myMuteButton != null)_myMuteButton.isSelected(theMute, false);
	}
	
	public void min(final double theMin){
		if(_myMinField != null)_myMinField.text(theMin + "", false);
	}
	
	public void max(final double theMax){
		if(_myMaxField != null)_myMaxField.text(theMax + "", false);
	}

	public void address(final String theAddress) {
		_myAddressField.text().text(theAddress);
	}
	
	public void value(final String theValue) {
		if(_myValueField != null)_myValueField.text(theValue, false);
	}
	
}
