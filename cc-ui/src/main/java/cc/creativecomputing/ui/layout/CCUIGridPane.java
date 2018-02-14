package cc.creativecomputing.ui.layout;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIGridPane extends CCUIPane{
	
	public static class CCUITableEntry{
		
		private CCUIWidget widget;
		
		public CCUIHorizontalAlignment horizontalAlignment = CCUIHorizontalAlignment.LEFT;
		public CCUIVerticalAlignment verticalAlignment = CCUIVerticalAlignment.TOP;
		
		public int column;
		public int columnSpan;
		
		public int row;
		public int rowSpan;
		
		private void set(CCUITableEntry theEntry) {
			horizontalAlignment = theEntry.horizontalAlignment;
			verticalAlignment = theEntry.verticalAlignment;
			
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
		myScale = (width() - 2 * _cMargin - (_myColumnWidths.length - 1) * -_cSpace) / myScale;
		double myX = _cMargin;
		_myColumnXs = new double[_myColumnWidths.length];
		for(int i = 0; i < _myColumnWidths.length;i++) {
			_myColumnWidths[i] = _myColumnWidths[i] * myScale;
			_myColumnXs[i] = myX;
			myX += _myColumnWidths[i] + _cSpace;
		}
		
		double myHeight = 0;
		
		for(CCUITableEntry myEntry:_myEntries) {
			CCUIWidget myWidget = myEntry.widget;
			switch(myEntry.horizontalAlignment) {
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
			myWidget.translation().y = -_cMargin - myEntry.row * (_myRowHeight + _cSpace);
			
			myHeight = CCMath.max(myHeight, (myEntry.row + 1) * (_myRowHeight + _cSpace) - _cSpace + _cMargin * 2);
		}
		
		_myMinSize.x = width();
		_myMinSize.y = myHeight;
		
		updateMatrices();
	}
}
