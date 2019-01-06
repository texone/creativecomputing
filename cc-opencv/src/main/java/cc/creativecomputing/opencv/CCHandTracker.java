package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;;

public class CCHandTracker {
	
	public static class CCVectorAndID{
		public final CCVector2 vector;
		public final int id;
		
		public CCVectorAndID(CCVector2 theVector, int theID) {
			vector = theVector;
			id = theID;
		}

		public double distance(CCVectorAndID theOther) {
			return vector.distance(theOther.vector);
		}
	}
	
	private static class CCConvexityDefect implements Comparable<CCConvexityDefect>{
		int start;
		int center;
		int end;
		double depth;
		
		public CCConvexityDefect(int theStart, int theCenter, int theEnd, double theDepth) {
			start = theStart;
			center = theCenter;
			end = theEnd;
			depth = theDepth;
		}

		@Override
		public int compareTo(CCConvexityDefect o) {
			return Integer.compare(start, o.start);
		}
		
 	}
	
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	@CCProperty(name = "hull color")
	private CCColor _cHullColor = new CCColor();
	@CCProperty(name = "reduction distance", min = 0, max = 100)
	private double _cReductionDistance = 10;
	@CCProperty(name = "min finger length", min = 0, max = 100)
	private double _cMinFingerLength = 30;
	@CCProperty(name = "max gap", min = 0, max = 100)
	private double _cMaxGap = 30;
	@CCProperty(name = "tip angle", min = 0, max = 100)
	private double _cTipAngle = 50;
	
	private MatVector _myContours;
	private Mat _myHandContourMat;
	
	private List<CCVector2> _myHandContour = new ArrayList<>();
	private List<CCVectorAndID> _myConvexHull = new ArrayList<>();
	private List<CCVectorAndID> _myHullWithDefects = new ArrayList<>();
	private List<CCVector3> _myFingerTips = new ArrayList<>();
	
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
		
		Mat myHullPointsMat = new Mat();
		Mat myHullIndicesMat = new Mat();

		convexHull(_myHandContourMat, myHullPointsMat, false, true);
		convexHull(_myHandContourMat, myHullIndicesMat, false, false);
		
		_myConvexHull.clear();
		for(int i = 0; i < myHullIndicesMat.rows();i++) {
			Mat myRow = myHullIndicesMat.row(i);
			int myID = myRow.getIntBuffer().get(0);
			
			_myConvexHull.add(new CCVectorAndID(_myHandContour.get(myID), myID));
		}
		
		Mat myDefectsMat = new Mat();
		convexityDefects(_myHandContourMat, myHullIndicesMat, myDefectsMat);
		
		List<CCConvexityDefect> myDefects = new ArrayList<>();
		
		for(int i = 0; i < myDefectsMat.rows();i++) {
			Mat myRow = myDefectsMat.row(i);
			int myStartIndex = myRow.getIntBuffer().get(0);
			int myEndIndex = myRow.getIntBuffer().get(1);
			int myFarthestIndex = myRow.getIntBuffer().get(2);
			int myDepth = myRow.getIntBuffer().get(3);
			
			myDefects.add(new CCConvexityDefect(myStartIndex,myFarthestIndex,myEndIndex, myDepth / 256d));
		}

		_myHullWithDefects.clear();
		Collections.sort(myDefects);
		for(int i = 0; i < myDefects.size();i++) {
			CCConvexityDefect myDefect = myDefects.get(i);
			CCConvexityDefect myNextDefect = myDefects.get((i + 1) % myDefects.size());

			if(_myHandContour.get(myDefect.start).distance(_myHandContour.get(myDefect.end))< _cMaxGap)continue;
			_myHullWithDefects.add(new CCVectorAndID(_myHandContour.get(myDefect.center), myDefect.center));
			_myHullWithDefects.add(new CCVectorAndID(_myHandContour.get(myDefect.end).add(_myHandContour.get(myNextDefect.start)).multiplyLocal(0.5) , myDefect.end));

		}
		
		_myFingerTips.clear();
		
		for(int i = 0; i < _myHullWithDefects.size();i++) {
			
			if(i % 2 == 1) {
				CCVector2 myPrev = _myHullWithDefects.get(i - 1).vector;
				CCVector2 myCurrent = _myHullWithDefects.get(i).vector;
				CCVector2 myNext = _myHullWithDefects.get((i + 1) % _myHullWithDefects.size()).vector;
				
				double myAngle = CCVector2.angle(myNext.subtract(myCurrent), myPrev.subtract(myCurrent));
				myAngle = CCMath.degrees(myAngle);
				if(CCMath.abs(myAngle) < 50) {
					_myFingerTips.add(new CCVector3(myCurrent.x,myCurrent.y,CCMath.max(myPrev.distance(myCurrent), myNext.distance(myCurrent))));
				}
			}
		}
		Collections.sort(_myFingerTips, (a,b) -> Double.compare(b.z, a.z));
	}
	
//	private List<CCVectorAndID> cleanHull(List<CCVectorAndID> theHull) {
//		
//		List<CCVectorAndID> myHullTipPoints = new ArrayList<>();
//		CCVectorAndID myLast = null;
//		CCVectorAndID myFirst = null;
//
//		List<CCVectorAndID> myGroupTipPoints = new ArrayList<>();
//		for(int i = 0; i < theHull.size();i++) {
//			CCVectorAndID myCurrent = theHull.get(i);
//			
//			if(myFirst == null) {
//				myFirst = myCurrent;
//			}
//			if(myLast == null) {
//				myHullTipPoints.add(myCurrent);
//				myLast = myCurrent;
//				continue;
//			}
//			
//			if(myCurrent.distance(myLast) > _cReductionDistance && myCurrent.distance(myFirst) > _cReductionDistance) {
//				myHullTipPoints.add(myCurrent);
////				if(myGroupTipPoints.size() <= 0) {
////					myHullTipPoints.add(myCurrent);
////				}else {
////					myHullTipPoints.add(myGroupTipPoints.get(0));
////					myGroupTipPoints.clear();
////				}
//			}else {
//				myGroupTipPoints.add(myCurrent);
//			}
//
//			myLast = myCurrent;
//		}
//		return myHullTipPoints;
//	}
	
	public List<CCVector2> handContour(){
		return _myHandContour;
	}
	
	public List<CCVectorAndID> handHull(){
		return _myConvexHull;
	}
	
	public List<CCVectorAndID> hullWithDefects(){
		return _myHullWithDefects;
	}
	
	public List<CCVector3> fingerTips(){
		return _myFingerTips;
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
