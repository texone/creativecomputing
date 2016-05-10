package cc.creativecomputing.kle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCSequence extends ArrayList<CCMatrix2>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4154710977769868741L;

	public static class CCSequenceSubIterator implements Iterator<CCMatrix2>{
		
		private Iterator<CCMatrix2> _myIterator;
		private int _myStartColumn;
		private int _myEndColumn;
		private int _myStartRow;
		private int _myEndRow;
		
		private CCSequenceSubIterator(List<CCMatrix2> theFrames, int theStartColumn, int theEndColumn, int theStartRow, int theEndRow){
			_myIterator = theFrames.iterator();
			_myStartColumn = theStartColumn;
			_myEndColumn = theEndColumn;
			_myStartRow = theStartRow;
			_myEndRow = theEndRow;
		}

		@Override
		public boolean hasNext() {
			return _myIterator.hasNext();
		}

		@Override
		public CCMatrix2 next() {
			return _myIterator.next().subMatrix(_myStartColumn, _myEndColumn, _myStartRow, _myEndRow);
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private int _myColumns;
	private int _myRows;
	private int _myDepth;
	
	public CCSequence(int theColumns, int theRows, int theDepth){
		_myColumns = theColumns;
		_myRows = theRows;
		_myDepth = theDepth;
	}
	
	public int columns(){
		return _myColumns;
	}
	
	public int rows(){
		return _myRows;
	}
	
	public int depth(){
		return _myDepth;
	}
	
	public void addEmptyFrame(){
		add(new CCMatrix2(_myColumns, _myRows, _myDepth));
	}
	
	public CCMatrix2 frame(int theIndex){
		return get(theIndex);
	}
	
	public CCMatrix2 frame(double theFrame){
		double myBlend = theFrame - (int)theFrame;
		CCMatrix2 myLower = frame((int)theFrame);
		CCMatrix2 myUpper = frame(CCMath.min((int)theFrame + 1, size() - 1));
		
		CCMatrix2 myResult = new CCMatrix2(_myColumns, _myRows, _myDepth);

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myResult.data()[c][r][d] = CCMath.blend(myLower.data()[c][r][d], myUpper.data()[c][r][d], myBlend);
				}
			}
		}

		return myResult;
	}
	
	public double value(double theFrame, int theColumn, int theRow, int theDepth){
		double myBlend = theFrame - (int)theFrame;
		CCMatrix2 myLower = frame((int)theFrame);
		CCMatrix2 myUpper = frame(CCMath.min((int)theFrame + 1, size() - 1));
		return CCMath.blend(myLower.data()[theColumn][theRow][theDepth], myUpper.data()[theColumn][theRow][theDepth], myBlend);
	}
	
	public int length(){
		return size();
	}
	
	public CCSequenceSubIterator iterator(int col, int row){
	    return new CCSequenceSubIterator(this, col, col+1, row, row+1);
	}
	
	public CCSequenceSubIterator iterator(int startCol, int endCol, int startRow, int endRow){
	    return new CCSequenceSubIterator(this, startCol, endCol, startRow, endRow);
	}
	
	@Override
	public boolean equals(Object theO) {
		if(!(theO instanceof CCSequence))return false;
		CCSequence mySequence = (CCSequence)theO;
		
		if(mySequence.size() != size())return false;
		
		for(int i = 0; i < size();i++){
			if(!get(i).equals(mySequence.get(i)))return false;
		}
		return true;
	}
}
