package cc.creativecomputing.controlui;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.protocol.midi.CCMidiIn;
import cc.creativecomputing.protocol.midi.CCMidiTimeCode;

public class CCTimelineSynch implements CCAnimatorListener{
	
	public enum CCSynchMode{
		OFF,
		MASTER,
		SLAVE
	}

//	@CCProperty(name = "animator")
	private CCAnimator _myAnimator = new CCAnimator();
	
	@CCProperty(name = "midi in")
	private CCMidiIn _myMidiIn = new CCMidiIn();
	
	@CCProperty(name = "time offset")
	private double _myTimeOffset = 0;
	
	private CCMidiTimeCode _myTimeCode = new CCMidiTimeCode();
	
	@CCProperty(name = "synch")
	private CCSynchMode _mySynchMode = CCSynchMode.OFF;
	
	private TimelineContainer _myTimeline = null;
	
	public CCTimelineSynch(CCAnimator theAnimator){
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
	}
	
//	public CCTimelineSynch(){
//		_myMidiIn.events().add(_myTimeCode);
//		_myAnimator.listener().add(this);
//		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PERFORMANT;
//	}
	
	public void timeline(TimelineContainer theTimeline){
		_myTimeline = theTimeline;
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	

	@Override
	public void start(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}
	
	@CCProperty(name = "bpm", defaultValue = 120)
	private void bpm(int theBPM){
		if(theBPM <= 0)return;
		_myTimeCode.bpm(theBPM);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_myTimeline == null)return;
		switch(_mySynchMode){
		case OFF:
			_myTimeline.update(theAnimator.deltaTime());
			break;
		case SLAVE:
			_myTimeline.time(_myTimeCode.time() + _myTimeOffset);
			break;
		case MASTER:
			break;
		}
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}
}
