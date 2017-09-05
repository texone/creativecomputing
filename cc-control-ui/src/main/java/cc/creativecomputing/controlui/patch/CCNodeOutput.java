package cc.creativecomputing.controlui.patch;

import java.util.ArrayList;
import java.util.List;

public class CCNodeOutput<Type> {
	public List<CCNodeInput> inputs = new ArrayList<>();
	
	private Type _myValue;
	
	public Type value(){
		return _myValue;
	}
	
	public void value(Type theValue){
		_myValue = theValue;
	}
}
