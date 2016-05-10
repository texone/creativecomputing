package cc.creativecomputing.math;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCMatrix2 {
	
	private static double TOLERANCE = 0.04f;

	private int _myColumns;
	private int _myRows;
	private int _myDepth;

	private double[][][] _myData;

	public CCMatrix2(int theColumns, int theRows, int theDepth) {
		_myColumns = theColumns;
		_myRows = theRows;
		_myDepth = theDepth;

		_myData = new double[_myColumns][_myRows][_myDepth];
	}
	
	public CCMatrix2(int theColumns, int theRows){
		this(theColumns, theRows, 1);
	}
	
	public void set(int theColumn, int theRow, double theValue){
		_myData[theColumn][theRow][0] = theValue;
	}
	
	public void set(int theColumn, int theRow, int theDepth, double theValue){
		_myData[theColumn][theRow][theDepth] = theValue;
	}
	
	public double[][][] data(){
		return _myData;
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
	
	public CCVector2 minMax(){
		CCVector2 myMinMax = new CCVector2(Double.MAX_VALUE,-Double.MAX_VALUE);
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myMinMax.x = CCMath.min(myMinMax.x, _myData[c][r][d]);
					myMinMax.y = CCMath.max(myMinMax.y, _myData[c][r][d]);
				}
			}
		}
		return myMinMax;
	}
	
	public CCVector2 minMax(int d){
		CCVector2 myMinMax = new CCVector2(Double.MAX_VALUE,-Double.MAX_VALUE);
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				myMinMax.x = CCMath.min(myMinMax.x, _myData[c][r][d]);
				myMinMax.y = CCMath.max(myMinMax.y, _myData[c][r][d]);
			}
		}
		return myMinMax;
	}
	
	public double[] get(int theColumn, int theRow){
		return _myData[CCMath.constrain(theColumn, 0, _myColumns - 1)][CCMath.constrain(theRow, 0, _myRows - 1)];
	}
	
	public double[] get(double theColumn, double theRow){
		int myColumn = CCMath.floor(theColumn);
		int myRow = CCMath.floor(theRow);
		double myColumnBlend = theColumn - myColumn;
		double myRowBlend = theRow - myRow;
		double[] myData00 = get(myColumn, myRow);
		double[] myData10 = get(myColumn + 1, myRow);
		double[] myData01 = get(myColumn, myRow + 1);
		double[] myData11 = get(myColumn + 1, myRow + 1);
		
		double[] myResult = new double[_myDepth];
		for(int i = 0; i < _myDepth;i++){
			double myData0 = CCMath.blend(myData00[i], myData10[i], myColumnBlend);
			double myData1 = CCMath.blend(myData01[i], myData11[i], myColumnBlend);
			myResult[i] = CCMath.blend(myData0, myData1, myRowBlend);
		}
		return myResult;
	}
	
	public double[] get(CCInterpolators theInterpolator, double theColumn, double theRow){
		switch(theInterpolator){
		case LINEAR:
			return get(theColumn, theRow);
		default:
			int myColumn = CCMath.floor(theColumn);
			int myRow = CCMath.floor(theRow);
			double myColumnBlend = theColumn - myColumn;
			double myRowBlend = theRow - myRow;
			
			double[][] cubicXInput = new double[4][];
			double[][] cubicYInput = new double[4][];
			for(int x = -1; x <= 2; x++){
				for(int y = -1; y <= 2; y++){
					double[] data = get(x + myColumn,y + myRow);
					cubicYInput[y + 1] = data;
				}
				double[]myData = new double[_myDepth];
				for(int i = 0; i < _myDepth;i++){
					myData[i] = theInterpolator.blend(cubicYInput[0][i], cubicYInput[1][i], cubicYInput[2][i], cubicYInput[3][i], myRowBlend);
				}
				cubicXInput[x + 1] = myData;
			}
			
			double[]myData = new double[_myDepth];
			for(int i = 0; i < _myDepth;i++){
				myData[i] = theInterpolator.blend(cubicXInput[0][i], cubicXInput[1][i], cubicXInput[2][i], cubicXInput[3][i], myColumnBlend);
			}
			return myData;
		}
	}

	public CCMatrix2 subtract(CCMatrix2 theFrame) {
		CCMatrix2 myResult = new CCMatrix2(_myColumns, _myRows, _myDepth);

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myResult._myData[c][r][d] = _myData[c][r][d] - theFrame._myData[c][r][d];
				}
			}
		}

		return myResult;
	}

	public CCMatrix2 add(CCMatrix2 theFrame) {
		CCMatrix2 myResult = new CCMatrix2(_myColumns, _myRows, _myDepth);

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myResult._myData[c][r][d] = _myData[c][r][d] + theFrame._myData[c][r][d];
				}
			}
		}

		return myResult;
	}

	public CCMatrix2 multiply(double theScale) {
		CCMatrix2 myResult = new CCMatrix2(_myColumns, _myRows, _myDepth);

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myResult._myData[c][r][d] = _myData[c][r][d] * theScale;
				}
			}
		}

		return myResult;
	}

	public CCMatrix2 multiplyLocal(double theScale) {

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					_myData[c][r][d] *= theScale;
				}
			}
		}

		return this;
	}
	
	public CCMatrix2 randomize(double theMin, double theMax) {

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					_myData[c][r][d] = CCMath.random(theMin, theMax);
				}
			}
		}

		return this;
	}

	public CCMatrix2 magnitudes() {
		CCMatrix2 myResult = new CCMatrix2(_myColumns, _myRows, 1);

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				double myMagnitude = 0;
				for (int d = 0; d < _myDepth; d++) {
					myMagnitude += CCMath.sq(_myData[c][r][d]);
				}
				myResult._myData[c][r][0] = CCMath.sqrt(myMagnitude);
			}
		}

		return myResult;
	}

	public CCMatrix2 subMatrix(int theColumn, int theRow) {
		CCMatrix2 myResult = new CCMatrix2(1, 1, _myDepth);
		myResult._myData[0][0] = _myData[theColumn][theRow];
		return myResult;
	}

	public CCMatrix2 subMatrix(int colStart, int colEnd, int rowStart, int rowEnd) {
		CCMatrix2 ret = new CCMatrix2(colEnd - colStart, rowEnd - rowStart, _myDepth);
		for (int i = colStart; i < colEnd; i++) {
			for (int j = rowStart; j < rowEnd; j++) {
				ret._myData[i - colStart][j - rowStart] = _myData[i][j];
			}
		}
		return ret;
	}

	public void setData(double theValue) {
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					_myData[c][r][d] = theValue;
				}
			}
		}
	}
	
	public void data(FloatBuffer theFloatBuffer){
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					_myData[c][r][d] = theFloatBuffer.get();
				}
			}
		}
	}
	
	public DoubleBuffer toDoubleBuffer(DoubleBuffer theStore){
		if(theStore == null){
			theStore = DoubleBuffer.allocate(_myColumns * _myRows * _myDepth);
		}
		
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					theStore.put(_myData[c][r][d]);
				}
			}
		}
		return theStore;
	}
	
	public void data(ByteBuffer theBuffer){
		theBuffer.rewind();
		_myColumns = theBuffer.getInt();
		_myRows = theBuffer.getInt();
		_myDepth = theBuffer.getInt();
		_myData = new double[_myColumns][_myRows][_myDepth];
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					_myData[c][r][d] = theBuffer.getDouble();
				}
			}
		}
	}
	
	public ByteBuffer toByteBuffer(){
		ByteBuffer myBuffer = ByteBuffer.allocate(_myColumns * _myRows * _myDepth * Double.BYTES + 3 * Integer.BYTES);
		myBuffer.putInt(_myColumns);
		myBuffer.putInt(_myRows);
		myBuffer.putInt(_myDepth);
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					myBuffer.putDouble(_myData[c][r][d]);
				}
			}
		}
		myBuffer.rewind();
		return myBuffer;
	}

	public String toString() {
		String s = "";
		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				s += "[";
				for (int d = 0; d < _myDepth; d++) {
					s += _myData[c][r][d];
					if (d < _myDepth - 1)
						s += ",";
				}
				s += "] ";
			}
			s += "\n";
		}
		return s;
	}
	
	

	@Override
	public boolean equals(Object theObj) {
		if(!(theObj instanceof CCMatrix2))return false;
		CCMatrix2 myFrame = (CCMatrix2)theObj;
		if(myFrame._myColumns != _myColumns)return false;
		if(myFrame._myRows != _myRows)return false;
		if(myFrame._myDepth != _myDepth)return false;

		for (int c = 0; c < _myColumns; c++) {
			for (int r = 0; r < _myRows; r++) {
				for (int d = 0; d < _myDepth; d++) {
					double myDif = CCMath.abs(myFrame._myData[c][r][d] - _myData[c][r][d]);
					if(myDif > TOLERANCE){
						return false;
					}
				}
			}
		}
		
		return true;
	}
}
