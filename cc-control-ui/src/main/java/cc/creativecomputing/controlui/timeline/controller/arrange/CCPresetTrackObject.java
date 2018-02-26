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
package cc.creativecomputing.controlui.timeline.controller.arrange;

import java.nio.file.Paths;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;

public class CCPresetTrackObject extends CCEventTrackAdapter{
	
	private CCObjectPropertyHandle _myPropertyHandle;
	
	public CCPresetTrackObject(CCObjectPropertyHandle theObjectPropertyHandle){
		_myPropertyHandle = theObjectPropertyHandle;
	}
	
	private String _myLastPreset = null;
	private CCDataObject _myPresetObject = null;
	
	@Override
	public void onTime(double theTime, CCEventTrackController theController, CCTimedEventPoint thePoint) {
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
