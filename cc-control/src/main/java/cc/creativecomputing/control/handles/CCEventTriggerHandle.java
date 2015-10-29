package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMethod;
import cc.creativecomputing.io.data.CCDataObject;

public class CCEventTriggerHandle extends CCPropertyHandle<Object>{
	
	private CCMethod<CCProperty> _myMethod;
	
	private CCTriggerProgress _myProgress;
	
	protected CCEventTriggerHandle(CCObjectPropertyHandle theParent, CCMethod<CCProperty> theMethod) {
		super(theParent, theMethod);
		_myMethod = theMethod;
		_myProgress = new CCTriggerProgress();
	}
	
	public CCTriggerProgress progress(){
		return _myProgress;
	}

	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public Object convertNormalizedValue(double theValue) {
		return null;
	}

	@Override
	public String valueString() {
		return null;
	}
	
	@Override
	public void value(Object theValue, boolean theOverWrite) {}
	
	private boolean _myDoTrigger = false;
	
	public void trigger(){
		_myDoTrigger = true;
	}
	
	@Override
	public void update(double theDeltaTime) {
		if(!_myDoTrigger)return;
		if(_myMethod.type() == CCTriggerProgress.class){
			_myMethod.trigger(_myProgress);
		}else{
			_myMethod.trigger();
		}
		_myDoTrigger = false;
	}

	@Override
	public CCDataObject data(){
		CCDataObject myResult = new CCDataObject();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData){}
}
