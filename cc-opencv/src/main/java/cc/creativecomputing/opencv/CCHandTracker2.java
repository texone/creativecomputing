package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.contourArea;
import static org.bytedeco.javacpp.opencv_imgproc.convexHull;
import static org.bytedeco.javacpp.opencv_imgproc.convexityDefects;
import static org.bytedeco.javacpp.opencv_imgproc.drawContours;
import static org.bytedeco.javacpp.opencv_imgproc.findContours;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCOneEuroFilter;
import cc.creativecomputing.math.spline.CCSplineSimplify;
import cc.creativecomputing.math.spline.Simplify3D;
import cc.creativecomputing.math.spline.SimplifyP5;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;;

public class CCHandTracker2 {
	
	public static interface CCFixedTipEvent{
		public void event(CCVector2 theTip);
	}
	
	public static class CCVectorAndID implements Comparable<CCVectorAndID>{
		public final CCVector3 vector;
		public final int id;
		public final boolean isDefect;
		
		public CCVectorAndID(CCVector3 theVector, int theID, boolean theIsDefect) {
			vector = theVector;
			id = theID;
			isDefect = theIsDefect;
		}

		public double distance(CCVectorAndID theOther) {
			return vector.distance(theOther.vector);
		}
		@Override
		public int compareTo(CCVectorAndID o) {
			return Integer.compare(id, o.id);
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
	
	@CCProperty(name = "shader filter")
	private CCCVShaderFilter _cFilter;
	
	@CCProperty(name = "morphology")
	private CCMorphologyFilter _cMorphology = new CCMorphologyFilter();;

	@CCProperty(name = "blur")
	private CCBlur _cBlur = new CCBlur();

	@CCProperty(name = "threshold")
	private CCThreshold _cThreshold = new CCThreshold();
	
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	@CCProperty(name = "hull color")
	private CCColor _cHullColor = new CCColor();
	@CCProperty(name = "max gap", min = 0, max = 100)
	private double _cMaxGap = 30;
	@CCProperty(name = "tip angle", min = 0, max = 100)
	private double _cTipAngle = 50;
	@CCProperty(name = "tip smooth", min = 0, max = 1)
	private double _cTipSmooth = 0;
	
	@CCProperty(name = "min rest time", min = 0, max = 1)
	private double _cMinRestTime = 2;
	@CCProperty(name = "min size")
	private double _cMinSize = 2;
	@CCProperty(name = "tip filter")
	private CCOneEuroFilter _cTipFilter = new CCOneEuroFilter();

	@CCProperty(name = "rest speed")
	private double _cRestSpeed = 1;
	@CCProperty(name = "progress angle", min = 0, max = 360)
	private double _cProgressAngle = 0;
	@CCProperty(name = "line strength", min = 0, max = 50)
	private double _cLineStrength = 0;
	@CCProperty(name = "line color")
	private CCColor _cLineColor = new CCColor();
	@CCProperty(name = "progress strength", min = 0, max = 50)
	private double _cProgressStrength = 0;
	@CCProperty(name = "progress color")
	private CCColor _cProgressColor = new CCColor();
	
	private CCVector2 _myTip = new CCVector2();
	private CCVector2 _myCenter = new CCVector2();
	private CCVector2 _myProgressCenter = new CCVector2();
	private boolean _myTipInRest = false;
	private double _myRestTime = 0;
	private double _myStartAngle = 0;
	private double _myProgress = 0;
	
	private MatVector _myContours;
	
	private List<CCVector3> _myHandContour = new ArrayList<>();
	private List<CCVectorAndID> _myConvexHull = new ArrayList<>();
	private List<CCVectorAndID> _myHullWithDefects = new ArrayList<>();
	private List<CCVector3> _myFingerTips = new ArrayList<>();
	
	public CCListenerManager<CCFixedTipEvent> fixedTipEvents = CCListenerManager.create(CCFixedTipEvent.class);
	
	private int _myBiggestContourIndex = -1;
	
	private CCCVVideoIn _myVideoIn;
	
	private static enum CCDrawMat{
		ORIGIN,
		SHADER,
		MORPHOLOGY,
		BLUR,
		THRESHOLD
	}
	
	@CCProperty(name = "draw mat")
	private CCDrawMat _cDrawMat = CCDrawMat.THRESHOLD;

	@CCProperty(name = "draw contour")
	private boolean _cDrawContour = true;
	
	private Mat _myDrawMat = null;

	private CCCVTexture _myTexture;
	
	final Lock lock = new ReentrantLock();
	
	private CCTexture2D _myMaskTexture;

	
	public CCHandTracker2(CCCVVideoIn theVideoIn, CCTexture2D theMaskTexture, Path theVertexHader, Path theFragmentShader) {
		_myContours = new MatVector();
		
		_cFilter = new CCCVShaderFilter(theVertexHader, theFragmentShader);
		_cFilter.addTexture(theMaskTexture, 1, "mask");
		_myVideoIn = theVideoIn;
		_myVideoIn.events.add(mat -> {
			if (_cDrawMat == CCDrawMat.ORIGIN)_myDrawMat = mat.clone();

			mat = _cFilter.process(mat);
			if(_cDrawMat == CCDrawMat.SHADER)_myDrawMat = mat.clone();
			
			mat = CCCVUtil.rgbToGray(mat);
			mat = _cMorphology.process(mat);
			if(_cDrawMat == CCDrawMat.MORPHOLOGY)_myDrawMat = CCCVUtil.grayToRGB(mat.clone());
			
			mat = _cBlur.process(mat);
			if(_cDrawMat == CCDrawMat.BLUR)_myDrawMat = CCCVUtil.grayToRGB(mat.clone());
			
			mat = _cThreshold.process(mat);
			if(_cDrawMat == CCDrawMat.THRESHOLD)_myDrawMat = CCCVUtil.grayToRGB(mat.clone());
			
			trackHands(mat);
			if(_cDrawContour)drawContour(_myDrawMat);
		});
		
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(false);
		
		_myMaskTexture = theMaskTexture;
	}
	
	public void active(boolean isActive) {
		_myVideoIn.isActive(isActive);
	}
	
	public void reset() {
//		_myHandContour.clear();
//		_myHullWithDefects.clear();
//		_myFingerTips.clear();
//		
//		if(!_myTip.isZero()) {
//			_myTip.set(0,0);
//		}
//		_myCenter.set(0,0);
	}
	
	public CCHandTracker2(CCCVVideoIn theVideoIn, CCTexture2D theMaskTexture) {
		this(
			theVideoIn,
			theMaskTexture,
			CCNIOUtil.classPath(CCHandTracker2.class, "cv_shader_vertex.glsl"),
			CCNIOUtil.classPath(CCHandTracker2.class, "cv_shader_fragment.glsl")
		);
	}
	
	public void preDisplay(CCGraphics g) {
		if(_myDrawMat == null)return;
		if(!_myVideoIn.isActive())return;
		lock.lock();
		_cFilter.preDisplay(g);
		_myTexture.image(_myDrawMat);
		lock.unlock();
		
	}
	
	private List<CCVector3> matToContour(Mat theMat){
		List<CCVector3> myResult = new ArrayList<>();
		for(int i = 0; i < theMat.rows();i++) {
			Mat myPoint = theMat.row(i);
			myResult.add(new CCVector3(myPoint.getIntBuffer().get(0),_myVideoIn.frameHeight() - myPoint.getIntBuffer().get(1)));
		}
		return myResult;
	}
	
	private static class CCHandInfo{
		public double area;
		public List<CCVector3> handContour;
		public List<CCVector3> fingerTips;
		public List<CCVectorAndID> convexHull;
		public List<CCVectorAndID> hullWithDefects;
		public boolean isHand = false;
	}
	
	private CCHandInfo getHandInfo(Mat theContour) {
		CCHandInfo myResult = new CCHandInfo();
		myResult.area = contourArea(theContour, false);
		if(myResult.area < _cMinSize)return myResult;
		
		myResult.isHand = true;
		myResult.handContour = matToContour(theContour);
		
		List<CCVector3> simpleLine = Simplify3D.simplify(myResult.handContour, _cMaxGap, true);
		if(simpleLine.size() > 4)simpleLine.remove(0);
		
		double myMinAngle = CCMath.TWO_PI;
		for(int i = 0; i < simpleLine.size();i++) {
			CCVector3 myPrev = simpleLine.get((i - 1 + simpleLine.size()) % simpleLine.size());
			CCVector3 myCurrent = simpleLine.get(i);
			CCVector3 myNext = simpleLine.get((i + 1) % simpleLine.size());
				
			double myAngle = CCVector2.angle(myNext.subtract(myCurrent).xy(), myPrev.subtract(myCurrent).xy());
			myAngle = CCMath.degrees(myAngle);
//			CCLog.info(myAngle);
		}
		
		Mat myHullPointsMat = new Mat();
		Mat myHullIndicesMat = new Mat();
	
		convexHull(theContour, myHullPointsMat, false, true);
		convexHull(theContour, myHullIndicesMat, false, false);
			
		myResult.convexHull = new ArrayList<>();
		for(int i = 0; i < myHullIndicesMat.rows();i++) {
			Mat myRow = myHullIndicesMat.row(i);
			int myID = myRow.getIntBuffer().get(0);
			
			myResult.convexHull.add(new CCVectorAndID(myResult.handContour.get(myID), myID, false));
		}
		Collections.sort(myResult.convexHull);	
		Mat myDefectsMat = new Mat();
		convexityDefects(theContour, myHullIndicesMat, myDefectsMat);
			
		List<CCConvexityDefect> myDefects = new ArrayList<>();
			
		for(int i = 0; i < myDefectsMat.rows();i++) {
			Mat myRow = myDefectsMat.row(i);
			int myStartIndex = myRow.getIntBuffer().get(0);
			int myEndIndex = myRow.getIntBuffer().get(1);
			int myFarthestIndex = myRow.getIntBuffer().get(2);
			int myDepth = myRow.getIntBuffer().get(3);
				
			myDefects.add(new CCConvexityDefect(myStartIndex,myFarthestIndex,myEndIndex, myDepth / 256d));
		}
	

		myResult.hullWithDefects = new ArrayList<>();
//		myResult.hullWithDefects.addAll(myResult.convexHull);
		
		
		Collections.sort(myDefects);
		myDefects.forEach(d -> {
			for(CCVectorAndID myHullv:myResult.convexHull) {
//				CCLog.info(myHullv.id);
			}
//			CCLog.info(d.start, d.center, d.end);
		});
		for(int i = 0; i < myDefects.size();i++) {
			CCConvexityDefect myDefect = myDefects.get(i);
			CCConvexityDefect myNextDefect = myDefects.get((i + 1) % myDefects.size());
			//if(myResult.handContour.get(myDefect.start).distance(myResult.handContour.get(myDefect.end)) < _cMaxGap)continue;

			myResult.hullWithDefects.add(new CCVectorAndID(myResult.handContour.get(myDefect.center), myDefect.center, true));
			myResult.hullWithDefects.add(new CCVectorAndID(myResult.handContour.get(myDefect.end) , myDefect.end, false));
	
		}
//		CCLog.info(myResult.hullWithDefects.get(myResult.hullWithDefects.size()-1).id, myResult.convexHull.get(myResult.convexHull.size() - 1).id);
		//myResult.hullWithDefects.add(myResult.convexHull.get(myResult.convexHull.size() - 1));

		myResult.fingerTips = new ArrayList<>();
		
		for(int i = 0; i < myResult.hullWithDefects.size();i++) {	
			if(i % 2 == 1) {
				CCVector3 myPrev = myResult.hullWithDefects.get(i - 1).vector;
				CCVector3 myCurrent = myResult.hullWithDefects.get(i).vector;
				CCVector3 myNext = myResult.hullWithDefects.get((i + 1) % myResult.hullWithDefects.size()).vector;
					
				double myAngle = CCVector2.angle(myNext.subtract(myCurrent).xy(), myPrev.subtract(myCurrent).xy());
				myAngle = CCMath.degrees(myAngle);
				
				if(CCMath.abs(myAngle) < _cTipAngle) {
					myResult.fingerTips.add(new CCVector3(myCurrent.x,myCurrent.y,CCMath.abs(myAngle)));//CCMath.max(myPrev.distance(myCurrent), myNext.distance(myCurrent))
				}
			}
		}
		Collections.sort(myResult.fingerTips, (a,b) -> Double.compare(a.z, b.z));
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
		double myFingers = 10;
		double myAngle = 100;
		CCHandInfo myBestFitInfo = null;
//		CCLog.info();
		int j = 0;
		for (int i = 0; i < _myContours.size(); i++) {
			CCHandInfo myInfo = getHandInfo(_myContours.get(i));
		
			if(myBestFitInfo == null) {
				myBestFitInfo = myInfo;
			}
			if(!myInfo.isHand)continue;
			if(myInfo.fingerTips.size() <= 0)continue;
			
			j++;
			if(myInfo.fingerTips.size()  < myFingers) {
//				CCLog.info(myFingers, myInfo.fingerTips.size());
				myFingers = myInfo.fingerTips.size();
				myBestFitInfo = myInfo;
				myBiggestArea = myBestFitInfo.area;
			}else if(myInfo.fingerTips.size() == myFingers && myInfo.fingerTips.get(0).z < myAngle) {
				myBestFitInfo = myInfo;
				myAngle =  myInfo.fingerTips.get(0).z;
//				CCLog.info(myAngle);
			}
//			else if(myInfo.fingerTips.size() == myFingers && myBestFitInfo.area > myBiggestArea) {
//				myBestFitInfo = myInfo;
//				myBiggestArea = myBestFitInfo.area;
//			}
		}
//		CCLog.info(_myContours.size(), j);
		
		// check if we found big enough contour
		if(myBestFitInfo == null) {
			reset();
			return;
		}

			
		_myConvexHull = myBestFitInfo.convexHull;
		
		

		lock.lock();
		_myHandContour = myBestFitInfo.handContour;
		_myHullWithDefects = myBestFitInfo.hullWithDefects;
		_myFingerTips = myBestFitInfo.fingerTips;
		
		
		
		lock.unlock();
	}
	
	public List<CCVector3> handContour(){
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
//		lock.lock();
//		if (_myContours.size() <= 0)
//			return;
//		if (_myBiggestContourIndex < 0)
//			return;
//		if(_myBiggestContourIndex >= _myContours.size() )
//			return;
//
//		try {
//		drawContours(theMat, _myContours, _myBiggestContourIndex, new Scalar(_cContourColor.b * 255d, _cContourColor.g * 255d, _cContourColor.r * 255d, 255));
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		lock.unlock();
		
//		convexHull(_myHandContour, myHullPoints, false, true);
//		drawContours(theMat, new MatVector(myHullPoints), 0, new Scalar(_cHullColor.b * 255d, _cHullColor.g * 255d, _cHullColor.r * 255d, 255));
//		
//		for(int i = 0; i < myHullPoints.rows();i++) {
//			Mat myPoint = myHullPoints.row(i);
//		CCLog.info(myPoint.getIntBuffer().get(0), myPoint.getIntBuffer().get(1));
//			ellipse(theMat, new RotatedRect(new Point2f(myPoint.getIntBuffer().get(0), myPoint.getIntBuffer().get(1)), new Size2f(20),0), new Scalar(_cHullColor.b * 255d, _cHullColor.g * 255d, _cHullColor.r * 255d, 255), 2, LINE_8);
//		}
		
	}
	
	public void drawDebug(CCGraphics g) {
		if(!_myVideoIn.isActive())return;
		lock.lock();
		g.color(_cContourColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		_myHandContour.forEach(v -> g.vertex(v));
		g.endShape();
		
		List<CCVector3> simpleLine = Simplify3D.simplify(_myHandContour, _cMaxGap, true);
		if(simpleLine.size() > 4)simpleLine.remove(0);
//		CCLog.info(simpleLine.size(), _myHandContour.size());
		g.color(_cHullColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		simpleLine.forEach(v -> g.vertex(v));
		g.endShape();
		
		g.pointSize(3);
		g.pointSmooth();
		g.beginShape(CCDrawMode.POINTS);
		simpleLine.forEach(v -> g.vertex(v));
		g.endShape();
		
		for(int i = 0; i < simpleLine.size();i++) {
//			if(i % 2 == 0)g.color(1d,0,0);
//			else g.color(0d,1,0);
//			
			g.color(_cHullColor);
			if(i == 0)g.color(CCColor.WHITE);
//			
			double myRadius = 2;
//			
			CCVector3 myCurrent = simpleLine.get(i);
//			
			g.ellipse(myCurrent.x, myCurrent.y,0,myRadius,myRadius, false);
		}
//		g.strokeWeight(3);
//		g.color(_cHullColor);
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		_myHullWithDefects.forEach(v -> g.vertex(v.vector));
//		g.endShape();
//
//		for(int i = 0; i < _myHullWithDefects.size();i++) {
//			if(i % 2 == 0)g.color(1d,0,0);
//			else g.color(0d,1,0);
//			
//			if(i == 0)g.color(CCColor.WHITE);
//			
//			double myRadius = 2;
//			
//			CCVector3 myCurrent = _myHullWithDefects.get(i).vector;
//			
//			g.ellipse(myCurrent.x, myCurrent.y,0,myRadius,myRadius, false);
//		}
		
//		g.strokeWeight(3);
//		g.color(_cHullColor);
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		_myConvexHull.forEach(v -> g.vertex(v.vector));
//		g.endShape();
		
//		for(int i = 0; i < _myConvexHull.size();i++) {
//			g.color(1d,0,0);
//			
//			if(i == 0)g.color(CCColor.WHITE);
//			
//			double myRadius = 2;
//			
//			CCVector3 myCurrent = _myConvexHull.get(i).vector;
//			
//			g.ellipse(myCurrent.x, myCurrent.y,0,myRadius,myRadius, false);
//		}
		
//		for(int i = 0; i < _myFingerTips.size();i++) {
//			
//			if(i == 0)g.color(CCColor.CYAN, 0.5);
//			else g.color(CCColor.MAGENTA, 0.5);
//			
//			CCVector3 myTip = _myFingerTips.get(i);
//			double myRadius = myTip.z / 5;
//			
//			g.ellipse(myTip.xy(),myRadius,myRadius, false);
//		}
//		g.color(CCColor.createFromHSB(1/6d - CCMath.saturate(_myRestTime / _cMinRestTime) * 1/6d, 1, 1), 0.5);
//		
//		g.ellipse(_myTip,10,10, false);
//		g.ellipse(_myCenter,10,10, false);
		
		lock.unlock();
	}
	
	public void drawSelection(CCGraphics g) {
		if(!_myVideoIn.isActive())return;
		lock.lock();
		
		g.color(_cLineColor);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(double a = _cProgressAngle * _myProgress; a < _cProgressAngle;a++) {
			double myAngle = CCMath.radians(a) - _myStartAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
			g.vertex(CCVector2.circlePoint(myAngle, 20 - _cLineStrength / 2, _myTip.x, _myTip.y));
			g.vertex(CCVector2.circlePoint(myAngle, 20 + _cLineStrength / 2, _myTip.x, _myTip.y));
		}
		g.endShape();
		
		g.color(_cProgressColor);
		if(_myProgress>= 1)g.color(CCColor.CYAN);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(double a = 0; a < _cProgressAngle * _myProgress;a++) {
			double myAngle = CCMath.radians(a) - _myStartAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
			g.vertex(CCVector2.circlePoint(myAngle, 20 - _cProgressStrength / 2, _myTip.x, _myTip.y));
			g.vertex(CCVector2.circlePoint(myAngle, 20 + _cProgressStrength / 2, _myTip.x, _myTip.y));
		}
		g.endShape();
		g.color(1d);
		
		lock.unlock();
	}
	
	public Mat infoMat() {
		return _myDrawMat;
	}
	
	public CCTexture2D texture() {
		return _myTexture;
	}

	public void update(CCAnimator theAnimator) {
	
		// check if we found fingertipps
		if(_myFingerTips != null && _myFingerTips.size() > 0) {
			CCVector3 myCenter = new CCVector3();
			for(CCVector3 myVector:_myHandContour) {
				myCenter.addLocal(myVector);
			}
			myCenter.multiplyLocal(1d/_myHandContour.size());
//			_myCenter = myCenter;
			_myCenter.x = _myCenter.x * _cTipSmooth + myCenter.x * (1 - _cTipSmooth);
			_myCenter.y = _myCenter.y * _cTipSmooth + myCenter.y * (1 - _cTipSmooth);
			CCVector2 myNewTip = _myFingerTips.get(0).xy();
			if(_myTip.isZero()) {
				_myTip.set(myNewTip);
			}else {
				_myTip.x = _myTip.x * _cTipSmooth + myNewTip.x * (1 - _cTipSmooth);
				_myTip.y = _myTip.y * _cTipSmooth + myNewTip.y * (1 - _cTipSmooth);
				
				boolean myTipInRest = myNewTip.distance(_myTip) < _cRestSpeed;
//										if(myTipInRest && !_myTipInRest) {
				CCVector2 myDirection = _myTip.subtract(_myCenter).normalizeLocal();
				_myStartAngle = CCMath.atan2(-myDirection.y, myDirection.x);
//								}
				_myProgressCenter = _myTip;
				_myTipInRest = myTipInRest;
			}
		}else {
			reset();
		}
		if(_myTip.isZero())return;
		
		if(!_myVideoIn.isActive()) {
			reset();
			return;
		}
		
		double myLastRestTime = _myRestTime;
		if(_myTipInRest)_myRestTime += theAnimator.deltaTime();
		else _myRestTime = 0;
		
		_myProgress = CCMath.saturate(_myRestTime / _cMinRestTime);
		
		if(myLastRestTime < _cMinRestTime && _myRestTime > _cMinRestTime) {
			fixedTipEvents.proxy().event(_myTip);
		}
		
//		_myTip.x = _cTipFilter.process(0, _myTip.x, theAnimator.deltaTime());
//		_myTip.y = _cTipFilter.process(0, _myTip.y, theAnimator.deltaTime());
	}
}
