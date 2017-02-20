package cc.creativecomputing.video;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.image.CCImage;

public class CCImageSequenceAsset extends CCAsset<CCImageSequence>{
	
	@CCProperty(name = "rate", min = 1, max = 120)
	private float _cRate = 5;
	
	@CCProperty(name = "speed", min = 0, max = 2)
	private float _cSpeed = 1;
	
	private CCImage _myFrame;
	
	private String[] _myExtensions;
	
	private CCAnimator _myAnimator;
	
	/**
	 * Keep the listeners for update events
	 */
	protected CCVideoTextureDataListener _myListener = null;
	
	public CCImageSequenceAsset(CCAnimator theAnimator){
		_myAsset = null;
		_myFrame = null;
		_myAnimator = theAnimator;
	}
	
	public void setListener(CCVideoTextureDataListener theListener) {
		_myListener = theListener;
	}
	
	@Override
	public CCImageSequence loadAsset(Path thePath){
		return new CCImageSequence(_myAnimator, thePath);
	}
	
	@Override
	public String[] extensions() {
		return _myExtensions;
	}
	
	public float rate(){
		return _cRate;
	}
	
	public CCImage frame(){
		return _myFrame;
	}
	
	public double duration(){
		if(_myAsset == null)return 0;
		return _myAsset.duration();
	}
	
	private double _myTime;
	
	private boolean _myIsFirstFrame = true;
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		_myTime = (theEventTime - theContentOffset) * _cSpeed;
		if(_myAsset == null)return;
		
		_myAsset.frameRate(_cRate);
		_myAsset.time(_myTime);
		_myFrame = _myAsset.currentImage();
		
		
		if(_myListener == null)return;
		
		if(_myIsFirstFrame){
			_myListener.onInit(_myFrame);
			_myIsFirstFrame = false;
		}else{
			_myListener.onUpdate(_myFrame);
		}
	}
	
	@Override
	public void renderTimedEvent(TimedEventPoint theEvent,Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		super.renderTimedEvent(theEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
		
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
//		CCSequence myData = null;
//		Path myFilePath = Paths.get(theEvent.content().value().toString());
//		if(_mySequenceMap.containsKey(myFilePath)){
//			myData = _mySequenceMap.get(myFilePath);
//		}else{
//			try{
//				
//				myData = loadAsset(myFilePath);
//				_mySequenceMap.put(myFilePath, myData);
//			}catch(Exception e){
//				
//			}
//		}
//		if(myData == null)return;
//		
//		
//		double myWidth = theUpper.getX() - theLower.getX();
//		double myHeight = theUpper.getY() - theLower.getY();
//		GeneralPath myPath = new GeneralPath();
//		myPath.moveTo(theLower.getX(), theLower.getY());
//		
//		double mySequenceLength = myData.length() / _cRate;
//		
//		for (int x = 0; x < myWidth - 1; x++) {
//			double myTime1 = CCMath.floorMod(CCMath.map(x, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset(), mySequenceLength);
//			int mySample1 = (int)CCMath.map(myTime1, 0, mySequenceLength, 0 ,myData.length());
//			double myTime2 = CCMath.floorMod(CCMath.map(x + 1, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset(), mySequenceLength);
//			int mySample2 = (int)CCMath.map(myTime2, 0, mySequenceLength, 0 ,myData.length());
//			
//			double value0 = Double.MAX_VALUE;
//			double value1 = -Double.MAX_VALUE;
//			
//			for(int j = mySample1;j <= mySample2;j++){
//				if(j >= myData.length() || j < 0)continue;
//				CCMatrix2 myFrame = myData.get(j);
//				CCVector2 myMinMax = myFrame.minMax(x % 2);
//				 value0 = CCMath.min(myMinMax.x, value0);
//				 value1 = CCMath.max(myMinMax.y, value1);
//			}
//			if(myTime2 < myTime1)theG2d.drawLine(x, 0, x, (int)(myHeight));
//			
//			myPath.moveTo(x + theLower.getX(),myHeight / 2 +  value0 * myHeight / 2);
//			myPath.lineTo(x + theLower.getX(),myHeight / 2 +  value1 * myHeight / 2);
//		}
//
//        theG2d.draw(myPath);
        
       
		
		
//		CCLog.info(lowerTime +  ":" + UpperTime +  ":" + theEvent.time() +  ":" + theEvent.endTime() + ":" + myData.player.length() / 1000d + ":" + myWidth );
//		double myLowerBound = CCMath.max(theEvent.time(), theView.context().lowerBound());
//    	double myUpperBound = CCMath.min(theEvent.endTime(), theView.context().upperBound());
    	
//    	Point2D p1 = theView.controller().curveToViewSpace(new ControlPoint(myLowerBound,_myController.value(0)));
//    	Point2D p2 = theView.controller().curveToViewSpace(new ControlPoint(myUpperBound,_myController.value(0)));
//        myPath.moveTo(theLower.getX(), theLower.getY());
//        myPath.lineTo(theUpper.getX(), theUpper.getY());
        
//        g.setColor(theView.fillColor());
	}
	
	@Override
	public void out() {
		_myAsset = null;
		_myFrame = null;
	}

	@Override
	public void play() {
	}

	@Override
	public void stop() {
	}
}
