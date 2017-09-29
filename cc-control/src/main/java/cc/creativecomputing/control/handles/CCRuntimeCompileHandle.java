package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.code.CCRuntimeCompilable;
import cc.creativecomputing.control.code.CCRuntimeCompiler;
import cc.creativecomputing.control.code.CCRuntimeCompiler.CCClassLoader;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCRuntimeCompileHandle extends CCPropertyHandle<CCRuntimeCompilable>{
	
	private CCClassLoader _myClassLoader;
	
	protected CCRuntimeCompileHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		
		_myClassLoader = CCRuntimeCompiler.loader(theMember.value().getClass());
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCRuntimeCompilable myRealtimeObject = value();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCRuntimeCompilable myRealtimeObject = value();
		onChange();
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
	
	@Override
	public void update(double theDeltaTime) {
		if(!_myClassLoader.isUpdated())return;
		Class<?> myClass = _myClassLoader.recompile();
		try {
			CCRuntimeCompilable myValue = (CCRuntimeCompilable)myClass.newInstance();
			CCLog.info(myValue);
			value(myValue, true);
			CCLog.info(value());
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.update(theDeltaTime);
	}
}