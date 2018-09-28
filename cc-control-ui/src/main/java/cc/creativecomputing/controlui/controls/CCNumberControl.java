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

import cc.creativecomputing.control.handles.CCNumberHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.ui.widget.CCUISlider;
import cc.creativecomputing.ui.widget.CCUIValueBox;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;

public abstract class CCNumberControl<Type extends Number> extends CCValueControl<Type, CCNumberHandle<Type>>{
	
	private double _myMin;
	private double _myMax;
	protected double _myValue;
	
	private CCUIValueBox _myValueField;
	private CCUISlider _mySlider;

	public CCNumberControl(CCNumberHandle<Type> theHandle, CCControlComponent theControlComponent){
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
	        _mySlider = new CCUISlider(14,_myMin, _myMax,_myValue);
	        _mySlider.flex(1);
	        _mySlider.margin(CCYogaEdge.LEFT, 10);
	        _mySlider.changeEvents.add(theE -> {
	        	value(theE , true);
			});
        }
        
        _myValueField = new CCUIValueBox(_myValue, _myMin, _myMax, theHandle.digits());
        _myValueField.maxWidth(120);
        _myValueField.flex(1);
        _myValueField.padding(CCYogaEdge.ALL, 4);
        _myValueField.changeEvents.add(theValue -> {
        	value(theValue, true);
        });
        value(_myHandle.value().doubleValue(), true);
	}

	@Override
	public void addToHorizontalPane(CCUIWidget thePane) {
		thePane.addChild(_myValueField);
		if(_mySlider != null)thePane.addChild(_mySlider);
	}
	
	public abstract Type value();
	
	public void value(double theValue, boolean theOverWrite){
		_myValue = theValue;
		_myHandle.value(_myValue, theOverWrite);
	}

}
