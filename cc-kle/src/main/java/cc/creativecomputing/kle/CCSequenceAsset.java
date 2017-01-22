package cc.creativecomputing.kle;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.formats.CCSequenceIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCSequenceAsset extends CCAsset<CCSequence>{
	
	
	private Map<Path, CCSequence> _mySequenceMap = new HashMap<>();
	
	@CCProperty(name = "rate", min = 1, max = 120)
	private float _cRate = 5;
	
	@CCProperty(name = "speed", min = 0, max = 2)
	private float _cSpeed = 1;
	
	private CCMatrix2 _myFrame;
	
	private final CCSequenceMapping<?> _myMapping;
	
	private String[] _myExtensions;
	
	public CCSequenceAsset(CCSequenceMapping<?> theMapping, String...theExtensions){
		_myAsset = null;
		_myFrame = null;
		_myMapping = theMapping;
		_myExtensions = theExtensions;
	}
	
	private CCSequence loadAsset(Path thePath){
		return CCSequenceIO.load(thePath, _myMapping);
	}
	
	@Override
	public String[] extensions() {
		return _myExtensions;
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			_myAsset = null;
			return;
		}
		if(_mySequenceMap.containsKey(thePath)){
			_myAsset = _mySequenceMap.get(thePath);
			return;
		}else{
			_myAsset = loadAsset(thePath);
			_mySequenceMap.put(thePath, _myAsset);
		}
	}
	
	public float rate(){
		return _cRate;
	}
	
	public CCMatrix2 frame(){
		return _myFrame;
	}
	
	public void frame(float theFrame){
		if(_myAsset == null)return;
		float myFrame = theFrame % _myAsset.length();
		_myFrame = _myAsset.frame(myFrame);
	}
	
	public double value(CCInterpolators theInterpolator, double theOffset, int theColumn, int theRow, int theDepth){
		if(_myAsset == null)return 0;
		double myFrame = CCMath.floorMod(((_myTime + theOffset) * _cRate), _myAsset.length());
		return _myAsset.value(theInterpolator, myFrame, theColumn, theRow, theDepth);
	}
	
	public double length(){
		if(_myAsset == null)return 0;
		return _myAsset.length() / _cRate;
	}
	
	public void frame(int theFrame){
		if(_myAsset == null)return;
		int myFrame = theFrame % _myAsset.length();
		_myFrame = _myAsset.frame(myFrame);
	}
	
	private double _myTime;
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		if(_myAsset == null)return;
		
		_myTime = (theEventTime - theContentOffset) * _cSpeed;
		double myFrame = CCMath.floorMod(_myTime * _cRate, _myAsset.length());
		_myFrame = _myAsset.frame(myFrame);
	}
	
	@Override
	public void renderTimedEvent(TimedEventPoint theEvent,Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		super.renderTimedEvent(theEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
		
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
		CCSequence myData = null;
		Path myFilePath = Paths.get(theEvent.content().value().toString());
		if(_mySequenceMap.containsKey(myFilePath)){
			myData = _mySequenceMap.get(myFilePath);
		}else{
			try{
				
				myData = loadAsset(myFilePath);
				_mySequenceMap.put(myFilePath, myData);
			}catch(Exception e){
				
			}
		}
		if(myData == null)return;
		
		
		double myWidth = theUpper.getX() - theLower.getX();
		double myHeight = theUpper.getY() - theLower.getY();
		GeneralPath myPath = new GeneralPath();
		myPath.moveTo(theLower.getX(), theLower.getY());
		
		double mySequenceLength = myData.length() / _cRate;
		
		for (int x = 0; x < myWidth - 1; x++) {
			double myTime1 = CCMath.floorMod(CCMath.map(x, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset(), mySequenceLength);
			int mySample1 = (int)CCMath.map(myTime1, 0, mySequenceLength, 0 ,myData.length());
			double myTime2 = CCMath.floorMod(CCMath.map(x + 1, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset(), mySequenceLength);
			int mySample2 = (int)CCMath.map(myTime2, 0, mySequenceLength, 0 ,myData.length());
			
			double value0 = Double.MAX_VALUE;
			double value1 = -Double.MAX_VALUE;
			
			for(int j = mySample1;j <= mySample2;j++){
				if(j >= myData.length() || j < 0)continue;
				CCMatrix2 myFrame = myData.get(j);
				CCVector2 myMinMax = myFrame.minMax(x % 2);
				 value0 = CCMath.min(myMinMax.x, value0);
				 value1 = CCMath.max(myMinMax.y, value1);
			}
			if(myTime2 < myTime1)theG2d.drawLine(x, 0, x, (int)(myHeight));
			
			myPath.moveTo(x + theLower.getX(),myHeight / 2 +  value0 * myHeight / 2);
			myPath.lineTo(x + theLower.getX(),myHeight / 2 +  value1 * myHeight / 2);
		}

        theG2d.draw(myPath);
        
       
		
		
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
//		_myAsset = null;
		_myFrame = null;
	}

	@Override
	public void play() {
	}

	@Override
	public void stop() {
	}
}
