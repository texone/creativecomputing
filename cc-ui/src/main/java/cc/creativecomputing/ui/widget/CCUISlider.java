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
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIDrawable;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

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
		public void draw(CCGraphics g, CCUIWidget theWidget) {
			_myRoundedRectangle.color().set(_myColor);
			_myRoundedRectangle.gradientColor().set(_myColor);
			_myRoundedRectangle.radius(_myCornerRadius);
			_myRoundedRectangle.position(theWidget.style().leftInset(), -theWidget.height());
			_myRoundedRectangle.size(theWidget.width(), theWidget.height());
			_myRoundedRectangle.draw(g);
		}

	}
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
//		myResult.leftInset(15);
//		myResult.rightInset(15);
		myResult.background(new CCUISliderDrawable(new CCColor(0.3d), 7));
		myResult.foreground(new CCUIFillDrawable(new CCColor(0.7d)));
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		return myResult;
	}
	
	private double _mySliderPos = 0;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	public CCEventManager<Double> changeEvents = new CCEventManager<>();
	
	private boolean _myIsHorizontal;

	public CCUISlider(CCUIWidgetStyle theStyle, double theWidth, double theHeight, double theMin, double theMax, double theValue, boolean theIsHorizontal){
		super(theStyle);
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myMinWidth = theWidth;
		_myMinHeight = theHeight;
		
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
	
	public CCUISlider(double theWidth, double theHeight, double theMin, double theMax, double theValue, boolean theIsHorizontal){
		this(createDefaultStyle(), theWidth, theHeight, theMin, theMax, theValue, theIsHorizontal);
	}

	public CCUISlider(CCUIWidgetStyle theStyle, double theWidth, double theHeight, double theMin, double theMax, double theValue){
		this(theStyle, theWidth, theHeight, theMin, theMax, theValue, true);
	}
	
	public CCUISlider(double theWidth, double theHeight, double theMin, double theMax, double theValue){
		this(createDefaultStyle(), theWidth, theHeight, theMin, theMax, theValue, true);
	}
	
	private double longSide(){
		if(_myIsHorizontal){
			return width();
		}else{
			return _myHeight;
		}
	}
	
	private double shortSide(){
		if(_myIsHorizontal){
			return _myHeight;
		}else{
			return width();
		}
	}
	
	private double longOffset(){
		if(_myIsHorizontal){
			return style().leftInset();
		}else{
			return style().topInset();
		}
	}
	
	private double shortOffset(){
		if(_myIsHorizontal){
			return style().topInset();
		}else{
			return style().leftInset();
		}
	}
	
	public double width() {
		return _myWidth - _myStyle.leftInset() - _myStyle.rightInset();
	}
	
	private void sliderPos(double thePosition) {
		if(_myIsHorizontal){
			_mySliderPos = CCMath.clamp(thePosition, longOffset() + shortSide() / 2, longOffset() + longSide() - shortSide() / 2);
			_myValue = CCMath.map(_mySliderPos,  longOffset() + shortSide() / 2, longOffset() + longSide() - shortSide() / 2, _myMin, _myMax);
			CCLog.info(thePosition,_mySliderPos,_myValue);
		}else{
			_mySliderPos = CCMath.clamp(thePosition, -style().topInset() - height() + width() / 2, -style().topInset() - width() / 2);
			_myValue = CCMath.map(_mySliderPos, -style().topInset() - height() + width() / 2, -style().topInset() - width() / 2,_myMax, _myMin );
			CCLog.info(thePosition,_mySliderPos,_myValue);
		}
		
		changeEvents.event(_myValue);
	}
	
	public void value(double theValue) {
		_myValue = CCMath.clamp(theValue, _myMin, _myMax);
		_mySliderPos = CCMath.map(theValue, _myMin, _myMax, longOffset() + shortSide() / 2, longOffset() +  longSide() - shortSide() / 2);

		changeEvents.event(_myValue);
	}
	
	public double value(){
		return _myValue;
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		if(_myStyle.background() != null)_myStyle.background().draw(g, this);
		if(_myStyle.border() != null)_myStyle.border().draw(g, this);
		g.color(255);
		_mySliderPos = CCMath.map(_myValue, _myMin, _myMax, longOffset() + shortSide() / 2, longOffset() +  longSide() - shortSide() / 2);
		if(_myIsHorizontal){
			g.ellipse(_mySliderPos, -_myHeight / 2, _myHeight / 2 - 2);
		}else{
			g.ellipse(_myWidth / 2, -_mySliderPos, _myWidth / 2 - 2);
		}
	}
}
