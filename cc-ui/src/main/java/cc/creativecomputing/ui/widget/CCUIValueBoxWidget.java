/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.event.CCUIWidgetEvent;
import cc.creativecomputing.ui.event.CCUIWidgetEventListener;
import cc.creativecomputing.ui.event.CCUIWidgetInteractionEvent;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name = "valuebox_widget")
public class CCUIValueBoxWidget extends CCUIWidget{
	
	private class CCUIValueUpDownWidgetController implements CCUIWidgetEventListener{
		
		private double _mySign;
		
		private CCUIValueUpDownWidgetController(int theSign) {
			_mySign = theSign;
		}
		
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case PRESS:
					_myValue = Float.parseFloat(_myValueField.text().text());
					_myValue += _mySign * _myStepSize;
					updateValue();
					break;
				}
			}
		}
	}
	
	private class CCUIValueDragController implements CCUIWidgetEventListener{
		
		private CCVector2 _myStart;
		private double _myStartValue;
		private int _myStepCounter = 0;
		private boolean _myIsVertical;
		
		public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
			if(theEvent instanceof CCUIWidgetInteractionEvent) {
				CCUIWidgetInteractionEvent myEvent = (CCUIWidgetInteractionEvent)theEvent;
				switch(myEvent.type()) {
				case PRESS:
					_myStepCounter++;
					_myStart = myEvent.position().clone();
					_myStartValue = _myValue;
					break;
				case DRAGG:
				case DRAGG_OUTSIDE:
					if(_myStepCounter == 0)return;
					if(_myStepCounter == 1) {
						_myIsVertical = 
							CCMath.abs(_myStart.x - myEvent.position().x) < 
							CCMath.abs(_myStart.y - myEvent.position().y);
					}
					_myStepCounter++;
					double myValueChange = 0;
					if(!_myIsVertical)myValueChange = myEvent.position().x - _myStart.x;
					else myValueChange = myEvent.position().y - _myStart.y;
					
					_myValue = _myStartValue + (myValueChange / 5) * _myStepSize;
					updateValue();
					break;
				case RELEASE:
				case RELEASE_OUTSIDE:
					if(_myStepCounter == 0)return;
					_myStepCounter = 0;
					break;
				}
			}
		}
	}
	
	@CCXMLProperty(name = "down")
	private CCUIWidget _myDownButton;
	
	@CCXMLProperty(name = "up")
	private CCUIWidget _myUpButton;
	
	@CCXMLProperty(name = "value_field")
	private CCUITextFieldWidget _myValueField;

	@CCXMLProperty(name = "label", optional = true)
	private CCUIWidget _myLabel;
	
	@CCXMLProperty(name = "min", node = false, optional = true)
	private double _myMin = 0;
	
	@CCXMLProperty(name = "max", node = false, optional = true)
	private double _myMax = 1;
	
	@CCXMLProperty(name = "value", node = false, optional = true)
	private double _myValue = 0;
	
	@CCXMLProperty(name = "stepsize", node = false, optional = true)
	private double _myStepSize = 0.01f;
	
	private int _myDigits = 0;

	public void setup(CCUI theUI, CCUIWidget theParent) {
		addListener(new CCUIValueDragController());
		_myDownButton.addListener(new CCUIValueUpDownWidgetController(-1));
		_myUpButton.addListener(new CCUIValueUpDownWidgetController(1));
		
		addChild(_myDownButton);
		addChild(_myUpButton);
		addChild(_myValueField);
		
		if(_myLabel != null) {
			_myLabel.height(height());
			addChild(_myLabel);
		}
		
		super.setup(theUI, this);
		
		_myValue = CCMath.constrain(_myValue, _myMin, _myMax);
		_myValueField.text().text(_myValue);
		_myValueField.valueText(true);
		
		String myStepSizeString = Double.toString(_myStepSize);
		int myIndex = myStepSizeString.indexOf('.');
		if(myIndex > 0) {
			_myDigits = myStepSizeString.length() - myIndex - 1;
		}else {
			_myDigits = 0;
		}
	};
	
	private void updateValue() {
		_myValue = CCMath.round(_myValue, _myDigits);
		_myValue = CCMath.constrain(_myValue, _myMin, _myMax);
		_myValueField.text().text(_myValue);
	}
}
