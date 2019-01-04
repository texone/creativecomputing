package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;;

public class CCHandTracker {
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	@CCProperty(name = "hull color")
	private CCColor _cHullColor = new CCColor();
	
	private MatVector _myContours;
	private Mat _myHandContourMat;
	
	private List<CCVector2> _myHandContour = new ArrayList<>();
	private List<CCVector2> _myConvexHull = new ArrayList<>();
	
	private int _myBiggestContourIndex = -1;
	
	public CCHandTracker() {
		_myContours = new MatVector();
		_myHandContourMat = new Mat();
	}
	
	private List<CCVector2> matToContour(Mat theMat){
		List<CCVector2> myResult = new ArrayList<>();
		for(int i = 0; i < theMat.rows();i++) {
			Mat myPoint = theMat.row(i);
			myResult.add(new CCVector2(myPoint.getIntBuffer().get(0),720 - myPoint.getIntBuffer().get(1)));
		}
		return myResult;
	}

	public void trackHands(Mat theMat) {
		Mat myGrayMat = theMat;
		if(myGrayMat.channels() == 3) {
			myGrayMat = CCCVUtil.rgbToGray(myGrayMat);
		}
		findContours(myGrayMat, _myContours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
		
		// we need at least one contour to work
		if (_myContours.size() <= 0)
			return;

		// find the biggest contour (let's suppose it's our hand)
		double myBiggestArea = 0.0;
		
		for (int i = 0; i < _myContours.size(); i++) {
			double area = contourArea(_myContours.get(i), false);
			if (area > myBiggestArea) {
				myBiggestArea = area;
				_myBiggestContourIndex = i;
			}
		}
		if (_myBiggestContourIndex < 0)
			return;
		
		_myHandContourMat = _myContours.get(_myBiggestContourIndex);
		
		_myHandContour = matToContour(_myHandContourMat);
		
		Mat myHullPoints = new Mat();
		Mat myHullIndices = new Mat();

		convexHull(_myHandContourMat, myHullPoints, false, true);
		convexHull(_myHandContourMat, myHullIndices, false, false);
		
		_myConvexHull = matToContour(myHullPoints);
//		const getHandContour = (handMask) => {
//			  const contours = handMask.findContours(
//			    cv.RETR_EXTERNAL,
//			    cv.CHAIN_APPROX_SIMPLE
//			  );
//			  // largest contour
//			  return contours.sort((c0, c1) => c1.area - c0.area)[0];
//			};
	}
	
	public List<CCVector2> handContour(){
		return _myHandContour;
	}
	
	public List<CCVector2> handHull(){
		return _myConvexHull;
	}
	
	
	
	public void drawContour(Mat theMat) {
		if (_myContours.size() <= 0)
			return;
		if (_myBiggestContourIndex < 0)
			return;

//		drawContours(theMat, _myContours, _myBiggestContourIndex, new Scalar(_cContourColor.b * 255d, _cContourColor.g * 255d, _cContourColor.r * 255d, 255));
//
//		
//		
//		convexHull(_myHandContour, myHullPoints, false, true);
//		drawContours(theMat, new MatVector(myHullPoints), 0, new Scalar(_cHullColor.b * 255d, _cHullColor.g * 255d, _cHullColor.r * 255d, 255));
//		
//		for(int i = 0; i < myHullPoints.rows();i++) {
//			Mat myPoint = myHullPoints.row(i);
//		CCLog.info(myPoint.getIntBuffer().get(0), myPoint.getIntBuffer().get(1));
//			ellipse(theMat, new RotatedRect(new Point2f(myPoint.getIntBuffer().get(0), myPoint.getIntBuffer().get(1)), new Size2f(20),0), new Scalar(_cHullColor.b * 255d, _cHullColor.g * 255d, _cHullColor.r * 255d, 255), 2, LINE_8);
//		}
		
	}
}
