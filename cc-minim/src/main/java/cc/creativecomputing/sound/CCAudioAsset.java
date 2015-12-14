package cc.creativecomputing.sound;

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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;


public class CCAudioAsset extends CCAsset<CCAudioAssetData>{
	
	private Map<Path, CCAudioAssetData> _myPlayerMap = new HashMap<>();
	
	@CCProperty(name = "min time offset", min = 0.01, max = 1)
	private float _cMaxTimeOffset = 0.05f;
	
	@CCProperty(name = "volume", min = 0, max = 1)
	private float _cVolume = 1;

	@CCProperty(name = "pan", min = -1, max = 1)
	private float _cPan = 0;
	
	public CCAudioAsset(){
		_myAsset = null;
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			if(_myAsset != null)_myAsset.player.pause();
			_myAsset = null;
			return;
		}
		if(_myPlayerMap.containsKey(thePath)){
			_myAsset = _myPlayerMap.get(thePath);
			return;
		}else{
			try{
				_myAsset = new CCAudioAssetData(CCSoundIO.loadFile(thePath, 2048), CCSoundIO.loadSample(thePath).getChannel(0)) ;
				_myPlayerMap.put(thePath, _myAsset);
			}catch(Exception e){
				_myAsset = null;
			}
		}
	}
	
	private boolean _myIsPlaying = false;
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		int myPlayTimeMillis = (int)((theEventTime - theContentOffset) * 1000);
		if(_myAsset == null)return;
		if(!_myIsPlaying)return;
		
		if(myPlayTimeMillis < 0 || myPlayTimeMillis > _myAsset.player.length()){
			if(_myAsset.player.isPlaying()){
				_myAsset.player.pause();
			}
			return;
		}
		if(!_myAsset.player.isPlaying())_myAsset.player.play(myPlayTimeMillis);
//		_myAsset.setGain(_cVolume);
		_myAsset.player.setBalance(_cPan);
		int myOffset = CCMath.abs(_myAsset.player.position() - myPlayTimeMillis);
		if(myOffset > _cMaxTimeOffset * 1000){
			_myAsset.player.skip(-(_myAsset.player.position() - myPlayTimeMillis));
		}
	}
	
	
	
	@Override
	public void renderTimedEvent(TimedEventPoint theEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		if(theEvent.content() == null || theEvent.content().value() == null)return;
		
		CCAudioAssetData myAudioData = null;
		Path myFilePath = Paths.get(theEvent.content().value().toString());
		if(_myPlayerMap.containsKey(myFilePath)){
			myAudioData = _myPlayerMap.get(myFilePath);
		}else{
			CCLog.info("RELOAD");
			try{
				
				myAudioData = new CCAudioAssetData(CCSoundIO.loadFile(myFilePath, 2048), CCSoundIO.loadSample(myFilePath).getChannel(0)) ;
				_myPlayerMap.put(myFilePath, myAudioData);
			}catch(Exception e){
				
			}
		}
		if(myAudioData == null)return;
		if(myAudioData.data == null)return;
		
		
		double myWidth = theUpper.getX() - theLower.getX();
		double myHeight = theUpper.getY() - theLower.getY();
		GeneralPath myPath = new GeneralPath();
		myPath.moveTo(theLower.getX(), theLower.getY());
		for (int x = 0; x < myWidth - 1; x++) {
			double myTime1 = CCMath.map(x, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset();
			int mySample1 = (int)CCMath.map(myTime1, 0, myAudioData.player.length() / 1000d, 0 ,myAudioData.data.length);
			double myTime2 = CCMath.map(x + 1, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset();
			int mySample2 = (int)CCMath.map(myTime2, 0, myAudioData.player.length() / 1000d, 0 ,myAudioData.data.length);
			
			float value0 = 0;
			float value1 = 0;
			for(int j = mySample1;j<mySample2;j++){
				if(j >= myAudioData.data.length || j < 0)continue;
				 value0 = CCMath.max(myAudioData.data[j], value0);
				 value1 = CCMath.min(myAudioData.data[j], value1);
			}
			
//			g.line(i / _cScale, 50 - song.getChannel(0)[i] * 50, i / _cScale + 1, 50 - song.getChannel(0)[i + _cScale] * 10);
			myPath.moveTo(x + theLower.getX(),myHeight / 2 +  value0 * myHeight / 2);
			myPath.lineTo(x + theLower.getX(),myHeight / 2 +  value1 * myHeight / 2);
		}
		
		CCLog.info(lowerTime +  ":" + UpperTime +  ":" + theEvent.time() +  ":" + theEvent.endTime() + ":" + myAudioData.player.length() / 1000d + ":" + myWidth );
//		double myLowerBound = CCMath.max(theEvent.time(), theView.context().lowerBound());
//    	double myUpperBound = CCMath.min(theEvent.endTime(), theView.context().upperBound());
    	
//    	Point2D p1 = theView.controller().curveToViewSpace(new ControlPoint(myLowerBound,_myController.value(0)));
//    	Point2D p2 = theView.controller().curveToViewSpace(new ControlPoint(myUpperBound,_myController.value(0)));
//        myPath.moveTo(theLower.getX(), theLower.getY());
//        myPath.lineTo(theUpper.getX(), theUpper.getY());
        
//        g.setColor(theView.fillColor());
        theG2d.draw(myPath);
	}
	
	@Override
	public void out() {
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}

	@Override
	public void play() {
		_myIsPlaying = true;
	}

	@Override
	public void stop() {
		_myIsPlaying = false;
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}
}
