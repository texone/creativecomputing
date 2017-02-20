package cc.creativecomputing.video;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCVideoAsset extends CCAsset<CCFFMPGMovie>{
	
	@CCProperty(name = "min time offset", min = 0.01, max = 1)
	private float _cMaxTimeOffset = 0.05f;
	
	private CCAnimator _myAnimator;
	
	public CCVideoAsset(CCAnimator theAnimator){
		_myAsset = null;
		_myAnimator = theAnimator;
	}
	
	@Override
	public CCFFMPGMovie loadAsset(Path thePath) {
		return new CCFFMPGMovie(_myAnimator, thePath) ;
	}
	
	private static final int fftSize = 512;
	
	private boolean _myIsPlaying = false;
	
	private double _myPlayTime = 0;
	
	
	@Override
	public void mute(boolean theMute) {
		
	}
	
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		_myPlayTime = (int)((theEventTime - theContentOffset));
		if(_myAsset == null)return;
		if(!_myIsPlaying)return;
		
		if(_myPlayTime < 0 || _myPlayTime > _myAsset.duration()){
			if(_myAsset.isRunning()){
				_myAsset.pause();
			}
			return;
		}
		if(!_myAsset.isRunning()){
			_myAsset.time(_myPlayTime);
		}
		double myOffset = CCMath.abs(_myAsset.time() - _myPlayTime);
		if(myOffset > _cMaxTimeOffset){
			_myAsset.time(_myPlayTime);
		}
	}
	
	
	@Override
	public void renderTimedEvent(TimedEventPoint theEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
		
	}
	
	@Override
	public void out() {
		if(_myAsset == null)return;
		_myAsset.pause();
	}

	@Override
	public void play() {
		_myIsPlaying = true;
	}

	@Override
	public void stop() {
		_myIsPlaying = false;
		if(_myAsset == null)return;
		_myAsset.pause();
	}
}
