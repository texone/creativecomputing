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
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIHorizontalFlowPane extends CCUIPane{

	public CCUIHorizontalFlowPane() {
		super();
	}

	public CCUIHorizontalFlowPane(double theWidth, double theHeight) {
		super(theWidth, theHeight);
	}
	
	@Override
	public void updateLayout() {
		double myX = _myInset;
		double myMaxHeight = 0;
		for(CCUIWidget myWidget:children()) {
			myMaxHeight = CCMath.max(myMaxHeight, myWidget.height());
		}
		for(CCUIWidget myWidget:children()) {
			double myY = 0;
			switch(myWidget.verticalAlignment()) {
			case TOP:
				myY = -_myInset;
				break;
			case CENTER:
				myY = -_myInset - myMaxHeight / 2 + myWidget.height() / 2;
				break;
			case BOTTOM:
				myY = -_myInset-myMaxHeight+myWidget.height();
				break;
			}
			myWidget.translation().set(myX, myY);
			myX += myWidget.width();
			myX += _cHorizontalSpace;
		}
		_myMinSize = new CCVector2(myX - _cHorizontalSpace + _myInset, 2 * _myInset + myMaxHeight);
	}
	
	
}
