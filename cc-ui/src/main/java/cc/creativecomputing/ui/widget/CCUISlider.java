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
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;

public class CCUISlider extends CCUIWidget{
	
	private double _mySliderPos = 0;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	public CCEventManager<Double> changeEvents = new CCEventManager<>();

	public CCUISlider(double theWidth, double theHeight, double theMin, double theMax, double theValue){
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myMin = theMin;
		_myMax = theMax;
		value(theValue);
		
		_mySliderPos = _myHeight / 2;
				
		mousePressed.add(event -> {
			sliderPos(event.x);
		});
		
		mouseMoved.add(pos -> {});
		
		mouseDragged.add(pos -> {sliderPos(pos.x);});
	}
	
	private void sliderPos(double thePosition) {
		_mySliderPos = CCMath.clamp(thePosition, _myHeight / 2, _myWidth - _myHeight / 2);
		_myValue = CCMath.map(_mySliderPos,  _myHeight / 2, _myWidth - _myHeight / 2, _myMin, _myMax);
		
		changeEvents.event(_myValue);
	}
	
	public void value(double theValue) {
		_myValue = CCMath.clamp(theValue, _myMin, _myMax);
		_mySliderPos = CCMath.map(theValue, _myMin, _myMax, _myHeight / 2, _myWidth - _myHeight / 2);
		changeEvents.event(_myValue);
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		g.color(255);
		g.ellipse(_mySliderPos, -_myHeight / 2, _myHeight / 2 - 2);
	}
}
