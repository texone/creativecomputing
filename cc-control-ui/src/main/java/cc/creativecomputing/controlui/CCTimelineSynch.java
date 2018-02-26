/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
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
	
	private CCTimelineContainer _myTimeline = null;
	
	public CCTimelineSynch(CCAnimator theAnimator){
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
	}
	
//	public CCTimelineSynch(){
//		_myMidiIn.events().add(_myTimeCode);
//		_myAnimator.listener().add(this);
//		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PERFORMANT;
//	}
	
	public void timeline(CCTimelineContainer theTimeline){
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
