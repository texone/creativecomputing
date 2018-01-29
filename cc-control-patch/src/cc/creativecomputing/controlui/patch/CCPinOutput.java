package cc.creativecomputing.controlui.patch;

import java.util.ArrayList;
import java.util.List;

public class CCPinOutput<Type> {
	public List<CCPinInput> inputs = new ArrayList<>();
	
	private Type _myValue;
	
	public Type value(){
		return _myValue;
	}
	
	public void value(Type theValue){
		_myValue = theValue;
	}
}
