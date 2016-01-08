package cc.creativecomputing.kle;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.formats.CCSequenceIO;
import cc.creativecomputing.math.CCMatrix2;

public class CCSequenceAsset extends CCAsset<CCSequence>{
	
	
	private Map<Path, CCSequence> _mySequenceMap = new HashMap<>();
	
	private Path _myPath;
	
	@CCProperty(name = "rate", min = 1, max = 120)
	private float _cRate = 5;
	
	private CCMatrix2 _myFrame;
	
	private final CCSequenceMapping<?> _myMapping;
	
	public CCSequenceAsset(CCSequenceMapping theMapping){
		_myAsset = null;
		_myFrame = null;
		_myMapping = theMapping;
	}
	
	private CCSequence loadAsset(Path thePath){
		return CCSequenceIO.load(thePath, _myMapping);
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			_myPath = thePath;
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
	
	public void frame(int theFrame){
		if(_myAsset == null)return;
		int myFrame = theFrame % _myAsset.length();
		_myFrame = _myAsset.frame(myFrame);
	}
	
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		if(_myAsset == null)return;
		
		float myFrame = ((float)theEventTime * _cRate) % _myAsset.length();
		_myFrame = _myAsset.frame(myFrame);
	}
	
	@Override
	public void renderTimedEvent(TimedEventPoint theTimedEvent,Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		super.renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
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
