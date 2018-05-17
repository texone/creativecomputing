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
package cc.creativecomputing.ui.layout;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public class CCUIHorizontalFlowPane extends CCUIPane{

	public CCUIHorizontalFlowPane(CCUIWidgetStyle theStyle) {
		super(theStyle);
	}
	
	public CCUIHorizontalFlowPane(){
		super(new CCUIWidgetStyle());
	}

	public CCUIHorizontalFlowPane(CCUIWidgetStyle theStyle, double theWidth, double theHeight) {
		super(theStyle, theWidth, theHeight);
	}
	
	public CCUIHorizontalFlowPane(double theWidth , double theHeight){
		super(new CCUIWidgetStyle(), theWidth, theHeight);
	}
	
	@Override
	public void updateLayout() {
		double myX = _myStyle.leftInset();
		double myMaxHeight = 0;
		for(CCUIWidget myWidget:children()) {
			myMaxHeight = CCMath.max(myMaxHeight, myWidget.height());
		}
		for(CCUIWidget myWidget:children()) {
			double myY = 0;
			switch(myWidget.style().verticalAlignment()) {
			case TOP:
				myY = -_myStyle.topInset();
				break;
			case CENTER:
				myY = -_myStyle.topInset() - myMaxHeight / 2 + myWidget.height() / 2;
				break;
			case BOTTOM:
				myY = -_myStyle.topInset() - myMaxHeight+myWidget.height();
				break;
			}
			if(myWidget.stretchHeight()){
				myWidget.height(_myHeight);
			}
			myWidget.translation().set(myX, myY);
			myX += myWidget.width();
			myX += _cHorizontalSpace;
		}
		_myMinWidth = myX - _cHorizontalSpace;
		_myMinHeight = myMaxHeight;
		super.updateLayout();
	}
	
	
}
