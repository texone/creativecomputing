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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public class CCUIVerticalFlowPane extends CCUIPane {
	
	public CCUIVerticalFlowPane(CCUIWidgetStyle theStyle){
		super(theStyle);
	}
	
	public CCUIVerticalFlowPane(){
		super(new CCUIWidgetStyle());
	}
	
	@Override
	public void updateLayout() {
		double myX = _myStyle.leftInset();
		double myY = -_myStyle.topInset();
		double myMaxWidth = 0;
		double myHeight = 0;
		int myStretch = 0;
		for(CCUIWidget myWidget:children()) {
			if(myWidget.stretchHeight()){
				myHeight += myWidget.minHeight();
				myStretch++;
			}else{
				myHeight += myWidget.height();
			}
		}
//		CCLog.info(getClass().getName(),height(),myHeight);
		double myStretchHeight = (height() - myHeight - _cVerticalSpace * CCMath.max(children().size() - 1,0)) / myStretch;
		for(CCUIWidget myWidget:children()) {
			myWidget.translation().set(myX, myY);
			if(myWidget.stretchHeight()){
				myWidget.height(myStretchHeight);
			}
			if(myWidget.stretchWidth()){
				myWidget.width(_myWidth);
			}
			myY -= myWidget.height();
			myY -= _cVerticalSpace;
			myMaxWidth = CCMath.max(myMaxWidth,myWidget.width());
		}
		_myMinWidth = myMaxWidth;
		_myMinHeight = -myY;

		super.updateLayout();
	}
	
}
