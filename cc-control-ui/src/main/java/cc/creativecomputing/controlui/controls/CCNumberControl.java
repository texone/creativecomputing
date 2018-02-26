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

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCNumberBox;
import cc.creativecomputing.math.CCMath;

public class CCNumberControl extends CCValueControl<Number, CCNumberPropertyHandle<Number>>{
	
	private static final int MAX_SLIDER_VALUE = 1000;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	private CCNumberBox _myValueField;
	private JSlider _mySlider;
	
	private boolean _myTriggerEvent = true;

	public CCNumberControl(CCNumberPropertyHandle<Number> theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
        //Create the label.
		addListener(theValue -> {
			_myTriggerEvent = false;
			_myValue = theValue.doubleValue();
			updateSlider(_myValue);
			_myValueField.setText(theHandle.valueString());
			_myTriggerEvent = true;
		});
 
        //Create the slider.
        _myMin = _myHandle.min().doubleValue();
        _myMax = _myHandle.max().doubleValue();
        _myValue = _myHandle.value().doubleValue();
        if(_myHandle.isNumberBox()){
        		_mySlider = null;
        }else{
	        _mySlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SLIDER_VALUE, 0);
	        _mySlider.addChangeListener(theE -> {
	        		if(!_myTriggerEvent)return;
	        		value((float)(_mySlider.getValue() / (float)MAX_SLIDER_VALUE * (_myMax - _myMin) + _myMin), true);
			});
	 
	        //Turn on labels at major tick marks.
	 
	        _mySlider.setMajorTickSpacing(MAX_SLIDER_VALUE / 10);
	        _mySlider.setMinorTickSpacing(MAX_SLIDER_VALUE / 20);
	        
	        _mySlider.setPaintTicks(false);
	        _mySlider.setPaintLabels(false);
	        _mySlider.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	        _mySlider.putClientProperty( "JComponent.sizeVariant", "mini" );
	        _mySlider.setPreferredSize(new Dimension(100,14));
        }
        
        _myValueField = new CCNumberBox(_myValue, _myMin, _myMax, theHandle.digits());
        _myValueField.changeEvents().add(theValue -> {
        		value(theValue, true);
        });
        CCUIStyler.styleTextField(_myValueField, 100);
        
        _myValueField.setHorizontalAlignment(JTextField.LEFT);
        value(_myHandle.value().doubleValue(), true);
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0, theY, GridBagConstraints.LINE_END,5, 5, 1, 5));
		if(_mySlider != null){
			thePanel.add(_mySlider, constraints(1, theY, GridBagConstraints.PAGE_END,5, 0, 1, 5));
			_myValueField.setPreferredSize(new Dimension(68, 13));
			thePanel.add(_myValueField, constraints(2, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
		}else{
			thePanel.add(_myValueField, constraints(1, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
		}
	}
	
	public Number value(){
		return _myValue;
	}
	
	public void value(double theValue, boolean theOverWrite){
		_myValue = theValue;
		_myHandle.value(_myValue, theOverWrite);
	}
	
	private void updateSlider(double theValue){
		if(_mySlider == null)return;
		double myValue = CCMath.constrain((theValue - _myMin) / (_myMax - _myMin) * MAX_SLIDER_VALUE, 0, MAX_SLIDER_VALUE);
		_mySlider.setValue((int)myValue);
	}

}
