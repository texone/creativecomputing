package cc.creativecomputing.video;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCMath;

public class CCImageSequenceAsset extends CCAsset<CCImageSequence>{
	
	@CCProperty(name = "rate", min = 1, max = 120)
	private float _cRate = 5;
	
	@CCProperty(name = "speed", min = 0, max = 2)
	private float _cSpeed = 1;
	
	@CCProperty(name = "play")
	private boolean _cPlay = false;
	
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
	
	public void update(CCAnimator theAnimator) {
		if(!_cPlay)return;
		
		if(_myAsset == null)return;

		_myTime = theAnimator.time() % _myAsset.duration() * _cSpeed;
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
	public void renderTimedEvent(CCTimedEventPoint theEvent,Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		super.renderTimedEvent(theEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
		
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
		CCImageSequence myData = null;
		Path myFilePath = Paths.get(theEvent.content().value().toString());
		if(_myAssetMap.containsKey(myFilePath)){
			myData = _myAssetMap.get(myFilePath);
		}else{
			try{
				myData = loadAsset(myFilePath);
				_myAssetMap.put(myFilePath, myData);
			}catch(Exception e){
				
			}
		}
		if(myData == null)return;
	
		double myWidth = theUpper.getX() - theLower.getX();
		double myHeight = theUpper.getY() - theLower.getY();
//		
		double mySequenceLength = myData.duration();
		
		double mySequenceWidth = CCMath.map(mySequenceLength, 0, UpperTime - lowerTime, 0, myWidth);
		
		double myFrameWidth = CCMath.map(1 / _cRate, 0, UpperTime - lowerTime, 0, myWidth);
		
		CCLog.info(mySequenceWidth + " : " + myWidth + ":" + theLower.getX()+":"+mySequenceLength);
		Stroke myStroke = theG2d.getStroke();
		theG2d.setStroke(new BasicStroke(2));
		for (double x = 0; x < myWidth; x+=mySequenceWidth) {
			theG2d.drawLine((int)(theLower.getX() + x), 0,  (int)(theLower.getX() + x), (int)(myHeight));
		}
		theG2d.setStroke(myStroke);
		
		if(myFrameWidth < 2)return;
		for (double x = 0; x < myWidth; x+=myFrameWidth) {
			theG2d.drawLine((int)(theLower.getX() + x), 0,  (int)(theLower.getX() + x), (int)(myHeight));
		}
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
