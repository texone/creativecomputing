package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.handles.CCDoubleHandle;
import cc.creativecomputing.controlui.CCControlComponent;

public class CCDoubleControl extends CCNumberControl<Double>{

	public CCDoubleControl(CCDoubleHandle theHandle, CCControlComponent theControlComponent) {
		super(theHandle, theControlComponent);
	}

	@Override
	public Double value() {
		return _myValue;
	}

}
