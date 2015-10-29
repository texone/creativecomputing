package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCPathHandle extends CCPropertyHandle<Path>{
	
	private CCAsset<?> _myAsset;
	
	protected CCPathHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		
		if(theParent.value() instanceof CCAsset<?>){
			_myAsset = (CCAsset<?>)theParent.value();
		}
	}
	
	@Override
	public void data(CCDataObject theData) {
		if(!theData.containsKey("value")){
			value(null, true);
			return;
		}
		value(Paths.get(theData.getString("value")), true);
	}

	@Override
	public Path convertNormalizedValue(double theValue) {
		return null;
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
	
	public void time(double theGlobalTime, double theEventTime){
		if(_myAsset == null)return;
		_myAsset.time(theGlobalTime, theEventTime);
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