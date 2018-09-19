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
package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCPathHandle extends CCPropertyHandle<Path>{
	
	private CCAsset<?> _myAsset;
	
	protected CCPathHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		CCLog.info(theParent, theParent.value());
		if(theParent.value() != null &&theParent.value() instanceof CCAsset<?>){
			_myAsset = (CCAsset<?>)theParent.value();
		}
	}
	
	@Override
	public void mute(boolean theMute) {
		_myAsset.mute(theMute);
	}
	
	@Override
	public void data(CCDataObject theData) {
		if(!theData.containsKey("value")){
			value(null, true);
			return;
		}
		value(Paths.get(theData.getString("value")), true);
	}
	
	public String[] extensions(){
		if(_myAsset == null)return null;
		return _myAsset.extensions();
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public void valueCasted(Object theValue, boolean theOverWrite) {
		Object myValue = theValue instanceof String ? Paths.get((String)theValue) : theValue;
		super.valueCasted(myValue, theOverWrite);
	}
	
	@Override
	public String valueString() {
		if(_myAsset.path() == null)return "";
		return _myAsset.path().toString();
	}
	
	public void time(double theGlobalTime, double theEventTime, double theContentOffset){
		if(_myAsset == null)return;
		_myAsset.time(theGlobalTime, theEventTime, theContentOffset);
	}
	
//	public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
//		_myAsset.renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
//	}
	
	public void reset(CCEventPoint theTimedEvent){
		_myAsset.reset(theTimedEvent);
	}
	
	public CCAsset<?> asset(){
		return _myAsset;
	}
	
	public void out(){
		if(_myAsset == null)return;
		_myAsset.out();
	}
	
	public void play(){
		if(_myAsset == null)return;
		_myAsset.play();
	}
	
	public void stop(){
		if(_myAsset == null)return;
		_myAsset.stop();
	}
}
