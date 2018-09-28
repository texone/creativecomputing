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
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shape.CCRoundedRectangle;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIDrawable;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.yoga.CCYogaNode;

public class CCUISlider extends CCUIWidget{
	
	public static class CCUISliderDrawable implements CCUIDrawable{
		
		private CCRoundedRectangle _myRoundedRectangle;
		
		@CCProperty(name = "color")
		private CCColor _myColor = new CCColor(1f);
		
		@CCProperty(name = "radius")
		private  double _myCornerRadius = 5;

		/**
		 * @param theID
		 */
		public CCUISliderDrawable(CCColor theColor, double theRadius) {
			this();
			_myColor = theColor;
			_myCornerRadius = theRadius;
		}
		
		public CCUISliderDrawable(){
			_myRoundedRectangle = new CCRoundedRectangle();
		}

		/* (non-Javadoc)
		 * @see cc.creativecomputing.newui.decorator.CCUIDecorator#draw(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.newui.widget.CCUIWidget)
		 */
		@Override
		public void draw(CCGraphics g, CCYogaNode theWidget) {
			_myRoundedRectangle.color().set(_myColor);
			_myRoundedRectangle.gradientColor().set(_myColor);
			_myRoundedRectangle.radius(_myCornerRadius);
//			_myRoundedRectangle.position(theWidget.style().leftInset(), -theWidget.height());
			_myRoundedRectangle.size(theWidget.width(), theWidget.height());
			_myRoundedRectangle.draw(g);
		}

	}
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
//		myResult.leftInset(15);
//		myResult.rightInset(15);
		myResult.background(new CCUISliderDrawable(new CCColor(0.2d), 7));
		myResult.foreground(new CCUIFillDrawable(new CCColor(0.7d)));
//		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myResult;
	}
	
	private double _mySliderPos = 0;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	public CCEventManager<Double> changeEvents = new CCEventManager<>();
	
	private boolean _myIsHorizontal;

	public CCUISlider(CCUIWidgetStyle theStyle, double theSize, double theMin, double theMax, double theValue, boolean theIsHorizontal){
		super(theStyle);
//		_myWidth = theWidth;
//		_myHeight = theHeight;
//		
//		_myMinWidth = theWidth;
//		_myMinHeight = theHeight;
		
		if(theIsHorizontal) {
			maxHeight(theSize);
			minHeight(theSize);
		}else {
			maxWidth(theSize);
			minWidth(theSize);
		}
		
		_myMin = theMin;
		_myMax = theMax;
		value(theValue);
		
		_myIsHorizontal = theIsHorizontal;
			
		mousePressed.add(event -> {
			if(_myIsHorizontal){
				sliderPos(event.x);
			}else{
				sliderPos(event.y);
			}
		});
		
		mouseMoved.add(pos -> {});
		
		mouseDragged.add(pos -> {
			if(_myIsHorizontal){
				sliderPos(pos.x);
			}else{
				sliderPos(pos.y);
			}
		});
	}
	
	public CCUISlider(double theSize, double theMin, double theMax, double theValue, boolean theIsHorizontal){
		this(createDefaultStyle(), theSize, theMin, theMax, theValue, theIsHorizontal);
	}

	public CCUISlider(CCUIWidgetStyle theStyle, double theSize, double theMin, double theMax, double theValue){
		this(theStyle, theSize, theMin, theMax, theValue, true);
	}
	
	public CCUISlider(double theSize, double theMin, double theMax, double theValue){
		this(createDefaultStyle(), theSize, theMin, theMax, theValue, true);
	}
	
	private double longSide(){
		if(_myIsHorizontal){
			return width();
		}else{
			return height();
		}
	}
	
	private double shortSide(){
		if(_myIsHorizontal){
			return height();
		}else{
			return width();
		}
	}
	
	private void sliderPos(double thePosition) {
		_mySliderPos = CCMath.clamp(thePosition, shortSide() / 2,longSide() - shortSide() / 2);
		_myValue = CCMath.map(_mySliderPos,  shortSide() / 2,  longSide() - shortSide() / 2, _myMin, _myMax);
		
		changeEvents.event(_myValue);
	}
	
	public void value(double theValue) {
		_myValue = CCMath.clamp(theValue, _myMin, _myMax);
		_mySliderPos = CCMath.map(theValue, _myMin, _myMax, shortSide() / 2, longSide() - shortSide() / 2);

		changeEvents.event(_myValue);
	}
	
	public double value(){
		return _myValue;
	}
	
	@Override
	public void displayContent(CCGraphics g) {
		_myStyle.background().draw(g, this);
		_myStyle.border().draw(g, this);
		g.color(255);
		_mySliderPos = CCMath.map(_myValue, _myMin, _myMax, shortSide() / 2, longSide() - shortSide() / 2);
		if(_myIsHorizontal){
			g.ellipse(_mySliderPos, height() / 2, height() / 2 - 2);
		}else{
			g.ellipse(width() / 2, _mySliderPos, width() / 2 - 2);
		}
	}
	
	@Override
	public CCYogaNode childAtPosition(CCVector2 thePosition) {
		CCLog.info(thePosition);
		return super.childAtPosition(thePosition);
	}
}
