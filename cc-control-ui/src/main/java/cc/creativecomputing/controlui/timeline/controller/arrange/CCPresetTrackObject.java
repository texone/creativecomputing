package cc.creativecomputing.controlui.timeline.controller.arrange;

import java.nio.file.Paths;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;

public class CCPresetTrackObject extends EventTrackAdapter{
	
	private CCObjectPropertyHandle _myPropertyHandle;
	
	public CCPresetTrackObject(CCObjectPropertyHandle theObjectPropertyHandle){
		_myPropertyHandle = theObjectPropertyHandle;
	}
	
	private String _myLastPreset = null;
	private CCDataObject _myPresetObject = null;
	
	@Override
	public void onTime(double theTime, EventTrackController theController, TimedEventPoint thePoint) {
		_myRestore = true;
		String myPreset = thePoint.content().value() == null ? null : (String)thePoint.content().value();
		
		if(_myLastPreset == myPreset || _myLastPreset != null && _myLastPreset.equals(myPreset)){
			if(_myPresetObject == null)return;
			_myPropertyHandle.data(_myPresetObject);
		}else{
			_myLastPreset = myPreset;
			if(myPreset == null){
				_myPresetObject = null;
			}else{
				_myPresetObject = CCDataIO.createDataObject(_myPropertyHandle.presetPath().resolve(Paths.get(_myLastPreset + ".json")));
				_myPresetObject.put("timeline", "timeline");
			}
			_myPropertyHandle.data(_myPresetObject);
		}
	}
	
	private boolean _myRestore = false;
	
	@Override
	public void onOut() {
		if(!_myRestore)return;
		_myRestore = false;
		_myPropertyHandle.restorePreset();
	}
}