package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.handles.CCFloatHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCFloatControl extends CCNumberControl<Float>{

	public CCFloatControl(CCFloatHandle theHandle, CCControlComponent theControlComponent) {
		super(theHandle, theControlComponent);
	}

	@Override
	public Float value() {
		return (float)_myValue;
	}

}
