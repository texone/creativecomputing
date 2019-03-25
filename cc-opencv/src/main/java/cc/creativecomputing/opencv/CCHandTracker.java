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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
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
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCOneEuroFilter;
import cc.creativecomputing.math.spline.CCSplineSimplify;
import cc.creativecomputing.math.spline.Simplify3D;
import cc.creativecomputing.math.spline.SimplifyP5;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;;

public class CCHandTracker {
	
	public static interface CCFixedTipEvent{
		public void event(CCVector2 theTip);
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
	@CCProperty(name = "jitter smooth", min = 0, max = 1)
	private double _cJitterSmooth = 0;
	
	@CCProperty(name = "min rest time", min = 0, max = 200)
	private double _cMinRestFrames = 30;
	@CCProperty(name = "min size")
	private double _cMinSize = 2;
	@CCProperty(name = "max jitter", min = 0, max = 100)
	private double _cMaxJitter = 30;
	@CCProperty(name = "max rest jitter", min = 0, max = 100)
	private double _cMaxRestJitter = 5;
	@CCProperty(name = "min valid frames", min = 0, max = 100)
	private double _cMinValidFrames = 20;
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
	
	
	
	
	private MatVector _myContours;
	
	
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

	
	public CCHandTracker(CCCVVideoIn theVideoIn, CCTexture2D theMaskTexture, Path theVertexHader, Path theFragmentShader) {
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
	
	public CCHandTracker(CCCVVideoIn theVideoIn, CCTexture2D theMaskTexture) {
		this(
			theVideoIn,
			theMaskTexture,
			CCNIOUtil.classPath(CCHandTracker.class, "cv_shader_vertex.glsl"),
			CCNIOUtil.classPath(CCHandTracker.class, "cv_shader_fragment.glsl")
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
	
	private class CCHandInfo{
		public int id = -1;
		public double area;
		public List<CCVector3> handContour;
		public List<CCVector3> simpleContour;
		public List<CCVector3> fingerTips;
		public boolean isHand = false;
		public boolean isCorrelated = false;
		
		private CCVector2 tip = new CCVector2();
		private CCVector2 center = new CCVector2();
		private CCVector2 progressCenter = new CCVector2();
		private double startAngle = 0;
		private double jitter = 0;
		private int validFrames = 0;
		
		private double restFrames = 0;
		private double progress = 0;
		
		public boolean isValid() {
			return fingerTips.size() <= 1 && jitter < 30;
//			if(myInfo.)continue;
//			if(myInfo.jitter > 30)continue;
//			if(myInfo.jitterFreeFrames < 30)continue;
		}
		
		public void track(Mat theContour) {
			area = contourArea(theContour, false);
			if(area < _cMinSize)return;
			
			isHand = true;
			handContour = matToContour(theContour);
			
			CCVector3 myCenter = new CCVector3();
			for(CCVector3 myVector:handContour) {
				myCenter.addLocal(myVector);
			}
			myCenter.multiplyLocal(1d/handContour.size());
			
			center = myCenter.xy();
			
			simpleContour = Simplify3D.simplify(handContour, _cMaxGap, true);
			if(simpleContour.size() > 4)simpleContour.remove(0);
			
			
			fingerTips = new ArrayList<>();
			double myMinAngle = CCMath.TWO_PI;
			for(int i = 0; i < simpleContour.size();i++) {
				CCVector3 myPrev = simpleContour.get((i - 1 + simpleContour.size()) % simpleContour.size());
				CCVector3 myCurrent = simpleContour.get(i);
				CCVector3 myNext = simpleContour.get((i + 1) % simpleContour.size());
					
				double myAngle = CCVector2.angle(myNext.subtract(myCurrent).xy(), myPrev.subtract(myCurrent).xy());
				myAngle = CCMath.degrees(myAngle);
				
//				CCLog.info(myAngle);
				if(myAngle > 0 && myAngle < _cTipAngle) {
					fingerTips.add(new CCVector3(myCurrent.x,myCurrent.y,CCMath.abs(myAngle)));//CCMath.max(myPrev.distance(myCurrent), myNext.distance(myCurrent))
				}
//				CCLog.info(myAngle);
			}
			Collections.sort(fingerTips, (a,b) -> Double.compare(a.z, b.z));
			if(fingerTips != null && fingerTips.size() > 0) {
				tip = fingerTips.get(0).xy();
				CCVector2 myDirection = tip.subtract(center).normalizeLocal();
				startAngle = CCMath.atan2(-myDirection.y, myDirection.x);
			}
			
			
		}
	}
	
	private CCHandInfo getHandInfo(Mat theContour) {
		CCHandInfo myResult = new CCHandInfo();
		myResult.track(theContour);
		return myResult;
	}
	
	private List<CCHandInfo> _myTrackedObjects = new ArrayList<>();
	
	private Map<Integer, CCHandInfo> _myTouchMap = new HashMap<>();
	
	private Queue<CCHandInfo> _myAddedTouches = new LinkedList<>();
	private Queue<CCHandInfo> _myMovedTouches = new LinkedList<>();
	private Queue<CCHandInfo> _myRemovedTouches = new LinkedList<>();
	private int _myIDCounter;
	
	private void correlatePositions(final List<CCHandInfo> theNewHands ){

		// populate a map with all distances between existing cursors and new positions
		Map<Double, List<CCVector2i>> myDistanceMap = new TreeMap<>();
		double myDistanceThreshold = 1000;
		        
		for (int myCursorID:_myTouchMap.keySet()) {
			//computeIntensity(myCursorIt, myRawRaster);
			CCHandInfo myTouch = _myTouchMap.get(myCursorID);
			myTouch.isCorrelated = false;
			for (int i = 0; i < theNewHands.size();i++) {
				double myDistance = myTouch.center.distance(theNewHands.get(i).center);
				if (myDistance < myDistanceThreshold) {
					List<CCVector2i> myList = myDistanceMap.get(myDistance);
					if(myList == null) {
						myList = new ArrayList<CCVector2i>();
						myDistanceMap.put(myDistance, myList);
					}
					myList.add(new CCVector2i(i, myCursorID));
				}
			}
		}

		// will contain the correlated cursor id at index n for position n or -1 if uncorrelated
		List<Integer> myCorrelatedPositions = new ArrayList<Integer>();
		for(int i = 0; i < theNewHands.size();i++) {
			myCorrelatedPositions.add(-1);
		}

		// iterate through the distance map and correlate cursors in increasing distance order
		synchronized (_myMovedTouches) {
			for (List<CCVector2i> myEntry:myDistanceMap.values()){
				for(CCVector2i myIndices:myEntry) {
					// check if we already have correlated one of our nodes
					int myHandIndex = myIndices.x;
					int myCursorId = myIndices.y;
		    	            
					CCHandInfo myLastHand = _myTouchMap.get(myCursorId);
					CCHandInfo myNewHand = theNewHands.get(myHandIndex);
		    	    
					if (myLastHand.isCorrelated) continue;
					
					if (myCorrelatedPositions.get(myHandIndex) == -1)  {
						// correlate
						myCorrelatedPositions.set(myHandIndex,myCursorId);
						myLastHand.isCorrelated = true;
		    	
						// update cursor with new position
						myLastHand.handContour = myNewHand.handContour;
						myLastHand.simpleContour = myNewHand.simpleContour;
						myLastHand.fingerTips = myNewHand.fingerTips;
						
						myLastHand.center.x = myNewHand.center.x * (1 - _cTipSmooth) + myLastHand.center.x * _cTipSmooth;
						myLastHand.center.y = myNewHand.center.y * (1 - _cTipSmooth) + myLastHand.center.y * _cTipSmooth;

						
						if(!myNewHand.tip.isZero()) {
							myLastHand.tip.x = myNewHand.tip.x * (1 - _cTipSmooth) + myLastHand.tip.x * _cTipSmooth;
							myLastHand.tip.y = myNewHand.tip.y * (1 - _cTipSmooth) + myLastHand.tip.y * _cTipSmooth;
							
							double myJitter = myNewHand.tip.distance(myLastHand.tip);
							myLastHand.jitter = myJitter * (1 - _cJitterSmooth) + myLastHand.jitter * _cJitterSmooth;
							myLastHand.validFrames++;
							if(!myNewHand.isValid())myLastHand.validFrames=0;
							
							double myLastRestTime = myLastHand.restFrames;
							if(myLastHand.jitter < _cMaxRestJitter)myLastHand.restFrames++;
							else myLastHand.restFrames = 0;
							
							myLastHand.progress = CCMath.saturate(myLastHand.restFrames / _cMinRestFrames);
							
							if(myLastRestTime < _cMinRestFrames && myLastHand.restFrames >= _cMinRestFrames) {
								fixedTipEvents.proxy().event(myLastHand.tip);
							}
						}
						myLastHand.startAngle = myNewHand.startAngle;

//						CCLog.info(myPosition, myTouch.center);
						// post a move event
						_myMovedTouches.add(myLastHand);
					}
					
				}
			}
		}
		        

		// Now let us iterate through all new positions and create 
		//"cursor add" events for every uncorrelated position

		synchronized (_myAddedTouches) {
			for (int i = 0; i < theNewHands.size(); ++i) {
				if (myCorrelatedPositions.get(i) == -1) {
					// new cursor
					int myNewID = _myIDCounter++;
					if (Double.isNaN(theNewHands.get(i).center.x)) {
						System.out.println("new cursor " + myNewID + " at " + theNewHands.get(i));
					}
					myCorrelatedPositions.set(i, myNewID);

					CCHandInfo myHand = theNewHands.get(i);
							
					myHand.id = myNewID;
					myHand.isCorrelated = true;

					_myTouchMap.put(myNewID, myHand);
					_myAddedTouches.add(myHand);
				}
			}
		}
		        
		
		List<Integer> myIdsToRemove = new ArrayList<Integer>();
		        
		// Now let us iterate through all cursors and create 
		//"cursor remove" events for every uncorrelated cursors
		synchronized (_myRemovedTouches) {
			for (Entry<Integer, CCHandInfo> myEntry:_myTouchMap.entrySet()) {
				if (!myEntry.getValue().isCorrelated) {
					// cursor removed
					_myRemovedTouches.add(myEntry.getValue());
					myIdsToRemove.add(myEntry.getKey());
				}
			}
		}
		
		for(int myID:myIdsToRemove) {
			_myTouchMap.remove(myID);
		}
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
		lock.lock();
		_myTrackedObjects.clear();
		for (int i = 0; i < _myContours.size(); i++) {
			CCHandInfo myInfo = getHandInfo(_myContours.get(i));
			if(myBestFitInfo == null) {
				myBestFitInfo = myInfo;
			}
			if(!myInfo.isHand)continue;
			_myTrackedObjects.add(myInfo);
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
		correlatePositions(_myTrackedObjects);
		
//		_myTouchMap.forEach((i,t) -> CCLog.info(t.id));
		// check if we found big enough contour
		if(myBestFitInfo == null) {
			reset();
			return;
		}
		
		lock.unlock();
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
	
		for(CCHandInfo myInfo:_myTouchMap.values()) {
//			if(myInfo.validFrames < 20)continue;
			g.color(_cContourColor);
			g.beginShape(CCDrawMode.LINE_LOOP);
			myInfo.handContour.forEach(v -> g.vertex(v));
			g.endShape();
			
			g.color(_cHullColor);
			g.beginShape(CCDrawMode.LINE_LOOP);
			myInfo.simpleContour.forEach(v -> g.vertex(v));
			g.endShape();
			
			g.pointSize(3);
			g.pointSmooth();
			g.beginShape(CCDrawMode.POINTS);
			myInfo.simpleContour.forEach(v -> g.vertex(v));
			g.endShape();
			
			double myRadius = 2;
			for(int i = 0; i < myInfo.simpleContour.size();i++) {		
				g.color(_cHullColor);
				if(i == 0)g.color(CCColor.WHITE);
		
				CCVector3 myCurrent = myInfo.simpleContour.get(i);		
				g.ellipse(myCurrent.x, myCurrent.y,0,myRadius,myRadius, false);
			}
	
			
			for(int i = 0; i < myInfo.fingerTips.size();i++) {
				
				if(i == 0)g.color(CCColor.CYAN, 0.5);
				else g.color(CCColor.MAGENTA, 0.5);
				
				CCVector3 myTip = myInfo.fingerTips.get(i);
				myRadius = 10;
				
				g.ellipse(myTip.xy(),myRadius,myRadius, false);
			}
	//		g.color(CCColor.createFromHSB(1/6d - CCMath.saturate(_myRestTime / _cMinRestTime) * 1/6d, 1, 1), 0.5);
	//		
			g.ellipse(myInfo.tip,10,10, false);
			
			double r = CCMath.min(myInfo.validFrames,20);
			g.ellipse(myInfo.center,r,r, false);
			
			g.color(CCColor.RED);
			g.text(myInfo.id, myInfo.center);
		}
		lock.unlock();
	}
	
	public void drawSelection(CCGraphics g) {
		if(!_myVideoIn.isActive())return;
		lock.lock();
		
		for(CCHandInfo myInfo:_myTouchMap.values()) {
			if(myInfo.validFrames < _cMinValidFrames)continue;
			g.color(1d);
			
			CCVector2 myDirection = myInfo.tip.subtract(myInfo.center).normalizeLocal();
			double startAngle = CCMath.atan2(-myDirection.y, myDirection.x);
			
			g.color(_cLineColor);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(double a = _cProgressAngle * myInfo.progress; a < _cProgressAngle;a++) {
				double myAngle = CCMath.radians(a) - startAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
				g.vertex(CCVector2.circlePoint(myAngle, 20 - _cLineStrength / 2, myInfo.tip.x, myInfo.tip.y));
				g.vertex(CCVector2.circlePoint(myAngle, 20 + _cLineStrength / 2, myInfo.tip.x, myInfo.tip.y));
			}
			g.endShape();
			
			g.color(_cProgressColor);
			if(myInfo.progress>= 1)g.color(CCColor.CYAN);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(double a = 0; a < _cProgressAngle * myInfo.progress;a++) {
				double myAngle = CCMath.radians(a) - startAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
				g.vertex(CCVector2.circlePoint(myAngle, 20 - _cProgressStrength / 2, myInfo.tip.x, myInfo.tip.y));
				g.vertex(CCVector2.circlePoint(myAngle, 20 + _cProgressStrength / 2, myInfo.tip.x, myInfo.tip.y));
			}
			g.endShape();
		}
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
	
		
//		if(_myTip.isZero())return;
		
		if(!_myVideoIn.isActive()) {
			reset();
			return;
		}
//		lock.lock();
//		lock.unlock();
//		double myLastRestTime = _myRestTime;
//		if(_myTipInRest)_myRestTime += theAnimator.deltaTime();
//		else _myRestTime = 0;
//		
//		_myProgress = CCMath.saturate(_myRestTime / _cMinRestTime);
//		
//		if(myLastRestTime < _cMinRestTime && _myRestTime > _cMinRestTime) {
//			fixedTipEvents.proxy().event(_myTip);
//		}
	}
}
