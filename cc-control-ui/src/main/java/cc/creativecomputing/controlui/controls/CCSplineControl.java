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

import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.ui.widget.CCUIIconWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;

public class CCSplineControl extends CCValueControl<CCSpline, CCSplineHandle>{

	private CCUIIconWidget _myButton;
	
	private CCSplineEditor _myCurveFrame;

	public CCSplineControl(CCSplineHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		addListener(theValue -> {
			_myHandle.value(theValue, false);
		});
		
		_myCurveFrame = new CCSplineEditor(theHandle.name());
		_myCurveFrame.width = 300;
		_myCurveFrame.height = 300;
		
		_myButton = new CCUIIconWidget(CCEntypoIcon.ICON_EDIT);
		_myButton.padding(CCYogaEdge.ALL, 2);
		_myButton.mouseReleased.add(event -> {
			_myCurveFrame.spline(value());				
        	CCControlApp.appManager.add(_myCurveFrame);
        	_myCurveFrame.show();
		});
	}

	@Override
	public void addToHorizontalPane(CCUIWidget thePane) {
		thePane.addChild(_myButton);
	}

	@Override
	public CCSpline value() {
		return _myHandle.value();
	}
}
