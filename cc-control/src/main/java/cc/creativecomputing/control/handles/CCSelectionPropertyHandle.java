package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.CCSelection.CCSelectionListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

public class CCSelectionPropertyHandle extends CCPropertyHandle<CCSelection>{

	protected CCSelectionPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		value().events().add(new CCSelectionListener() {
			
			@Override
			public void onChangeValues(CCSelection theSelection) {
				// TODO Auto-generated method stub
				
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void onChange(String theValue) {
				_myEvents.proxy().onChange(value());
			}
		});
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return value().value();
	}
	
	@Override
	public Object dataObject() {
		return value().value();
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		String myName = theData.getString("value");
		value().value(myName);
		
	}
	
	

}