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
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;;

public class CCHandTracker {
	
	public static interface CCFixedTipEvent{
		public void event(CCVector2 theTip);
	}
	
	public static class CCVectorAndID{
		public final CCVector2 vector;
		public final int id;
		public final boolean isDefect;
		
		public CCVectorAndID(CCVector2 theVector, int theID, boolean theIsDefect) {
			vector = theVector;
			id = theID;
			isDefect = theIsDefect;
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
	private Mat _myHandContourMat;
	
	private List<CCVector2> _myHandContour = new ArrayList<>();
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

	
	public CCHandTracker(CCCVVideoIn theVideoIn, Path theVertexHader, Path theFragmentShader) {
		_myContours = new MatVector();
		_myHandContourMat = new Mat();
		
		_cFilter = new CCCVShaderFilter(theVertexHader, theFragmentShader);
		
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
	}
	
	public void active(boolean isActive) {
		_myVideoIn.isActive(isActive);
	}
	
	public void reset() {
		_myHandContour.clear();
		_myHullWithDefects.clear();
		_myFingerTips.clear();
		
		if(!_myTip.isZero()) {
			_myTip.set(0,0);
		}
		_myCenter.set(0,0);
	}
	
	public CCHandTracker(CCCVVideoIn theVideoIn) {
		this(
			theVideoIn,
			CCNIOUtil.classPath(CCHandTracker.class, "cv_shader_vertex.glsl"),
			CCNIOUtil.classPath(CCHandTracker.class, "cv_shader_fragment.glsl")
		);
	}
	
	public void preDisplay(CCGraphics g) {
		if(_myDrawMat == null)return;
		lock.lock();
		_cFilter.preDisplay(g);
		_myTexture.image(_myDrawMat);
		lock.unlock();
		
	}
	
	private List<CCVector2> matToContour(Mat theMat){
		List<CCVector2> myResult = new ArrayList<>();
		for(int i = 0; i < theMat.rows();i++) {
			Mat myPoint = theMat.row(i);
			myResult.add(new CCVector2(myPoint.getIntBuffer().get(0),_myVideoIn.frameHeight() - myPoint.getIntBuffer().get(1)));
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
		
		// check if we found big enough contour
		if(myBiggestArea < _cMinSize || _myBiggestContourIndex < 0) {
			reset();
			return;
		}
		
		_myHandContourMat = _myContours.get(_myBiggestContourIndex);
			
		List<CCVector2> myHandContour = matToContour(_myHandContourMat);
			
		Mat myHullPointsMat = new Mat();
		Mat myHullIndicesMat = new Mat();
	
		convexHull(_myHandContourMat, myHullPointsMat, false, true);
		convexHull(_myHandContourMat, myHullIndicesMat, false, false);
			
		_myConvexHull.clear();
		for(int i = 0; i < myHullIndicesMat.rows();i++) {
			Mat myRow = myHullIndicesMat.row(i);
			int myID = myRow.getIntBuffer().get(0);
			
			_myConvexHull.add(new CCVectorAndID(myHandContour.get(myID), myID, false));
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
	

		List<CCVectorAndID> myHullWithDefects = new ArrayList<>();
		Collections.sort(myDefects);
		int myLastStart = 0;
		for(int i = 0; i < myDefects.size();i++) {
			CCConvexityDefect myDefect = myDefects.get(i);
			CCConvexityDefect myNextDefect = myDefects.get((i + 1) % myDefects.size());
			if(myHandContour.get(myDefect.start).distance(myHandContour.get(myDefect.end))< _cMaxGap)continue;
//			for(int j = myLastStart; j < myDefect.start;j++) {
//				for(CCVectorAndID myHullPoint : _myConvexHull) {
//					
//				}
//			}
//			myLastStart = myDefect.start;
			myHullWithDefects.add(new CCVectorAndID(myHandContour.get(myDefect.center), myDefect.center, true));
			myHullWithDefects.add(new CCVectorAndID(myHandContour.get(myDefect.end) , myDefect.end, false));
	
		}

		List<CCVector3> myFingerTips = new ArrayList<>();
		
		CCVector2 myCenter = new CCVector2();
		double centerCount = 0;
		for(int i = 0; i < myHullWithDefects.size();i++) {
				
			if(i % 2 == 1) {
				CCVector2 myPrev = myHullWithDefects.get(i - 1).vector;
				CCVector2 myCurrent = myHullWithDefects.get(i).vector;
				CCVector2 myNext = myHullWithDefects.get((i + 1) % myHullWithDefects.size()).vector;
					
				double myAngle = CCVector2.angle(myNext.subtract(myCurrent), myPrev.subtract(myCurrent));
				myAngle = CCMath.degrees(myAngle);
				centerCount++;
				if(CCMath.abs(myAngle) < 50) {
					myFingerTips.add(new CCVector3(myCurrent.x,myCurrent.y,CCMath.max(myPrev.distance(myCurrent), myNext.distance(myCurrent))));
				}
				myCenter.addLocal(myCurrent);
			}
		}
		myCenter.multiplyLocal(1d/centerCount);
		Collections.sort(myFingerTips, (a,b) -> Double.compare(b.z, a.z));
		
		

		lock.lock();
		_myHandContour = myHandContour;
		_myHullWithDefects = myHullWithDefects;
		_myFingerTips = myFingerTips;
		_myCenter = myCenter;
		
		// check if we found fingertipps
		if(myFingerTips.size() > 0) {
			CCVector2 myNewTip = myFingerTips.get(0).xy();
			if(_myTip.isZero()) {
				_myTip.set(myNewTip);
			}else {
				_myTip.x = _myTip.x * _cTipSmooth + myNewTip.x * (1 - _cTipSmooth);
				_myTip.y = _myTip.y * _cTipSmooth + myNewTip.y * (1 - _cTipSmooth);
						
				boolean myTipInRest = myNewTip.distance(_myTip) < _cRestSpeed;
//							if(myTipInRest && !_myTipInRest) {
				CCVector2 myDirection = _myTip.subtract(_myCenter).normalizeLocal();
				_myStartAngle = CCMath.atan2(-myDirection.y, myDirection.x);
//						}
				_myProgressCenter = _myTip;
				_myTipInRest = myTipInRest;
			}
		}else {
			reset();
		}
		lock.unlock();
	}
	
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
		lock.lock();
		g.color(_cContourColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		_myHandContour.forEach(v -> g.vertex(v));
		g.endShape();
		
		g.strokeWeight(5);
		g.color(_cHullColor);
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		_myHullWithDefects.forEach(v -> g.vertex(v.vector));
		g.endShape();
		
		g.pointSize(10);
		g.pointSmooth();
		g.strokeWeight(2);
		

		for(int i = 0; i < _myHullWithDefects.size();i++) {
			if(i % 2 == 0)g.color(1d,0,0);
			else g.color(0d,1,0);
			
			if(i == 0)g.color(CCColor.CYAN);
			
			double myRadius = 2;
			
			CCVector2 myCurrent = _myHullWithDefects.get(i).vector;
			
			g.ellipse(myCurrent.x, myCurrent.y,0,myRadius,myRadius, false);
		}
		
		for(int i = 0; i < _myFingerTips.size();i++) {
			
			if(i == 0)g.color(CCColor.CYAN, 0.5);
			else g.color(CCColor.MAGENTA, 0.5);
			
			CCVector3 myTip = _myFingerTips.get(i);
			double myRadius = myTip.z / 5;
			
			g.ellipse(myTip.xy(),myRadius,myRadius, false);
		}
		g.color(CCColor.createFromHSB(1/6d - CCMath.saturate(_myRestTime / _cMinRestTime) * 1/6d, 1, 1), 0.5);
		
		g.ellipse(_myTip,10,10, false);
		g.ellipse(_myCenter,10,10, false);
		
		lock.unlock();
	}
	
	public void drawSelection(CCGraphics g) {
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
