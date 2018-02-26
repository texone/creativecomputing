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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIGridPane extends CCUIPane{
	
	public static class CCUITableEntry{
		
		private CCUIWidget widget;
		
		public int column;
		public int columnSpan;
		
		public int row;
		public int rowSpan;
		
		private void set(CCUITableEntry theEntry) {
			column = theEntry.column;
			row = theEntry.row;
			
			columnSpan = theEntry.columnSpan;
			rowSpan = theEntry.rowSpan;
		}
		
	}
	
	private List<CCUITableEntry> _myEntries = new ArrayList<>();
	
	private double _myRowHeight;
	
	private double[] _myColumnWidths;
	private double[] _myColumnXs;
	
	
	
	public CCUIGridPane() {
		super();
	}

	public CCUIGridPane(double theWidth, double theHeight) {
		super(theWidth, theHeight);
	}
	
	public void columnWidths(double...theWidths) {
		_myColumnWidths = theWidths;
	}
	
	public void rowHeight(double theRowHeight) {
		_myRowHeight = theRowHeight;
	}
	
	@Override
	public void addChild(CCUIWidget theWidget) {
		addChild(theWidget,new CCUITableEntry());
	}
	
	private int _myRows = 0;
	
	public void addChild(CCUIWidget theWidget, CCUITableEntry theEntry) {
		super.addChild(theWidget);
		
		CCUITableEntry myTableEntry = new CCUITableEntry();
		myTableEntry.set(theEntry);
		myTableEntry.widget = theWidget;
		_myEntries.add(myTableEntry);
		
		double myScale = 0;
		for(double myColumnWidth:_myColumnWidths) {
			myScale += myColumnWidth;
		}
		myScale = (width() - 2 * _myInset - (_myColumnWidths.length - 1) * -_cHorizontalSpace) / myScale;
		double myX = _myInset;
		_myColumnXs = new double[_myColumnWidths.length];
		for(int i = 0; i < _myColumnWidths.length;i++) {
			_myColumnWidths[i] = _myColumnWidths[i] * myScale;
			_myColumnXs[i] = myX;
			myX += _myColumnWidths[i] + _cHorizontalSpace;
		}
		
		double myHeight = 0;
		
		for(CCUITableEntry myEntry:_myEntries) {
			CCUIWidget myWidget = myEntry.widget;
			switch(myWidget.horizontalAlignment()) {
			case LEFT:
				myWidget.translation().x = _myColumnXs[myEntry.column];
				break;
			case RIGHT:
				myWidget.translation().x = _myColumnXs[myEntry.column] + _myColumnWidths[myEntry.column] - myWidget.width();
				break;
			case CENTER:
				myWidget.translation().x = _myColumnXs[myEntry.column] + _myColumnWidths[myEntry.column] / 2 - myWidget.width() / 2;
				break;
			}
			myWidget.translation().y = -_myInset - myEntry.row * (_myRowHeight + _cVerticalSpace);
			switch(myWidget.verticalAlignment()){
			case TOP:
				break;
			case CENTER:
				myWidget.translation().y -= (_myRowHeight - myWidget.height()) / 2;
				break;
			case BOTTOM:
				myWidget.translation().y -= (_myRowHeight - myWidget.height());
				break;
			}
			
			myHeight = CCMath.max(myHeight, (myEntry.row + 1) * (_myRowHeight + _cVerticalSpace) - _cVerticalSpace + _myInset * 2);
			
			_myRows =  CCMath.max(myEntry.row ,_myRows);
		}
		
		_myMinSize.x = width();
		_myMinSize.y = myHeight;
		
		updateMatrices();
	}
}
