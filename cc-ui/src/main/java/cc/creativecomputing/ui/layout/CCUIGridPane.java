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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public class CCUIGridPane extends CCUIPane{
	
	public static class CCUITableEntry{
		
		private CCUIWidget widget;
		
		public int column;
		public int columnSpan = 1;
		
		public int row;
		public int rowSpan = 1;
		
		private void set(CCUITableEntry theEntry) {
			column = theEntry.column;
			row = theEntry.row;
			
			columnSpan = theEntry.columnSpan;
			rowSpan = theEntry.rowSpan;
		}
		
	}
	
	private List<CCUITableEntry> _myEntries = new ArrayList<>();
	
	private double _myDefaultRowHeight;
	
	private double[] _myColumnWidths;
	private double[] _myColumnXs;
	
	public CCUIGridPane(CCUIWidgetStyle theStyle) {
		super(theStyle);
	}
	
	public CCUIGridPane(){
		super(new CCUIWidgetStyle());
	}

	public CCUIGridPane(CCUIWidgetStyle theStyle, double theWidth, double theHeight) {
		super(theStyle, theWidth, theHeight);
	}
	
	public CCUIGridPane(double theWidth, double theHeight){
		super(new CCUIWidgetStyle(), theWidth, theHeight);
	}
	
	public void columnWidths(double...theWidths) {
		_myColumnWidths = theWidths;
	}
	
	public void rowHeight(double theRowHeight) {
		_myDefaultRowHeight = theRowHeight;
	}
	
	public double rowHeight() {
		return _myDefaultRowHeight;
	}
	
	@Override
	public void addChild(CCUIWidget theWidget) {
		addChild(theWidget,new CCUITableEntry());
	}
	
	@Override
	public void removeAll() {
		super.removeAll();
		_myRows = 0;
		_myEntries.clear();
	}
	
	@Override
	public void removeChild(CCUIWidget theWidget) {
		for(CCUITableEntry myEntry:new ArrayList<>(_myEntries)){
			if(myEntry.widget == theWidget)_myEntries.remove(myEntry);
		}
		super.removeChild(theWidget);
	}
	
	private void calcColumnWidths(){
		double myScale = 0;

		for(double myColumnWidth:_myColumnWidths) {
			myScale += myColumnWidth;
		}
//		CCLog.info(this, myScale, width(),_myParent,_myParent != null ? _myParent.width():"");
		myScale = (width() - _myStyle.leftInset() - _myStyle.rightInset() - (_myColumnWidths.length - 1) * _cHorizontalSpace) / myScale;
//		CCLog.info(this, myScale);
		double myX = _myStyle.leftInset();
		_myColumnXs = new double[_myColumnWidths.length];
		for(int i = 0; i < _myColumnWidths.length;i++) {
			_myColumnWidths[i] = _myColumnWidths[i] * myScale;
			_myColumnXs[i] = myX;
			myX += _myColumnWidths[i] + _cHorizontalSpace;
		}
	}
	
	private int _myRows = 0;
	
	private double[] _myRowHeights;
	private double[] _myRowYs;
	
	private void calcRowHeights(){

		_myRows = 0;
		
		for(CCUITableEntry myEntry:_myEntries){
			_myRows =  CCMath.max(myEntry.row + 1,_myRows);
		}
		
		_myRowHeights = new double[_myRows];
		_myRowYs = new double[_myRows];
		for(int i = 0; i < _myRows;i++){
			_myRowHeights[i] = _myDefaultRowHeight + _cVerticalSpace;
		}
		for(CCUITableEntry myEntry:_myEntries){
			_myRowHeights[myEntry.row] = CCMath.max(_myRowHeights[myEntry.row], myEntry.widget.minHeight() + _cVerticalSpace);
		}
		double myY = 0;
		for(int i = 0; i < _myRows;i++){
			_myRowYs[i] = myY;
			myY += _myRowHeights[i];
		}
		_myMinHeight = myY;
	}
	
	public void updateLayout(){
		
		calcColumnWidths();
		calcRowHeights();
		
		for(CCUITableEntry myEntry:_myEntries) {
			CCUIWidget myWidget = myEntry.widget;
			if(myWidget.stretchWidth()){
				double myWidth = 0;
				for(int i = myEntry.column; i < myEntry.column + myEntry.columnSpan;i++){
					myWidth += _myColumnWidths[i] + _cHorizontalSpace;
				}
				myWidget.width(myWidth - _cHorizontalSpace);
			}
			switch(myWidget.style().horizontalAlignment()) {
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
			myWidget.translation().y = -_myStyle.topInset() - _myRowYs[myEntry.row];
			switch(myWidget.style().verticalAlignment()){
			case TOP:
				break;
			case CENTER:
				myWidget.translation().y -= (_myRowHeights[myEntry.row] - _cVerticalSpace - myWidget.height()) / 2;
				break;
			case BOTTOM:
				myWidget.translation().y -= (_myRowHeights[myEntry.row] - _cVerticalSpace - myWidget.height());
				break;
			}
		}
		
		_myMinWidth = width();
		
		super.updateLayout();
	}
	
	public int rows(){
		return _myRows;
	}
	
	public void insertRow(int theRow){
		for(CCUITableEntry myEntry:_myEntries){
			if(myEntry.row >= theRow){
				myEntry.row += 1;
			}
		}
		updateLayout();
	}
	
	public void addChild(CCUIWidget theWidget, CCUITableEntry theEntry) {
		CCUITableEntry myTableEntry = new CCUITableEntry();
		myTableEntry.set(theEntry);
		myTableEntry.widget = theWidget;
		_myEntries.add(myTableEntry);
		
		super.addChild(theWidget);
	}
	
	public void addChild(CCUIWidget theWidget, int theColumn, int theRow, int theColumnSpan, int theRowSpan) {
		CCUITableEntry myTableEntry = new CCUITableEntry();
		myTableEntry.column = theColumn;
		myTableEntry.row = theRow;
		myTableEntry.columnSpan = theColumnSpan;
		myTableEntry.rowSpan = theRowSpan;
		myTableEntry.widget = theWidget;
		_myEntries.add(myTableEntry);
		
		super.addChild(theWidget);
	}
	
	public void addChild(CCUIWidget theWidget, int theColumn, int theRow) {
		addChild(theWidget, theColumn, theRow, 1, 1);
	}

	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		for(CCUIWidget myChild:_myChildren) {
//			CCLog.info(myChild, myChild.translation());
		}
	}
}
