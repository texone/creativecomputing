package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.handles.CCIntHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCIntControl extends CCNumberControl<Integer>{

	public CCIntControl(CCIntHandle theHandle, CCControlComponent theControlComponent) {
		super(theHandle, theControlComponent);
	}

	@Override
	public Integer value() {
		return (int)_myValue;
	}

}
