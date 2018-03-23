package cc.creativecomputing.video;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCVideoAsset extends CCAsset<CCFFMPGMovie>{
	
	@CCProperty(name = "min time offset", min = 0.01, max = 1)
	private float _cMaxTimeOffset = 0.05f;
	
	private CCAnimator _myAnimator;
	

	/**
	 * Keep the listeners for update events
	 */
	protected CCVideoTextureDataListener _myListener = null;
	
	public CCVideoAsset(CCAnimator theAnimator){
		_myAsset = null;
		_myAnimator = theAnimator;
	}
	
	@Override
	public CCFFMPGMovie loadAsset(Path thePath) {
		return new CCFFMPGMovie(_myAnimator, thePath) ;
	}
	
	public void setListener(CCVideoTextureDataListener theListener) {
		_myListener = theListener;
	}
	
	private double _myPlayTime = 0;
	
	private boolean _myIsFirstFrame = true;
	
	
	@Override
	public void mute(boolean theMute) {
		
	}
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		if(_myAsset == null)return;
		_myPlayTime = (theEventTime - theContentOffset) % _myAsset.duration();
		
		if(_myPlayTime < 0){
			if(_myAsset.isRunning()){
				_myAsset.pause();
			}
			return;
		}
		if(!_myAsset.isRunning()){
			_myAsset.time(_myPlayTime);
			_myAsset.play();
		}
		double myOffset = CCMath.abs(_myAsset.time() - _myPlayTime);
		if(myOffset > _cMaxTimeOffset){
			_myAsset.time(_myPlayTime);
		}
		
		if(_myListener == null)return;
		
		if(_myIsFirstFrame){
			_myListener.onInit(_myAsset);
			_myIsFirstFrame = false;
		}else{
			_myListener.onUpdate(_myAsset);
		}
	}
	
	
	@Override
	public void renderTimedEvent(CCTimedEventPoint theEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		super.renderTimedEvent(theEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
		
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
		CCFFMPGMovie myData = null;
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
		
		double myFrameWidth = CCMath.map(1 / myData.frameRate(), 0, UpperTime - lowerTime, 0, myWidth);
		
		CCLog.info(mySequenceWidth + " : " + myWidth + ":" + theLower.getX()+":"+mySequenceLength + ":" + myData.frameRate() + " : " + myFrameWidth);
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
		if(_myAsset == null)return;
		_myAsset.pause();
	}

	@Override
	public void play() {
	}

	@Override
	public void stop() {
		if(_myAsset == null)return;
		_myAsset.pause();
	}
}
