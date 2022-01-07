package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_core.flip;
import static org.bytedeco.javacpp.opencv_core.min;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.contourArea;
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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPolygon2;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCOneEuroFilter;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSimplify3D;
import cc.creativecomputing.opencv.filtering.CCAbsDifference;
import cc.creativecomputing.opencv.filtering.CCBackgroundSubtractorKNN;
import cc.creativecomputing.opencv.filtering.CCBackgroundSubtractorMOG2;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVExtract;
import cc.creativecomputing.opencv.filtering.CCCVResize;
import cc.creativecomputing.opencv.filtering.CCColorConversion;
import cc.creativecomputing.opencv.filtering.CCFlip;
import cc.creativecomputing.opencv.filtering.CCInRange;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCRotate90;
import cc.creativecomputing.opencv.filtering.CCThreshold;;

public class CCHandTracker {
	
	public static interface CCFixedTipEvent{
		public void event(CCVector2 theTip);
	}
	
	public static class CCHandInfo{
		public int id = -1;
		public double area;
		public List<CCVector3> handContour;
		public List<CCVector3> simpleContour;
		public List<CCVector3> fingerTips;
		public boolean isHand = false;
		public boolean isCorrelated = false;
		
		public CCVector2 tip = new CCVector2();
		public CCVector2 center = new CCVector2();
		public CCVector2 progressCenter = new CCVector2();
		private double startAngle = 0;
		private double jitter = 0;
		public int validFrames = 0;
		
		private double restFrames = 0;
		private double progress = 0;
		
		
		
		private List<CCVector3> copyList(List<CCVector3> theList){
			List<CCVector3> clone = new ArrayList<>();
			theList.forEach(v -> clone.add(v.clone()));
			return clone;
		}
		
		public void transform(CCTransform theTransform) {
			
		}
		
		public CCHandInfo clone() {
			CCHandInfo myResult = new CCHandInfo();
			myResult.id = id;
			myResult.area = area;
			myResult.handContour = copyList(handContour);
			myResult.simpleContour = copyList(simpleContour);
			myResult.fingerTips = copyList(fingerTips);
			myResult.isHand = isHand;
			myResult.isCorrelated = isCorrelated;
			
			myResult.tip = tip.clone();
			myResult.center = center.clone();
			myResult.progressCenter = progressCenter.clone();
			myResult.startAngle = startAngle;
			myResult.jitter = jitter;
			myResult.validFrames = validFrames;
			
			myResult.restFrames = restFrames;
			myResult.progress = progress;
			
			return myResult;
		}
	}
	
	@CCProperty(name = "pause")
	private boolean _cPause = false;
	
	@CCProperty(name = "mask")
	private boolean _cMask = true;
	
	@CCProperty(name = "mask flip")
	private CCFlip _cMaskFlip = new CCFlip();
	
	@CCProperty(name = "flip")
	private CCFlip _cFlip = new CCFlip();
	
	@CCProperty(name = "rotate")
	private CCRotate90 _cRotate = new CCRotate90();
	
	@CCProperty(name = "extact")
	private CCCVExtract _cExtract = new CCCVExtract();

	@CCProperty(name = "resize")
	private CCCVResize _cResize = new CCCVResize();
	
	@CCProperty(name = "blur")
	private CCBlur _cBlur = new CCBlur();
	
	@CCProperty(name = "color convert")
	private CCColorConversion _cConvert = new CCColorConversion();
	
	@CCProperty(name = "in range")
	private CCInRange _cInRange = new CCInRange();
	
	
	@CCProperty(name = "background knn")
	private CCBackgroundSubtractorKNN _cBackgroundKNN = new CCBackgroundSubtractorKNN();
	@CCProperty(name = "background mog2")
	private CCBackgroundSubtractorMOG2 _cBackgroundMog2 = new CCBackgroundSubtractorMOG2();
	
	@CCProperty(name = "morphology")
	private CCMorphologyFilter _cMorphology = new CCMorphologyFilter();
	@CCProperty(name = "abs difference")
	private CCAbsDifference _cAbsDifference = new CCAbsDifference();


	@CCProperty(name = "post blur")
	private CCBlur _cPostBlur = new CCBlur();

	@CCProperty(name = "threshold")
	private CCThreshold _cThreshold = new CCThreshold();
	
	@CCProperty(name = "max gap", min = 0, max = 100)
	private double _cMaxGap = 30;
	@CCProperty(name = "start end gap", min = 0, max = 100)
	private double _cStartEndDistance = 30;
	@CCProperty(name = "min mask distance", min = 0, max = 100)
	private double _cMinMaskDistance = 30;
	@CCProperty(name = "tip angle", min = 0, max = 100)
	private double _cTipAngle = 50;
	@CCProperty(name = "min length", min = 0, max = 500)
	private double _cMinLength = 30;
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
	@CCProperty(name = "min valid frames", min = 0, max = 100)
	private double _cMinValidFrames = 20;

	@CCProperty(name = "progress angle", min = 0, max = 360)
	private double _cProgressAngle = 0;

	@CCProperty(name = "selection radius", min = 0, max = 50)
	private double _cSelectionRadius = 20;
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
	
	private CCCVVideoIn _myVideoIn;
	
	private boolean _myIsInDebug = false;
	
	private boolean _myIsInConfig = false;
	
	private static enum CCDrawMat{
		INPUT,
		MASKED,
		EXTRACT,
		RESIZE,
		BLUR,
		ABS_DIFFERENCE,
		COLOR_CONVERSION,
		BACKGROUND,
		COLOR_RANGE,
		MORPHOLOGY,
		POST_BLUR,
		THRESHOLD
	}
	
	@CCProperty(name = "draw mat")
	private CCDrawMat _cDrawMat = CCDrawMat.THRESHOLD;
	
	private Mat _myDebugMat = null;
	private Mat _myInputMat = null;

	private CCCVTexture _myDebugTexture;
	private boolean _myUpdateDebugTexture;

	private CCCVTexture _myTexture;
	
	final Lock lock = new ReentrantLock();
	final Lock lockProcess = new ReentrantLock();
	final Lock lockDebug = new ReentrantLock();
	
	private List<CCHandInfo> _myTrackedObjects = new ArrayList<>();
	
	private Map<Integer, CCHandInfo> _myCVHandMap = new HashMap<>();
	private List<CCHandInfo> _myCVHands = new ArrayList<>();
	private List<CCHandInfo> _myHands = new ArrayList<>();
	
	private Queue<CCHandInfo> _myAddedHands = new LinkedList<>();
	private Queue<CCHandInfo> _myMovedHands = new LinkedList<>();
	private Queue<CCHandInfo> _myRemovedHands = new LinkedList<>();
	private int _myIDCounter;
	
	private CCTransform _myTransform = new CCTransform();
	
	private Mat _myMask;
	
	@CCProperty(name = "mask outline")
	private CCLinearSpline _myMaskSpline = new CCLinearSpline();
	
	private CCPolygon2 _myMaskPolygon = new CCPolygon2();
	
	public CCHandTracker(CCCVVideoIn theVideoIn, Path theMaskTexture) {
		_myVideoIn = theVideoIn;
		_myMask = new Mat();
		 flip(imread(theMaskTexture.toAbsolutePath().toString()), _myMask, 0);
		
		_myContours = new MatVector();
		
		_myVideoIn.events.add(this::updateVideo);
		
		_myDebugTexture = new CCCVTexture();
		_myDebugTexture.mustFlipVertically(false);
		
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(false);
		
//		_cProcessor = new Thread(this::processVideo);
//		_cProcessor.start();
	}
	
	public CCTransform transform() {
		return _myTransform;
	}
	
	public void isInDebug(boolean isInDebug) {
		_myIsInDebug = isInDebug;
	}
	
	public void isInConfig(boolean isInConfig) {
		_myIsInConfig = isInConfig;
	}
	
	public void updateDebugTexture(boolean theUpdateDebugTexture) {
		_myUpdateDebugTexture = theUpdateDebugTexture;
	}
	
	private boolean _myUpdateDifferenceMat = true;
	
	private Mat _myDifferenceMat;
	
	@CCProperty(name = "update difference mat")
	public void updateDifferenceMat(){
		_myUpdateDifferenceMat = true;
	}
	
	private void checkDebugMat(CCDrawMat theMode, Mat theMat) {
		if(!_myIsInDebug)return;
		if(_cDrawMat != theMode)return;
		
		if(!lockDebug.tryLock())return;
		_myDebugMat = theMat.clone();
		if(_myDebugMat.channels() == 1) {
			_myDebugMat = CCCVUtil.grayToRGB(theMat.clone());
		}
		lockDebug.unlock();
	}
	
	private Mat _myLastMat;
	
	private int activeThreads = 0;
	
	private class CCMatHandler implements Runnable{

		private Mat mat;
		
		public CCMatHandler(Mat theMat) {
			mat = theMat;
			mat = _cFlip.process(mat);
			mat = _cRotate.process(mat);
		}
		@Override
		public void run() {
			try {
				//if(activeThreads > 0)return;
				//CCLog.info(activeThreads);
				//activeThreads++;
				//long mills = System.currentTimeMillis();
				
				if(_cPause) {
					if(_myLastMat == null)_myLastMat = mat.clone();
					mat = _myLastMat.clone();
				}else {
					_myLastMat = mat.clone();
				}
				
				_myInputMat = mat.clone();
				//_myInputMat = _cFlip.process(_myInputMat);
				//_myInputMat = _cRotate.process(_myInputMat);
				checkDebugMat(CCDrawMat.INPUT, mat);

				if(_myIsInConfig) {
					_myDebugMat = mat.clone();
					activeThreads++;
					return;
				}
				
				if(_cMask) {
					if(_myMask.cols() == mat.cols() && _myMask.rows() == mat.rows()) {
						min(_cMaskFlip.process(_myMask.clone()), mat, mat); 
					}else {
						//throw new RuntimeException("Mask has to match the camera resolution of: " + mat.cols() + " , " + mat.rows());
					}
				}
				checkDebugMat(CCDrawMat.MASKED, mat);
				
				
				
				mat = _cExtract.process(mat);
				checkDebugMat(CCDrawMat.EXTRACT, mat);
				
				mat = _cResize.process(mat);
				checkDebugMat(CCDrawMat.RESIZE, mat);
				
				mat = _cBlur.process(mat);
				checkDebugMat(CCDrawMat.BLUR, mat);
				
				mat = _cConvert.process(mat);
				checkDebugMat(CCDrawMat.COLOR_CONVERSION, mat);
				
				if(_myUpdateDifferenceMat || _myDifferenceMat == null) {
					_myDifferenceMat = mat.clone();
					_myUpdateDifferenceMat = false;
				}
				mat = _cAbsDifference.process(mat, _myDifferenceMat);
				checkDebugMat(CCDrawMat.ABS_DIFFERENCE, mat);
				mat = _cBackgroundKNN.process(mat);
				mat = _cBackgroundMog2.process(mat);
				checkDebugMat(CCDrawMat.BACKGROUND, mat);
	
				mat = _cInRange.process(mat);
				checkDebugMat(CCDrawMat.COLOR_RANGE, mat);
				
				if(mat.channels() == 3) {
					mat = CCCVUtil.rgbToGray(mat);
				}
				
				mat = _cMorphology.process(mat);
				checkDebugMat(CCDrawMat.MORPHOLOGY, mat);
				
	
				mat = _cPostBlur.process(mat);
				checkDebugMat(CCDrawMat.POST_BLUR, mat);
				
				mat = _cThreshold.process(mat);
				checkDebugMat(CCDrawMat.THRESHOLD, mat);
				
				trackHands(mat);
				mat.close();
				//activeThreads--;
			//	CCLog.info("Tracking loop:",System.currentTimeMillis() - mills);
			}catch(Exception e) {
				//activeThreads--;
			}
		}
		
		
	}
	
	
	private void updateVideo(Mat theImage) {
//		if(!_myIsPostFirstFrame)return;
		new Thread(new CCMatHandler(theImage.clone())).start();
	}
	
	public void active(boolean isActive) {
		_myVideoIn.isActive(isActive);
	}
	
	private List<CCVector3> matToContour(Mat theMat){
		List<CCVector3> myResult = new ArrayList<>();
		for(int i = 0; i < theMat.rows();i++) {
			Mat myPoint = theMat.row(i);
			CCVector3 myVector = new CCVector3(
				myPoint.getIntBuffer().get(0),
				myPoint.getIntBuffer().get(1)
			);
			myVector = _myTransform.applyForward(myVector);
			myResult.add(myVector);
		}
		return myResult;
	}
	
	private CCHandInfo getHandInfo(Mat theContour) {
		CCHandInfo myResult = new CCHandInfo();
		double area = contourArea(theContour, false);
		if(area < _cMinSize)return myResult;
		//CCLog.info(area);
		
		List<CCVector3> handContour = matToContour(theContour);
		
		CCVector3 myCenter = new CCVector3();
		for(CCVector3 myVector:handContour) {
			myCenter.addLocal(myVector);
		}
		myCenter.multiplyLocal(1d/handContour.size());
		
		CCVector2 center = myCenter.xy();
		
		List<CCVector3> simpleContour = CCSimplify3D.simplify(handContour, _cMaxGap, true);
		
		if(simpleContour.size() > 3) {
			CCVector3 first = simpleContour.get(0);
			CCVector3 last = simpleContour.get(simpleContour.size() - 1);
			
			if(first.distance(last) < _cStartEndDistance)simpleContour.remove(0);
		}
		
		
		List<CCVector3> fingerTips = new ArrayList<>();
		
		CCVector3 minTip = null;
		double myMinAngle = 360;
		
		for(int i = 0; i < simpleContour.size();i++) {
			CCVector3 myPrev = simpleContour.get((i - 1 + simpleContour.size()) % simpleContour.size());
			CCVector3 myCurrent = simpleContour.get(i);
			CCVector3 myNext = simpleContour.get((i + 1) % simpleContour.size());
				
			double myAngle = -CCVector2.angle(myNext.subtract(myCurrent).xy(), myPrev.subtract(myCurrent).xy());
			double myLength = CCMath.max(myNext.distance(myCurrent), myPrev.distance(myCurrent));
			
			double myMaskDistance = _myMaskPolygon.distance(new CCVector2(myCurrent.x,myCurrent.y));
			myAngle = CCMath.degrees(myAngle);
			
			if(myAngle > 0 && myAngle < _cTipAngle && myLength > _cMinLength && myMaskDistance > _cMinMaskDistance) {
				fingerTips.add(new CCVector3(myCurrent.x,myCurrent.y,CCMath.abs(myAngle)));
			}
		}
		Collections.sort(fingerTips, (a,b) -> Double.compare(a.z, b.z));
		CCVector2 tip = new CCVector2();
		double startAngle = 0;
		if(fingerTips != null && fingerTips.size() > 0) {
			tip = fingerTips.get(0).xy();
			CCVector2 myDirection = tip.subtract(center).normalizeLocal();
			startAngle = CCMath.atan2(-myDirection.y, myDirection.x);
		}
		
		myResult.area = area;
		myResult.isHand = true;
		
		myResult.handContour = handContour;
		myResult.simpleContour = simpleContour;
		myResult.fingerTips = fingerTips;
		
		myResult.center = center;
		myResult.tip = tip;
		myResult.startAngle = startAngle;
		
		return myResult;
	}
	
	private Map<Double, List<CCVector2i>> createDistanceMap(final List<CCHandInfo> theNewHands, double theDistanceThreshold){
		Map<Double, List<CCVector2i>> myDistanceMap = new TreeMap<>();
		
		for (int myCursorID:_myCVHandMap.keySet()) {
			//computeIntensity(myCursorIt, myRawRaster);
			CCHandInfo myHand = _myCVHandMap.get(myCursorID);
			myHand.isCorrelated = false;
			for (int i = 0; i < theNewHands.size();i++) {
				double myDistance = myHand.center.distance(theNewHands.get(i).center);
				if (myDistance > theDistanceThreshold) continue;
				
				if(!myDistanceMap.containsKey(myDistance)) {
					myDistanceMap.put(myDistance, new ArrayList<CCVector2i>());
				}
				myDistanceMap.get(myDistance).add(new CCVector2i(i, myCursorID));
			}
		}
		
		return myDistanceMap;
	}
	
	private void updateHandInfo(CCHandInfo theLastHand, CCHandInfo theNewHand) {
		theLastHand.isCorrelated = true;
    	
		// update cursor with new position
		theLastHand.handContour = theNewHand.handContour;
		theLastHand.simpleContour = theNewHand.simpleContour;
		theLastHand.fingerTips = theNewHand.fingerTips;
		theLastHand.startAngle = theNewHand.startAngle;
			
		theLastHand.center.x = theNewHand.center.x * (1 - _cTipSmooth) + theLastHand.center.x * _cTipSmooth;
		theLastHand.center.y = theNewHand.center.y * (1 - _cTipSmooth) + theLastHand.center.y * _cTipSmooth;

		double myJitter = theNewHand.tip.distance(theLastHand.tip);
		
		if(theNewHand.tip.isZero()) {
//			CCLog.info(theNewHand.tip.isZero(),theLastHand.tip);
		//	return;
		}else {
			theLastHand.tip.x = theNewHand.tip.x * (1 - _cTipSmooth) + theLastHand.tip.x * _cTipSmooth;
			theLastHand.tip.y = theNewHand.tip.y * (1 - _cTipSmooth) + theLastHand.tip.y * _cTipSmooth;
		}
			
		theLastHand.jitter = myJitter * (1 - _cJitterSmooth) + theLastHand.jitter * _cJitterSmooth;
		theLastHand.validFrames++;
				
		double myLastRestTime = theLastHand.restFrames;
		if(theLastHand.jitter < _cMaxJitter && !theLastHand.tip.isZero())theLastHand.restFrames++;
		else {
			theLastHand.restFrames = 0;
		}
				
		theLastHand.progress = CCMath.saturate(theLastHand.restFrames / _cMinRestFrames);
				
		if(myLastRestTime < _cMinRestFrames && theLastHand.restFrames >= _cMinRestFrames) {
//			if(_myBoundingPolygon== null || _myBoundingPolygon.vertices().size() < 3 || _myBoundingPolygon.isInShape(theLastHand.tip)) {
				fixedTipEvents.proxy().event(theLastHand.tip);
//			}
		}
	}
	
	private void correlatePositions(final List<CCHandInfo> theNewHands ){

		// populate a map with all distances between existing cursors and new positions
		
		double myDistanceThreshold = 1000;
		Map<Double, List<CCVector2i>> myDistanceMap = createDistanceMap(theNewHands, myDistanceThreshold);        
		

		// will contain the correlated cursor id at index n for position n or -1 if uncorrelated
		List<Integer> myCorrelatedPositions = new ArrayList<Integer>();
		for(int i = 0; i < theNewHands.size();i++) {
			myCorrelatedPositions.add(-1);
		}

		// iterate through the distance map and correlate cursors in increasing distance order
		synchronized (_myMovedHands) {
			for (List<CCVector2i> myEntry:myDistanceMap.values()){
				for(CCVector2i myIndices:myEntry) {
					// check if we already have correlated one of our nodes
					int myHandIndex = myIndices.x;

					if (myCorrelatedPositions.get(myHandIndex) != -1)  continue;
					
					int myCursorId = myIndices.y;        
					CCHandInfo myLastHand = _myCVHandMap.get(myCursorId);
					CCHandInfo myNewHand = theNewHands.get(myHandIndex);
		    	    
					if (myLastHand.isCorrelated) continue;
					
					
					// correlate
					myCorrelatedPositions.set(myHandIndex,myCursorId);
					
					updateHandInfo(myLastHand, myNewHand);
					
					// post a move event
					_myMovedHands.add(myLastHand);
				}
			}
		}
		        

		// Now let us iterate through all new positions and create 
		//"cursor add" events for every uncorrelated position

		synchronized (_myAddedHands) {
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

					_myCVHandMap.put(myNewID, myHand);
					_myAddedHands.add(myHand);
				}
			}
		}
		        
		
		List<Integer> myIdsToRemove = new ArrayList<Integer>();
		        
		// Now let us iterate through all cursors and create 
		//"cursor remove" events for every uncorrelated cursors
		synchronized (_myRemovedHands) {
			for (Entry<Integer, CCHandInfo> myEntry:_myCVHandMap.entrySet()) {
				if (!myEntry.getValue().isCorrelated) {
					// cursor removed
					_myRemovedHands.add(myEntry.getValue());
					myIdsToRemove.add(myEntry.getKey());
				}
			}
		}
		
		for(int myID:myIdsToRemove) {
			_myCVHandMap.remove(myID);
		}
	}

	private void trackHands(Mat theMat) {
		Mat myGrayMat = theMat;
		if(myGrayMat.channels() == 3) {
			myGrayMat = CCCVUtil.rgbToGray(myGrayMat);
		}
//		flip(myGrayMat, myGrayMat, 0);
		findContours(myGrayMat, _myContours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
		
		// we need at least one contour to work
		
		/*
		if (_myContours.size() <= 0)
			return;
*/
		// find the biggest contour (let's suppose it's our hand)

		double myFingers = 10;
		double myAngle = 100;
		CCHandInfo myBestFitInfo = null;
		
		
		_myTrackedObjects.clear();
		for (int i = 0; i < _myContours.size(); i++) {
			CCHandInfo myInfo = getHandInfo(_myContours.get(i));
			if(myBestFitInfo == null) {
				myBestFitInfo = myInfo;
			}
			if(!myInfo.isHand)continue;
			_myTrackedObjects.add(myInfo);
			if(myInfo.fingerTips.size() <= 0)continue;
			
			if(myInfo.fingerTips.size()  < myFingers) {
				myFingers = myInfo.fingerTips.size();
				myBestFitInfo = myInfo;
			}else if(myInfo.fingerTips.size() == myFingers && myInfo.fingerTips.get(0).z < myAngle) {
				myBestFitInfo = myInfo;
				myAngle =  myInfo.fingerTips.get(0).z;
			}
		}
		correlatePositions(_myTrackedObjects);

		if(!lock.tryLock())return;
		_myCVHands.clear();
		_myCVHandMap.values().forEach(v -> _myCVHands.add(v.clone()));
		lock.unlock();
	}
	
	public List<CCHandInfo> hands(){
		return _myHands;
	}
	
	private boolean _myIsPostFirstFrame = false;
	
	public void update(CCAnimator theAnimator) {
		//CCLog.info("activethreads",activeThreads);
		_myIsPostFirstFrame = true;
		if(!_myVideoIn.isActive()) {
			return;
		}
		_myTransform.scale(1 / _cResize.scaleX(), 1 / _cResize.scaleY(), 1);
		_myMaskPolygon.vertices().clear();
		for(CCVector3 myVertex:_myMaskSpline) {
			_myMaskPolygon.addVertex(myVertex.x*_myTexture.width(), (1 - myVertex.y) * _myTexture.height());
		}
		if(!lock.tryLock())return;
		_myHands.clear();
		_myCVHands.forEach(v -> _myHands.add(v.clone()));
		lock.unlock();
	}
	
	public void preDisplay(CCGraphics g) {
		//_myBackgroundTexture.image(_myVideoIn.background());
		if(!_myVideoIn.isActive())return;
		if(!_myIsInDebug)return;
		if(!lockDebug.tryLock())return;
			if(_myDebugMat != null) {
				_myTexture.image(_myDebugMat);
			}
			//CCLog.info("PRE",_myInputMat);
		if(_myUpdateDebugTexture && _myInputMat != null) {
			_myDebugTexture.image(_myInputMat);
		}
		lockDebug.unlock();
	}
	
	@CCProperty(name = "draw contour")
	private boolean _cDrawContour = false;
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	
	private void drawContour(CCHandInfo myInfo, CCGraphics g) {
		if(!_cDrawContour)return;
		
		g.pushAttribute();
		g.strokeWeight(2);
		g.color(_cContourColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		myInfo.handContour.forEach(v -> g.vertex(v));
		g.endShape();
		g.popAttribute();
	}
	
	@CCProperty(name = "draw mask contour")
	private boolean _cDraMaskwContour = false;
	@CCProperty(name = "mask color color")
	private CCColor _cMaskContourColor = new CCColor();
	
	private void drawMaskContour(CCGraphics g) {
		if(!_cDraMaskwContour)return;
		
		g.pushAttribute();
		g.strokeWeight(2);
		g.color(_cMaskContourColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		_myMaskPolygon.vertices().forEach(v -> g.vertex(v));
		g.endShape();
		g.popAttribute();
	} 
	
	

	@CCProperty(name = "draw simple contour")
	private boolean _cDrawSimpleContour = false;
	@CCProperty(name = "simple contour color")
	private CCColor _cSimpleContourColor = CCColor.CYAN.clone();
	@CCProperty(name = "contour radius")
	private double _cContourRadius = 1;
	
	private void drawSimpleContour(CCHandInfo myInfo, CCGraphics g) {
		if(!_cDrawSimpleContour)return;
		
		g.pushAttribute();
		g.strokeWeight(2);
		g.color(_cSimpleContourColor);
		g.beginShape(CCDrawMode.LINE_LOOP);
		myInfo.simpleContour.forEach(v -> g.vertex(v));
		g.endShape();
		g.popAttribute();
		
		for(int i = 0; i < myInfo.simpleContour.size();i++) {		
			g.color(_cSimpleContourColor);
			if(i == 0)g.color(CCColor.WHITE);
	
			CCVector3 myCurrent = myInfo.simpleContour.get(i);		
			g.ellipse(myCurrent.x, myCurrent.y,0,_cContourRadius,_cContourRadius, false);
		}
	}
	
	@CCProperty(name = "draw finger tips")
	private boolean _cDrawFingerTip = false;
	@CCProperty(name = "tip radius")
	private double _cTipRadius = 10;
	
	private void drawFingerTips(CCHandInfo myInfo, CCGraphics g) {
		if(!_cDrawFingerTip)return;

		g.color(CCColor.MAGENTA, 0.5);
		
		myInfo.fingerTips.forEach(myTip -> g.ellipse(myTip.xy(),_cTipRadius,_cTipRadius, false));
		
		if(myInfo.tip.isZero())return;
		
		g.color(CCColor.CYAN, 0.5);
		g.ellipse(myInfo.tip,_cTipRadius,_cTipRadius, false);
	}
	

	@CCProperty(name = "draw center tip")
	private boolean _cDrawCenterTip = false;
	
	private void drawTipCenter(CCHandInfo myInfo, CCGraphics g) {
		if(!_cDrawCenterTip)return;
		g.color(CCColor.RED);
		g.ellipse(myInfo.tip,3,3, true);
		double r = CCMath.min(myInfo.validFrames,20) * 0.2;
		g.ellipse(myInfo.center,r,r, true);

		g.color(CCColor.RED);
		g.text(myInfo.id, myInfo.center.x,myInfo.center.y - 10		);
		
		g.line(myInfo.center, myInfo.tip);
	}
	

	@CCProperty(name = "draw jitter")
	private boolean _cDrawJitter = false;

	@CCProperty(name = "jitter scale")
	private double _cJitterScale = 5;
	
	private void drawJitter(CCHandInfo myInfo, CCGraphics g) {
		if(!_cDrawJitter)return;

		g.color(CCColor.GREEN);
		g.rect(myInfo.center.x,myInfo.center.y,myInfo.jitter * _cJitterScale,10);
		

		g.color(CCColor.WHITE);
		g.line(myInfo.center.x + _cMaxJitter * _cJitterScale,myInfo.center.y +10,
		myInfo.center.x + _cMaxJitter * _cJitterScale,myInfo.center.y);
		g.text((int)myInfo.jitter, myInfo.center.x + 2,myInfo.center.y + 2);
	}
	

	@CCProperty(name = "draw image")
	private boolean _cDrawImage = true;

	@CCProperty(name = "draw selection")
	private boolean _cDrawSelection = false;
	
//	@CCProperty(name = "spline")
//	private CCLinearSpline _cSpline = new CCLinearSpline();
//	
//	private CCPolygon2 _myBoundingPolygon = new CCPolygon2();
//	
//	@CCProperty(name = "draw spline")
//	private boolean _cDrawSpline = false;
//	
//	public void drawSpline(CCGraphics g) {
//		if(!_cDrawSpline)return;
//		
//		g.color(255);
//		g.beginShape(CCDrawMode.LINE_STRIP);
//		for(CCVector2 v:_myBoundingPolygon.vertices()) {
//			 g.vertex(v);
//		}
//		g.endShape();
//	}
	
	public void drawDebug(CCGraphics g) {
		if(!_myVideoIn.isActive())return;
	
		g.color(255);
		g.pushMatrix();
		switch(_cDrawMat) {
		case RESIZE:
		case COLOR_CONVERSION:
		case BACKGROUND:
		case COLOR_RANGE:
		case MORPHOLOGY:
		case BLUR:
		case POST_BLUR:
		case THRESHOLD:
			g.applyTransform(transform());
			break;
		}
		_myDebugTexture.mustFlipVertically(false);
		if(_cDrawImage)g.image(_myTexture, 0,0);
		g.popMatrix();
		
//		drawSpline(g);
		if(_myIsInConfig)return;
		for(CCHandInfo myInfo:_myHands) {
//			if(myInfo.validFrames < 20)continue;
			
			drawContour(myInfo,g);
			drawSimpleContour(myInfo, g);
			drawFingerTips(myInfo, g);
			drawTipCenter(myInfo, g);
			drawJitter(myInfo, g);
			
		}

		drawMaskContour(g);
		if(_cDrawSelection)drawSelection(g);
	}
	
	public void drawSelection(CCGraphics g) {
		if(!_myVideoIn.isActive())return;
		
		for(CCHandInfo myInfo:_myHands) {
			if(myInfo.tip.isZero())continue;
			if(myInfo.validFrames < _cMinValidFrames) {
				//CCLog.info(myInfo.validFrames);
				continue;
			}
			g.color(1d);
			
			CCVector2 myDirection = myInfo.tip.subtract(myInfo.center).normalizeLocal();
			double startAngle = CCMath.atan2(-myDirection.y, myDirection.x);
			
			g.color(_cLineColor);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(double a = _cProgressAngle * myInfo.progress; a < _cProgressAngle;a++) {
				double myAngle = CCMath.radians(a) - startAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
				g.vertex(CCVector2.circlePoint(myAngle, _cSelectionRadius - _cLineStrength / 2, myInfo.tip.x, myInfo.tip.y));
				g.vertex(CCVector2.circlePoint(myAngle, _cSelectionRadius + _cLineStrength / 2, myInfo.tip.x, myInfo.tip.y));
			}
			g.endShape();
			
			g.color(_cProgressColor);
			if(myInfo.progress>= 1)g.color(CCColor.CYAN);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(double a = 0; a < _cProgressAngle * myInfo.progress;a++) {
				double myAngle = CCMath.radians(a) - startAngle - CCMath.radians(_cProgressAngle / 2);// - CCMath.HALF_PI;
				g.vertex(CCVector2.circlePoint(myAngle, _cSelectionRadius - _cProgressStrength / 2, myInfo.tip.x, myInfo.tip.y));
				g.vertex(CCVector2.circlePoint(myAngle, _cSelectionRadius + _cProgressStrength / 2, myInfo.tip.x, myInfo.tip.y));
			}
			g.endShape();
		}
		g.color(1d);
	}
	
	public Mat infoMat() {
		return _myDebugMat;
	}
	
	public CCTexture2D debugTexture() {
		return _myDebugTexture;
	}
	
	public CCTexture2D texture() {
		return _myTexture;
	}
//	
//	public CCTexture2D backgroundTexture() {
//		return _myBackgroundTexture;
//	}

	

}
