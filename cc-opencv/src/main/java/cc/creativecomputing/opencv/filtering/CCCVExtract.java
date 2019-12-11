package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the normalized box filter. 
 * @author chris
 *
 */
public class CCCVExtract extends CCImageProcessor{
	
	@CCProperty(name = "x0", min = 0, max = 1)
	public double _cX0 = 0;
	
	@CCProperty(name = "x1", min = 0, max = 1)
	public double _cX1 = 1;
	
	@CCProperty(name = "y0", min = 0, max = 1)
	public double _cY0 = 0;
	
	@CCProperty(name = "y1", min = 0, max = 1)
	public double _cY1 = 1;
	
	private int _myX = 0;
	private int _myY = 0;
	
	@Override
	public Mat implementation(Mat theSource) {
		if(_cX1 <= _cX0)return theSource;
		if(_cY1 <= _cY0)return theSource;
		
		int myWidth = (int)(theSource.cols() * (_cX1 - _cX0)) / 4 * 4;
		int myHeight = (int)(theSource.rows() *(_cY1 - _cY0)) / 4 * 4;
		
		_myX = (int)(theSource.cols() * _cX0);
		_myY = (int)(theSource.rows() * _cY0);
		
		return new Mat(theSource, new Rect(_myX, _myY, myWidth,myHeight));
	}

	public int x() {
		return _myX;
	}
	
	public int y() {
		return _myY;
	}
}
