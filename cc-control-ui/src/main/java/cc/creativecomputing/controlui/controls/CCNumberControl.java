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

import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIRoundedFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUISlider;
import cc.creativecomputing.ui.widget.CCUIValueBox;

public class CCNumberControl extends CCValueControl<Number, CCNumberPropertyHandle<Number>>{
	
	private static final int MAX_SLIDER_VALUE = 1000;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	private CCUIValueBox _myValueField;
	private CCUISlider _mySlider;

	public CCNumberControl(CCNumberPropertyHandle<Number> theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
        //Create the label.
		addListener(theValue -> {
			_myValue = theValue.doubleValue();
			_mySlider.value(_myValue);
			_myValueField.value(theHandle.value().doubleValue(),false);
		});
 
        //Create the slider.
        _myMin = _myHandle.min().doubleValue();
        _myMax = _myHandle.max().doubleValue();
        _myValue = _myHandle.value().doubleValue();
        if(_myHandle.isNumberBox()){
        		_mySlider = null;
        }else{
	        _mySlider = new CCUISlider(100,14,_myMin, _myMax,_myValue);
	        _mySlider.background(new CCUIRoundedFillDrawable(new CCColor(0.3d), 7));
	        _mySlider.foreground(new CCUIFillDrawable(new CCColor(0.7d)));
	        _mySlider.verticalAlignment(CCUIVerticalAlignment.CENTER);
	        _mySlider.changeEvents.add(theE -> {
	        		value(theE / MAX_SLIDER_VALUE * (_myMax - _myMin) + _myMin, true);
			});
        }
        
        _myValueField = new CCUIValueBox(CCUIConstants.DEFAULT_FONT, _myValue, _myMin, _myMax, theHandle.digits());
        _myValueField.background(new CCUIFillDrawable(new CCColor(0.3d)));
        _myValueField.width(100);
        _myValueField.inset(4);
        _myValueField.changeEvents.add(theValue -> {
        		value(theValue, true);
        });
        value(_myHandle.value().doubleValue(), true);
	}
	
	@Override
	public void addToPane(CCUIGridPane thePane, int theY, int theDepth) {
		thePane.addChild(_myLabel, 0, theY);
		thePane.addChild(_myValueField, 1, theY);
		
		if(_mySlider == null)return;
		thePane.addChild(_mySlider, 2, theY);
	}
	
	public Number value(){
		return _myValue;
	}
	
	public void value(double theValue, boolean theOverWrite){
		_myValue = theValue;
		_myHandle.value(_myValue, theOverWrite);
	}

}
